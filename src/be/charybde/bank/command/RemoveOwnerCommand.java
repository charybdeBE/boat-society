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
public class RemoveOwnerCommand implements ICommandHandler {
    private static ICommandHandler instance = new RemoveOwnerCommand();

    private RemoveOwnerCommand() {

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
        if(player != null && !Vault.getPermission().has(player, "bcc.admin") && !account.isAllowed(player.getName())){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        for(int i = 1; i < args.length; ++i) {
            account.delOwner(args[i].toLowerCase());
            Map<String, String> message = new HashMap<>();
            message.put("account", account.getDisplayName());
            message.put("person", args[i]);
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("ownerless", message), player);
        }

        return true;
    }
}
