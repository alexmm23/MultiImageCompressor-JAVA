package Client;

import java.awt.image.BufferedImage;
public interface Client {
    void receiveMessage(String message);
    void receiveImage(BufferedImage image);
    void receiveCall(String message);
    void receiveResponse(String message);

}