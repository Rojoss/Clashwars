package com.clashwars.clashwars.config;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;

public class PortalData {
    private String cuboid = "";
    private String target = "";
    private String targetServer = "";

    public PortalData() {
        //--
    }

    public Cuboid getCuboid() {
        return Cuboid.deserialize(cuboid);
    }
    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid.toString();
    }

    public Location getTarget() {
        return CWUtil.locFromStringSimple(target);
    }
    public void setTarget(Location target) {
        this.target = CWUtil.locToStringSimple(target);
    }

    public String getTargetServer() {
        return targetServer;
    }
    public void setTargetServer(String targetServer) {
        this.targetServer = targetServer;
    }
}
