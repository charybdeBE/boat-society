package be.charybde.bank.command.bank;

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
public class CreateBankCommand implements ICommandHandler {
    private static ICommandHandler instance = new CreateBankCommand();

    private CreateBankCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }


    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 2)
            return false;

        if(player != null  && !Vault.getPermission().has(player, "bcc.admin")){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.BANK).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Bank it = Bank.fetch(e.getKey());
            if(it.getName().equals(args[0])){
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("alreadyexist"), player);
                return true;
            }
        }

        new Bank(args[0], args[1].toLowerCase(), 0.0D, true);

            //TODO MSG
        Map<String, String> message = new HashMap<>();
        message.put("account", args[0]);
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("create", message), player);
        Utils.logTransaction(player.getName(), args[0], "bank-create", "", "");

        return true;
    }
}
