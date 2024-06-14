package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void register(String name) throws RemoteException;
    String call(String message) throws RemoteException;
    void sendResponse(String clientName, byte[] byteImage) throws RemoteException;
    void receiveImages(byte[][] images) throws RemoteException;
    byte[][] getImages(float quality, int method) throws RemoteException;
    int getImagesCount() throws RemoteException;
    double getTotalSize() throws RemoteException;

}
