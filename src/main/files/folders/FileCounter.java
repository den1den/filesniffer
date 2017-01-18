package main.files.folders;

import main.files.MyFileVisitor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by Dennis on 8-12-2016.
 */
public class FileCounter extends MyFileVisitor {
    private int i = 0;

    FileCounter() {
    }

    FileCounter(boolean ignoreAccesDenied) {
        super(ignoreAccesDenied);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        i++;
        return super.visitFile(file, attrs);
    }

    public static int countfiles(Path dir, boolean ignoreAccesDenied) throws IOException {
        FileCounter fc = new FileCounter(ignoreAccesDenied);
        Files.walkFileTree(dir, fc);
        return fc.i;
    }
}
