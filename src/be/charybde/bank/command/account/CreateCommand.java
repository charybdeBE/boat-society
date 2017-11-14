package be.charybde.bank.command.account;

import be.charybde.bank.command.commandUtil;
import be.charybde.bank.entities.Account;
import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import be.charybde.bank.command.ICommandHandler;
import be.charybde.bank.entities.Bank;
import be.charybde.bank.entities.Entities;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class CreateCommand implements ICommandHandler {
    private static ICommandHandler instance = new CreateCommand();

    private CreateCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }


    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 1)
            return false;

        if(player != null && !Vault.getPermission().has(player, "bcc.create")  && !Vault.getPermission().has(player, "bcc.admin")){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.ACCOUNT).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(it.getName().equals(args[0])){
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("alreadyexist"), player);
                return true;
            }
        }

        Bank organisation = Bank.fetchFromPlayer(player.getName().toLowerCase());
        String banker = "bcc";
        if(organisation != null){
            banker = organisation.getName();
        }

        if (args.length == 1){
            new Account(args[0], new ArrayList<>(), false, null, banker, true);
        }
        else {
            ArrayList<String> owners = new ArrayList<>();
            for(int i = 1; i < args.length; ++i)
                owners.add(args[i].toLowerCase());
            new Account(args[0], owners, false, null, banker, true);
        }
        Map<String, String> message = new HashMap<>();
        message.put("account", args[0]);
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("create", message), player);
        Utils.logTransaction(player.getName(), args[0], "create", "", "");

        return true;
    }
}
