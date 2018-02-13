package gvvghost.javaapp;
import java.awt.*;
import java.io.*;
import java.net.*;
import org.jfree.ui.RefineryUtilities;

public class Server extends Thread
{
    Server() {}

    void runServer()
    {
        appWindow = new AppWindow();
        appWindow.pack();
        appWindow.setMinimumSize(new Dimension(600, 550));
        RefineryUtilities.centerFrameOnScreen(appWindow);
        appWindow.setVisible(true);
        ServerSocket sSocket = null;

        try
        {
            try
            {
                int i = 0;
                InetAddress ia = InetAddress.getByName(ipA);
                System.out.println();
                sSocket = new ServerSocket(port , 0, ia);
                appWindow.setToTextArea("Server started");
                while(true)
                {
                    Socket socket = sSocket.accept();
                    appWindow.addStringToTextArea("\nClient accepted");
                    new Server().setSocket(i++, socket);
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

    private void setSocket(int num, Socket socket)
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
                appWindow.newIncomingData(line);
                appWindow.addStringToTextArea("The client sent me messege:\n\t"
                        + line + "\nI'm sending it back...");

                dos.writeUTF("Server recive text : " + line);
                dos.flush();

                if (line.equalsIgnoreCase("quit"))
                {
                    socket.close();
                    appWindow.addStringToTextArea(String.format(TEMPL_CONN, num).toString());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Exeption: " + e);
        }
    }


    private static AppWindow appWindow;
    private static final int port = 4321;
    private static final String ipA = "127.0.0.1";
    private String TEMPL_MSG = "The client '%d' sent me messege : \n\t";
    private String TEMPL_CONN = "The client '%d' close the connection\n";
    private Socket socket;
    private int    num;
}
