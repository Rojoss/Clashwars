package com.clashwars.clashwars.config;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.HashMap;
import java.util.Map;

public class PortalCfg extends EasyConfig {

    public HashMap<String, String> PORTALS = new HashMap<String, String>();

    public PortalCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<String, PortalData> getPortals() {
        Map<String, PortalData> players = new HashMap<String, PortalData>();
        for (String id : PORTALS.keySet()) {
            players.put(id, ClashWars.inst().getGson().fromJson(PORTALS.get(id), PortalData.class));
        }
        return players;
    }

    public PortalData getPortal(String id) {
        if (PORTALS.containsKey(id)) {
            return ClashWars.inst().getGson().fromJson(PORTALS.get(id), PortalData.class);
        }
        return null;
    }

    public void setPortal(String id, PortalData pd) {
        PORTALS.put(id, ClashWars.inst().getGson().toJson(pd, PortalData.class));
        save();
    }

    public void removePortal(String id) {
        PORTALS.remove(id);
        save();
    }

}
