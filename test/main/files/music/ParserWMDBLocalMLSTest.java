package main.files.music;//import static org.junit.Assert.*;

import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dennis on 6-12-2016.
 */
public class ParserWMDBLocalMLSTest {

    File file0 = new File("D:\\Music\\BACKUP_WMPDB\\LocalMLS_0.wmdb");

    @Test
    public void tst() throws Exception {
        int i = 0;
        Set<Charset> candidates = new HashSet<>();
        for (Charset c :
                Charset.availableCharsets().values()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file0), c));
            String line = reader.readLine();
            if(line.length() < 2000){
                System.out.println(i + ": " + c + ": " + line);
            } else {
                System.out.println(i + ": " + c + ": size = " + line.length());
            }
            if("Windows Media Player".equals(line)){
                candidates.add(c);
            } else {
                while (line!=null){
                    if (line.contains("A\u0000c\u0000d\u0000a\u0000")) {
                        candidates.add(c);
                        break;
                    }
                    line = reader.readLine();
                }

            }


//            PrintStream o = new PrintStream("out/raw/out-"+c.displayName()+".txt");
//            while (line != null){
//                o.println(line);
//                line = reader.readLine();
//            }

            i++;
        }
        System.err.println("candidates = " + candidates);

        for (Charset c:
             candidates) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file0), c));
            PrintStream o = new PrintStream("out/candidates/out-"+c.displayName()+".txt");
            while (true){
                String line = reader.readLine();
                if(line == null){
                    break;
                }
                o.println(line);
            }
        }
    }
}