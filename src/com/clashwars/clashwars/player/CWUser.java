package com.clashwars.clashwars.player;

import com.clashwars.clashwars.ClashWars;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * CWUser class to handle all users.
 * Each user/player gets an instance of this class.
 */
public class CWUser {

    private ClashWars cw;

    private UUID uuid;
    private int userID = -1;
    private int charID = -1;

    /** Create a new CWUser instance with the given player UUID. */
    public CWUser(UUID uuid) {
        this.cw = ClashWars.inst();

        this.uuid = uuid;
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCharID() {
        return charID;
    }

    public void setCharID(int charID) {
        this.charID = charID;
    }



    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        return getPlayer() == null ? false : getPlayer().isOnline();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CWUser) {
            CWUser other = (CWUser)obj;

            return other.getUUID().equals(getUUID());
        }
        return false;
    }

}
