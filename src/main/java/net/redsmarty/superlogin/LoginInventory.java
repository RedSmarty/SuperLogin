package net.redsmarty.superlogin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LoginInventory {
    private Inventory inventory;

    public LoginInventory(String type) {
        if (type.equals("login")) {
            inventory = Bukkit.createInventory(null, 27, "Enter your pin to log in");
        } else {
            inventory = Bukkit.createInventory(null, 27, "Enter your pin to register");
        }
    }

    public Inventory getLoginInventory() {
        inventory.setItem(0, createGuiItem(Material.NAME_TAG, "1", 1));
        inventory.setItem(1, createGuiItem(Material.NAME_TAG, "2", 2));
        inventory.setItem(2, createGuiItem(Material.NAME_TAG, "3", 3));
        inventory.setItem(9, createGuiItem(Material.NAME_TAG, "4", 4));
        inventory.setItem(10, createGuiItem(Material.NAME_TAG, "5", 5));
        inventory.setItem(11, createGuiItem(Material.NAME_TAG, "6", 6));
        inventory.setItem(18, createGuiItem(Material.NAME_TAG, "7", 7));
        inventory.setItem(19, createGuiItem(Material.NAME_TAG, "8", 8));
        inventory.setItem(20, createGuiItem(Material.NAME_TAG, "9", 9));
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType().isAir()) {
                inventory.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "", 1));
            }
        }
        inventory.setItem(3, createGuiItem(Material.BARRIER, "Delete last digit", 1));
        return inventory;
    }

    public static ItemStack createGuiItem(final Material material, final String name, int amount) {
        final ItemStack item = new ItemStack(material, amount);
        if (material != Material.AIR) {
            final ItemMeta meta = item.getItemMeta();

            // Set the name of the item
            meta.setDisplayName(name);

            item.setItemMeta(meta);

        }
            return item;
    }
}
