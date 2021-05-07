package net.redsmarty.superlogin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.redsmarty.superlogin.EventListener;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class SuperLogin extends JavaPlugin {
    public static SuperLogin instance;
    public ArrayList<Player> loggedInPlayers;
    public ArrayList<UUID> registeredPlayers;
    public FileConfiguration db;
    private RegisterLoginHandler loginHandler;
    HashMap<UUID, InetSocketAddress> sessions = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        File dbFile = new File(this.getDataFolder().getPath() + System.getProperty("file.separator"), "playerdb.yml");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db = YamlConfiguration.loadConfiguration(dbFile);
        db.getKeys(false).forEach(key -> registeredPlayers.add(UUID.fromString(key)));
        loginHandler = new RegisterLoginHandler();
        EventListener listener = new EventListener();
        listener.initialize();
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public static SuperLogin getInstance() {
        return instance;
    }

    public RegisterLoginHandler getLoginHandler() {
        return loginHandler;
    }
}
