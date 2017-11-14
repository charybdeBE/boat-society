package be.charybde.bank.entities;

import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laurent on 19.04.17.
 */
public class Account implements Entity {
    private String name;
    private String surname;
    private String bankName;
    private ArrayList<String> authorizedPlayer;
    private Boolean notif;
    private String color;



//Kept for legacy section
    public Account(String n, ArrayList<String> auth, boolean notif, boolean save){
        this(n, auth, notif, null, "BCC", save);
    }

    public Account(String n, ArrayList<String> auth, boolean notif){
        this(n, auth, notif, true);
    }

    public Account(String n){
        this(n, new ArrayList<String>(), false, true);
    }

    //end section
    public Account(String name, ArrayList<String> auth, Boolean notif, String color, String bankName, boolean save) {
        this.name = "_bank_"+name;
        surname = name;
        authorizedPlayer = auth;
        this.notif = notif;
        this.color = color;
        this.bankName = bankName;
        if(save) {
            this.save();
            Vault.getEconomy().createPlayerAccount(name);
            double xx = Vault.getEconomy().getBalance(name);
            Vault.getEconomy().withdrawPlayer(name, xx);
        }
    }

    public static Account fetch(String name){
        BCC plugin = BCC.getInstance();
        ArrayList<String> auth = (ArrayList<String>) plugin.getStorage(Entities.ACCOUNT).get(name+".owners", null);
        Boolean notif = (Boolean) plugin.getStorage(Entities.ACCOUNT).get(name+".notifications", null);
        String color = (String) plugin.getStorage(Entities.ACCOUNT).get(name+".color", null);
        String bankName = (String) plugin.getStorage(Entities.ACCOUNT).get(name+".bank", null);
        if(auth == null || notif == null)
            return null;
        return new Account(name, auth, notif, color, bankName, false);
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
        Bank.fetch(this.bankName).clientMoney(amount);
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
            Bank.fetch(this.bankName).clientMoney(amount * -1);
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
        plugin.getStorage(Entities.ACCOUNT).set(this.surname+".owners", this.authorizedPlayer);
        plugin.getStorage(Entities.ACCOUNT).set(this.surname+".notifications", this.notif);
        plugin.getStorage(Entities.ACCOUNT).set(this.surname+".color", this.color);
        plugin.getStorage(Entities.ACCOUNT).set(this.surname+".bank", this.bankName);
        plugin.saveStorage(Entities.ACCOUNT);
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

    public void setBank(String s){
        this.bankName = s;
        this.save();
    }

    public Bank getBank() {
        return Bank.fetch(this.bankName);
    }
}
