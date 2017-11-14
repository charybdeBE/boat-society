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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BankInfoCommand implements ICommandHandler {
    private static ICommandHandler instance = new BankInfoCommand();

    private BankInfoCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }

    @Override
    public boolean handle(String command, String[] args, Player player) {
        //TODO all option
        if(args.length < 1)
            return false;

        Bank subject = Bank.fetchFromPlayer(player.getName().toLowerCase());
        if(subject == null){
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }

        ArrayList<Account> clients = subject.fecthClients();
        String clientList = "";
        for(Account it : clients){
            clientList += it.getDisplayName() + ", ";
        }

        Map<String, String> message = new HashMap<>();
        message.put("name", subject.getName());
        message.put("money", Double.toString(subject.getMoney()));
        message.put("client", clientList);
        commandUtil.sendToPlayerOrConsole(Utils.formatMessage("bankinfo", message), player);

        return true;
    }
}