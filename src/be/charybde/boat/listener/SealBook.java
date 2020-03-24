package be.charybde.boat.listener;

import be.charybde.boat.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.HashMap;

public class SealBook implements Listener {

    public HashMap<Location, Inventory> configs;
    public HashMap<Inventory, String> names;
    private static SealBook instance;

    public static SealBook getMailBox() {
        return SealBook.instance;
    }

    public SealBook() {
        instance = this;
        configs = new HashMap<>();
        names = new HashMap<>();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = e.getClickedBlock();
            if (Utils.isWallSign(b.getType())) { //Todo check what is behind // TODO check permissions
                Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equalsIgnoreCase("[Seal]")) {
                    String name = sign.getLine(1);
                    Inventory inv = configs.get(e.getClickedBlock().getLocation());
                    if(inv == null){
                        inv = Bukkit.createInventory(null, InventoryType.FURNACE, name);
                        configs.put(e.getClickedBlock().getLocation(), inv);
                        names.put(inv, name);
                    }
                    e.getPlayer().openInventory(inv);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (configs.containsValue(event.getInventory())) { //TODO Handle errors
            if (event.getInventory().getItem(0) != null && event.getInventory().getItem(0).getType() == Material.WRITABLE_BOOK) {
                Inventory inv = event.getInventory();
                ItemStack item = event.getInventory().getItem(0);
                BookMeta meta = (BookMeta) item.getItemMeta();
                item.setType(Material.WRITTEN_BOOK);
                String name = names.get(inv);
                meta.setAuthor(name);
                meta.setTitle("Journal de Bord");
                item.setItemMeta(meta);
                inv.setItem(0, item);
            }
        }
    }
}
