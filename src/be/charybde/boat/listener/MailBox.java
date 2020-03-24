package be.charybde.boat.listener;

import be.charybde.boat.Utils;
import be.charybde.boat.entities.Boat;
import be.charybde.boat.entities.Metro;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import java.util.HashMap;

public class MailBox implements Listener {

    public HashMap<String, Boat> port; //Contains all the boat
    private HashMap<String, Metro> lines;
    public HashMap<Inventory, String> configs;
    private static MailBox instance;

    public static MailBox getMailBox() {
        return MailBox.instance;
    }

    public MailBox() {
        instance = this;
        lines = new HashMap<>();
        port = new HashMap<>();
        configs = new HashMap<>();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { //TODO destroy golden axe
            Block b = e.getClickedBlock();
            if (Utils.isWallSign(b.getType())) { //Todo check what is behind
                Sign sign = (Sign) b.getState();
                String name = sign.getLine(1);
                if(e.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLDEN_AXE) {
                    if (sign.getLine(0).equalsIgnoreCase("[Boat]")) { //TODO functions
                        Boat boat = port.get(name);
                        if (boat == null) {
                            Inventory i = Utils.deserializeInventory(name);
                            if (i != null) {
                                boat = new Boat(i, name);
                            } else {
                                boat = new Boat(e.getClickedBlock().getLocation(), name);
                            }
                            port.put(name, boat);
                            configs.put(boat.getGUI(), name);
                        }
                        if (boat.isInEquipage(e.getPlayer())) {
                            e.getPlayer().openInventory(boat.getGUI());
                        } else {
                            e.getPlayer().sendMessage(Utils.formatMessage("noIn"));
                        }
                    } else if (sign.getLine(0).equalsIgnoreCase("[Metro]")) {
                        Metro met = lines.get(name);
                        if (met == null) {
                            Inventory i = Utils.deserializeInventory(name);
                            if (i != null) {
                                met = new Metro(i, name);
                            } else {
                                met = new Metro(e.getClickedBlock().getLocation(), name);
                            }
                            lines.put(name, met);
                            configs.put(met.getGUI(), name);
                        }
                        if (met.isInEquipage(e.getPlayer())) {
                            e.getPlayer().openInventory(met.getGUI());
                        } else {
                            e.getPlayer().sendMessage(Utils.formatMessage("noIn"));
                        }
                    }
                }
            }
        } else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Block b = e.getClickedBlock();
            if (Utils.isWallSign(b.getType())) { //Todo check what is behind
                Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equalsIgnoreCase("[Boat]")) { //TODO functions
                    String name = sign.getLine(1);

                    double price = Double.parseDouble(sign.getLine(3).replaceAll("[A-Za-z ]", ""));
                    Boat boat = port.get(name);
                    if (boat == null) {
                        Inventory i = Utils.deserializeInventory(name);
                        if (i != null) {
                            boat = new Boat(i, name);
                            port.put(name, boat);
                            configs.put(boat.getGUI(), name);
                        }
                    }
                    if (boat != null) {
                        boat.run(e.getPlayer(), price);
                    }
                } else if (sign.getLine(0).equalsIgnoreCase("[Metro]")) {
                    String name = sign.getLine(1);

                    double price = Double.parseDouble(sign.getLine(3).replaceAll("[A-Za-z ]", ""));
                    Metro met = lines.get(name);
                    if (met == null) {
                        Inventory i = Utils.deserializeInventory(name);
                        if (i != null) {
                            met = new Metro(i, name);
                            lines.put(name, met);
                            configs.put(met.getGUI(), name);
                        }
                    }
                    if (met != null) {
                        met.run(e.getPlayer(), price);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBoatConfigClose(InventoryCloseEvent event) {
        if (configs.containsKey(event.getInventory())) { //TODO Handle errors //TODO handle signed
            Utils.serializeInventory(configs.get(event.getInventory()), event.getInventory());
        }
    }

}
