package be.charybde.bank.command.account;

import be.charybde.bank.entities.Account;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import be.charybde.bank.command.ICommandHandler;
import be.charybde.bank.command.commandUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 21.04.17.
 */
public class NotifSwitchCommand implements ICommandHandler {
    private static ICommandHandler instance = new NotifSwitchCommand();

    private NotifSwitchCommand() {

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

        Map<String, String> message = new HashMap<>();
        message.put("account", account.getDisplayName());
        if(args.length == 1){
            account.setNotif(!account.getNotif());

            if(account.getNotif()){
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notifOn", message), player);
            }
            else {
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notifOff", message), player);
            }
            return true;
        }
        else{
            if(args[1].equals("on")){
                account.setNotif(true);
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notifOn", message), player);
                return true;
            }else if(args[1].equals("off")){
                account.setNotif(false);
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notifOff", message), player);
                return true;
            }
            else
                return false;
        }

    }
}
