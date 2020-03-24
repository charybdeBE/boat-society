package be.charybde.boat;

import be.charybde.boat.command.*;
import be.charybde.boat.listener.MailBox;
import be.charybde.boat.listener.SealBook;
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

//TODO logs ?

public class Main extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final Logger transactionlogger = Logger.getLogger("Transactions");
    private static CommandDispatcher commandDispatcher = new CommandDispatcher();
    private File languageF;
    private FileConfiguration language;
    public static Main plugin = null;

    public static Main getInstance(){
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


        getServer().getPluginManager().registerEvents(new MailBox(), this);
        getServer().getPluginManager().registerEvents(new SealBook(), this);
        log("Enabled", Level.INFO);

    }

    @Override
    public void onDisable(){
        log("Disable", Level.INFO);

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
        String commandName = cmd.getName().toLowerCase();
        if(!commandName.equalsIgnoreCase("b"))
            return true;

        if(args.length == 0)
            args = new String[]{"list"};

        return commandUtil.handleCommand(sender, cmd, commandLabel, args, commandDispatcher);
    }

    private void setupCommands() {
//        commandDispatcher.registerHandler("create", CreateCommand.getInstance());
//        commandDispatcher.registerHandler("flush", FlushCommand.getInstance());
    }


    public static void log(String message, Level level) {
        if(!message.isEmpty())
            logger.log(level,("[BOAT] " + message));
    }

    private void createDir() {
        if(!this.getDataFolder().exists()) {
            if(!this.getDataFolder().mkdir()) {
                log("Could not create plugin folder!", Level.SEVERE);
            }
        }
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