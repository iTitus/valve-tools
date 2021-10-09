package io.github.ititus.valve_tools.vpk;

import io.github.ititus.io.PathUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestTheThing {

    public static void main(String[] args) throws Exception {
        Path csgo = PathUtil.resolveRealDir(Path.of("C:/Program Files (x86)/Steam/steamapps/common/Counter-Strike Global Offensive"));
        Path out = PathUtil.createOrResolveRealDir(Path.of(System.getProperty("user.home"), "Desktop/csgo_out"));

        Path vpk1 = PathUtil.resolveRealFile(csgo.resolve("csgo/pak01_dir.vpk"));
        Path vpk2 = PathUtil.resolveRealFile(csgo.resolve("csgo/pakxv_lowviolence_dir.vpk"));
        Path vpk3 = PathUtil.resolveRealFile(csgo.resolve("platform/platform_pak01_dir.vpk"));

        try (FileSystem fs = FileSystems.newFileSystem(vpk1, (ClassLoader) null)) {
            Path root = expectOne(fs.getRootDirectories());
            System.out.println(fs);
            /*try (Stream<Path> stream = Files.walk(root)) {
                stream
                        .filter(Files::isRegularFile)
                        .sorted()
                        .forEachOrdered(System.out::println);
            }*/

            Path someRandomFile = root.resolve("sound/weapons/xm1014/xm1014_pump.wav");
            System.out.println(someRandomFile.toUri());
        }

        /*extractAll(vpk1, out);
        extractAll(vpk2, out);
        extractAll(vpk3, out);*/

        Path skinDir = PathUtil.resolveRealDir(out.resolve("pak01/materials/models/weapons/customization"));
        List<String> images;
        try (Stream<Path> stream = Files.walk(skinDir)) {
            images = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> PathUtil.getExtension(p).map("vtf"::equals).orElse(false))
                    .map(skinDir::relativize)
                    .sorted(PathUtil.ASCIIBETICAL)
                    .map(Path::toString)
                    .map(s -> s.replace('\\', '/'))
                    .collect(Collectors.toList());
        }

        Files.write(out.resolve("images.txt"), images);
    }

    private static void extractAll(Path vpk, Path out) {
        String vpkName = PathUtil.getNameWithoutExtension(vpk);
        if (vpkName.endsWith("_dir")) {
            vpkName = vpkName.substring(0, vpkName.length() - 4);
        }

        Path extractDir = PathUtil.createOrResolveRealDir(out.resolve(vpkName));
        System.out.println("Extracting " + vpk + " to " + extractDir);
        try (FileSystem fs = FileSystems.newFileSystem(vpk, (ClassLoader) null)) {
            Path root = expectOne(fs.getRootDirectories());
            try (Stream<Path> stream = Files.walk(root)) {
                stream
                        .filter(Files::isRegularFile)
                        .forEach(p -> {
                            Path extracted = extractDir.resolve(root.relativize(p).toString());
                            try {
                                Files.createDirectories(extracted.getParent());
                                Files.copy(p, extracted, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static <T> T expectOne(Iterable<? extends T> iterable) {
        Iterator<? extends T> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            throw new NoSuchElementException();
        }

        T first = iterator.next();
        if (iterator.hasNext()) {
            throw new RuntimeException();
        }

        return first;
    }
}
