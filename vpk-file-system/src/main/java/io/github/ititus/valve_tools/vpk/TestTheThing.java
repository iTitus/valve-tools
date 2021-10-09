package io.github.ititus.valve_tools.vpk;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestTheThing {

    public static void main(String[] args) throws Exception {
        Path vpk = Path.of("C:/Program Files (x86)/Steam/steamapps/common/Counter-Strike Global Offensive/csgo/pak01_dir.vpk");
        try (FileSystem fs = FileSystems.newFileSystem(vpk, (ClassLoader) null)) {
            Path root = fs.getRootDirectories().iterator().next();
            System.out.println(fs);
            try (Stream<Path> stream = Files.walk(root)) {
                stream
                        .filter(Files::isRegularFile)
                        .sorted()
                        .forEachOrdered(System.out::println);
            }
        }
    }
}
