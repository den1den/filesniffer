package main;//import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by Dennis on 5-12-2016.
 */
public class FileCheckerTest {
    @Test
    public void analyzeImageTrue() throws Exception {
        Path imagePath = FileSystems.getDefault().getPath("D:\\Pictures\\kicking-butt-14395194.jpg");
        FileChecker.ImageAnalysisResult imageAnalysisResult = FileChecker.analyzeImage(imagePath);
        assert imageAnalysisResult.image;
    }

    @Test
    public void analyzeImageFalse() throws Exception {
        Path imagePath = FileSystems.getDefault().getPath("files\\kicking-butt-14395194-false.jpg");
        FileChecker.ImageAnalysisResult imageAnalysisResult = FileChecker.analyzeImage(imagePath);
        assert !imageAnalysisResult.image;
    }

}