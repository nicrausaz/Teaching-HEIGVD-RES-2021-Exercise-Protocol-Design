
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client
{
    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean connected;


    public void connect(String server, int port)
    {
        try
        {
            clientSocket = new Socket(server, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connected = true;

        } catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
        }
    }


    public void disconnect()
    {
        if (connected())
        {
            LOG.log(Level.INFO, "Send quit to server");
            out.println(ProtocolCodes.QUIT);
            connected = false;
            cleanup();
        }
    }

    public boolean connected()
    {
        return connected;
    }

    private void cleanup()
    {
        try
        {
            if (in != null)
            {
                in.close();
            }
        } catch (IOException e)
        {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

        if (out != null)
        {
            out.close();
        }

        try
        {
            if (clientSocket != null)
            {
                clientSocket.close();
            }
        } catch (IOException e)
        {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String sendOperation(String message)
    {
        String response = null;
        try
        {
            out.println(message);
            response = in.readLine();

        } catch (Exception e)
        {
            LOG.log(Level.SEVERE, e.getMessage());
        }

        // Gestion de la réponse

        if (response != null)
        {
            String[] tokens = response.split(" ");

            switch (tokens[0])
            {
                case ProtocolCodes.RESULT:
                    response = "Result: " + tokens[1];
                    break;
                case ProtocolCodes.ERROR:
                    response = "Error: " + tokens[1];
                    break;
            }
        }
        return response;
    }

    public static void main(String[] args)
    {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        String input;
        client.connect("localhost", 8085);

        while (client.connected())
        {
            System.out.print("Saisie: ");
            input = scanner.nextLine();

            System.out.println(client.sendOperation(input));
        }

    }
}
