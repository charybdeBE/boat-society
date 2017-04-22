package be.charybde.bank;

import org.bukkit.ChatColor;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by laurent on 20.04.17.
 */
public class Utils {
    private static final Logger transactionlogger = Logger.getLogger("Transactions");


    public static String formatMessage(String path, Map<String, String> replace){
        String bcc = ChatColor.GREEN + "[" + ChatColor.WHITE + "BCC" + ChatColor.GREEN + "] ";
        String toret = (String) BCC.getInstance().getLang().get(path);
        if(replace != null){
            for(Map.Entry<String, String> ent : replace.entrySet()){
                String val = ChatColor.WHITE + ent.getValue() + ChatColor.GREEN;
                toret = toret.replace("\\!"+ent.getKey(), val);
                toret = toret.replace("!"+ent.getKey(), val);
            }
        }
        return bcc + toret;
    }

    public static String formatMessage(String path){
        return formatMessage(path, null);
    }

    public static void logTransaction(String user, String account, String Operation,String amount) {
        String message = "User: " + user + ", Account: " + account + ", Operation: " + Operation + ", Amount : " + amount + "";
        transactionlogger.log(Level.INFO, message);
    }
}
