package Logic;

import Utils.ImageUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ImageCompressor {
    private final File imageDir;
    private String[] imageNames;
    private final String targetDir;
    public ImageCompressor(String pathToDir, String targetDir){
        imageDir = new File(pathToDir);
        this.targetDir = targetDir;
    }
    public ImageCompressor(String pathToDir){
        imageDir = new File(pathToDir);
        this.targetDir = pathToDir;
    }
    public static long getDirectorySize(Path path){
        long size = 0;
        try (Stream<Path> walk = Files.walk(path)) {
            size = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            System.out.println("Error leyendo el tamaño del archivo");
                            return 0L;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            System.out.println("Error en la operación");
        }
        return size;
    }
    public Map<String,Long> loadImages(){
        Map<String,Long> originalImages = new HashMap<>();
        imageNames = imageDir.list();
        if(imageNames == null || imageNames.length ==0){
            System.out.println("Carpeta vacia");
        }else{
            for(int i = 0; i<imageNames.length-1; i++){
                String currentImageName = imageNames[i];
                File auxiliarFile = new File(currentImageName);
                originalImages.put(currentImageName,auxiliarFile.length());
            }
        }
        return originalImages;
    }
    public String[] getImageNames(){
        String[] names = new String[imageNames.length-1];
        for(int i = 0; i<imageNames.length-1; i++){
            names[i] = imageDir + "/"+imageNames[i];
        }
        return names;
    }
    public byte[][] startCompression(float quality) throws IOException {
        int iterator = 0;
        byte[][] compressedImages = new byte[imageNames.length-1][];
        for(String imageName: imageNames){
            if(!imageName.contains("jpg") && !imageName.contains("jpeg") && !imageName.contains("png")) {
                continue;
            }
            System.out.println("Archivo valido");
            int startingIndex = imageName.indexOf(".");
            if(startingIndex != -1){
                String filePath = imageDir +"/"+ imageName;
                //Leer la imagen
                ImageIcon imageIcon = new ImageIcon(filePath);
                BufferedImage originalImage = ImageIO.read(new File(filePath));
                //Obtener diimensiones
                int width = imageIcon.getIconWidth();
                int height = imageIcon.getIconHeight();
                int newWidth = (int)(width*quality);
                int newHeight = (int)(height*quality);
                //Redimensionar imagen
                BufferedImage resizedImage = new BufferedImage(newWidth,newHeight,originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage,0,0,newWidth,newHeight,null);
                g2d.dispose();
                //Convertir las imagenes redimensionadas a byte[]
                byte[] compressedImage = ImageUtils.imageToByteArray(resizedImage);
                compressedImages[iterator] = compressedImage;
                //Guardar imagen redimensionada
                //File compressedImageFile = new File(this.targetDir+File.separator+iterator+extension);
                //ImageIO.write(resizedImage,extension.substring(1),compressedImageFile);
                iterator++;
                System.out.println("redimensionada con exito!");
            }
        }
        return compressedImages;
    }
    public static byte[] compressImage(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        writer.write(null, new IIOImage(image, null, null), param);

        byte[] compressedImage = byteArrayOutputStream.toByteArray();
        ios.close();
        writer.dispose();

        return compressedImage;
    }
}
