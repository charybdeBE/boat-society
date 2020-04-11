package be.charybde.boat.travel;

import be.charybde.boat.Utils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_14_R1.Vector3f;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.List;

public class Journey {

    private Pair<Location, Location> start;
    private Pair<Location, Location> end;
    private int rotation;
    private List<Entity> passengers;

    public Journey(Pair<Location, Location> start, Pair<Location, Location> end, int rotation) {
        this.start = start;
        this.end = end;
        this.rotation = rotation;
    }

    public void travel() {
        BoundingBox box = Utils.getBoundingBoxFromLocations(start);
        passengers  = (List<Entity>) start.getFirst().getWorld().getNearbyEntities(box);
        if(passengers.size() > 50){
            return; // TODO Throw
        }
        for(Entity passenger: passengers) {
            this.teleportRelative(passenger);
        }
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public void teleportRelative(Entity passenger) {
        Location passengerLoc = passenger.getLocation();
        double offsetX = passengerLoc.getX() - start.getFirst().getX();
        double offsetY = passengerLoc.getY() - start.getFirst().getY();
        double offsetZ = passengerLoc.getZ() - start.getFirst().getZ();

        double normalizedX = offsetX;
        double normalizedZ = offsetZ;

        if(this.rotation == 90) {
            normalizedX = offsetZ;
            normalizedZ = -1 * offsetX;
        }
        if(this.rotation == 180 || this.rotation == -180) {
            normalizedX = -1 * offsetX;
            normalizedZ = -1 * offsetZ;
        }
        if(this.rotation == -90) {
            normalizedX = -1 * offsetZ;
            normalizedZ = offsetX;
        }

        Location newLoc = new Location(end.getFirst().getWorld(),
                end.getFirst().getX() + normalizedX,
                end.getFirst().getY() + offsetY,
                end.getFirst().getZ() + normalizedZ);

        passenger.teleport(newLoc);
    }
}
