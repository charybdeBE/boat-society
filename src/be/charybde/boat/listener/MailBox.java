package be.charybde.boat.listener;

import be.charybde.boat.Utils;
import be.charybde.boat.entities.AbstractTransport;
import be.charybde.boat.entities.Boat;
import be.charybde.boat.entities.Chariot;
import be.charybde.boat.entities.Metro;
import org.bukkit.Location;
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

    public HashMap<String, AbstractTransport> port; //Contains all the boat
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
                    if (isPrivateTransport(sign)) { //TODO functions
                        AbstractTransport abstractTransport = port.get(name);
                        if (abstractTransport == null) {
                            abstractTransport = getEntity(sign, name, e.getClickedBlock().getLocation());
                        }
                        if (abstractTransport.isInEquipage(e.getPlayer())) {
                            e.getPlayer().openInventory(abstractTransport.getGUI());
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
                if (isPrivateTransport(sign)) {
                    String name = sign.getLine(1);

                    double price;
                    try {
                        price = Double.parseDouble(sign.getLine(3).replaceAll("[A-Za-z ]", ""));
                    } catch (Exception a) {
                        price = 0.0;
                    }
                    AbstractTransport abstractTransport = port.get(name);
                    if (abstractTransport == null) {
                        abstractTransport = getEntity(sign, name);
                    }
                    if (abstractTransport != null) {
                        abstractTransport.run(e.getPlayer(), price);
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


    private boolean isPrivateTransport(Sign sign) {
        return sign.getLine(0).equalsIgnoreCase("[Boat]") || sign.getLine(0).equalsIgnoreCase("[Chariot]");
    }

    //TODO move in factory ?
    private AbstractTransport getEntity(Sign s, String name) {
        return getEntity(s, name, null);
    }

    private AbstractTransport getEntity(Sign s, String name, Location location) {
        Inventory i = Utils.deserializeInventory(name);
        AbstractTransport abstractTransport = null;
        if(i == null && location == null)
            return null;
        switch (s.getLine(0).toLowerCase()) {
            case "[boat]":
                if(i == null) {
                    abstractTransport = new Boat(location, name);
                } else {
                    abstractTransport = new Boat(i, name);
                }
                break;
            case "[chariot]":
                if(i == null) {
                    abstractTransport = new Chariot(location, name);
                } else {
                    abstractTransport = new Chariot(i, name);
                }
                break;
        }
        if(abstractTransport != null){
            port.put(name, abstractTransport);
            configs.put(abstractTransport.getGUI(), name);
        }
        return abstractTransport;
    }

}
