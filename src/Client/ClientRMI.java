package Client;

import Server.ServerInterface;
import Utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {
    public static void main(String[] args) {
        try {
            // Conectar al registro RMI y obtener la referencia del servidor
            Registry registry = LocateRegistry.getRegistry("localhost", 3000);
            ServerInterface servidor = (ServerInterface) registry.lookup("Server");
            // Cargar una imagen desde el disco
            File file = new File("src/resources/Sockets.png");
            System.out.println(file.getAbsolutePath());
            BufferedImage imagen = ImageIO.read(file);
            byte[] byteImage = ImageUtils.imageToByteArray(imagen);
            // Enviar la imagen al servidor
            String clientName = "Cliente2";
            servidor.register(clientName);
            servidor.sendResponse(clientName,byteImage);
            System.out.println("Imagen enviada al servidor.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
