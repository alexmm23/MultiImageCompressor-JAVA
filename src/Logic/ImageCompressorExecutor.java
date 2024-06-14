package Logic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCompressorExecutor {
    private final File imageDir;
    private String[] imageNames;
    private final float quality;
    private final String outputDir;

    public ImageCompressorExecutor(String pathToDir, float quality, String outputDir) {
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
        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // Usar el número de núcleos disponibles
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (String imageName : imageNames) {
            Runnable task = new CompressionTask(imageName);
            executor.execute(task);
        }
        executor.shutdown();
    }

    private class CompressionTask implements Runnable {
        private final String imageName;

        public CompressionTask(String imageName) {
            this.imageName = imageName;
        }

        @Override
        public void run() {
            compressImage(imageName);
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

                    // Crear una imagen redimensionada con graphics
                    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                    Graphics2D g2d = resizedImage.createGraphics();
                    g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                    g2d.dispose();

                    //Guardar imagen redimensionada con nuevo nombre
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
