package main.files.music;//import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;

/**
 * Created by Dennis on 5-12-2016.
 */
public class ParserWMDBFolderTest {
    ParserWMDBFolder p = new ParserWMDBFolder();

    @Test
    public void parse() throws Exception {
        File dbfile = new File("D:\\Music\\BACKUP_WMPDB\\wmpfolders.wmdb");
        assert dbfile.exists();
        assert dbfile.isFile();
        p.parser(dbfile);
    }
}