import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class BarcodeReader {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        VideoCapture capture = new VideoCapture(0);
        Mat frame = new Mat();

        if (capture.isOpened()) {
            while (true) {
                capture.read(frame);
                if (!frame.empty()) {
                    // Process the frame here
                    break;
                }
            }
        }

        capture.release();
    }
}
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public static void main(String[] args) {
    // ... (capture the frame)

    MatOfByte matOfByte = new MatOfByte();
    Imgcodecs.imencode(".jpg", frame, matOfByte);
    byte[] byteArray = matOfByte.toArray();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
    BufferedImage bufferedImage = ImageIO.read(inputStream);

    LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    Result result;

    try {
        Reader reader = new MultiFormatReader();
        result = reader.decode(bitmap);
        System.out.println("Barcode Text: " + result.getText());
    } catch (NotFoundException e) {
        System.out

    println("Barcode not found");
} catch (ChecksumException e) {
    System.out.println("Checksum error");
} catch (FormatException e) {
    System.out.println("Format error");
}

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;

import java.io.File;
import java.nio.file.Path;

public static void generateBarcode(String text, String outputPath) {
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

public static void main(String[] args) {
    // ... (capture and read the barcode)

    // Generate a barcode from a text
    String textToEncode = "Sample Text";
    String outputPath = "output.png";
    generateBarcode(textToEncode, outputPath);
}




