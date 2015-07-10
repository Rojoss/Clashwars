package com.clashwars.clashwars.commands;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.clashwars.util.Util;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.Enjin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Commands {

    private ClashWars cw;

    private List<UUID> cows = new ArrayList<UUID>();
    private List<String> parkourCompleted = new ArrayList<String>();

    public Commands(ClashWars cw) {
        this.cw = cw;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //============================================================================
        //================================== /cow ====================================
        //============================================================================
        if (label.equalsIgnoreCase("cow")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                player.playSound(player.getLocation(), Sound.COW_HURT, 2, 2 - CWUtil.randomFloat());

                if (!cows.contains(player.getUniqueId())) {
                    cows.add(player.getUniqueId());
                    final Hat prevHat = HatManager.getHat(player);
                    final Hat h = new Hat(player, EntityType.COW);
                    h.equip();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            h.remove();
                            cows.remove(player.getUniqueId());
                            if (prevHat != null) {
                                if (prevHat.getItem() != null) {
                                    Hat newHat = new Hat(player, prevHat.getItem());
                                } else {
                                    Hat newHat = new Hat(player, prevHat.getEntityType());
                                }
                            }
                        }
                    }.runTaskLater(cw, 100);
                }
            }
            return true;
        }


        //============================================================================
        //================================= /crash ===================================
        //============================================================================
        if (label.equalsIgnoreCase("crash")) {
            if (sender instanceof Player) {
                sender.sendMessage(Util.formatMsg("Can only crash the server from the console!"));
                return true;
            }
            cw.getServer().broadcastMessage(CWUtil.integrateColor("&4&lFORCING A SERVER CRASH!!!"));
            cw.getServer().broadcastMessage(CWUtil.integrateColor("&4&lFORCING A SERVER CRASH!!!"));
            cw.getServer().broadcastMessage(CWUtil.integrateColor("&4&lFORCING A SERVER CRASH!!!"));
            List<World> worlds = Bukkit.getWorlds();
            for (World world : worlds) {
                Block block = world.getSpawnLocation().getBlock();
                while (block.getType() != Material.SOUL_SAND) {
                    block = block.getRelative(BlockFace.NORTH);
                }
            }
            return true;
        }


        //============================================================================
        //================================ /parkour ==================================
        //============================================================================
        if (label.equalsIgnoreCase("parkour")) {
            if (!(sender instanceof BlockCommandSender)) {
                return true;
            }
            BlockCommandSender cmdSender = (BlockCommandSender)sender;

            List<Player> nearbyPlayers = CWUtil.getNearbyPlayers(cmdSender.getBlock().getLocation(), 2);
            for (Player player : nearbyPlayers) {
                if (parkourCompleted.contains(player.getName())) {
                    player.sendMessage(CWUtil.integrateColor("&6You already completed the parkour!"));
                    return true;
                }

                cw.getServer().broadcastMessage(CWUtil.integrateColor("&a&l" +player.getName() + " &6completed the lobby parkour!"));
                parkourCompleted.add(player.getName());
            }
            return true;
        }


        //============================================================================
        //============================== /enjinprofile ===============================
        //============================================================================
        if (label.equalsIgnoreCase("enjinprofile")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            JSONObject users = Enjin.getUsers();

            String userID = Enjin.getUserIdByCharacter(player.getName(), true);
            if (userID == null) {
                player.sendMessage(CWUtil.integrateColor("&4&lNOT LINKED&8&l: &4Your character isn't linked with the website."));
                player.sendMessage(CWUtil.integrateColor("&4This can have one of the following reasons:"));
                player.sendMessage(CWUtil.integrateColor("&81. &cYou haven't joined the website."));
                player.sendMessage(CWUtil.integrateColor("    &7Please join the website first: &9http://clashwars.com"));
                player.sendMessage(CWUtil.integrateColor("&82. &cYou don't have this character added to your profile."));
                player.sendMessage(CWUtil.integrateColor("    &7Follow this step by step tutorial! &9http://goo.gl/BrckMP"));
                player.sendMessage(CWUtil.integrateColor("    &7After adding your character also add it to this server!"));
                player.sendMessage(CWUtil.integrateColor("&83. &cSomething went wrong with syncing your account."));
                player.sendMessage(CWUtil.integrateColor("    &7Try again later... :D (Or contact a staff member!)"));
                player.sendMessage(CWUtil.integrateColor("&a&lYou can still play! &7(Some advanced features might be locked)"));
            } else {
                player.sendMessage(CWUtil.integrateColor("&3&lLINKED&8&8l: &aYour character is linked to your website profile!"));
                player.sendMessage(CWUtil.integrateColor("&3Profile&8: &bhttp://clashwars.com/profile/" + userID));
            }

            return true;
        }

        if (label.equalsIgnoreCase("soundtest") || label.equalsIgnoreCase("st")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            if (!player.isOp() && !player.hasPermission("cmd.soundtest")) {
                sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                return true;
            }

            cw.soundMenu.showMenu(player, 0, null);
            return true;
        }


        //============================================================================
        //============= /pe {particle|list} {xo} {yo} {zo} {speed} {amt} =============
        //============================================================================
        if (label.equalsIgnoreCase("pe")) {
            //Console check
            if (!(sender instanceof Player)) {
                sender.sendMessage(CWUtil.formatARMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;

            //Permission check.
            if (!player.isOp() && !player.hasPermission("cwcore.pe")) {
                player.sendMessage(CWUtil.formatARMsg("&cInsuficient permissions."));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                List<String> effectNames = new ArrayList<String>();
                for (ParticleEffect effect : ParticleEffect.values()) {
                    effectNames.add(effect.toString().toLowerCase().replace("_", ""));
                }
                player.sendMessage(CWUtil.formatARMsg("&6&lEffect List&8&l: &7" + CWUtil.implode(effectNames, "&8, &7")));
                return true;
            }

            if (args.length < 6) {
                player.sendMessage(CWUtil.formatARMsg("&cInvalid usage: &4/pe {particle|list} {xo} {yo} {zo} {speed} {amt}"));
                return true;
            }

            ParticleEffect effect = ParticleEffect.fromName(args[0]);
            if (effect == null) {
                player.sendMessage(CWUtil.formatARMsg("&cInvalid effect specified. See &4/pe list &cfor effects."));
                return true;
            }

            float xo = CWUtil.getFloat(args[1]);
            float yo = CWUtil.getFloat(args[2]);
            float zo = CWUtil.getFloat(args[3]);

            float speed = CWUtil.getFloat(args[4]);
            int amt = CWUtil.getInt(args[5]);

            effect.display(xo, yo, zo, speed, amt, player.getLocation());
            return true;
        }


        //============================================================================
        //=================== /sound {sound|list} [volume] [pitch] ===================
        //============================================================================
        if (label.equalsIgnoreCase("sound") || label.equalsIgnoreCase("so")) {
            //Console check
            if (!(sender instanceof Player)) {
                sender.sendMessage(CWUtil.formatARMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player)sender;

            //Permission check.
            if (!player.isOp() && !player.hasPermission("cwcore.sound")) {
                player.sendMessage(CWUtil.formatARMsg("&cInsuficient permissions."));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                List<String> soundNames = new ArrayList<String>();
                for (Sound sound : Sound.values()) {
                    soundNames.add(sound.toString().toLowerCase().replace("_", ""));
                }
                player.sendMessage(CWUtil.formatARMsg("&6&lSound List&8&l: &7" + CWUtil.implode(soundNames, "&8, &7")));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(CWUtil.formatARMsg("&cInvalid usage: &4/sound {sound|list} [volume] [pitch]"));
                return true;
            }

            Sound sound = cw.getCore().getSounds().getSound(args[0]);
            if (sound == null) {
                player.sendMessage(CWUtil.formatARMsg("&cInvalid sound specified. See &4/sound list &cfor all sounds."));
                return true;
            }

            float volume = 1;
            if (args.length > 1) {
                volume = CWUtil.getFloat(args[1]);
                if (volume < 0) {
                    volume = 1;
                }
            }
            final float volumeFinal = volume;

            float pitch = 1;
            if (args.length > 2) {
                pitch = CWUtil.getFloat(args[2]);
                if (pitch < 0) {
                    volume = 1;
                }
            }

            player.playSound(player.getLocation(), sound, volume, pitch);
            return true;
        }


        return false;
    }
}
