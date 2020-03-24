package be.charybde.boat.command;

import org.bukkit.entity.Player;

public interface ICommandHandler {
    boolean handle(String command, String[] args, Player player);
}