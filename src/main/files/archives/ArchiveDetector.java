package main.files.archives;

import java.io.*;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dennis on 8-12-2016.
 */
public class ArchiveDetector {

    private static final String UNRAR_EXE = "C:\\Program Files\\WinRAR\\UnRAR.exe";
    private static final java.util.regex.Pattern ARCHIVE_VERSION = Pattern.compile("Details:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_NAME = Pattern.compile("\\s*Name:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_SIZE = Pattern.compile("\\s*Size:\\s(\\d+)");
    private static final java.util.regex.Pattern ARCHIVE_PACKED_SIZE = Pattern.compile("\\s*Packed size:\\s(\\d+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_TYPE = Pattern.compile("\\s*Type:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_ATTS = Pattern.compile("\\s*Attributes:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_CRC = Pattern.compile("\\s*CRC32:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_COMPRESSION = Pattern.compile("\\s*Compression:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_OS = Pattern.compile("\\s*Host OS:\\s(.+)");
    private static final java.util.regex.Pattern ARCHIVE_FILE_TIME = Pattern.compile("\\s*mtime:\\s(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2},\\d{3})");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm,sss");

    private static String match(String line, Pattern pattern){
//        System.out.println(" line = `" + line + "`");
        Matcher matcher = pattern.matcher(line);
        matcher.matches();
        String group = matcher.group(1);
//        System.out.println("found = " + group);
//        System.out.println();
        return group;
    }

    public WinRarListResults getWinrarList(Path file)  {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{UNRAR_EXE, "lt", file.toString()});
            String version;
            List<WinRarListResults.RarFile> files = new LinkedList<>();
            List<WinRarListResults.RarDir> dirs = new LinkedList<>();
            try (InputStream inputStream = process.getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                line = reader.readLine(); // empty
                line = reader.readLine(); // UNRAR 5.21 freeware
                line = reader.readLine(); // empty
                line = reader.readLine(); // Archive
                line = reader.readLine(); // empty
                Matcher matcher = ARCHIVE_VERSION.matcher(line);
                matcher.matches();
                version = matcher.group(1);

                line = reader.readLine(); // empty
                line = reader.readLine(); // name
                while (line != null) {
                    String name, type, attributes, CRC32, OS, compression;
                    long size = 0, packed_size = 0, mtime;
                    boolean dir;

                    name = match(line, ARCHIVE_FILE_NAME);
                    line = reader.readLine();
                    type = match(line, ARCHIVE_FILE_TYPE);
                    dir = "Directory".equals(type);
                    if (!dir) {
                        line = reader.readLine();
                        size = Long.parseLong(match(line, ARCHIVE_FILE_SIZE));
                        line = reader.readLine();
                        packed_size = Long.parseLong(match(line, ARCHIVE_PACKED_SIZE));
                        line = reader.readLine(); // ratio
                    }
                    try {
                        line = reader.readLine();
                        mtime = SIMPLE_DATE_FORMAT.parse(match(line, ARCHIVE_FILE_TIME)).getTime(); //
                    } catch (ParseException e) {
                        throw new IOError(e);
                    }
                    line = reader.readLine();
                    attributes = match(line, ARCHIVE_FILE_ATTS);
                    line = reader.readLine();
                    CRC32 = match(line, ARCHIVE_FILE_CRC);
                    line = reader.readLine(); // host os
                    OS = match(line, ARCHIVE_FILE_OS);
                    line = reader.readLine(); // Compression
                    compression = match(line, ARCHIVE_FILE_COMPRESSION);
                    line = reader.readLine(); // empty
                    line = reader.readLine(); // next?

                    if (dir) {
                        WinRarListResults.RarDir result = new WinRarListResults.RarDir(name, type, attributes, CRC32, OS, compression, mtime);
                        dirs.add(result);
                    } else {
                        WinRarListResults.RarFile result = new WinRarListResults.RarFile(name, type, attributes, CRC32, OS, compression, size, packed_size, mtime);
                        files.add(result);
                    }
                }
                while (line != null) ;
                return new WinRarListResults(file, version, files, dirs);
            }
        }catch (IOException e){
            throw new Error(e);
        }
    }

    public static class WinRarListResults {
        Path file;
        String version;
        List<RarFile> files;
        List<RarDir> dirs;

        WinRarListResults(Path file, String version, List<RarFile> files, List<RarDir> dirs) {
            this.file = file;
            this.version = version;
            this.files = files;
            this.dirs = dirs;
        }

        public static class RarDir {
            String name;
            String type, attributes, CRC32, OS, compression;
            long mtime;

            RarDir(String name, String type, String attributes, String CRC32, String OS, String compression, long mtime) {
                this.name = name;
                this.type = type;
                this.attributes = attributes;
                this.CRC32 = CRC32;
                this.OS = OS;
                this.compression = compression;
                this.mtime = mtime;
            }

            @Override
            public String toString() {
                return "RarDir{" +
                        "name='" + name + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }
        }

        public static class RarFile {
            String name;
            String type, attributes, CRC32, OS, compression;
            long size, packed_size, mtime;

            RarFile(String name, String type, String attributes, String CRC32, String OS, String compression, long size, long packed_size, long mtime) {
                this.name = name;
                this.type = type;
                this.attributes = attributes;
                this.CRC32 = CRC32;
                this.OS = OS;
                this.compression = compression;
                this.size = size;
                this.packed_size = packed_size;
                this.mtime = mtime;
            }

            @Override
            public String toString() {
                return "RarFile{" +
                        "name='" + name + '\'' +
                        ", type='" + type + '\'' +
                        ", compression='" + ((double)packed_size)/size + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "WinRarListResults{" +
                    "version='" + version + '\'' +
                    ", files=" + files.size() +
                    ", dirs=" + dirs.size() +
                    ", file=" + file +
                    '}';
        }
    }
}
