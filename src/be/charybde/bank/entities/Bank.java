package be.charybde.bank.entities;

import be.charybde.bank.BCC;
import be.charybde.bank.Utils;
import be.charybde.bank.Vault;

import java.util.ArrayList;
import java.util.Map;


//Note client account not computyed by default
public class Bank implements Entity {

    private String name;
    private double money;
    private String owner;
    private ArrayList<String> employees;

    // Constructor/fetcher

    public Bank(String _name, String _owner, double money, boolean save){
        this.name = _name;
        this.owner = _owner;
        this.employees = new ArrayList<>();
        this.employees.add(owner);
        this.money = money;
        if(save)
            this.save();
    }

    public static Bank fetch(String name){
        BCC plugin = BCC.getInstance();
        ArrayList<String> auth = (ArrayList<String>) plugin.getStorage(Entities.BANK).get(name+".employees", null); //To be in use
        String owner = (String) plugin.getStorage(Entities.BANK).get(name+".owner", null);
        Double money = (Double) plugin.getStorage(Entities.BANK).get(name+".money", null);
        if(owner == null)
            return null;
        return new Bank(name, owner, money, false);
    }

 /*   public static Map<Bank> fecthAll(){
        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.ACCOUNT).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(it.getName().equals(args[0])){
                commandUtil.sendToPlayerOrConsole(Utils.formatMessage("alreadyexist"), player);
                return true;
            }
        }

    }*/

    public static Bank fetchFromPlayer(String player){
        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.BANK).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Bank it = Bank.fetch(e.getKey());
            if(it.hasEmployee(player)){
                return it;
            }
        }
        return null;
    }


    // Public section

    public void clientMoney(double change){
        this.money += change;
        this.save();
    }

    public boolean withdraw(String player, double amount, String reason){
        if(amount > 0.0D && this.money >= amount){
            Vault.getEconomy().depositPlayer(player, amount);
            this.money -= amount;
            this.save();
            Utils.logTransaction(player, this.name, "Bank-withdraw", Double.toString(amount), reason);
            return true;
        }

        return false;
    }

    public boolean pay(String player, double amount, String reason){
        if(amount > 0.0D && Vault.getEconomy().getBalance(player) >= amount){
            Vault.getEconomy().withdrawPlayer(player, amount);
            this.money += amount;
            this.save();
            Utils.logTransaction(player, this.name, "Bank-depot", Double.toString(amount), reason);
            return true;
        }

        return false;
    }



    @Override
    public void save() {
        BCC plugin = BCC.getInstance();
        plugin.getStorage(Entities.BANK).set(this.name+".owner", this.owner);
        plugin.getStorage(Entities.BANK).set(this.name+".employees", this.employees);
        plugin.getStorage(Entities.BANK).set(this.name+".money", this.money);

        plugin.saveStorage(Entities.BANK);
    }

    public boolean hasEmployee(String player){
        return this.employees.contains(player);
    }


    public String getName(){
        return this.name;
    }

    public double getMoney(){
        return this.money;
    }

    public ArrayList<Account> fecthClients(){
        ArrayList<Account> result = new ArrayList<>();
        Map<String, Object> g =  BCC.getInstance().getStorage(Entities.ACCOUNT).getValues(false);
        for(Map.Entry<String, Object> e : g.entrySet()){
            Account it = Account.fetch(e.getKey());
            if(it != null){
                Bank b = it.getBank();
                if(b != null && b.getName().equals(this.name)){
                    result.add(it);
                }
            }
        }
        return result;
    }
}
