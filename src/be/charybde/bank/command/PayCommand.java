package be.charybde.bank.command;

import be.charybde.bank.Account;
import be.charybde.bank.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class PayCommand implements ICommandHandler {

    private static ICommandHandler instance = new PayCommand();

    private PayCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }
    @Override
    //TODO error when not exist
    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 2)
            return false;

        Account account = Account.fetch(args[0]);
        if(account == null) {
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }
        if(  account.pay(Double.parseDouble(args[1]), player.getName())){
            Map<String, String> message = new HashMap<>();
            message.put("money", args[1]);
            message.put("account", args[0]);
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("pay", message), player);
            return true;
        }
        else{
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notpay"), player);
            return true;
        }

    }
}
