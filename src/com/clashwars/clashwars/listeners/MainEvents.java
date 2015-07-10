package com.clashwars.clashwars.listeners;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.clashwars.player.CWUser;
import com.clashwars.clashwars.util.Util;
import com.clashwars.cwcore.mysql.MySQL;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.Enjin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainEvents implements Listener {

    private ClashWars cw;

    public MainEvents(ClashWars cw) {
        this.cw = cw;
    }


    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final CWUser user = cw.um.getUser(player);

        //Sync user/character with database ASYNC
        if (cw.getSql() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Long t = System.currentTimeMillis();
                    try {
                        //Get the enjin ID with the Enjin API.
                        String enjinID = Enjin.getUserIdByCharacter(player.getName(), false);
                        int userID = -1;
                        int charID = -1;
                        if (enjinID == null || enjinID.isEmpty()) {
                            //No website account found (Do nothing but send a message)
                            player.sendMessage(CWUtil.integrateColor("&4&lNOT LINKED&8&l: &4Your character isn't linked with the website."));
                            player.sendMessage(CWUtil.integrateColor("&cCheck out &4/enjinprofile &cfor more information!"));
                        } else {
                            //Create/get the User based on enjin ID. (if user has multiple characters and already registered one there will be an user already,
                            //If the player has one character or multiple and it's the first he joins with it will need to create a new user based on the enjin ID)
                            try {
                                Statement selectUserS = cw.getSql().createStatement();
                                ResultSet userResult = selectUserS.executeQuery("SELECT user_id FROM Users WHERE enjin_id='" + enjinID + "';");

                                if (!userResult.next()) {
                                    //New user
                                    player.sendMessage(CWUtil.integrateColor("&3It seems like this is the first time you join this server!"));
                                    player.sendMessage(CWUtil.integrateColor("&3We have detected your website account and linked this."));
                                    player.sendMessage(CWUtil.integrateColor("&3Please go to &bhttp://clashwars.com/profile/" + enjinID));
                                    player.sendMessage(CWUtil.integrateColor("&3If this isn't your profile please type &b/enjinprofile."));

                                    Statement newUserS = cw.getSql().createStatement();
                                    int added = newUserS.executeUpdate("INSERT INTO Users (enjin_id) VALUES ('" + enjinID + "');");
                                    //Get the new added user ID (don't know a better to get this)
                                    if (added > 0) {
                                        userResult = selectUserS.executeQuery("SELECT user_id FROM Users WHERE enjin_id='" + enjinID + "';");
                                        if (userResult.next()) {
                                            userID = userResult.getInt("user_id");
                                        }
                                    }
                                } else {
                                    //Existing user
                                    userID = userResult.getInt("user_id");
                                }

                            } catch (SQLException e) {
                                cw.log("Failed to retrieve user from database!");
                                e.printStackTrace();
                            }
                        }
                        user.setUserID(userID);

                        //Then we get the character
                        //If the character isn't added, add it. If it is save the id's in CWPLayer and update name etc if needed.
                        Statement selectS = cw.getSql().createStatement();
                        ResultSet charResult = selectS.executeQuery("SELECT char_id,user_id,username,prev_names FROM Characters WHERE uuid='" + player.getUniqueId().toString() + "';");
                        if (!charResult.next()) {
                            //Create new character
                            try {
                                Statement newCharS = cw.getSql().createStatement();
                                int added = 0;
                                if (enjinID != null && !enjinID.isEmpty() && userID >= 1) {
                                    added = newCharS.executeUpdate("INSERT INTO Characters (user_id,uuid,username) VALUES ('" + userID + "','" + player.getUniqueId().toString() + "','" + player.getName() + "');");
                                } else {
                                    added = newCharS.executeUpdate("INSERT INTO Characters (uuid,username) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "');");
                                }

                                if (added > 0) {
                                    charResult = selectS.executeQuery("SELECT char_id FROM Characters WHERE uuid='" + player.getUniqueId().toString() + "';");
                                    if (charResult.next()) {
                                        charID = charResult.getInt("char_id");
                                        user.setCharID(charID);
                                    }
                                }
                            } catch (SQLException e) {
                                cw.log("Failed to insert new character in the database!");
                                e.printStackTrace();
                            }
                        } else {
                            //Existing character
                            user.setCharID(charResult.getInt("char_id"));

                            //Check for username change.
                            if (!player.getName().equals(charResult.getString("username"))) {
                                player.sendMessage(Util.formatMsg("&6We have changed your username from &7&l" + charResult.getString("username") + " &6to &a&l" + player.getName() + "&6!"));
                                String prevNames = charResult.getString("prev_names");
                                if (prevNames == null || prevNames.isEmpty()) {
                                    prevNames = charResult.getString("username");
                                } else {
                                    prevNames += "," + charResult.getString("username");
                                }

                                //Update DB with new username.
                                try {
                                    if (userID > 0) {
                                        PreparedStatement updatePS = cw.getSql().prepareStatement("UPDATE Characters SET last_joined=?,username=?,prev_names=?,user_id=? WHERE char_id=?;");
                                        updatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updatePS.setString(2, player.getName());
                                        updatePS.setString(3, prevNames);
                                        updatePS.setInt(4, userID);
                                        updatePS.setInt(5, charResult.getInt("char_id"));
                                        updatePS.executeUpdate();
                                    } else {
                                        PreparedStatement updatePS = cw.getSql().prepareStatement("UPDATE Characters SET last_joined=?,username=?,prev_names=? WHERE char_id=?;");
                                        updatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updatePS.setString(2, player.getName());
                                        updatePS.setString(3, prevNames);
                                        updatePS.setInt(4, charResult.getInt("char_id"));
                                        updatePS.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    player.sendMessage(Util.formatMsg("&cFailed at updating your username in the database."));
                                    player.sendMessage(Util.formatMsg("&cIf this message keeps apearing please contact a staff member!"));
                                    cw.log("Failed to update username in change in database!");
                                    e.printStackTrace();
                                }
                            } else {
                                //Update just the last joined field and user ID if it's set.
                                try {
                                    if (userID > 0) {
                                        PreparedStatement updateDatePS = cw.getSql().prepareStatement("UPDATE Characters SET last_joined=?, user_id=? WHERE char_id=?;");
                                        updateDatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updateDatePS.setInt(2, userID);
                                        updateDatePS.setInt(3, charResult.getInt("char_id"));
                                        updateDatePS.executeUpdate();
                                    } else {
                                        PreparedStatement updateDatePS = cw.getSql().prepareStatement("UPDATE Characters SET last_joined=? WHERE char_id=?;");
                                        updateDatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updateDatePS.setInt(2, charResult.getInt("char_id"));
                                        updateDatePS.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    cw.log("Failed to update last joined value for player!");
                                }
                            }
                        }
                    } catch(SQLException e) {
                        cw.log("Failed to sync userdata with MySQL database!");
                    }
                }
            }.runTaskAsynchronously(cw);
        }
    }

}
