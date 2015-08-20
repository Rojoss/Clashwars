package com.clashwars.clashwars.listeners;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.clashwars.config.PortalData;
import com.clashwars.clashwars.util.Util;
import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.events.PlayerMoveBlockEvent;
import com.clashwars.cwcore.utils.CWUtil;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Map;

public class HubEvents implements Listener {

    private ClashWars cw;

    public HubEvents(ClashWars cw) {
        this.cw = cw;
    }


    @EventHandler
    private void move(PlayerMoveBlockEvent event) {
        Player player = event.getPlayer();

        //Portals
        Map<String, PortalData> portals = cw.portalCfg.getPortals();
        for (PortalData portal : portals.values()) {
            if (!portal.getCuboid().contains(event.getTo())) {
                continue;
            }

            if (portal.getTargetServer().isEmpty()) {
                if (portal.getTarget() != null) {
                    player.teleport(portal.getTarget());
                    break;
                } else {
                    player.sendMessage(Util.formatMsg("&cNo valid destination!"));
                    Location from = event.getFrom().getLocation().add(0.5f, 0.5f, 0.5f);
                    from.setPitch(player.getLocation().getPitch());
                    from.setYaw(player.getLocation().getYaw());
                    break;
                }
            } else {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(portal.getTargetServer());
                player.sendPluginMessage(cw, "BungeeCord", out.toByteArray());
            }
        }

        //JumpPads
        Block block = event.getTo().getRelative(BlockFace.DOWN);
        if (block.getType() != Material.SLIME_BLOCK) {
            return;
        }

        Block signBlock = block.getRelative(BlockFace.DOWN);
        if (signBlock.getType() != Material.SIGN_POST && signBlock.getType() != Material.WALL_SIGN) {
            return;
        }

        Sign sign = (Sign)signBlock.getState();
        if (!CWUtil.removeColour(sign.getLine(0)).equalsIgnoreCase("&5[JumpPad]")) {
            return;
        }

        Vector velocity = CWUtil.vecFromString(sign.getLine(1));
        if (velocity == null || velocity.equals(new Vector(0,0,0))) {
            return;
        }

        player.setVelocity(player.getVelocity().add(velocity));
    }
}
