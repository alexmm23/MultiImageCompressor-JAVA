package Client;

import Client.Client;

import java.awt.image.BufferedImage;

public class ClientImplementation implements Client {
    private String name;
    public ClientImplementation(String name){
        this.name = name;
    }
    @Override
    public void receiveMessage(String message) {
        System.out.println("Message received: " + message);
    }

    @Override
    public void receiveImage(BufferedImage image) {
        System.out.println("Image received");
    }

    @Override
    public void receiveCall(String message) {
        System.out.println("Call received: " + message);
    }

    @Override
    public void receiveResponse(String message) {
        System.out.println("Response received: " + message);
    }
}
