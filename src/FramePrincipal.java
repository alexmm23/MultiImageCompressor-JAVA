import Logic.ImageCompressor;
import Server.ServerInterface;
import Utils.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;

public class FramePrincipal extends JFrame implements ActionListener {
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton sendButton;
    private JTextField textField;
    private JLabel lblCompressionQuality;
    private JLabel label;
    private JLabel label2;
    private JLabel label3;
    private JLabel lblResultadoFork;
    private JLabel lblResultadoExecutor;
    private JRadioButton radioSecuencial;
    private JRadioButton radioExecutor;
    private JRadioButton radioForkJoin;
    private ButtonGroup group;
    private JTextArea txtAResults;
    private JTextArea txtAFirstArray;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane2;
    private String originFolderSelected;
    private String outputFolderSelected;
    private JLabel originFolderLabel;
    private JLabel outputFolderLabel;
    private final ServerInterface server;
    private Registry registry;
    private String clientName;
    public FramePrincipal() throws RemoteException, NotBoundException {
        clientName = JOptionPane.showInputDialog("Ingresa tu nombre");
        registry = LocateRegistry.getRegistry("localhost", 3000);
        server = (ServerInterface) registry.lookup("Server");
        server.register(clientName);
        txtAFirstArray = new JTextArea(200,200);
        txtAFirstArray.setLineWrap(true);
        txtAResults = new JTextArea(200,200);
        originFolderLabel = new JLabel("Directorio de imagenes:");
        originFolderLabel.setBounds(50, 450, 300, 30);
        outputFolderLabel = new JLabel("Directorio de salida:");
        outputFolderLabel.setBounds(400, 450, 300, 30);
        button3 = new JButton("Cargar Imagenes");
        button3.setBounds(50, 400, 300, 30);
        button4 = new JButton("Seleccionar directorio de salida");
        button4.setBounds(400, 400, 300, 30);
        sendButton = new JButton("Enviar al servidor");
        sendButton.setBounds(50, 600, 300, 30);
        originFolderSelected = "";
        outputFolderSelected = "";
        txtAResults.setLineWrap(true);
        scrollPane = new JScrollPane(txtAFirstArray);
        scrollPane.setBounds(50, 30, 300, 200);
        scrollPane2 = new JScrollPane(txtAResults);
        scrollPane2.setBounds(400, 30, 300, 200);
        radioSecuencial = new JRadioButton("Secuencial");
        radioSecuencial.setBounds(400, 250, 100, 30);
        radioExecutor = new JRadioButton("Executor");
        radioExecutor.setBounds(400, 310, 100, 30);
        radioForkJoin = new JRadioButton("ForkJoin");
        radioForkJoin.setBounds(400, 370, 100, 30);
        group = new ButtonGroup();
        group.add(radioSecuencial);
        group.add(radioExecutor);
        group.add(radioForkJoin);
        button1 = new JButton("Comprimir");
        button1.setBounds(50, 350, 145, 30);
        button2 = new JButton("Borrar");
        button2.setBounds(200, 350, 145, 30);
        label = new JLabel("Resultado:");
        label.setBounds(50, 500, 200, 30);
        lblResultadoFork = new JLabel("Resultado ForkJoin:");
        lblResultadoFork.setBounds(400, 500, 200, 30);
        lblResultadoExecutor = new JLabel("Resultado Executor:");
        lblResultadoExecutor.setBounds(400, 550, 200, 30);
        label2 = new JLabel("Imagenes encontradas");
        label2.setBounds(50, 0, 200, 30);
        label3 = new JLabel("Carpeta comprimida");
        label3.setBounds(400, 0, 200, 30);
        textField = new JTextField("");
        textField.setBounds(50, 280, 300, 30);
        lblCompressionQuality = new JLabel("Calidad de compresion");
        lblCompressionQuality.setBounds(50, 250, 200, 30);
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(sendButton);
        add(textField);
        add(label);
        add(label2);
        add(label3);
        add(originFolderLabel);
        add(outputFolderLabel);
        add(lblCompressionQuality);
        add(lblResultadoFork);
        add(lblResultadoExecutor);
        add(scrollPane);
        add(scrollPane2);
        add(radioSecuencial);
        add(radioExecutor);
        add(radioForkJoin);
        setTitle("Merge Sort");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);
        sendButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == button1) {
            final long startTime = System.currentTimeMillis();
            //Obtener la calidad de compresion de las imagenes desde el jtextfield
            if(textField.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Por favor ingresa la calidad de compresion", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println(textField.getText());
            if(!textField.getText().matches("[+-]?([0-9]*[.])?[0-9]+")){
                JOptionPane.showMessageDialog(null, "Por favor ingresa un numero valido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            float compressionQuality = Float.parseFloat(textField.getText().toString());
            if(originFolderSelected.isEmpty() || outputFolderSelected.isEmpty()){
                JOptionPane.showMessageDialog(null, "Por favor selecciona un directorio de imagenes", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int imageCount = 0;
            double totalSize = 0;
            try{
                imageCount = server.getImagesCount();
                totalSize = server.getTotalSize()/1000000.0;
            }catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            String firstMapString = "Cantidad de imagenes encontradas: "+imageCount + "\n";
            firstMapString += "\n" + totalSize+ " Megabytes";
            txtAFirstArray.setText(firstMapString);
            var option = group.getSelection();
            if (option == radioSecuencial.getModel()) {
                System.out.println("Secuencial");
                writeImages(compressionQuality,1);
                long endTime = System.currentTimeMillis();
                long result = endTime - startTime;
                label.setText("Secuencial: " + result + "ms");
            } else if (option == radioExecutor.getModel()) {
                System.out.println("Executor");
                long endTime = System.currentTimeMillis();
                writeImages(compressionQuality,3);
                long result = endTime - startTime;
                lblResultadoExecutor.setText("Executor: " + result + "ms");
            } else if (option == radioForkJoin.getModel()) {
                System.out.println("Fork Join");
                writeImages(compressionQuality,2);
                long endTime = System.currentTimeMillis();
                long result =  (endTime - startTime);
                lblResultadoFork.setText("Fork Join: " + result + "ms");
            } else {
                JOptionPane.showMessageDialog(null, "Por favor selecciona un tipo de compresion", "Error", JOptionPane.ERROR_MESSAGE);
            }
            double finalSize = ImageCompressor.getDirectorySize(Path.of(outputFolderSelected))/1000000.0;
            String result = "Tamaño carpeta salida: " + finalSize + " Megabytes";
            result += "\n" + "Porcentaje de compresion: " + compressionQuality*100 + "%";
            result += "\n" + "Imagenes comprimidas: " + imageCount;
            txtAResults.setText(result);
        }
        if (e.getSource() == button2) {
            originFolderSelected = "";
            outputFolderSelected = "";
            originFolderLabel.setText("Directorio de entrada de imagenes:");
            outputFolderLabel.setText("Directorio de salida:");
            txtAResults.setText("");
            txtAFirstArray.setText("");
            textField.setText("");
        }
        if(e.getSource() == button3){
            originFolderSelected= showFileSelector("Selecciona el directorio de imagenes");
            originFolderLabel.setText("Directorio de entrada de imagenes: "+originFolderSelected);
            System.out.println("Path selected: "+  originFolderSelected);
        }
        if(e.getSource() == button4){
            outputFolderSelected = showFileSelector("Selecciona el directorio de salida");
            outputFolderLabel.setText("Directorio de salida: "+outputFolderSelected);
            System.out.println("Path selected: "+  outputFolderSelected);
        }
        if(e.getSource() == sendButton){
            try {
                ImageCompressor i = new ImageCompressor(originFolderSelected);
                i.loadImages();
                String[] imageNames = i.getImageNames();
                System.out.println(Arrays.toString(imageNames));
                ArrayList<BufferedImage> imagesToOpen = ImageUtils.openImages(imageNames);
                byte[][] imagesToSend = new byte[imagesToOpen.size()][];
                for(BufferedImage image : imagesToOpen){
                    byte[] byteImage = ImageUtils.imageToByteArray(image);
                    imagesToSend[imagesToOpen.indexOf(image)] = byteImage;
                }
                server.receiveImages(imagesToSend);
                System.out.println(imagesToSend.length);
                System.out.println("Imagenes enviadas al servidor.");
                JOptionPane.showMessageDialog(null, "Imagenes enviadas al servidor", "Exito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void writeImages(float compressionQuality, int method) {
        byte[][] responseImages;
        int i = 0;
        try{
            responseImages = server.getImages(compressionQuality, method);
            for (byte[] image : responseImages) {
                BufferedImage bufferedImage = ImageUtils.byteArrayToImage(image);
                ImageUtils.saveImage(bufferedImage,i, outputFolderSelected);
                i++;
            }
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public String showFileSelector(String title){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(null);
        fileChooser.setDialogTitle(title);
        fileChooser.setApproveButtonText("Seleccionar");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileHidingEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imagenes", "jpg", "jpeg", "png"));
        //Obtener el directorio seleccionado
        return fileChooser.getSelectedFile() != null ? fileChooser.getSelectedFile().getAbsolutePath() : "";
    }
    public static void main(String[] args) throws IOException {
        try {
            FramePrincipal frame = new FramePrincipal();
            frame.setVisible(true);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}

