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
        int index = line.indexOf(":");
        // get player's name, it will before the first ":"
        String playerName = line.substring(0, index);
        // remove ":" and " ";
        String[] tokens = line.substring(index+1).trim().split(" ");

        if (tokens.length < 1) {
            out.write("No command entered, you have to say something to play the game.");
            return;
        }
        out.write(stagController.handleTokens(playerName, tokens));
    }
}
