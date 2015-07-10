package com.clashwars.clashwars.player;

import com.clashwars.clashwars.ClashWars;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * Manager class to access and manage all CWUsers.
 * When this class is created all users will be loaded in from the database.
 */
public class UserManager {

    private ClashWars cw;

    private HashMap<UUID, CWUser> users = new HashMap<UUID, CWUser>();
    public ResultSet sqlCharacters;

    public UserManager(ClashWars cw) {
        this.cw = cw;
        populate();
    }

    /** Load in all users from the database. */
    private void populate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cw.getSql() != null) {
                    try {
                        Statement statement = cw.getSql().createStatement();
                        sqlCharacters = statement.executeQuery("SELECT char_id,user_id,uuid FROM Characters;");

                        while (sqlCharacters.next()) {
                            if (sqlCharacters.getString("uuid").contains("-")) {
                                UUID uuid = UUID.fromString(sqlCharacters.getString("uuid"));
                                CWUser cwu = getUser(uuid);

                                cwu.setUserID(sqlCharacters.getInt("user_id"));
                                cwu.setCharID(sqlCharacters.getInt("char_id"));
                            }
                        }
                    } catch (SQLException e) {
                        cw.log("Failed to load userdata from MySQL database!");
                    }
                }
            }
        }.runTaskAsynchronously(cw);
    }


    /** Get a CWUser from a OfflinePlayer. It will create a new CWUser if it doesn't exist */
    public CWUser getUser(OfflinePlayer p) {
        return getUser(p.getUniqueId());
    }

    /** Get a CWUser from a player name. It will create a new CWUser if it doesn't exist */
    public CWUser getUser(String name) {
        return getUser(cw.getServer().getOfflinePlayer(name));
    }

    /** Get a CWUser from a player UUID. It will create a new CWUser if it doesn't exist */
    public CWUser getUser(UUID uuid) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            CWUser cwu = new CWUser(uuid);
            users.put(uuid, cwu);
            return cwu;
        }
    }

    /** Get a hashmap with all CWPlayers by their UUID. */
    public HashMap<UUID, CWUser> getUsers() {
        return users;
    }

    /**
     * Get a list of all CWPlayers.
     * If onlineOnly is set to true it will only return users that are online.
     */
    public List<CWUser> getUsers(boolean onlineOnly) {
        List<CWUser> userList = new ArrayList<CWUser>();
        for (CWUser cwp : users.values()) {
            if (cwp.isOnline()) {
                userList.add(cwp);
            }
        }
        return userList;
    }
}
