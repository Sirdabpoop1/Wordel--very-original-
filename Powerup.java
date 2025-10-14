import java.util.*;

public class Powerup {
    // Define variables
    private String name;
    private String description;

    public Powerup(String name, String description) {
        // Initialize variables
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void use(Player player, Player other, char[] key, List<String> bank) {
        // Executes one of the following options depending on the name of the powerup
        switch (name) {
            case "The KEYS to the Cheese Factory":
                revealLetter(player, other, key);
                break;
            case "The Hash-Slinging Slasher":
                confuse(player, other);
                break;
            case "10L Coffee Cup":
                skipTurn(player, other);
                player.sendMessage("Your opponent's turn was skipped!");
                break;
            case "Standing Up School Application":
                swapWord(player, other, bank);
                player.sendMessage("Your opponent's word was swapped!");
                break;
            case "YoHoHo's Shiny Right Hook":
                List<String> guesses = other.getGuesses();
                player.sendMessage("Your opponent's last guess was " + guesses.get(guesses.size() - 1).toUpperCase());
        }
    }

    public void revealLetter(Player player, Player other, char[] key) {
        // Reveals a letter
        Random rand = new Random();
        int i = rand.nextInt(key.length);
        player.sendMessage("There is a " + key[i] + " in your word!");
        other.sendMessage(player.getName() + " has used a powerup!");
    }

    public void confuse(Player player, Player other) {
        // Makes opponent's next results random
        other.setLost(true);
        player.sendMessage("You have confuzzled your opponent!");
        other.sendMessage(player.getName() + " has used a powerup!");
    }

    public void skipTurn(Player player, Player other) {
        // Skips opponent's turn
        other.setSkip(true);
        player.sendMessage("You have skipped your opponent's turn!");
        other.sendMessage(player.getName() + " has used a powerup!");
    }

    public void swapWord(Player player, Player other, List<String> bank) {
        // Sets opponent's word to a new, random one
        Random rand = new Random();
        int i = rand.nextInt(bank.size());
        other.setWord(bank.get(i));
        player.sendMessage("You have swapped your opponent's word!");
        other.sendMessage(player.getName() + " has used a powerup!");
    }
}
