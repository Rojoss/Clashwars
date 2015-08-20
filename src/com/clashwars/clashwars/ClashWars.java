package com.clashwars.clashwars;

import com.clashwars.clashwars.commands.Commands;
import com.clashwars.clashwars.config.PluginCfg;
import com.clashwars.clashwars.listeners.HubEvents;
import com.clashwars.clashwars.listeners.MainEvents;
import com.clashwars.clashwars.player.UserManager;
import com.clashwars.clashwars.util.SoundMenu;
import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.mysql.MySQL;
import com.google.gson.Gson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.logging.Logger;

public class ClashWars extends JavaPlugin {

    private static ClashWars instance;
    private CWCore cwcore;
    private Gson gson = new Gson();

    private Commands cmds;

    private MySQL sql;
    private Connection c;

    public PluginCfg pluginCfg;

    public UserManager um;

    public SoundMenu soundMenu;

    private final Logger log = Logger.getLogger("Minecraft");


    @Override
    public void onDisable() {
        log("disabled");
    }

    @Override
    public void onEnable() {
        Long t = System.currentTimeMillis();
        instance = this;

        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cwcore = (CWCore)plugin;

        pluginCfg = new PluginCfg("plugins/ClashWars/ClashWars.yml");
        pluginCfg.load();

        sql = new MySQL(this, "37.26.106.5", "3306", "clashwar_data", "clashwar_main", pluginCfg.SQL__PASS);
        try {
            c = sql.openConnection();
        } catch(Exception e) {
            log("##############################################################");
            log("Unable to connect to MySQL!");
            log("Stats and all other data won't be synced/stored!");
            log("The plugin should still be able to run fine but this message shouldn't be ignored!");
            log("##############################################################");
        }

        soundMenu = new SoundMenu();

        um = new UserManager(this);

        registerEvents();

        cmds = new Commands(this);

        log("loaded successfully");
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);
        pm.registerEvents(new HubEvents(this), this);
        pm.registerEvents(soundMenu, this);
    }


    public void log(Object msg) {
        log.info("[ClashWars " + getDescription().getVersion() + "] " + msg.toString());
    }

    public void logError(Object msg) {
        log.severe("[ClashWars " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static ClashWars inst() {
        return instance;
    }

    public CWCore getCore() {
        return cwcore;
    }




    public Gson getGson() {
        return gson;
    }

    public Connection getSql() {
        return c;
    }

}
