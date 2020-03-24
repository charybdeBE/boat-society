package be.charybde.boat.entities;

import be.charybde.bank.entities.Account;
import be.charybde.boat.Main;
import be.charybde.boat.Utils;
import be.charybde.boat.Vault;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Metro {
    Inventory gui;
    Location motor;
    private ArrayList<Pair<String, Location>> journey; //Journey on page 3 Station: X,y,z

    private ArrayList<String> equipage;
    private String name;
    private String owner; // Armateur

    private double consommation = 10000000;
//TODO message the station name

    public Metro(Location motor, String s) {
        this.motor = motor;
        this.name = s;
        this.gui = Bukkit.createInventory(null, 9, "§6" + s);
        this.journey = new ArrayList<>();
    }

    public Metro(Inventory i, String s) {
        this.name = s;
        this.gui = i;
        this.journey = new ArrayList<>();
    }

    public void setJourney(BookMeta bookMeta) {
        try {
            this.journey = new ArrayList<>();
            String[] metaA = bookMeta.getPage(3).replace("§0", "").split("\n");
            for (String meta : metaA) {
                String[] namePos = meta.split(":");
                String name = namePos[0];
                String[] loc = namePos[1].trim().split(",");
                Location location = new Location(Bukkit.getWorld("world"), Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]));
                this.journey.add(new Pair<>(name, location));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }


    public void setEquipage(BookMeta bookMeta) {
        try {
            this.equipage = new ArrayList<>(Arrays.asList(bookMeta.getPage(2).replace("§0", "").split("\n")));
        } catch (IllegalArgumentException e) {

        }
    }

    public void run(Player player, double price){
        this.updateFromGUI();
        this.run(player, price, this.journey.size() * 2);
    }
    public void run(Player player, double price, int maxHop) {
        this.updateFromGUI();
        Pair<String, Location> destination = this.destination(player);
        if (destination != null && maxHop > 0) {
            if (Vault.getEconomy().has(player.getName(), price)) {
                Account linked = Account.fetch(this.owner.toLowerCase());
                if (linked == null) {
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
                    long seconds = 5;
                    Utils.serializeInventory(name, gui);
                    player.teleport(destination.getSecond());
                    HashMap<String, String> message = new HashMap<>();
                    message.put("ss", destination.getFirst());
                    player.sendMessage(Utils.formatMessage("arrival", message));
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                        this.run(player, 0, maxHop - 1);
                    }, (seconds * 20)); // Always multiply by twenty because that's the amount of ticks in Minecraft
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
            this.journey = null;
            this.equipage = null;
        }
    }

    private double getDistance() {
        return 0.0;
    }

    private Pair<String, Location> destination(Player p) {
        Location player = p.getLocation();
        for(int i = 0; i < this.journey.size(); ++i){
            Pair<String, Location> it = this.journey.get(i);
            double dist = player.distance(it.getSecond());
            if(dist < 3){
                it.getSecond();
                if(i == this.journey.size() - 1) {
                    return this.journey.get(0);
                } else {
                    return this.journey.get(i+1);
                }
            }
        }
        return null;
    }
}
