package be.charybde.bank;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    public static void logTransaction(String user, String account, String Operation,String amount, String communication) {
        String com = communication.equals("")? "None" : communication;
        String message = "User: " + user + ", Account: " + account + ", Operation: " + Operation + ", Amount : " + amount + ", Communication : " + com;
        transactionlogger.log(Level.INFO, message);
    }

    public static String formatDouble(Double d){
        if(d == 0.0D)
            return "0.00";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat format = new DecimalFormat("#,###.00", symbols);
        String formatted = String.format("%s", format.format(d));
        return formatted;
    }

    public static String formatArray(String[] array){
        return Utils.formatArray(array, 0);
    }

    public static String formatArray(String[] array, int start){
        StringBuilder builder = new StringBuilder();

        for(int i = start; i < array.length; ++i){
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(array[i]);
        }

        String string = builder.toString();
        return string;
    }
}
