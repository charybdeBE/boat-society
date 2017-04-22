package be.charybde.bank.command;

import be.charybde.bank.Account;
import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class CreateCommand implements ICommandHandler{
    private static ICommandHandler instance = new CreateCommand();

    private CreateCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }


    //TODO  doublons
    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 1)
            return false;

        if(player != null && !Vault.getPermission().has(player, "bcc.create")){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        Map<String, Object> g =  BCC.getInstance().getStorage().getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(it.getName().equals(args[0])){
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("alreadyexist"), player);
                return true;
            }
        }

        if (args.length == 1){
            new Account(args[0]);
        }
        else {
            ArrayList<String> owners = new ArrayList<>();
            for(int i = 1; i < args.length; ++i)
                owners.add(args[i].toLowerCase());
            new Account(args[0], owners, false);
        }
        Map<String, String> message = new HashMap<>();
        message.put("account", args[0]);
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("create", message), player);

        return true;
    }
}
