package be.charybde.bank.entities;

public interface Entity {
    static Entity fetch(String name){
        return null;
    }
    void save();
    String getName();

}
