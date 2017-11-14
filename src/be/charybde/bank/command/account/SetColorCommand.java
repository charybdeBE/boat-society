package be.charybde.bank.command.account;

import be.charybde.bank.command.commandUtil;
import be.charybde.bank.entities.Account;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import be.charybde.bank.command.ICommandHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 09.05.17.
 */
public class SetColorCommand implements ICommandHandler {
    private static ICommandHandler instance = new SetColorCommand();
    private SetColorCommand(){}

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


        if(account.setColor(args[1])){
            Map<String, String> message = new HashMap<>();
            message.put("account", account.getDisplayName());
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("color", message), player);
        }
        else {
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("colornotfound"), player);
        }


        return true;
    }
}
