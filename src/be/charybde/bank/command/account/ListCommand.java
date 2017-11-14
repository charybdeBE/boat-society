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
import java.util.Objects;

/**
 * Created by laurent on 20.04.17.
 */
public class ListCommand implements ICommandHandler {
    private static ICommandHandler instance = new ListCommand();

    private ListCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }

    @Override
    public boolean handle(String command, String[] args, Player player) {
        boolean all = false;
        if(player != null && args.length == 1 && args[0].equals("all")){
            if(Vault.getPermission().has(player, "bcc.list"))
                all = true;
            else{
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
                return true;
            }
        }
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("list"), player);
        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.ACCOUNT).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(all || player == null || it.isAllowed(player.getName())){
                Map<String, String> message = new HashMap<>();
                message.put("account", it.getDisplayName());
                message.put("money", Utils.formatDouble(it.getBalance()));
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("balance", message), player);

            }
        }

        return true;
    }
}