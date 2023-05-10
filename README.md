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
