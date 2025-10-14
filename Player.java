import java.io.*;
import java.net.*;
import java.util.*;

public class Player {
    // Defining member variables
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;
    private List<String> guesses = new ArrayList<>();
    private boolean isSkipped = false;
    private boolean isLost = false;
    private String word;
    private Backpack backpack = new Backpack();

    public Player(Socket socket, String name, String word) throws IOException {
        // Initializes each variable
        this.socket = socket;
        this.name = name;
        this.word = word;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public void setName(String name) {
        // For changing names
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setWord(String word) {
        // For changing names
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void sendMessage(String msg) {
        // Allows server to send a message to player
        out.println(msg);
    }

    public String readClient() throws IOException {
        // Reads the client's message
        String msg = in.readLine();
        return msg;
    }

    public void addGuess(String guess) {
        guesses.add(guess);
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkip(boolean value) {
        isSkipped = value;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean value) {
        isLost = value;
    }

    public void close() throws IOException {
        // Closes socket
        socket.close();
    }
}
