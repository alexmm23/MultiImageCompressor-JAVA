package Server;

import Client.Client;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferDouble;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void register(String name) throws RemoteException;
    String call(String message) throws RemoteException;
    void sendResponse(String clientName, byte[] byteImage) throws RemoteException;
    void receiveImages(byte[][] images) throws RemoteException;
    byte[][] sendImages(float quality, int method) throws RemoteException;

}
