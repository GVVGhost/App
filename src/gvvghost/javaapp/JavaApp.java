package gvvghost.javaapp;

import java.io.*;
import java.net.*;

public class JavaApp extends Thread
{
    public JavaApp() {}

    public void setSocket(int num, Socket socket)
    {
        this.num = num;
        this.socket = socket;
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run()
    {
        try
        {
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream dis = new DataInputStream(sin);
            DataOutputStream dos = new DataOutputStream(sout);
            String line = null;
            while(true)
            {
                line = dis.readUTF();
                System.out.println(String.format(TEMPL_MSG, num) + line);
                System.out.println("I'm sending it back...");
                dos.writeUTF("Server recive text : " + line);
                dos.flush();
                System.out.println();
                if (line.equalsIgnoreCase("quit"))
                {
                    socket.close();
                    System.out.println(String.format(TEMPL_CONN, num));
                    break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Exeption: " + e);
        }
    }

    public static void main(String[] args)
    {
        ServerSocket sSocket = null;
        try
        {
            try
            {
                int i = 0;
                InetAddress ia = InetAddress.getByName("localhost");
                System.out.println();
                sSocket = new ServerSocket(port , 0, ia);

                System.out.println("Server started\n\n");

                while(true)
                {
                    Socket socket = sSocket.accept();
                    System.out.println("Client accepted");
                    new JavaApp().setSocket(i++, socket);
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception :"  + e);
            }
        }
        finally
        {
            try
            {
                if(sSocket != null)
                {
                    sSocket.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    private static final int port = 4321;
    private String TEMPL_MSG = "The client '%d' sent me messege : \n\t";
    private String TEMPL_CONN = "The client '%d' close the connection";
    private Socket socket;
    private int    num;
}