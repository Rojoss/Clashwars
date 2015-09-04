package com.clashwars.clashwars.config;

import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.block.Block;

public class CmdBlockData {

    private String block = "";
    private String cmd = "";

    public CmdBlockData() {
        //--
    }

    public Block getBlock() {
        return CWUtil.locFromStringSimple(block).getBlock();
    }
    public void setBlock(Block block) {
        this.block = CWUtil.locToStringSimple(block.getLocation());
    }

    public String getCmd() {
        return cmd;
    }
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

}
