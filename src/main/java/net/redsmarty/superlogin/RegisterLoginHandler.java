package net.redsmarty.superlogin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RegisterLoginHandler {
    FileConfiguration db;
    ArrayList<Player> loggedInPlayers;
    ArrayList<UUID> registeredPlayers;

    public RegisterLoginHandler() {
        SuperLogin sl = SuperLogin.getInstance();
        loggedInPlayers = sl.loggedInPlayers;
        registeredPlayers = sl.registeredPlayers;
        db = SuperLogin.getInstance().db;
    }

    public void registerPlayer(Player player, String pin) {
        db.set(player.getUniqueId().toString(), pin);
        db.set("hi", "hey");
        loggedInPlayers.add(player);
        registeredPlayers.add(player.getUniqueId());
        try {
            db.save(new File(SuperLogin.getInstance().getDataFolder().getPath() + System.getProperty("file.separator"),"playerdb.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SuperLogin.getInstance().sessions.put(player.getUniqueId(), player.getAddress());
        player.sendMessage("You have successfully registered.");
    }
    public boolean loginPlayer(Player player, String pin) {
        if (db.getString(player.getUniqueId().toString()).equals(pin)) {
            loggedInPlayers.add(player);
            player.sendMessage("You have successfully logged in");
            player.closeInventory();
            SuperLogin.getInstance().sessions.put(player.getUniqueId(), player.getAddress());
            return true;
        } else {
            player.sendMessage("Incorrect pin, try again.");
            player.closeInventory();
            return false;
        }
    }

}
