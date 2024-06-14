package Logic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
public class ImageCompressionTask {
    private final File imageDir;
    private String[] imageNames;
    private final float quality;
    private final String outputDir;

    public String[] getImageNames() {
        return imageNames;
    }

    public ImageCompressionTask(String pathToDir, float quality, String outputDir) {
        this.imageDir = new File(pathToDir);
        this.quality = quality;
        this.outputDir = outputDir;
    }

    public void startCompression() {
        imageNames = imageDir.list();
        if (imageNames == null || imageNames.length == 0) {
            System.out.println("No hay imágenes para comprimir.");
            return;
        }

        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new CompressionTask(imageNames, 0, imageNames.length - 1));
        pool.shutdown();
    }

    private class CompressionTask extends RecursiveAction {
        private final String[] imageNames;
        private final int start;
        private final int end;

        private CompressionTask(String[] imageNames, int start, int end) {
            this.imageNames = imageNames;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start < 5) { // Threshold for sequential processing
                for (int i = start; i <= end; i++) {
                    compressImage(imageNames[i]);
                }
            } else {
                int middle = (start + end) / 2;
                CompressionTask leftTask = new CompressionTask(imageNames, start, middle);
                CompressionTask rightTask = new CompressionTask(imageNames, middle + 1, end);
                invokeAll(leftTask, rightTask);
            }
        }

        private void compressImage(String imageName) {
            try {
                // Obtener la extensión del archivo
                int startingIndex = imageName.lastIndexOf(".");
                if (startingIndex != -1) {
                    String extension = imageName.substring(startingIndex);
                    String slicedName = imageName.substring(0, startingIndex);

                    // Leer la imagen original
                    File originalImageFile = new File(imageDir + File.separator + imageName);
                    BufferedImage originalImage = ImageIO.read(originalImageFile);

                    // Calcular las nuevas dimensiones
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    int newWidth = (int) (width * quality);
                    int newHeight = (int) (height * quality);

                    // Crear una imagen redimensionada
                    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                    Graphics2D g2d = resizedImage.createGraphics();
                    g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                    g2d.dispose();

                    // Guardar la imagen redimensionada
                    File compressedImageFile = new File(outputDir + File.separator + slicedName + extension);
                    ImageIO.write(resizedImage, "png", compressedImageFile);

                    System.out.println("Imagen comprimida: " + compressedImageFile.getAbsolutePath());
                }
            } catch (IOException e) {
                System.out.println("Error al comprimir la imagen: " + e.getMessage());
            }
        }
    }

}
