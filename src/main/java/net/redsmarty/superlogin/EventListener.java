package net.redsmarty.superlogin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.geysermc.floodgate.api.FloodgateApi;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
    HashMap<UUID, InetSocketAddress> sessions;
    ArrayList<Player> loggedInPlayers = new ArrayList<>();
    ArrayList<UUID> registeredPlayers = new ArrayList<>();
    HashMap<Player, StringBuilder> pin = new HashMap<>();
    Inventory loginInventory;
    Inventory registerInventory;
    RegisterLoginHandler loginHandler;

    public void initialize() {
        SuperLogin sl = SuperLogin.getInstance();
        loggedInPlayers = sl.loggedInPlayers;
        registeredPlayers = sl.registeredPlayers;
        loginInventory = new LoginInventory("login").getLoginInventory();
        registerInventory = new LoginInventory("register").getLoginInventory();
        loginHandler = sl.getLoginHandler();
        sessions = sl.sessions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (sessions.containsKey(player.getUniqueId())) {
            if (sessions.get(player.getUniqueId()).equals(player.getAddress())) {
                loggedInPlayers.add(player);
                return;
            }
        }
        if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
            loggedInPlayers.add(player);
            return;
        }
        pin.put(event.getPlayer(), new StringBuilder());
        if (registeredPlayers.contains(player.getUniqueId())) {
            player.openInventory(loginInventory);
        } else {
            player.openInventory(registerInventory);
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!loggedInPlayers.contains(event.getPlayer())) {
            if (event.getTo().getY() > event.getFrom().getY() || event.getTo().getY() < event.getFrom().getY() || event.getFrom() == event.getTo()) {
                return;
            }
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!loggedInPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public  void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (!loggedInPlayers.contains(event.getDamager())) {
                    event.setCancelled(true);
                }
            }
            if (event.getEntity() instanceof Player) {
                if (!loggedInPlayers.contains(event.getEntity())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (SuperLogin.getInstance().getConfig().getStringList("allowed_commands").contains(event.getMessage())) return;
        if (!loggedInPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!loggedInPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void  onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        if (!loggedInPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() == loginInventory || event.getInventory() == registerInventory) {
            StringBuilder sb = pin.get(event.getWhoClicked());
            if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                sb.append(event.getCurrentItem().getAmount());
                if (sb.length() == 4) {
                    if (!registeredPlayers.contains(event.getWhoClicked().getUniqueId())) {
                        loginHandler.registerPlayer((Player) event.getWhoClicked(), sb.toString());
                    } else {
                        if (!loginHandler.loginPlayer((Player) event.getWhoClicked(), sb.toString())) {
                            pin.get(event.getWhoClicked()).setLength(0);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(SuperLogin.getInstance(), () -> event.getWhoClicked().openInventory(loginInventory), 60L);
                        }
                    }
                }
            }
            if (event.getCurrentItem().getType() == Material.BARRIER) {
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
                for (int i = 0; i < sb.length(); i++) {
                    event.getClickedInventory().setItem(i + 13, LoginInventory.createGuiItem(Material.BARRIER, "*", 1));
                }
            }
            event.getWhoClicked().sendMessage(pin.get(event.getWhoClicked()).toString());
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory() == loginInventory || event.getInventory() == registerInventory) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!loggedInPlayers.contains(player)) {
            if (registeredPlayers.contains(player.getUniqueId())) {
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SuperLogin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        player.openInventory(registerInventory);
                    }
                }, 40L);
            }
        }
    }

}
