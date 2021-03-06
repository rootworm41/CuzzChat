package cuzzchat;

import java.awt.Component;
import java.awt.Container;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author gabri
 */
public class Client
{

    final static int SERVER_PORT = 1234;
    static DataInputStream inputStream;
    static DataOutputStream outputStream;
    File home = FileSystemView.getFileSystemView().getHomeDirectory();
    String desktop = home.getAbsolutePath();
    String username;
    static Socket socket;

    public Client(String args[]) throws IOException
    {
        Client.main(args);
    }

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        // GET LOCALHOST IP 
        InetAddress IP = InetAddress.getByName("localhost");

        // ESTABLISH CONNECTION 
        socket = new Socket(IP, SERVER_PORT);

        // OBTAIN INPUT AND OUTPUT STREAMS
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void sendMessage(String message)
    {
        try
        {
            // WRITE TO OUTPUT STREAM
            outputStream.writeUTF(message);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }    
    
    public String readMessage()
    {
        try
        {
            // R-EAD THE RECIEVED MESSAGE
            return inputStream.readUTF();

        }
        catch (IOException e)
        {
            System.out.println(e);
        }

        return "null";
    }
    
    public void sendFile(File file) throws IOException
    {
        
        //Specify the file
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fileInputStream); 
          
        //Get socket's output stream
        OutputStream outStream = (OutputStream) outputStream;
                
        //Read File Contents into contents array 
        byte[] contents;
        long fileLength = file.length();         
        long current = 0;
         
        long start = System.nanoTime();
        while(current!=fileLength){ 
            int size = 10000;
            if(fileLength - current >= size)
                current += size;    
            else{ 
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            contents = new byte[size]; 
            bis.read(contents, 0, size); 
            outStream.write(contents);
            System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!\n");
        }   
        
        outStream.flush(); 
        outputStream =  new DataOutputStream(socket.getOutputStream());
        //File transfer done. Close the socket connection!
       
        System.out.println("File sent succesfully!");
    }

    public void receiveFile(String filename, long fileSize) throws UnknownHostException, IOException
    {
        byte[] contents = new byte[10000];
        
        int bytesRead = 0;
        long size = fileSize;
        
        //Initialize the FileOutputStream to the output file's full path.  
        File writtenFile = new File(desktop + "\\"  + filename);
        FileOutputStream fos = new FileOutputStream(writtenFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);   
        
        while (size > 0 && (bytesRead = inputStream.read(contents, 0, (int) Math.min(contents.length, size))) != -1)
        {
            
            bos.write(contents, 0, bytesRead);
            size -= bytesRead;
            
        }       

        bos.flush();
        
        System.out.println("File saved successfully!");
    }

}
