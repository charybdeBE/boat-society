package be.charybde.boat.entities;

import be.charybde.boat.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class Chariot extends AbstractTransport {
    protected double consommation = 512.0;
    protected long travelTime = 10;
    protected String autorite = "Police";

    public Chariot(Location motor, String s) {
        super(motor, s);
    }

    public Chariot(Inventory i, String s) {
        super(i, s);
    }


    protected boolean haveEnoughFuel(int consommation) {
        return consommation <= Utils.countNumberOfItem(this.gui, Material.CHARCOAL) && consommation <= Utils.countNumberOfItem(this.gui, Material.HAY_BLOCK);
    }

    protected void consumeFuel(int consommation) {
        Utils.removeNumberFromInventory(this.gui, Material.CHARCOAL, consommation);
        Utils.removeNumberFromInventory(this.gui, Material.HAY_BLOCK, consommation);
    }
}
