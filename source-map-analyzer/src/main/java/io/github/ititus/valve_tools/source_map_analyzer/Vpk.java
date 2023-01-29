package io.github.ititus.valve_tools.source_map_analyzer;

import io.github.ititus.commons.io.PathUtil;
import picocli.CommandLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@CommandLine.Command(name = "vpk", mixinStandardHelpOptions = true, subcommands = CommandLine.HelpCommand.class)
class Vpk {

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

    private static String sanitize(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        name.codePoints().forEachOrdered(cp -> {
            if (Character.isISOControl(cp)) {
                return;
            } else if (cp == '\\' || cp == '/' || cp == ':' || cp == '?' || cp == '"' || cp == '<' || cp == '>' || cp == '|' || cp >= '\u007f') {
                b.append('_');
            }

            b.append((char) cp);
        });

        if (b.isEmpty() || b.charAt(b.length() - 1) == '.' || b.charAt(b.length() - 1) == ' ') {
            b.append("_");
        }

        var idx = b.indexOf(".");
        if (3 <= idx && idx <= 4 && Set.of("CON", "PRN", "AUX", "NUL", "COM0", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT0", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9").contains(b.substring(0, idx).toUpperCase(Locale.ROOT))) {
            b.insert(0, '_');
        }

        return b.toString();
    }

    @CommandLine.Command(mixinStandardHelpOptions = true)
    int extract(
            @CommandLine.Option(names = {"-o", "--out"}, defaultValue = ".", paramLabel = "<output dir>") Path outDir,
            @CommandLine.Parameters(index = "0", paramLabel = "<file to extract>") Path fileToExtract
    ) throws Exception {
        System.out.println("extracting '" + fileToExtract + "' to '" + outDir + "'");
        try (var fs = FileSystems.newFileSystem(fileToExtract)) {
            var resolvedOutDir = PathUtil.createOrResolveRealDir(outDir);
            var root = getRoot(fs).toRealPath();
            try (var s = Files.walk(root)) {
                s.forEach(p -> {
                    try {
                        p = p.toRealPath();
                        Path relative = root.relativize(p).normalize();
                        var sanitized = IntStream.range(0, relative.getNameCount())
                                .mapToObj(relative::getName)
                                .map(Path::toString)
                                .filter(name -> !name.isEmpty())
                                .peek(name -> {
                                    if (".".equals(name) || "..".equals(name)) {
                                        throw new RuntimeException("unexpected dots found in path");
                                    }
                                })
                                .map(Vpk::sanitize)
                                .collect(Collectors.joining("/"));
                        var resultPath = resolvedOutDir.resolve(sanitized).toAbsolutePath().normalize();
                        System.out.println(p + " -> " + resultPath);
                        if (Files.isDirectory(p)) {
                            PathUtil.createOrResolveRealDir(resultPath);
                        } else if (Files.isRegularFile(p)) {
                            Files.copy(p, PathUtil.createParentsAndResolveFile(resultPath), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            throw new RuntimeException("file must be either a regular file or directory");
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }

        return CommandLine.ExitCode.OK;
    }
}
