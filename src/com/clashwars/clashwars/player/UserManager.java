package com.clashwars.clashwars.player;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.cwcore.utils.CWUtil;
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
                                CWUser cwu = getUser(uuid, false);

                                cwu.setUserID(sqlCharacters.getInt("user_id"));
                                cwu.setCharID(sqlCharacters.getInt("char_id"));
                                users.put(uuid, cwu);
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

    public CWUser getUser(final UUID uuid) {
        return getUser(uuid, true);
    }

    /** Get a CWUser from a player UUID. It will create a new CWUser if it doesn't exist */
    public CWUser getUser(final UUID uuid, boolean checkDatabase) {
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            final CWUser cwu = new CWUser(uuid);
            users.put(uuid, cwu);

            if (checkDatabase) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (cw.getSql() != null) {
                            try {
                                Statement checkStatement = cw.getSql().createStatement();
                                ResultSet character = checkStatement.executeQuery("SELECT char_id,user_id FROM Characters WHERE uuid='" + uuid.toString() + "';");

                                if (character.next()) {
                                    cwu.setUserID(character.getInt("user_id"));
                                    cwu.setCharID(character.getInt("char_id"));
                                } else {
                                    Statement updateStatement = cw.getSql().createStatement();
                                    int added = updateStatement.executeUpdate("INSERT INTO Characters (uuid,username) VALUES ('" + uuid.toString() + "','" + CWUtil.getName(uuid) + "');");
                                    if (added > 0) {
                                        Statement getStatement = cw.getSql().createStatement();
                                        ResultSet newChar = checkStatement.executeQuery("SELECT char_id,user_id FROM Characters WHERE uuid='" + uuid.toString() + "';");

                                        if (newChar.next()) {
                                            cwu.setUserID(newChar.getInt("user_id"));
                                            cwu.setCharID(newChar.getInt("char_id"));
                                        }
                                    } else {
                                        cw.log("Failed to insert user in database!");
                                    }
                                }
                            } catch (SQLException e) {
                                cw.log("Failed to load user ID from database!");
                            }
                        } else {
                            cw.log("Failed to load user ID from database!");
                        }
                    }
                }.runTaskAsynchronously(cw);
            }
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
