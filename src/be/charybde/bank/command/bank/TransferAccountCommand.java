package be.charybde.bank.command.bank;

import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import be.charybde.bank.command.ICommandHandler;
import be.charybde.bank.command.commandUtil;
import be.charybde.bank.entities.Account;
import be.charybde.bank.entities.Bank;
import be.charybde.bank.entities.Entities;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TransferAccountCommand implements ICommandHandler {
    private static ICommandHandler instance = new TransferAccountCommand();

    private TransferAccountCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }

// transfert account bank
    public boolean handle(String command, String[] args, Player player) {
        if(args.length < 2)
            return false;

        if(player != null  && !Vault.getPermission().has(player, "bcc.admin")){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notallowed2"), player);
            return true;
        }

        Account subject = Account.fetch(args[0].toLowerCase());
        if(subject == null){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }

        Bank newBank = Bank.fetch(args[1].toLowerCase());
        if(newBank == null){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }

        Bank oldBank = subject.getBank();
        if(oldBank != null){
            if(oldBank.getName().equalsIgnoreCase(newBank.getName())){
                return true; //todo msg
            }
            else {
                oldBank.clientMoney(subject.getBalance() * -1);
            }
        }

        newBank.clientMoney(subject.getBalance());
        subject.setBank(newBank.getName());

        Map<String, String> message = new HashMap<>();
        message.put("account", subject.getDisplayName());
        if(oldBank != null)
            message.put("bank1", oldBank.getName());
        else
            message.put("bank1", "nul part");
        message.put("bank2", newBank.getName());
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("transfert", message), player);
        return true;
    }
}
