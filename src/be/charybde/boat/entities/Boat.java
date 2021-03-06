package be.charybde.boat.entities;

import be.charybde.boat.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class Boat extends AbstractTransport {
    protected double consommation = 512.0;
    protected long travelTime = 15;
    protected String autorite = "Amirauté";

    public Boat(Location motor, String s) {
        super(motor, s);
    }

    public Boat(Inventory i, String s) {
        super(i, s);
    }

    protected boolean haveEnoughFuel(int consommation) {
        return consommation <= Utils.countNumberOfItem(this.gui, Material.CHARCOAL);
    }

    protected void consumeFuel(int consommation) {
        Utils.removeNumberFromInventory(this.gui, Material.CHARCOAL, consommation);
    }
}
