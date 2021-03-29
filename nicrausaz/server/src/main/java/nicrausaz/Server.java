import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server
{
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    private final int PORT = 8085;
    private ServerSocket srvSocket;

    public void serveClients()
    {
        LOG.info("Starting the Receptionist Worker on a new thread...");
        new Thread(new MainWorker()).start();
    }

    private class MainWorker implements Runnable
    {

        @Override
        public void run()
        {
            try
            {
                srvSocket = new ServerSocket(PORT);
            } catch (IOException e)
            {
                LOG.log(Level.SEVERE, null, e);
                return;
            }

            while (true)
            {
                LOG.log(Level.INFO, "Waiting for a new client");

                try
                {
                    Socket clientSocket = srvSocket.accept();
                    LOG.info("New client");
                    new Thread(new Worker(clientSocket)).start();
                } catch (IOException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }

        private class Worker implements Runnable
        {
            Socket clientSocket;
            BufferedReader in = null;
            PrintWriter out = null;

            public Worker(Socket clientSocket)
            {
                try
                {
                    this.clientSocket = clientSocket;
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                } catch (IOException ex)
                {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void run()
            {
                String line;
                boolean shouldRun = true;

                try
                {
                    LOG.info("READING REQUEST");
                    while ((shouldRun) && (line = in.readLine()) != null)
                    {
                        String[] tokens = line.split(" ");

                        if (tokens.length == 0 || tokens.length > 3)
                        {
                            out.println(ProtocolCodes.ERROR + " " + ProtocolCodes.SYNTAX_ERR);
                        } else
                        {
                            double left, right;
                            if (tokens.length != 3)
                            {
                                left = 0;
                                right = 0;
                            } else
                            {
                                left = Double.parseDouble(tokens[1]);
                                right = Double.parseDouble(tokens[2]);
                            }

                            switch (tokens[0])
                            {
                                case ProtocolCodes.QUIT:
                                    shouldRun = false;
                                    break;
                                case ProtocolCodes.ADD:
                                    out.println(ProtocolCodes.RESULT + " " + (left + right));
                                    break;
                                case ProtocolCodes.SUB:
                                    out.println(ProtocolCodes.RESULT + " " + (left - right));
                                    break;
                                case ProtocolCodes.MULT:
                                    out.println(ProtocolCodes.RESULT + " " + (left * right));
                                    break;

                                case ProtocolCodes.DIV:
                                    if (right != 0)
                                    {
                                        out.println(ProtocolCodes.RESULT + " " + (left / right));
                                        break;
                                    }
                                    out.println(ProtocolCodes.RESULT + " " + 0);
                                    break;

                                case ProtocolCodes.POW:
                                    out.println(ProtocolCodes.RESULT + " " + (Math.pow(left, right)));
                                    break;
                                default:
                                    out.println(ProtocolCodes.ERROR + " " + ProtocolCodes.CMD_ERR);
                                    break;

                            }
                        }
                    }

                    LOG.info("Cleaning");
                    clientSocket.close();
                    in.close();
                    out.close();

                } catch (IOException ex)
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        } catch (IOException ex1)
                        {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    if (out != null)
                    {
                        out.close();
                    }
                    if (clientSocket != null)
                    {
                        try
                        {
                            clientSocket.close();
                        } catch (IOException ex1)
                        {
                            LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                        }
                    }
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }


    public static void main(String[] args)
    {
        Server server = new Server();
        server.serveClients();
    }
}

