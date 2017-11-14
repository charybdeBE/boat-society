package be.charybde.bank.command.bank;

import be.charybde.bank.Utils;
import be.charybde.bank.command.ICommandHandler;
import be.charybde.bank.command.commandUtil;
import be.charybde.bank.entities.Bank;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PayToBankCommand implements ICommandHandler {
    private static ICommandHandler instance = new PayToBankCommand();

    private PayToBankCommand() {

    }

    public static ICommandHandler getInstance() {
        return instance;
    }

    @Override
    public boolean handle(String command, String[] args, Player player) {
        if (args.length < 2)
            return false;

        Bank subject = Bank.fetchFromPlayer(player.getName().toLowerCase());
        if (subject == null) {
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notfound"), player);
            return true;
        }

        String reason = "";
        reason = Utils.formatArray(args, 1);

        try {
            Double.parseDouble(args[0]);
        } catch (Exception e) {
            return false;
        }

        boolean result = subject.pay(player.getName(), Double.parseDouble(args[0]), reason);
        if (result) {
            Map<String, String> message = new HashMap<>();
            message.put("bank", subject.getName());
            message.put("money", args[0]);
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("bankpay", message), player);
        } else {
            commandUtil.sendToPlayerOrConsole(Utils.formatMessage("notpay"), player);
        }

        return true;
    }
}