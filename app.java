import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class app {

    private static final String API = "86f5283d-2fe4-4ab7-95fc-a488e53f246b";
    private static final String baseURL = "https://dictionaryapi.com/api/v3/references/thesaurus/json/";

    public static List<String> loadWordBank(String filePath) throws IOException {
        // Initialize new array of string
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

    // Returns true if guess does NOT have any special chars
    public static boolean specialCharCheck(String guess) {
        return guess.matches("^[a-zA-Z]+$");
    }

    public static boolean isWord(String guess) {
        String jsonResponse = "";

        try {
            // Initialize client
            HttpClient client = HttpClient.newHttpClient();
            String url = baseURL + guess + "?key=" + API;
            // Create request to Merriam API
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            // Gets server's response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Checks if the status code is good
            if (response.statusCode() == 200) {
                // Gets the main part of the response
                jsonResponse = response.body();
            }
        } catch (Exception e) {
            System.out.println("API call failed");
        }

        // Returns true if the response starts with [{, since all existing words return
        // jsons with [{ at the start,
        // while words that don't exist return jsons with [" or [] at the start
        return jsonResponse.startsWith("[{");
    }

    public static void SinglePlayerMode(char[] key, Scanner input) {
        List<String> guesses = new ArrayList<>();

        for (int x = 0; x < 7; x++) {
            // Formatting + Returning past guesses
            System.out.println();
            for (int i = 0; i < 6; i++) {
                Wait(500);
                if (i < guesses.size()) {
                    System.out.println(guesses.get(i));
                    System.out.println("----------------------------");
                } else {
                    System.out.println("[ ]   [ ]   [ ]   [ ]   [ ]");
                    System.out.println("----------------------------");
                }
            }

            for (int i = 0; i < 3; i++) {
                System.out.println();
            }

            String guess = ""; // Cuz dumb java initialization logic
            Boolean isValid = false;

            System.out.println("Please enter your guess");

            // Guess Logic
            while (!isValid) {

                // Prompts user for guess
                guess = input.nextLine();
                // Turns the guess into an array of chars
                char[] guess_array = guess.toCharArray();

                // Checks if user guess is less than 5 characters and has no special chars
                // Saves API calls
                if (guess_array.length != 5 || !specialCharCheck(guess)) {
                    System.out.println(
                            "Please enter a valid word that is 5 letters long. Words will special characters are not valid.");
                    continue;
                }

                // API check to see if word exists
                else if (!isWord(guess)) {
                    System.out.println(
                            "Please enter a valid word that is 5 letters long. Words will special characters are not valid.");
                    continue;
                }
                // If all conditions are met, set isValid to true
                isValid = true;
            }

            guesses.add(guess);
            Clear();
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

        // Gets the wordbank, which then gets the random word
        List<String> bank = loadWordBank("wordbank.txt");
        String word = getRandomWord(bank);
        // Converts into an array of chars
        char[] key = word.toCharArray();

        // Fun fluff text
        if (playercount == 1) {
            System.out.println("Entering 1 player mode...");
            Wait(1300);
            Clear();
            SinglePlayerMode(key, input);
        } else {
            System.out.println("Entering 2 player mode...");
            Wait(1300);
            Clear();
        }
    }
}