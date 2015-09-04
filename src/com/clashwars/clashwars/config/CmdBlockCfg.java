package com.clashwars.clashwars.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdBlockCfg extends EasyConfig {

    public List<CmdBlockData> CMD_BLOCKS = new ArrayList<CmdBlockData>();

    public CmdBlockCfg(String fileName) {
        this.setFile(fileName);
    }

    public String getCmd(Block block) {
        for (CmdBlockData data : CMD_BLOCKS) {
            if (data.getBlock().getLocation().equals(block.getLocation())) {
                return data.getCmd();
            }
        }
        return "";
    }

    public void setCmdBlock(Block block, String cmd) {
        for (CmdBlockData data : CMD_BLOCKS) {
            if (data.getBlock().getLocation().equals(block.getLocation())) {
                CMD_BLOCKS.remove(data);
                break;
            }
        }
        CmdBlockData data = new CmdBlockData();
        data.setBlock(block);
        data.setCmd(cmd);
        CMD_BLOCKS.add(data);
        save();
    }

    public void removeCmdBlock(Block block) {
        for (CmdBlockData data : CMD_BLOCKS) {
            if (data.getBlock().getLocation().equals(block.getLocation())) {
                CMD_BLOCKS.remove(data);
                save();
                return;
            }
        }
    }

}
