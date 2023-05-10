import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.Code128Writer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class BarcodeApp {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String outputPath = "output.png";
        String textToEncode = "Sample Text";

        Mat frame = captureFrameFromCamera();

        if (frame != null) {
            String barcodeText = readBarcodeFromFrame(frame);
            System.out.println("Barcode Text: " + barcodeText);

            generateBarcode(textToEncode, outputPath);
            System.out.println("Generated barcode image: " + outputPath);
        }
    }

    private static Mat captureFrameFromCamera() {
        VideoCapture capture = new VideoCapture(0);
        Mat frame = new Mat();

        if (capture.isOpened()) {
            while (true) {
                capture.read(frame);
                if (!frame.empty()) {
                    break;
                }
            }
        }

        capture.release();
        return frame;
    }

    private static String readBarcodeFromFrame(Mat frame) {
        try {
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", frame, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("Barcode not found");
        } catch (ChecksumException e) {
            System.out.println("Checksum error");
        } catch (FormatException e) {
            System.out.println("Format error");
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
        }

        return null;
    }

    private static void generateBarcode(String text, String outputPath) {
        Code128Writer code128Writer = new Code128Writer();
        BitMatrix bitMatrix;

        try {
            bitMatrix = code128Writer.encode(text, BarcodeFormat.CODE_128, 300, 100);
            Path path = new File(outputPath).toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        } catch (WriterException | IOException e) {
            System.err.println("Error generating barcode: " + e.getMessage());
        }
    }
    private static void loadOpenCVNativeLibrary() {
    String osName = System.getProperty("os.name");
    String opencvNativeLibraryName = "opencv_java451";
    String opencvNativeLibraryPath = "";

    if (osName.startsWith("Windows")) {
        opencvNativeLibraryPath = "C:\\Users\\adadi\\opencv-4.5.1-vc14_vc15\\opencv\\build\\java\\x64\\" + opencvNativeLibraryName + ".dll";
    } else if (osName.startsWith("Linux")) {
        opencvNativeLibraryPath = "/path/to/libopencv_java451.so";
    } else if (osName.startsWith("Mac")) {
        opencvNativeLibraryPath = "/path/to/libopencv_java451.dylib";
    } else {
        throw new UnsupportedOperationException("Unsupported operating system: " + osName);
    }

    System.load(opencvNativeLibraryPath);
}

    
}
