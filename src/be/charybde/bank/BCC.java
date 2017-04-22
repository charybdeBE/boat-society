package be.charybde.bank;

import be.charybde.bank.command.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class BCC extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final Logger transactionlogger = Logger.getLogger("Transactions");
    private static CommandDispatcher commandDispatcher = new CommandDispatcher();
    private File storageF, languageF;
    private FileConfiguration storage, language;
    public static BCC plugin = null;

    public static BCC getInstance(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        createDir();
        Vault v = new Vault();
        v.setupEconomy();
        v.setupPermissions();
        setupCommands();
        createStorage();
        setupLang();

        try {
            FileHandler fh = new FileHandler("plugins/BCC/transactions.log", true);
            TransferFormatter formatter = new TransferFormatter();
            fh.setFormatter(formatter);
            fh.setLevel(Level.FINEST);
            transactionlogger.addHandler(fh);
            transactionlogger.setLevel(Level.INFO);
            transactionlogger.setParent(logger);
            transactionlogger.setUseParentHandlers(false);
        } catch(IOException ex) {
            log("Failed to create transaction log", Level.INFO);
        }



        log("Enabled", Level.INFO);

    }

    @Override
    public void onDisable(){
        log("Disable", Level.INFO);

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        String commandName = cmd.getName().toLowerCase();
        if(!commandName.equalsIgnoreCase("bank"))
            return true;

        return commandUtil.handleCommand(sender, cmd, commandLabel, args, commandDispatcher);
    }

    private void setupCommands() {
        commandDispatcher.registerHandler("create", CreateCommand.getInstance());
        commandDispatcher.registerHandler("pay", PayCommand.getInstance());
        commandDispatcher.registerHandler("list", ListCommand.getInstance());
        commandDispatcher.registerHandler("withdraw", WithdrawCommand.getInstance());
        commandDispatcher.registerHandler("notification", NotifSwitchCommand.getInstance());
        commandDispatcher.registerHandler("addowner", AddOwnerCommand.getInstance());
        commandDispatcher.registerHandler("removeowner", RemoveOwnerCommand.getInstance());

        commandDispatcher.registerHandler("ajoute", PayCommand.getInstance());
        commandDispatcher.registerHandler("liste", ListCommand.getInstance());
        commandDispatcher.registerHandler("retire", WithdrawCommand.getInstance());
        commandDispatcher.registerHandler("plusutilisateur", AddOwnerCommand.getInstance());
        commandDispatcher.registerHandler("moinsutilisateur", RemoveOwnerCommand.getInstance());
    }


    public static void log(String message, Level level) {
        if(!message.isEmpty())
            logger.log(level,("[Banque Celeste] " + message));
    }

    private void createDir() {
        if(!this.getDataFolder().exists()) {
            if(!this.getDataFolder().mkdir()) {
                log("Could not create plugin folder!", Level.SEVERE);
            }
        }
    }


    private void createStorage() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            storageF = new File(getDataFolder(), "account.yml");
            if (!storageF.exists()) {
                log("account.yml not found, creating!", Level.INFO);
                storageF.getParentFile().mkdirs();
                saveResource("account.yml", false);
            } else {
                log("account.yml  found, loading!", Level.INFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storage = new YamlConfiguration();
        try {
            storage.load(storageF);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getStorage(){
        return storage;
    }

    public FileConfiguration getLang(){
        return language;
    }

    public void setupLang(){
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            languageF = new File(getDataFolder(), "french.yml");
            if (!languageF.exists()) {
                log("french.yml not found, creating!", Level.INFO);
                languageF.getParentFile().mkdirs();
                saveResource("french.yml", false);
            } else {
                log("french.yml  found, loading!", Level.INFO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        language = new YamlConfiguration();
        try {
            language.load(languageF);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveStorage() {
        try {
            storage.save(storageF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class TransferFormatter extends Formatter {
        private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(df.format(new Date(record.getMillis()))).append(" - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }

        @Override
        public String getHead(Handler h) {
            return super.getHead(h);
        }

        @Override
        public String getTail(Handler h) {
            return super.getTail(h);
        }
    }
}