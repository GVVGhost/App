package gvvghost.javaapp;
import java.io.*;
import java.net.*;

public class JavaApp
{
    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;

        try
        {
            serverSocket = new ServerSocket(5555);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        while(true)
        {
            try
            {
                Socket s = serverSocket.accept();
                System.out.println(s.getInetAddress().toString());
                OutputStream outputStream = s.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("It is the server!\n");
                dataOutputStream.close();
                outputStream.close();
            }
            catch (IOException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }
    }
}
