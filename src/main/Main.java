package main;

import main.database.DBConnection;
import main.files.MyFileVisitor;
import main.files.folders.FileConsumerVisitor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Dennis on 4-12-2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        long t0 = System.currentTimeMillis();
        fillDBWithRars();
        long t1 = System.currentTimeMillis();

        System.out.println("(time) = " + (t1 - t0));
    }

    private static void fillDBWithRars() throws SQLException {
        final String ROOT = "G:\\Recovered RAR files";
        final int BAG_SIZE = 1000;
        final Connection CON = DBConnection.getConnection();
        final String SQL = DBConnection.getInsertSQL("finesniffer_file", new String[]{"original_filename", "extension", "file_group_id"}, new String[]{"?", "?", "NULL"});

        CON.setAutoCommit(false);
        PreparedStatement statement = CON.prepareStatement(SQL);

        Iterator<File> walker = FileUtils.iterateFiles(new File(ROOT), null, true);
        Set<String> extensions = new HashSet<>();

        int cnt = 0;
        int bag = 0;
        while (walker.hasNext()){
            File next = walker.next();
            if(!next.isFile()){
                continue;
            }
            String path = next.getAbsolutePath();

            String ext = FilenameUtils.getExtension(path).toLowerCase();
            if(extensions.add(ext)){
                System.out.println("ext = " + ext);
            }

            if("rar".equals(ext)) {
                statement.setString(1, path);
                statement.setString(2, ext);
                statement.addBatch();
                cnt++;

                if(++bag == BAG_SIZE){
                    statement.executeBatch();
                    bag=0;
                }
            }
        }
        statement.executeBatch();
        CON.commit();

        System.out.println("cnt = " + cnt);
    }

    private static void fillDBrars() throws SQLException, IOException {
        //BATCH >= 100 => time = 500 ms
        final int BATCH = 100;
        final String ROOT = "T:\\TEMP";

        StringBuilder SQL = new StringBuilder("INSERT INTO `filesniffer_django`.`finesniffer_file` (`original_filename`, `extension`, `file_group_id`) VALUES (?, ?, NULL)");
        for (int i = 1; i < BATCH; i++) {
            SQL.append(",(?,?,NULL)");
        }
        SQL.append(';');

        PreparedStatement statement = DBConnection.getConnection().prepareStatement(SQL.toString());
        ArrayList<String> files = new ArrayList<>(BATCH);

        Iterator<File> walker = FileUtils.iterateFiles(new File(ROOT), null, true);

        int bag = 0;
        while (walker.hasNext()){
            File p = walker.next();
            if(!p.isFile()){
                continue;
            }
            String path = p.getAbsolutePath();
            statement.setString(2 * bag + 1, path);
            statement.setString(2 * bag + 2, FilenameUtils.getExtension(path).toLowerCase());
            files.add(path);
            bag++;

            if(bag == BATCH){
                statement.execute();
                files.clear();
                bag = 0;
            }
        }

        if(files.size() > 0){
            SQL = new StringBuilder("INSERT INTO `filesniffer_django`.`finesniffer_file` (`original_filename`, `extension`, `file_group_id`) VALUES ");
            for (int i = 0; i < files.size(); i++) {
                SQL.append("(?,?,NULL)");
                if(i < files.size() - 1){
                    SQL.append(",");
                }
            }
            statement = DBConnection.getConnection().prepareStatement(SQL.toString());
            bag = 0;
            for (String path : files) {
                statement.setString(2 * bag + 1, path);
                statement.setString(2 * bag + 2, FilenameUtils.getExtension(path));
                bag++;
            }
            statement.execute();
        }
    }
}
