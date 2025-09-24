import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class WordelGame {
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

    public static void main(String[] args) throws IOException{
        List<String> words = loadWordBank("wordbank.txt");
        String goodWord = getRandomWord(words);
        char[] goodLetters = goodWord.toCharArray();

        System.out.println("Welcome to Wordel! ");

    }

    
}