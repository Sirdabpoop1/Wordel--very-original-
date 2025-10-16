import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final String[] funnyWordList = { "vanquished", "defeated", "defenestrated", "smoked", "dominated",
            "discombobulated", "punted", "oh so very tragically beaten" };

    public static Powerup getPowerup() {
        // Creates a powerup object at random
        String[] names = { "The KEYS to the Cheese Factory", "The Hash-Slinging Slasher", "10L Coffee Cup",
                "Standing Up School Application", "YoHoHo's Shiny Right Hook" };
        String[] powerupDesc = { "Gives you one letter",
                "Makes your opponent's next guess give random information",
                "You get 2 turns for the price of 1",
                "Changes your opponent's word",
                "See your opponent's last used guess"};
        Random rand = new Random();
        int i = rand.nextInt(names.length);
        return new Powerup(names[i], powerupDesc[i]);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Waiting for 2 challengers...");

        // Initializes server socket
        ServerSocket serverSocket = new ServerSocket(PORT);
        // Loads wordbank
        List<String> bank = App.loadWordBank("wordbank.txt");
        //Loads guessbank
        List<String> guessBank = App.loadGuessBank("guessbank.txt");

        // Checks for when 2 players connect to the socket
        Socket p1Socket = serverSocket.accept();
        System.out.println("Player 1 has joined!");
        Socket p2Socket = serverSocket.accept();
        System.out.println("Player 2 has joined!");
        System.out.println("GAME ON!");

        String word = App.getRandomWord(bank);
        word = word.toUpperCase();

        Player p1 = new Player(p1Socket, "Player 1", word);
        Player p2 = new Player(p2Socket, "Player 2", word);

        // Setting names
        p1.sendMessage("Select your name: ");
        p2.sendMessage("Select your name: ");
        String p1Name = p1.readClient();
        String p2Name = p2.readClient();
        p1.setName(p1Name);
        p2.setName(p2Name);

        boolean p1Turn = true;
        boolean gameOver = false;
        int numturn = 0;

        while (!gameOver) {
            // Sets a p1/p2 to current/other depending on whose turn it is
            Player current;
            Player other;
            if (p1Turn) {
                current = p1;
                other = p2;
                numturn++;
            } else {
                current = p2;
                other = p1;
            }

            // Skip logic
            if (current.isSkipped()) {
                current.sendMessage("Your turn has been skipped!");
                App.Wait(1500);
                current.setSkip(false);
                // Sets other player as current player, essentially skipping the current
                // player's turn
                p1Turn = !p1Turn;
                continue;
            }

            // Send messages to commence le game
            current.sendMessage("CLEAR");
            other.sendMessage("CLEAR");
            current.sendMessage("You're up! It is turn " + numturn + "\n");
            other.sendMessage("Waiting for " + current.getName() + " to play...");
            // Checks if current has powerups
            if (!current.getBackpack().returnPowerups().isEmpty()) {
                // Prompts to see if they would like to use
                current.sendMessage("Would you like to use a powerup? Yes/No");
                String isUsing = current.readClient();
                if (isUsing.equalsIgnoreCase("YES")) {
                    // Prints current backpack full of powerups
                    current.getBackpack().sendPowerups(current);
                    // Prompts for a powerup and applies effects in Powerup.java class
                    current.sendMessage("Pick a powerup to use! Enter it its row number: ");
                    String choice = current.readClient().trim();
                    while (Integer.parseInt(choice) > current.getBackpack().returnPowerups().size() || Integer.parseInt(choice) < 1) {
                        current.sendMessage("Please enter a valid powerup: ");
                        choice = current.readClient().trim();
                    }
                    Powerup selected = current.getBackpack().returnPowerups().get(Integer.parseInt(choice)-1);
                    String name = selected.getName();
                    current.getBackpack().usePowerup(name, current, other, guessBank, word.toCharArray());
                    current.sendMessage("\n");
                }
            }

            for (int i = 0; i < current.getFormattedGuesses().size(); i++) {
                current.sendMessage(current.getFormattedGuesses().get(i));
                current.sendMessage("---------------------------------");
                App.Wait(1000);
            }
            current.sendMessage("\n");

            current.sendMessage("Please enter your guess");

            boolean isValid = false;
            String guess = "";
            // Guess check logic
            while (!isValid) {
                guess = current.readClient();
                if (!App.isWord(guess, guessBank)) {
                    current.sendMessage(
                            "Please enter a valid guess of a 5 letter word that exists in the English dictionary");
                } else {
                    isValid = true;
                }
            }

            guess = guess.toUpperCase();
            current.sendMessage("CLEAR");
            boolean GOTEMS = false;
            String[] results = new String[4];
            // Check guess after accounting for confusion
            if (current.isLost()) {
                Random rand = new Random();
                int randIndex = rand.nextInt(bank.size());
                String randGuess = bank.get(randIndex);
                results = App.checkGuess(randGuess.toUpperCase(), current.getWord().toCharArray(), GOTEMS);
                current.addGuess(randGuess);
            } else {
                results = App.checkGuess(guess, current.getWord().toCharArray(), GOTEMS);
                current.addGuess(guess);
            }

            GOTEMS = Boolean.parseBoolean(results[3]);
            
            current.addFormattedGuesses(results[0]);

            if (current.isLost()){
                current.sendMessage("\n");
                current.sendMessage("YOU HAVE BEEN CONFUZZLED!");
                current.sendMessage("\n");
                current.setLost(false);
            }

            // Return past guesses + current guess
            for (int i = 0; i < current.getFormattedGuesses().size(); i++) {
                current.sendMessage(current.getFormattedGuesses().get(i));
                current.sendMessage("---------------------------------");
                App.Wait(1000);
            }
            if (GOTEMS) {
                // Send victory and defeat messages
                current.sendMessage("\n");
                current.sendMessage("CONGRATS! YOU HAVE WON BY GUESSING THE WORD! The word was " + current.getWord());
                Random rand = new Random();
                int funnyIndex = rand.nextInt(funnyWordList.length);
                String funny = funnyWordList[funnyIndex];
                other.sendMessage("\n");
                other.sendMessage("You have been " + funny + " by the likes of " + current.getName()
                        + "! Better luck next time :(");
                other.sendMessage("The word was " + current.getWord());

                gameOver = true;
            } else {
                // Send info
                Powerup p = getPowerup();
                current.sendMessage("\n");
                current.getBackpack().addPowerup(p);
                current.sendMessage("You recieved a wild " + p.getName());
                other.sendMessage("\n");
                other.sendMessage(current.getName() + " guessed! They have " + results[1]
                        + " correct letters in the right place and " + results[2]
                        + " correct letters in the wrong place");
                App.Wait(4000);
                p1Turn = !p1Turn;
            }
        }
        p1.close();
        p2.close();
        serverSocket.close();
        System.out.println("Game over");
    }
}