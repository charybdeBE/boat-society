package be.charybde.bank.command;

import be.charybde.bank.Account;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class AddOwnerCommand implements ICommandHandler {
    private static ICommandHandler instance = new AddOwnerCommand();

    private AddOwnerCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }

    @Override
    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 1)
            return false;

        Account account = Account.fetch(args[0]);
        if(account == null) {
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }
        if(player != null && !Vault.getPermission().has(player, "bcc.create") && !account.isAllowed(player.getName())){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        for(int i = 1; i < args.length; ++i) {
            account.addOwner(args[i].toLowerCase());
            Map<String, String> message = new HashMap<>();
            message.put("account", args[0]);
            message.put("person", args[i]);
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("owner", message), player);
        }

        return true;
    }
}
