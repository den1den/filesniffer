package main.files.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Dennis on 5-12-2016.
 */
public class MusicFileChecker {
    public static final java.util.regex.Pattern OUTPUT_REGEX = Pattern.compile("-\\s(\\w+):\\s(.+)\\s\\[(-?\\d+)\\]");
    static File MP3DiagsExeDir = new File(new File("").getAbsoluteFile(), "tools\\MP3DiagsExe\\");

    Result checkMP3(File f){
        List<String[]> output = new LinkedList<>();
        try {
            Process process = Runtime.getRuntime().exec(
                    new String[]{"cmd", "/C", "MP3DiagsCLI.cmd", f.getAbsolutePath()},
                    null,
                    MP3DiagsExeDir
            );
            BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bufferedReaderErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            do {
                flushBuffer(bufferedReaderInput, output);
                flushBuffer(bufferedReaderErr, output);
                process.waitFor(1, TimeUnit.SECONDS);
            } while (process.isAlive());

            int exitValue = process.exitValue();
            if(exitValue != 0){
                return null;
            }

            flushBuffer(bufferedReaderInput, output);
            flushBuffer(bufferedReaderErr, output);

            return new Result(output);
        } catch (IOException e) {
            throw new Error(e);
        } catch (InterruptedException e) {
            throw new Error("MP3DiagsCLI has FAILED to finish in 5 seconds", e);
        }
    }

    private void flushBuffer(BufferedReader bufferedReaderErr, List<String[]> output) throws IOException {
        while (true){
            String line = bufferedReaderErr.readLine();
            if(line == null){
                return;
            }

            Matcher matcher = OUTPUT_REGEX.matcher(line);
            if(!matcher.find()){
                // System.out.println("line = " + line);
                continue;
            }

            output.add(new String[]{
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3)
            });
        }
    }

    static class Result {
        List<String[]> output;
        final int warnings, errors;

        public Result(List<String[]> output) {
            this.output = new ArrayList<>(output);
            this.warnings = (int)this.output.stream().filter(s -> (s[0].equals("WARNING"))).count();
            this.errors = (int)this.output.stream().filter(s -> (s[0].equals("ERROR"))).count();
        }public String getMessages(){
            return output.stream().map(s -> (s[1] + System.lineSeparator())).collect(Collectors.joining());
        }

        @Override
        public String toString() {
            return "Result{" +
                    "warnings=" + warnings +
                    ", errors=" + errors +
                    '}';
        }
    }
}
