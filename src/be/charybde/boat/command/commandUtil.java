package be.charybde.boat.command;

/**
 * Created by laurent on 20.04.17.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import be.charybde.boat.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class commandUtil {
    private static final String RootCommand = "g";

    private commandUtil() {
    }

    public static void sendToPlayerOrConsole(String message, Player player) {
        if(player != null) {
            player.sendMessage(message);
        } else {
            Main.getInstance().log(message, Level.INFO);
        }

    }

    public static Collection<String> getCollectionFromSingle(String single) {
        LinkedList coll = new LinkedList();
        coll.add(single);
        return coll;
    }

    public static String getMessageForSubs(Collection<String> subCommands, String... preCommands) {
        return getCommandList(subCommands, "Available Subcommands: ", "\n", true, preCommands);
    }

    public static String getCommandList(Collection<String> subCommands, String header, String delimiter, boolean prefixRoot, String... preCommands) {
        StringBuilder builder = new StringBuilder(subCommands.size() * 25);
        builder.append(header);
        builder.append("\n");
        StringBuilder rootBuilder = new StringBuilder(preCommands.length * 25);
        String[] first = preCommands;
        int var8 = preCommands.length;

        for(int sub = 0; sub < var8; ++sub) {
            String pre = first[sub];
            if(rootBuilder.length() == 0) {
                rootBuilder.append("/");
                rootBuilder.append("g");
            }

            rootBuilder.append(" ");
            rootBuilder.append(pre);
        }

        boolean var11 = true;
        if(subCommands.isEmpty()) {
            builder.append("N/A");
        } else {
            Iterator var12 = subCommands.iterator();

            while(var12.hasNext()) {
                String var13 = (String)var12.next();
                if(!var11) {
                    builder.append(delimiter);
                }

                if(prefixRoot) {
                    builder.append(rootBuilder.toString());
                }

                if(prefixRoot || !var11) {
                    builder.append(" ");
                }

                builder.append(var13);
                if(var11) {
                    var11 = false;
                }
            }
        }

        return builder.toString();
    }

    public static String getAllCommands() {
        LinkedList commands = new LinkedList();
        commands.add("pay ACCOUNT ~");
        commands.add("list~(Gives a list of boat account)");
        commands.add("ACCOUNT ~(check the account)");
        return formatAllCommands(commands, "g");
    }

    public static String formatAllCommands(List<String> commands, String rootCommand) {
        StringBuilder builder = new StringBuilder(200);
        builder.append("Available Commands: ");
        Iterator var3 = commands.iterator();

        while(var3.hasNext()) {
            String comm = (String)var3.next();
            builder.append(ChatColor.GOLD);
            builder.append("\n/");
            builder.append(rootCommand);
            builder.append(" ");
            String[] parts = comm.split("~");
            builder.append(parts[0]);
            if(parts.length > 1) {
                builder.append(" ");
                builder.append(ChatColor.WHITE);
                builder.append(parts[1]);
            }
        }

        builder.append("\n");
        return builder.toString();
    }

    public static boolean handleCommand(CommandSender sender, Command cmd, String commandLabel, String[] args, CommandDispatcher commandDispatcher) {
        Player player = null;
        if(sender instanceof Player) {
            player = (Player) sender;
        }

        String[] remainingArgs;
        String subCommandName;
        if(args.length == 0) {
            subCommandName = "";
            remainingArgs = new String[0];
        } else {
            subCommandName = args[0].toLowerCase();
            remainingArgs = new String[args.length - 1];
            if(args.length > 1) {
                for(int i = 1; i < args.length; ++i) {
                    remainingArgs[i - 1] = args[i].toLowerCase();
                }
            }
        }

        return commandDispatcher.handle(subCommandName, remainingArgs, player);
    }
}
