package nicrausaz;

import java.io.*;
import java.net.Socket;

public class Worker implements Runnable
{
    private static class Commands
    {
        static final String LIST = "LIST";
        static final String ADD = "ADD";
        static final String SUB = "SUB";
        static final String MULT = "MULT";
        static final String DIV = "DIV";
        static final String POW = "POW";
    }

    private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private boolean working = true;

    Worker(Socket client)
    {
        try
        {
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        out.println("Hey, avaible commands are: LIST, ADD, SUB, MULT, POW, DIV");
        out.flush();

        try
        {
            String received = in.readLine();
            while (working && received != null)
            {
                out.println("> " + received.toUpperCase());
                out.flush();

                String[] tokens = received.split(" ");

                double a = 0, b = 0;

                switch (tokens[0])
                {
                    case Commands.LIST:
                        out.println("Available commands are: LIST, ADD, SUB, MULT, POW, DIV");
                        out.flush();
                        break;

                    case Commands.ADD:
                        if (tokens.length > 1)
                        {
                            a = Double.parseDouble(tokens[1]);
                            b = tokens.length == 3 ? Double.parseDouble(tokens[2]) : 0;
                            out.println("RESULT " + (a + b));
                        } else
                        {
                            out.println("ERROR SYNTAX_ERR");
                        }
                        out.flush();
                        break;
                    case Commands.SUB:
                        if (tokens.length > 1)
                        {
                            a = Integer.parseInt(tokens[1]);
                            b = tokens.length == 3 ? Integer.parseInt(tokens[2]) : 0;
                            out.println("RESULT " + (a - b));
                        } else
                        {
                            out.println("ERROR SYNTAX_ERR");
                        }
                        out.flush();

                        break;
                    case Commands.MULT:
                        if (tokens.length > 1)
                        {
                            a = Integer.parseInt(tokens[1]);
                            b = tokens.length == 3 ? Integer.parseInt(tokens[2]) : 0;
                            out.println("RESULT " + (a * b));
                        } else
                        {
                            out.println("ERROR SYNTAX_ERR");
                        }
                        out.flush();
                        break;
                    case Commands.DIV:
                        if (tokens.length > 1)
                        {
                            a = Integer.parseInt(tokens[1]);
                            b = tokens.length == 3 ? Integer.parseInt(tokens[2]) : 0;
                            if (b != 0)
                            {
                                out.println("RESULT " + (a / b));
                            } else
                            {
                                out.println("ERROR ARM_ERR");
                            }
                        } else
                        {
                            out.println("ERROR SYNTAX_ERR");
                        }

                        out.flush();
                        break;
                    case Commands.POW:
                        if (tokens.length > 1)
                        {
                            a = Integer.parseInt(tokens[1]);
                            b = tokens.length == 3 ? Integer.parseInt(tokens[2]) : 0;
                            out.println("RESULT " + Math.pow(a, b));
                        } else
                        {
                            out.println("ERROR SYNTAX_ERR");
                        }
                        out.flush();
                        break;
                    default:
                        out.println("ERROR CMD_ERR");
                        out.flush();
                }

                if (received.equalsIgnoreCase("bye"))
                {
                    working = false;
                    // break;
                }

                received = in.readLine();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if (!working)
        {
            out.println("Bye !");
            out.flush();

            try
            {
                client.close();
                out.close();
                in.close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
