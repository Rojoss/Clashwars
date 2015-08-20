package com.clashwars.clashwars.listeners;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.events.PlayerMoveBlockEvent;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class HubEvents implements Listener {

    private ClashWars cw;

    public HubEvents(ClashWars cw) {
        this.cw = cw;
    }


    @EventHandler
    private void move(PlayerMoveBlockEvent event) {
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

        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(velocity));
    }
}
