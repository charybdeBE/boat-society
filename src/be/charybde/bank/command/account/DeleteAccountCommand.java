package be.charybde.bank.command.account;

import be.charybde.bank.entities.Account;
import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import be.charybde.bank.command.ICommandHandler;
import be.charybde.bank.command.commandUtil;
import be.charybde.bank.entities.Entities;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 39.06.17.
 */
public class DeleteAccountCommand implements ICommandHandler {
    private static ICommandHandler instance = new DeleteAccountCommand();

    private DeleteAccountCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }


    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 1)
            return false;

        if(player != null && !Vault.getPermission().has(player, "bcc.admin")){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.ACCOUNT).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(it.getName().equals(args[0])){
                BCC plugin = BCC.getInstance();
                plugin.getStorage(Entities.ACCOUNT).set(args[0], null);
                plugin.saveStorage(Entities.ACCOUNT);
                Map<String, String> message = new HashMap<>();
                message.put("account", args[0]);
                message.put("money", String.valueOf(it.getBalance()));
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("delete", message), player);
                Utils.logTransaction(player.getName(), args[0], "delete", Double.toString(it.getBalance()), "");
                return true;
            }
        }

        Map<String, String> message = new HashMap<>();
        message.put("account", args[0]);
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound", message), player);

        return true;
    }
}
