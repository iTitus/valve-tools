package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.io.PathUtil;
import io.github.ititus.commons.math.time.DurationFormatter;
import picocli.CommandLine;

import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@CommandLine.Command(name = "vpk", mixinStandardHelpOptions = true, subcommands = CommandLine.HelpCommand.class)
class Vpk {

    private static final Set<String> RESERVED_NAMES = Set.of("CON", "PRN", "AUX", "NUL", "COM0", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT0", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

    private Vpk() {
    }

    private static Path getRoot(FileSystem fs) {
        Iterator<Path> rootIterator = fs.getRootDirectories().iterator();
        if (!rootIterator.hasNext()) {
            throw new IllegalStateException();
        }

        var root = rootIterator.next();
        if (rootIterator.hasNext()) {
            throw new IllegalStateException();
        }

        return root;
    }

    private static char sanitizeChar(int cp) {
        if (Character.isISOControl(cp)) {
            return 0;
        } else if (cp == '\\' || cp == '/' || cp == ':' || cp == '?' || cp == '"' || cp == '<' || cp == '>' || cp == '|' || cp >= '\u007f') {
            return '_';
        }

        return (char) cp;
    }

    private static String sanitizeName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        int i = 0;
        while (i < name.length()) {
            int cp = name.codePointAt(i);
            var sanitizedChar = sanitizeChar(cp);
            if (sanitizedChar != 0) {
                b.append(sanitizedChar);
            }

            i += Character.charCount(cp);
        }

        if (b.isEmpty() || b.charAt(b.length() - 1) == '.' || b.charAt(b.length() - 1) == ' ') {
            b.append('_');
        }

        int idx = b.indexOf(".");
        if ((idx == 3 || idx == 4) && RESERVED_NAMES.contains(b.substring(0, idx).toUpperCase(Locale.ROOT))) {
            b.insert(0, '_');
        }

        return b.toString();
    }

    private static String sanitizePath(Path relative) {
        return IntStream.range(0, relative.getNameCount())
                .mapToObj(relative::getName)
                .map(Path::toString)
                .filter(name -> !name.isEmpty())
                .peek(name -> {
                    if (".".equals(name) || "..".equals(name)) {
                        throw new RuntimeException("unexpected dots found in path");
                    }
                })
                .map(Vpk::sanitizeName)
                .collect(Collectors.joining("/"));
    }

    private static void extractImpl(Path resolvedOutDir, Path root, Path p) {
        try {
            var relative = root.relativize(p);
            var sanitized = sanitizePath(relative);
            var resultPath = resolvedOutDir.resolve(sanitized).toAbsolutePath().normalize();
            System.out.println(relative + " -> " + resultPath);
            if (Files.isDirectory(p)) {
                PathUtil.createOrResolveRealDir(resultPath);
            } else if (Files.isRegularFile(p)) {
                Files.copy(p, PathUtil.createParentsAndResolveFile(resultPath), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new RuntimeException("file must be either a regular file or directory");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CommandLine.Command(mixinStandardHelpOptions = true)
    int extract(
            @CommandLine.Option(names = {"-o", "--out"}, defaultValue = ".", paramLabel = "<output dir>") Path outDir,
            @CommandLine.Parameters(index = "0", paramLabel = "<file to extract>") Path fileToExtract
    ) throws Exception {
        System.out.println("extracting '" + fileToExtract + "' to '" + outDir + "'");
        var time = Instant.now();
        try (var fs = FileSystems.newFileSystem(fileToExtract)) {
            var resolvedOutDir = PathUtil.createOrResolveRealDir(outDir);
            var root = getRoot(fs).toRealPath();
            try (var s = Files.walk(root)) {
                s.forEach(p -> extractImpl(resolvedOutDir, root, p));
            }
        }
        var dur = Duration.between(time, Instant.now());
        System.out.println(DurationFormatter.format(dur));

        return CommandLine.ExitCode.OK;
    }
}
