import java.io.*;
import java.net.*;
import java.util.*;

public class App {

    public static List<String> loadWordBank(String filePath) throws IOException {
        // Define new array of string
        List<String> words = new ArrayList<>();

        // Attempt reading the file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Adds words from the file into the list until there are no more words
            String word;

            while ((word = br.readLine()) != null) {
                words.add(word);
            }
        }

        return words;
    }

    public static String getRandomWord(List<String> words) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("no words");
        } else {
            Random rand = new Random();
            int index = rand.nextInt(words.size());
            return words.get(index);
        }
    }

    // To clear console
    public static void Clear() {
        // Clears the console (duh)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Waiter to wait for the sake of waiting
    public static void Wait(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWord(String guess, List<String> words) {

        if (words.contains(guess.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    public static String[] checkGuess(String guess, char[] key, boolean GOTEMS) {
        // Initialize StringBuilder
        StringBuilder answer = new StringBuilder();
        List<Character> guessbuild = new ArrayList<>();

        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String RESET = "\u001B[0m";

        int greencount = 0;
        int yellowcount = 0;

        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);

            // Checks the frequency of the guessed letter in key
            int count = 0;

            for (int k = 0; k < key.length; k++) {
                if (key[k] == c) {
                    count++;
                }
            }

            // Checks if char is in the right letter and in the right position
            if (c == key[i]) {
                answer.append(GREEN).append(c).append(RESET);
                greencount++;
                // Checks if char is in the answer. Checking if frequency of the letter in
                // guessbuild is less than count ensures no repeated yellows
            } else if (new String(key).indexOf(c) != -1 && Collections.frequency(guessbuild, c) < count) {
                answer.append(YELLOW).append(c).append(RESET);
                yellowcount++;
            } else {
                answer.append(RESET).append(c);
            }

            answer.append("   ");
            // Appends the newest character to guessbuild
            guessbuild.add(c);
        }
        // If all 5 letters are green, then the word is solved and GOTEMS is set to
        // true!
        if (greencount == guess.length()) {
            GOTEMS = true;
        }
        return new String[] { answer.toString(), String.valueOf(greencount), String.valueOf(yellowcount),
                String.valueOf(GOTEMS) };
    }

    public static void baseGame(Scanner input) throws IOException {

        boolean GOTEMS = false;

        // Gets the wordbank, which then gets the random word
        List<String> bank = loadWordBank("wordbank.txt");
        String word = getRandomWord(bank);
        word = word.toUpperCase();
        // Converts into an array of chars
        char[] key = word.toCharArray();

        List<String> guesses = new ArrayList<>();

        for (int x = 0; x < 7; x++) {
            // Formatting + Returning past guesses
            System.out.println();
            for (int i = 0; i < 6; i++) {
                Wait(500);
                if (i < guesses.size()) {
                    System.out.println(guesses.get(i));
                } else {
                    System.out.println("[ ]   [ ]   [ ]   [ ]   [ ]");
                }
                System.out.println("----------------------------");
            }

            for (int i = 0; i < 2; i++) {
                System.out.println();
            }

            if (GOTEMS) {
                System.out.println("YOU WON!!! The word was " + new String(key));
                Wait(2000);
                return;
            } else if (!GOTEMS && x == 6) {
                System.out.println("OOOOOOOOOH YOU LOOOOOSSEEEE (noob)!!! The word was " + new String(key));
                return;
            }

            String guess = ""; // Cuz dumb java initialization logic
            Boolean isValid = false;

            System.out.println("Please enter your guess");

            // Guess Logic
            while (!isValid) {

                // Prompts user for guess
                guess = input.nextLine();
                if (!isWord(guess, bank)) {
                    System.out.println(
                            "Please enter a valid word that is 5 letters long. Words will special characters are not valid.");
                } else {
                    isValid = true;
                }
            }

            guess = guess.toUpperCase();
            String[] fancyGuess = checkGuess(guess, key, GOTEMS);

            guesses.add(fancyGuess[0]);
            GOTEMS = Boolean.parseBoolean(fancyGuess[3]);
            Clear();
        }

    }

    public static void multiplayerTime(Scanner input) {
        System.out.println("Searching for an opponent...");
        try {
            // Creates TCP socket
            Socket socket = new Socket("10.2.225.253", 12345);
            // Creates reader in order to recieve messages from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Creates a writer in order to send messages to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Creates a new thread running in paralell with the rest of the program.
            Thread listener = new Thread(new Runnable() {
                @Override
                // Continually executes public void run
                public void run() {
                    try {
                        String line;
                        // Reads the messages from the server and prints it in client terminal
                        while ((line = in.readLine()) != null) {
                            // If server sends the message CLEAR
                            if (line.equalsIgnoreCase("CLEAR")) {
                                Clear();
                            } else {
                                System.out.println(line);
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server");
                    }
                }
            });
            // Runs the thread
            listener.start();
            // Continuously sends input that the user types (after clicking enter) to the
            // server
            while (true) {
                String inputLine = input.nextLine();
                out.println(inputLine);
            }

        } catch (IOException e) {
            System.out.println("Unable to find an opponent :(" + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println(
                "Welcome to Wordel, the very original word guessing app that is totally, most definitely, not a copy of Wordle!");
        // Create input object to read console
        Scanner input = new Scanner(System.in);
        // Prompt for player count
        System.out.println("How many players would you like to play with? 1 or 2?");
        // Checks to ensure user/users enter 1 or 2
        int playercount = 0;
        while (playercount != 1 && playercount != 2) {
            try {
                playercount = input.nextInt();
                // Consumes unread newline
                input.nextLine();
                if (playercount != 1 && playercount != 2) {
                    System.out.println("Please enter either 1 or 2 :(");
                }
                // This handles the any non-int input without killing the code
            } catch (InputMismatchException ex) {
                System.out.println("Invalid input. Please enter either 1 or 2 :(");
                // Clears newline
                input.nextLine();
            }
        }

        // Fun fluff text
        if (playercount == 1) {
            System.out.println("Entering 1 player mode...");
            Wait(1300);
            Clear();
            baseGame(input);
        } else {
            System.out.println("Entering 2 player mode...");
            Wait(1300);
            multiplayerTime(input);
            Clear();
        }
    }
}
