package be.charybde.boat.entities;

import be.charybde.bank.entities.Account;
import be.charybde.boat.Main;
import be.charybde.boat.Utils;
import be.charybde.boat.Vault;
import be.charybde.boat.travel.Journey;
import jdk.nashorn.internal.scripts.JO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractTransport {
    Location motor; //May be useless
    Inventory gui;

    Pair<Location, Location> startPosition;
    Pair<Location, Location> middlePosition;
    Pair<Location, Location> endPosition;
    int rotation = 0;

    private ArrayList<String> equipage;
    private String name;
    private String owner; // Armateur

    protected double consommation;
    protected long travelTime;
    protected String autorite;

    public AbstractTransport(Location motor, String s) {
        this.motor = motor;
        this.name = s;
        this.gui = Bukkit.createInventory(null, 9, "§6" + s);
    }

    public AbstractTransport(Inventory i, String s) {
        this.name = s;
        this.gui = i;
    }

    public void setJourney(BookMeta bookMeta) {
        try {
            Location start1 = parseItem(bookMeta, "1start");
            Location start2 = parseItem(bookMeta, "2start");
            startPosition = new Pair<>(start1, start2);
            Location mid1 = parseItem(bookMeta, "1middle", "flat");
            Location mid2 = parseItem(bookMeta, "2middle", "flat");
            middlePosition = new Pair<>(mid1, mid2);
            Location end1 = parseItem(bookMeta, "1end");
            Location end2 = parseItem(bookMeta, "2end");
            endPosition = new Pair<>(end1, end2);
        } catch (IllegalArgumentException e) {
        }
    }


    public void setEquipage(BookMeta bookMeta) {
        try {
            this.equipage = new ArrayList<>(Arrays.asList(bookMeta.getPage(2).toLowerCase().replace("§0", "").split("\n")));
        } catch (IllegalArgumentException e) {

        }
    }

    public double getDistance() {
        return this.startPosition.getFirst().distance(this.endPosition.getFirst());
    }

    public void run(Player player, double price) {
        this.updateFromGUI();
        Pair<Journey, Journey> journeys = this.getJourneys(player);
        if (journeys != null) {
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
                if (conso < 1) {
                    conso = 1;
                }
                if (haveEnoughFuel(conso)) {
                    consumeFuel(conso);
                    journeys.getFirst().travel();
                    Utils.serializeInventory(name, gui);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin,
                            () -> {
                                journeys.getSecond().travel();
                                //if player have fallen
                                journeys.getFirst().getPassengers().forEach(passenger -> {
                                    if(passenger.getLocation().getWorld() == Bukkit.getWorld("flat")) {
                                       journeys.getSecond().teleportRelative(passenger);
                                    }
                                });
                            },
                            (travelTime * 20)); // Always multiply by twenty because that's the amount of ticks in Minecraft
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
        return p.isOp() || this.equipage.contains(p.getName().toLowerCase());
    }

    protected abstract boolean haveEnoughFuel(int consommation);

    protected abstract void consumeFuel(int consommation);

    private void updateFromGUI() {
        try {
            if (this.gui.getItem(0) != null && this.gui.getItem(0).getType() == Material.WRITTEN_BOOK) {
                BookMeta bm = (BookMeta) this.gui.getItem(0).getItemMeta();
                if (bm.getAuthor().equalsIgnoreCase(autorite)) {
                    this.setEquipage(bm);
                    this.setJourney(bm);
                    String[] metaA = bm.getPage(1).replace("§0", "").split("\n");
                    for (String meta : metaA) {
                        if (meta.startsWith("owner")) {
                            this.owner = meta.trim().substring("owner".length()).trim();
                        }
                        if (meta.startsWith("rotation")) {
                            this.rotation = Integer.parseInt(meta.trim().substring("rotation".length()).trim());
                        }
                    }
                } else {
                    System.out.println("Not legal" + bm.getAuthor() + "!= Amirauté");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            this.endPosition = null;
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

    // First origin Second destination
    private Pair<Journey, Journey> getJourneys(Player p) {
        Location player = p.getLocation();
        if (this.startPosition != null && this.endPosition != null) {
            BoundingBox start = Utils.getBoundingBoxFromLocations(startPosition);
            if (Utils.isInBox(p, start)) {
                Journey j1 = new Journey(this.startPosition, this.middlePosition, 0);
                Journey j2 = new Journey(this.middlePosition, this.endPosition, this.rotation);
                return new Pair<>(j1, j2);
            }
            BoundingBox end = Utils.getBoundingBoxFromLocations(endPosition);
            if (Utils.isInBox(p, end)) {
                Journey j1 = new Journey(this.endPosition, this.middlePosition, -1 * this.rotation);
                Journey j2 = new Journey(this.middlePosition, this.startPosition, 0);
                return new Pair<>(j1, j2);
            }

            p.sendMessage(Utils.formatMessage("far"));
        }
        return null;
    }

}

