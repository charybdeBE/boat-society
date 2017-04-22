package be.charybde.bank.command;

import be.charybde.bank.Account;
import be.charybde.bank.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class WithdrawCommand implements ICommandHandler {

    private static ICommandHandler instance = new WithdrawCommand();

    private WithdrawCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }
    @Override
    //TODO error when not exist
    //TODO perm for super op
    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 2)
            return false;
        if(player == null)
            return false;

        Account account = Account.fetch(args[0]);
        if(!account.isAllowed(player.getPlayerListName())){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed"), player);
            return true;
        }

        if(  account.withdraw(Double.parseDouble(args[1]), player.getName())){
            Map<String, String> message = new HashMap<>();
            message.put("money", args[1]);
            message.put("account", args[0]);
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("withdraw", message), player);
            return true;
        }
        else{
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notwithdraw"), player);
            return true;
        }

    }
}
