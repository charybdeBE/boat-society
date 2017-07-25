package be.charybde.bank;

import be.charybde.bank.command.commandUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class Account {
    private String name;
    private String surname;
    private ArrayList<String> authorizedPlayer;
    private Boolean notif;
    private String color;

    public Account(String n, ArrayList<String> auth, boolean notif, boolean save){
        name = "_bank_"+n;
        surname = n;
        authorizedPlayer = auth;
        this.notif = notif;
        if(save) {
            this.save();
            Vault.getEconomy().createPlayerAccount(name);
            double xx = Vault.getEconomy().getBalance(name);
            Vault.getEconomy().withdrawPlayer(name, xx);

        }

    }

    public Account(String n, ArrayList<String> auth, boolean notif){
        this(n, auth, notif, true);
    }

    public Account(String n){
        this(n, new ArrayList<String>(), false, true);
    }

    public Account(String name, ArrayList<String> auth, Boolean notif, String color, boolean b) {
        this(name, auth, notif, b);
        this.color = color;
    }

    public static Account fetch(String name){
        BCC plugin = BCC.getInstance();
        ArrayList<String> auth = (ArrayList<String>) plugin.getStorage().get(name+".owners", null);
        Boolean notif = (Boolean) plugin.getStorage().get(name+".notifications", null);
        String color = (String) plugin.getStorage().get(name+".color", null);
        if(auth == null || notif == null)
            return null;
        return new Account(name, auth, notif, color, false);
    }

    public boolean addOwner(String auth){
        authorizedPlayer.add(auth.toLowerCase());
        this.save();
        return  true;
    }

    public boolean pay(double amount, String player) {
        return this.pay(amount, player, "");
    }

    public boolean pay(double amount, String player, String communication){
        if(amount < 0.0D || Vault.getEconomy().getBalance(player) < amount)
            return false;
        Vault.getEconomy().depositPlayer(this.name, amount);
        Vault.getEconomy().withdrawPlayer(player, amount);
        if(notif){
            Map<String, String> message = new HashMap<>();
            message.put("account", this.getDisplayName());
            message.put("money", Utils.formatDouble(amount));
            message.put("person", player);
            if(!communication.equals("")){
                communication = ChatColor.GREEN + "(" + ChatColor.WHITE + communication + ChatColor.GREEN + ")";
            }
            message.put("motif", communication);
            sendNotification(Utils.formatMessage("notiftextIn", message));
        }
        Utils.logTransaction(player, this.surname, "pay", Double.toString(amount), communication);
        return true;
    }

    //Nb the check for player auth should have been done before
    public boolean withdraw(double amount, String player) {
        return this.withdraw(amount, player, "");
    }

    public boolean withdraw(double amount, String player , String communication){
        if(amount > 0.0D && this.getBalance() >= amount){
            Vault.getEconomy().depositPlayer(player, amount);
            Vault.getEconomy().withdrawPlayer(this.name, amount);
            if(notif){
                Map<String, String> message = new HashMap<>();
                message.put("account", this.getDisplayName());
                message.put("money", Utils.formatDouble(amount));
                message.put("person", player);
                if(!communication.equals("")){
                    communication = ChatColor.GREEN + "(" + ChatColor.WHITE + communication + ChatColor.GREEN + ")";
                }
                message.put("motif", communication);
                sendNotification(Utils.formatMessage("notiftextOut", message));
            }
            Utils.logTransaction(player, this.surname, "withdraw", Double.toString(amount), communication);
            return true;
        }

        return false;
    }

    public boolean isAllowed(String player) {
        return this.authorizedPlayer.contains(player.toLowerCase());
    }

    public double getBalance(){
        return Vault.getEconomy().getBalance(this.name);
    }



    public void save(){
        BCC plugin = BCC.getInstance();
        plugin.getStorage().set(this.surname+".owners", this.authorizedPlayer);
        plugin.getStorage().set(this.surname+".notifications", this.notif);
        plugin.getStorage().set(this.surname+".color", this.color);
        plugin.saveStorage();
    }

    public String getName() {
        return this.surname;
    }

    public void setNotif(boolean what){
        notif = what;
        this.save();
    }

    public Boolean getNotif() {
        return notif;
    }


    public boolean setColor(String c){
        if(c.startsWith("&")){
            String newS = ChatColor.translateAlternateColorCodes('&', c);
            this.color = newS.substring(1);
        }
        else{
            try {
                ChatColor cc = ChatColor.valueOf(c.toUpperCase());
                System.out.println(cc);
                this.color = String.valueOf(cc.getChar());
            }catch (IllegalArgumentException e){
                return false;
            }
        }
        this.save();
        return true;
    }

    private void sendNotification(String s){
        PluginCommand mail = Bukkit.getPluginCommand("mail");
        String mail_args[] = new String[3];
        mail_args[0] = "send";
        mail_args[1] = "";
        mail_args[2] = s;
        for(String own : this.authorizedPlayer){
            mail_args[1] = own;
            mail.getExecutor().onCommand(Bukkit.getConsoleSender(), mail, "mail", mail_args);
        }
    }

    public String getDisplayName(){
        if(this.color != null){
            return ChatColor.getByChar(this.color) + "" + this.surname + ChatColor.GREEN;
        }
        else
            return this.surname;
    }

    public void delOwner(String s) {
        this.authorizedPlayer.remove(s);
        this.save();
    }
}
