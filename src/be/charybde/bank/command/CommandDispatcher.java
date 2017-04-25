package be.charybde.bank.command;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandDispatcher {
    private Map<String, ICommandHandler> handlers = new LinkedHashMap();

    public CommandDispatcher() {
    }

    public synchronized void registerHandler(String commandName, ICommandHandler handler) {
        this.handlers.put(commandName, handler);
    }

    public boolean handle(String command, String[] args, Player player) {
        String lower = command.toLowerCase();
        if(!handlers.containsKey(lower))
            return false;
        return this.handlers.containsKey(lower)?((ICommandHandler)this.handlers.get(lower)).handle(lower, args, player):((ICommandHandler)this.handlers.get("")).handle("", args, player);
    }
}
