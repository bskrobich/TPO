package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {

    public static void processDir(String dirName, String resultFileName) {

        try (FileChannel outputChannel = FileChannel.open(Paths.get(resultFileName),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ){
            Charset outputCharset = StandardCharsets.UTF_8;
            Files.walkFileTree(Paths.get(dirName), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try (FileChannel inputChannel = FileChannel.open(file)) {
                        if (!file.getFileName().toString().equals(".DS_Store")) {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                            Charset cp1250 = Charset.forName("cp1250");

                            while (inputChannel.read(buffer) != -1) {
                                buffer.flip();
                                CharBuffer decoded = cp1250.decode(buffer);
                                outputChannel.write(outputCharset.encode(decoded));
                                buffer.clear();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.println(exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
