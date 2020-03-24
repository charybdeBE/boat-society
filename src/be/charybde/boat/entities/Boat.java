package be.charybde.boat.entities;

import be.charybde.bank.entities.Account;
import be.charybde.boat.Main;
import be.charybde.boat.Utils;
import be.charybde.boat.Vault;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Boat {
    Location start, middle, end;
    Location motor; //May be useless
    Inventory gui;

    private ArrayList<String> equipage;
    private String name;
    private String owner; // Armateur

    private double consommation = 512.0;


    public Boat(Location motor, String s) {
        this.motor = motor;
        this.name = s;
        this.gui = Bukkit.createInventory(null, 9, "§6" + s);
    }

    public Boat(Inventory i, String s) {
        this.name = s;
        this.gui = i;
    }

    public void setJourney(BookMeta bookMeta) {
        try {
            start = parseItem(bookMeta, "start");
            middle = parseItem(bookMeta, "middle", "flat");
            end = parseItem(bookMeta, "end");
        } catch (IllegalArgumentException e) {
        }
    }


    public void setEquipage(BookMeta bookMeta) {
        try {
            this.equipage = new ArrayList<>(Arrays.asList(bookMeta.getPage(2).replace("§0", "").split("\n")));
        } catch (IllegalArgumentException e) {

        }
    }

    public double getDistance() {
        return this.start.distance(this.end);
    }

    public void run(Player player, double price) {
        this.updateFromGUI();
        Location destination = this.destination(player);
        if (destination != null) {
            if (Vault.getEconomy().has(player.getName(), price)) {
                Account linked = Account.fetch(this.owner.toLowerCase());
                if(linked == null){
                    return;
                }
                Vault.getEconomy().withdrawPlayer(player, price);
                double newBalance = linked.getBalance() + price;
                linked.setBalance(newBalance);
                linked.save(true);
                int conso = (int) Math.floor(this.getDistance() / this.consommation);
                System.out.println(conso);
                if (conso <= Utils.countNumberOfItem(this.gui, Material.CHARCOAL)) {
                    Utils.removeNumberFromInventory(this.gui, Material.CHARCOAL, conso);
                    player.teleport(middle);
                    long seconds = 15;
                    Utils.serializeInventory(name, gui);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> player.teleport(destination), (seconds * 20)); // Always multiply by twenty because that's the amount of ticks in Minecraft
                } else {
                    player.sendMessage(Utils.formatMessage("noFuel"));
                }
            } else {
                player.sendMessage(Utils.formatMessage("noMoney"));
            }
        }
    }

    public Inventory getGUI() {
        return gui;
    }

    public String getName() {
        return name;
    }

    public boolean isInEquipage(Player p) {
        this.updateFromGUI();
        if (this.equipage == null) {
            return true;
        }
        return this.equipage.contains(p.getName());
    }

    private void updateFromGUI() {
        try {
            if (this.gui.getItem(0) != null && this.gui.getItem(0).getType() == Material.WRITTEN_BOOK) {
                BookMeta bm = (BookMeta) this.gui.getItem(0).getItemMeta();
                if (bm.getAuthor().equalsIgnoreCase("Amirauté")) {
                    this.setEquipage(bm);
                    this.setJourney(bm);
                    String[] metaA = bm.getPage(1).replace("§0", "").split("\n");
                    for (String meta : metaA) {
                        System.out.println(meta);
                        if (meta.startsWith("owner")) {
                            this.owner = meta.trim().substring("owner".length()).trim();
                        }
                    }
                } else {
                    System.out.println("Not legal" + bm.getAuthor() + "!= Amirauté");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            this.end = null;
            this.equipage = null;
        }
    }

    private Location parseItem(BookMeta book, String pos, String world) {
        String[] metaA = book.getPage(1).replace("§0", "").split("\n");
        for (String meta : metaA) {
            if (meta.startsWith(pos)) {
                String loc = meta.trim().substring(pos.length());
                String[] a = loc.split(",");
                return new Location(Bukkit.getWorld(world), Double.parseDouble(a[0]), Double.parseDouble(a[1]), Double.parseDouble(a[2]));
            }
        }
        return null;
    }

    private Location parseItem(BookMeta book, String pos) {
        return parseItem(book, pos, "world");
    }

    private Location destination(Player p) { //TODO check start end
        Location player = p.getLocation();
        if (this.start != null && this.end != null) {
            double start = player.distance(this.start);
            double end = player.distance(this.end);
            if (start < 15.0 || end < 15.0) {
                if (start < end) {
                    return this.end;
                } else {
                    return this.start;
                }
            }
            p.sendMessage(Utils.formatMessage("far"));
        }
        return null;
    }
}

