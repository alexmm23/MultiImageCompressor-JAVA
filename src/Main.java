import Server.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class Main {
    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            ServerInterface stub = (ServerInterface) registry.lookup("Server");
            String response = stub.call("Hello, world!");
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client.Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}