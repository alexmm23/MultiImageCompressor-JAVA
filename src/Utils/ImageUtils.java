package Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageUtils {
    public static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    public static BufferedImage byteArrayToImage(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }
    public static ArrayList<BufferedImage> openImages(String[] imageNames) throws IOException {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (String imageName : imageNames) {
            images.add(ImageIO.read(new File(imageName)));
        }
        return images;
    }
    public static void saveImage(BufferedImage image,int id,  String outputDir) throws IOException {
        ImageIO.write(image, "png", new File(outputDir + File.separator + "compressed_" + id + ".png"));
    }
}
