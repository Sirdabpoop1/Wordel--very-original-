import java.util.*;

public class Backpack {
    private List<Powerup> powerups = new ArrayList<>();

    public void addPowerup(Powerup p) {
        // Adds newly created powerup to the list of available powerups
        powerups.add(p);
    }

    public void usePowerup(String name, Player player, Player other, List<String> bank, char[] key) {
        for (int i = 0; i < powerups.size(); i++) {
            Powerup p = powerups.get(i);
            // If the name of the powerup is not in the list powerups, then we assume the
            // player does not have said powerup and the if statement doesn't go through
            if (p.getName().equalsIgnoreCase(name)) {
                p.use(player, other, key, bank);
                powerups.remove(i);
                return;
            }
        }
        player.sendMessage("You don't have that powerup! Please choose another!");
    }

    public void sendPowerups(Player current) {
        // Iterates through powerups and prints each value
        current.sendMessage("Powerups in your backpack:");
        for (int i = 0; i < powerups.size(); i++) {
            Powerup p = powerups.get(i);
            i++;
            current.sendMessage(i + ". " + p.getName() + ": " + p.getDescription());
            i--;
        }
        current.sendMessage("\n");
    }

    public List<Powerup> returnPowerups() {
        return powerups;
    }
}
