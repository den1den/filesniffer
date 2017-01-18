package main.files;//import static org.junit.Assert.*;

import main.files.archives.ArchiveDetector;
import main.files.folders.*;
import org.junit.Test;

import java.nio.file.*;

/**
 * Created by Dennis on 8-12-2016.
 */
public class FilesTest {
    @Test
    public void testcounter() throws Exception {
        Path path = FileSystems.getDefault().getPath("D:\\Repos\\filesniffer\\src");
        int i = FileCounter.countfiles(path, true);
        System.out.println("i = " + i);
    }

    @Test
    public void test() throws Exception {
        MyFileVisitor.MyFileReader fileReader = new MyFileVisitor.MyFileReader();
        Path path = Paths.get("H:\\Recovered data 12-07-2016 at 10_58_53\\More Lost Files(RAW)\\RAR compression file\\1185865121-1186793742_090");
        Files.walkFileTree(path, fileReader);
    }

    @Test
    public void testArchives() throws Exception {
        Path archiveFile = Paths.get("H:\\Recovered data 12-07-2016 at 10_58_53\\More Lost Files(RAW)\\RAR compression file\\1185865121-1186793742_090.RAR");
        ArchiveDetector.WinRarListResults results = new ArchiveDetector().getWinrarList(archiveFile);
        System.out.println("results = " + results);
    }

    @Test
    public void fillDB() throws Exception {
        //TODO
        ArchiveDetector detector = new ArchiveDetector();
        FileConsumerVisitor visitor = new FileConsumerVisitor(path -> {
            ArchiveDetector.WinRarListResults winrarList = detector.getWinrarList(path);
            System.out.println("winrarList = " + winrarList);
        });
        visitor.exec("H:\\Recovered data 12-07-2016 at 10_58_53\\More Lost Files(RAW)\\RAR compression file", 1);
    }
}