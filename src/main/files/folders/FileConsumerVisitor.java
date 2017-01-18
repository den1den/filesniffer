package main.files.folders;

import main.files.MyFileVisitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

/**
 * Created by Dennis on 18-1-2017.
 */
public class FileConsumerVisitor extends MyFileVisitor {

    private DoWithFile doWithFile;

    public FileConsumerVisitor(DoWithFile doWithFile) {
        this.doWithFile = doWithFile;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        FileVisitResult result = super.visitFile(file, attrs);
        doWithFile.accept(file);
        return result;
    }
}
