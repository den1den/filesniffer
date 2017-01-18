package main.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by Dennis on 8-12-2016.
 */
public class MyFileVisitor extends SimpleFileVisitor<Path> {
    protected boolean ignoreAccesDenied;

    protected MyFileVisitor() {
        ignoreAccesDenied = false;
    }

    protected MyFileVisitor(boolean ignoreAccesDenied) {
        this.ignoreAccesDenied = ignoreAccesDenied;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if(ignoreAccesDenied && exc.getClass().equals(AccessDeniedException.class)){
            return FileVisitResult.CONTINUE;
        }
        return visitFileFailed(file, exc);
    }

    static protected class MyFileReader extends MyFileVisitor {
        public MyFileReader() {
            super(false);
        }

        final LinkedList<TraversedResult> results = new LinkedList<>();
        final List<FailedResult> failed = new LinkedList<>();

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            System.out.println("dir = [" + dir + "], attrs = [" + attrs + "]");
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            TraversedResult result = new TraversedResult(file, attrs);
            results.add(result);
            System.out.println("result = " + result);
            return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return super.postVisitDirectory(dir, exc);
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            failed.add(new FailedResult(file, exc));
            return CONTINUE;
        }

        private class FailedResult {
            Path p;
            IOException e;

            FailedResult(Path p, IOException e) {
                this.p = p;
                this.e = e;
            }

            @Override
            public String toString() {
                return "\r\nFailedResult{" +
                        "p=" + p +
                        ", e=" + e +
                        "}";
            }
        }

        private class TraversedResult {
            Path p;
            BasicFileAttributes attributes;

            TraversedResult(Path p, BasicFileAttributes attributes) {
                this.p = p;
                this.attributes = attributes;
            }

            @Override
            public String toString() {
                return "TraversedResult{" +
                        "p=" + p +
                        ", attributes=" + attributes +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "MyFileReader{" +
                    "\nresults=" + Arrays.toString(results.toArray()) +
                    ", \nfailed=" + Arrays.toString(failed.toArray()) +
                    '}';
        }
    }

    public static interface DoWithFile extends Consumer<Path> {}

    public void exec(Path path){
        try {
            Files.walkFileTree(path, this);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    public void exec(Path path, int depth){
        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), depth, this);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void exec(String path){
        this.exec(Paths.get(path));
    }

    public void exec(String path, int depth){
        this.exec(Paths.get(path), depth);
    }
}
