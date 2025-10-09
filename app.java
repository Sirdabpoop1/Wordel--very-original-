import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class app {
    public static List<String> loadWordBank(String filePath) throws IOException{
        List<String> words = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String word;

            while ((word = br.readLine()) != null){
                if (word.length() == 5 && word.matches("[a-z]+")) {
                    words.add(word); 
                }
            }
        }
        
        return words;
    }

    public static String getRandomWord(List<String> words){
        if (words.isEmpty()) {
            throw new IllegalArgumentException("no words");
        }
        else{
            Random rand = new Random();
            int index = rand.nextInt(words.size());
            return words.get(index);
        }
    }

    //To clear console
    public static void Clear(){
        //Clears the console (duh)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    //Waiter to wait for the sake of waiting
    public static void Wait(long time) {
        try{
            Thread.sleep(time);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void SinglePlayerMode(String word) {
        
    }

    public static void main(String[] args) throws IOException{
        
        System.out.println("Welcome to Wordel!");
        // Create input object to read console
        Scanner input = new Scanner(System.in);
        //Prompt for player count
        System.out.println("How many players would you like to play with? 1 or 2?");
        //Checks to ensure user/users enter 1 or 2
        int playercount = 0;
        while (playercount != 1 && playercount != 2) {
            try {
                playercount = input.nextInt();
                if (playercount != 1 && playercount != 2) {
                    System.out.println("Please enter either 1 or 2 :(");
                }
            } catch (InputMismatchException ex) {
                // This handles the “f” (or any non-int) input
                System.out.println("Invalid input.");
                //Goes to next line for the other error line.
                input.nextLine();
            }
        }
        
        //Gets the wordbank, which then gets the random word.

        List <String> bank = loadWordBank("wordbank.txt");
        String word = getRandomWord(bank);


        //Fun fluff text
        if (playercount == 1){
            System.out.println("Entering 1 player mode...");
            Wait(1300);
            SinglePlayerMode(word);
            Clear();
        }
        else{
            System.out.println("Entering 2 player mode...");
            Wait(1300);
            Clear();
        }
    }
}