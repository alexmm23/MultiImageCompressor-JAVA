package Server;

import Client.Client;
import Logic.ImageCompressor;
import Utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import Client.ClientImplementation;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {
    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

    public ServerImplementation() throws RemoteException {
        super();
    }
    @Override
    public void register(String name) throws RemoteException {
        clients.add(new ClientImplementation(name));
        System.out.println("Client registered with name: " + name);
    }

    @Override
    public String call(String message) throws RemoteException {
        return message;
    }
    @Override
    public int getImagesCount() throws RemoteException {
        return images.size();
    }

    @Override
    public void sendResponse(String clientName, byte[] byteImage) throws RemoteException {
        try {
            BufferedImage image = ImageUtils.byteArrayToImage(byteImage);
            File outputfile = new File("src/resources/" + clientName + ".png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Imagen recibida y guardada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public double getTotalSize() throws RemoteException {
        double totalSize = 0;
        for(BufferedImage image : images){
            totalSize += image.getData().getDataBuffer().getSize();
        }
        return totalSize;
    }

    @Override
    public void receiveImages(byte[][] images) throws RemoteException {
        for(byte[] image : images){
            try {
                BufferedImage bufferedImage = ImageUtils.byteArrayToImage(image);
                ServerImplementation.images.add(bufferedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Images received");
        System.out.println("Number of images: " + ServerImplementation.images.size());
    }

    @Override
    public byte[][] getImages(float quality, int method) throws RemoteException {
        byte[][] responseImages = new byte[images.size()][];
        int i= 0;
        switch (method){
            case 1:
                //Secuencial
                for(BufferedImage image : images){
                    try {
                        byte[] compressedImage = ImageCompressor.compressImage(image, quality);
                        responseImages[i] = compressedImage;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                System.out.println("Secuencial");
            case 2:
                // Fork Join
                System.out.println("Fork Join");
                break;
            case 3:
                // Executor service
                System.out.println("Executor service");
                break;
            default:
                System.out.println("Invalid method");
        }
        return responseImages;
    }

    public static void main(String[] args){
        try{
            java.rmi.registry.LocateRegistry.createRegistry(3000).rebind("Server", new ServerImplementation());
            System.out.println("Server.Server ready");
        } catch (Exception e) {
            System.err.println("Server.Server exception: " + e.toString());
            e.printStackTrace();
        }

    }
}
