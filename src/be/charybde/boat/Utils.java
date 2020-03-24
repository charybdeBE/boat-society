package be.charybde.boat;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by laurent on 20.04.17.
 */
public class Utils {
    private static final Logger transactionlogger = Logger.getLogger("Transactions");


    public static String formatMessage(String path, Map<String, String> replace) {
        String bcc = ChatColor.GREEN + "[" + ChatColor.WHITE + "BOAT" + ChatColor.GREEN + "] ";
        String toret = (String) Main.getInstance().getLang().get(path);
        if (replace != null) {
            for (Map.Entry<String, String> ent : replace.entrySet()) {
                try {
                    String val = ChatColor.WHITE + ent.getValue() + ChatColor.GREEN;
                    toret = toret.replace("\\!" + ent.getKey(), val);
                    toret = toret.replace("!" + ent.getKey(), val);
                } catch (Exception e) {
                    System.out.println("error in sending message");
                }

            }
        }
        return bcc + toret;
    }

    public static String formatMessage(String path) {
        return formatMessage(path, null);
    }

    public static void logTransaction(String user, String account, String Operation, String amount, String communication) {
        String com = communication.equals("") ? "None" : communication;
        String message = "User: " + user + ", Account: " + account + ", Operation: " + Operation + ", Amount : " + amount + ", Communication : " + com;
        transactionlogger.log(Level.INFO, message);
    }

    public static String formatDouble(Double d) {
        if (d == 0.0D)
            return "0.00";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        DecimalFormat format = new DecimalFormat("#,###.00", symbols);
        String formatted = String.format("%s", format.format(d));
        return formatted;
    }

    public static String formatArray(String[] array) {
        return Utils.formatArray(array, 0);
    }

    public static String formatArray(String[] array, int start) {
        StringBuilder builder = new StringBuilder();

        for (int i = start; i < array.length; ++i) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(array[i]);
        }

        String string = builder.toString();
        return string;
    }

    public static boolean isWallSign(Material m) {
        Material[] wallSign = {Material.ACACIA_WALL_SIGN,
                Material.SPRUCE_WALL_SIGN,
                Material.BIRCH_WALL_SIGN,
                Material.OAK_WALL_SIGN,
                Material.JUNGLE_WALL_SIGN,
                Material.DARK_OAK_WALL_SIGN};
        return Arrays.stream(wallSign).anyMatch(ws -> ws == m);
    }

    public static int countNumberOfItem(Inventory inventory, Material item) {
        int res = 0;
        for (ItemStack stackk : inventory) {
            if (stackk != null && stackk.getType() == item) {
                res += stackk.getAmount();
            }
        }
        return res;
    }

    public static Inventory removeNumberFromInventory(Inventory inventory, Material item, int count) {
        int reminder = count;
        for (ItemStack stackk : inventory) {
            if (stackk != null && stackk.getType() == item) {
                if (reminder > stackk.getAmount()) {
                    reminder -= stackk.getAmount();
                    stackk.setAmount(0);
                } else if (reminder > 0) {
                    stackk.setAmount(stackk.getAmount() - reminder);
                    reminder = 0;
                }
            }
        }
        return inventory;
    }

    public static void serializeInventory(String name, Inventory inventory) {
        try {
            String filename = name;
            filename += ".yml";
            File file = new File(Main.getInstance().getDataFolder(), filename);
            YamlConfiguration serial = new YamlConfiguration();
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            } else {
                serial.load(file);
            }
            try {
                serial.set("name", name);
                serial.set("inventory", inventory.getStorageContents());
                serial.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Inventory deserializeInventory(String fn) {
        try {
            String filename = fn;
            filename += ".yml";
            File file = new File(Main.getInstance().getDataFolder(), filename);
            YamlConfiguration serial = new YamlConfiguration();
            if (!file.exists()) {
                return null;
            }
            serial.load(file);
            String name = (String) serial.get("name", "");
            ArrayList<ItemStack> is = (ArrayList<ItemStack>) serial.get("inventory");
            Inventory inv = Bukkit.createInventory(null, 9, "§6"+name);
            for (int i = 0; i < is.size(); i++) {
                inv.setItem(i, is.get(i));
            }
            return inv;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
