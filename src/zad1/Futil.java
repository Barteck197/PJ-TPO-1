package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class Futil {
    static void processDir(String directoryName, String resultFileName) {
        createFileIfNotExists();

        Charset charset1250 = Charset.forName("Cp1250");
        Charset charsetUTF8 = StandardCharsets.UTF_8;

        try {
            FileChannel outFileChannel = FileChannel.open(Paths.get(resultFileName), StandardOpenOption.WRITE);
            Files.walkFileTree(Paths.get(directoryName), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().equals(resultFileName)) {
//                        System.out.println("Found end file. Skipping.");
                        return FileVisitResult.CONTINUE;
                    }

                    // Otwieramy kanał do aktualnie czytanego pliku
                    FileChannel currentFileChannel = FileChannel.open(file);

                    // Bufor bajtowy o rozmiarze równym czytanemu plikowi
                    ByteBuffer byteBuffer = ByteBuffer.allocate((int) currentFileChannel.size());
                    byteBuffer.clear();

                    // Czytamy z aktualnego kanału
                    currentFileChannel.read(byteBuffer);

                    // Powrót bufora na początek
                    byteBuffer.flip();

                    // Odkodowanie
                    CharBuffer charBuffer = charset1250.decode(byteBuffer);

                    // Zapisanie do kanału
                    outFileChannel.write(charsetUTF8.encode(charBuffer));

                    currentFileChannel.close();

                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    boolean finishedSearch = Files.isSameFile(dir, Paths.get(directoryName));
                    if (finishedSearch) {
                        outFileChannel.close();
                        return TERMINATE;
                    }
                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFileIfNotExists() {
        Path newPath = Paths.get("TPO1res.txt");
        if (!Files.exists(newPath)) {
            try {
                Files.createFile(newPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
