@Service
public class HeicConverter {

    public byte[] heicToJpg(MultipartFile file) throws Exception {
        Path tmp = Files.createTempFile("in", ".heic");
        try {
            file.transferTo(tmp);
            try (IOFileStream fs = new IOFileStream(tmp.toString(), IOMode.READ)) {
                HeicImage heic = HeicImage.load(fs);
                int w = (int) heic.getWidth();
                int h = (int) heic.getHeight();
                int[] pixels = heic.getInt32Array(PixelFormat.Argb32);

                // le JPEG n'a pas de canal alpha, donc on utilise TYPE_INT_RGB
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                img.setRGB(0, 0, w, h, pixels, 0, w);

                var out = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", out);
                return out.toByteArray();
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
