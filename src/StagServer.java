import gameParser.entity.Player;

import java.io.*;
import java.net.*;

class StagServer
{
    private StagController stagController;

    public static void main(String args[])
    {
        if(args.length != 2) System.out.println("Usage: java StagServer <gameParser.entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber)
    {
        try {
            StagGameModel gameModel = new StagGameModel(entityFilename, actionFilename);
            this.stagController = new StagController(gameModel);
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss)
    {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException
    {
        String line = in.readLine();
        String[] tokens = line.split(" ");
        String playerName = tokens[0].split(":")[0];// remove :
        // Player curPlayer = getPlayer.(playerName);

        if (tokens.length <= 1) {
            out.write("No command entered, you have to say something to play the game.");
            return;
        }
        out.write(stagController.handleTokens(tokens));
        System.out.println("You said: " + line);
    }
}
