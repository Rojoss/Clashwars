package com.clashwars.clashwars.commands;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.clashwars.config.PortalData;
import com.clashwars.clashwars.util.Util;
import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.cuboid.Selection;
import com.clashwars.cwcore.cuboid.SelectionStatus;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.Enjin;
import org.bukkit.*;
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
                sender.sendMessage(CWUtil.formatCWMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player) sender;

            //Permission check.
            if (!player.isOp() && !player.hasPermission("cwcore.pe")) {
                player.sendMessage(CWUtil.formatCWMsg("&cInsuficient permissions."));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                List<String> effectNames = new ArrayList<String>();
                for (ParticleEffect effect : ParticleEffect.values()) {
                    effectNames.add(effect.toString().toLowerCase().replace("_", ""));
                }
                player.sendMessage(CWUtil.formatCWMsg("&6&lEffect List&8&l: &7" + CWUtil.implode(effectNames, "&8, &7")));
                return true;
            }

            if (args.length < 6) {
                player.sendMessage(CWUtil.formatCWMsg("&cInvalid usage: &4/pe {particle|list} {xo} {yo} {zo} {speed} {amt}"));
                return true;
            }

            ParticleEffect effect = ParticleEffect.fromName(args[0]);
            if (effect == null) {
                player.sendMessage(CWUtil.formatCWMsg("&cInvalid effect specified. See &4/pe list &cfor effects."));
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
                sender.sendMessage(CWUtil.formatCWMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player)sender;

            //Permission check.
            if (!player.isOp() && !player.hasPermission("cwcore.sound")) {
                player.sendMessage(CWUtil.formatCWMsg("&cInsuficient permissions."));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                List<String> soundNames = new ArrayList<String>();
                for (Sound sound : Sound.values()) {
                    soundNames.add(sound.toString().toLowerCase().replace("_", ""));
                }
                player.sendMessage(CWUtil.formatCWMsg("&6&lSound List&8&l: &7" + CWUtil.implode(soundNames, "&8, &7")));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(CWUtil.formatCWMsg("&cInvalid usage: &4/sound {sound|list} [volume] [pitch]"));
                return true;
            }

            Sound sound = cw.getCore().getSounds().getSound(args[0]);
            if (sound == null) {
                player.sendMessage(CWUtil.formatCWMsg("&cInvalid sound specified. See &4/sound list &cfor all sounds."));
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


        //============================================================================
        //================================= /portal ==================================
        //============================================================================
        if (label.equalsIgnoreCase("portal")) {
            //Console check
            if (!(sender instanceof Player)) {
                sender.sendMessage(CWUtil.formatCWMsg("&cThis is a player command only."));
                return true;
            }
            Player player = (Player)sender;

            //Permission check.
            if (!player.isOp() && !player.hasPermission("cw.portal")) {
                player.sendMessage(CWUtil.formatCWMsg("&cInsuficient permissions."));
                return true;
            }

            //Show help with no args
            if (args.length < 1) {
                showPortalHelp(player);
                return true;
            }


            //List all portals
            if (args[0].equalsIgnoreCase("list")) {
                return true;
            }


            //Show portal information
            if (args[0].equalsIgnoreCase("info")) {
                if (args.length < 2) {
                    player.sendMessage(Util.formatMsg("&cNo portal ID specified! &7/portal info {ID}"));
                    return true;
                }
                PortalData portal = cw.portalCfg.getPortal(args[1]);
                if (portal == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid portal ID specified! &7/portal list for a list of all portals!"));
                    return true;
                }

                Cuboid cuboid = portal.getCuboid();
                player.sendMessage(CWUtil.integrateColor("&8===== &4&lPortal Info &8====="));
                player.sendMessage(CWUtil.integrateColor("&6Name/ID&8: &7" + args[0]));
                player.sendMessage(CWUtil.integrateColor("&6World&8: &7" + cuboid.getWorld().toString()));
                player.sendMessage(CWUtil.integrateColor("&6Location 1&8: &c" + cuboid.getMinX() + "&7,&a" + cuboid.getMinY() + "&7,&9" + cuboid.getMinZ()));
                player.sendMessage(CWUtil.integrateColor("&6Location 2&8: &c" + cuboid.getMaxX() + "&7,&a" + cuboid.getMaxY() + "&7,&9" + cuboid.getMaxZ()));
                if (portal.getTargetServer().isEmpty()) {
                    if (portal.getTarget() != null) {
                        Location target = portal.getTarget();
                        player.sendMessage(CWUtil.integrateColor("&6Target&8: &8(&7LOCATION&8) " + target.getBlockX() + "&7,&a" + target.getBlockY() + "&7,&9" + target.getBlockZ() + " &e" + target.getWorld().toString()));
                    } else {
                        player.sendMessage(CWUtil.integrateColor("&6Target&8: &c&lNo target set!"));
                    }
                } else {
                    player.sendMessage(CWUtil.integrateColor("&6Target&8: &8(&7SERVER&8) &a" + portal.getTargetServer()));
                }
                return true;
            }

            //Create a new portal
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 2) {
                    player.sendMessage(Util.formatMsg("&cNo portal ID specified! &7/portal create {ID}"));
                    return true;
                }
                PortalData portal = cw.portalCfg.getPortal(args[1]);
                if (portal != null) {
                    player.sendMessage(Util.formatMsg("&cInvalid portal ID specified! &7A portal with this ID already exists."));
                    return true;
                }

                Selection selection = cw.getCore().getSel();
                SelectionStatus status = selection.getStatus(player);
                if (status == SelectionStatus.NONE) {
                    player.sendMessage(Util.formatMsg("&cNo cuboid selected! &7Use &c/cww &7to get the wand and select two points."));
                    return true;
                }

                if (status == SelectionStatus.POS2) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7You are missing &cposition 1&7!"));
                    return true;
                }

                if (status == SelectionStatus.POS1) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7You are missing &cposition 2&7!"));
                    return true;
                }

                Cuboid cuboid = selection.getSelection(player);
                if (cuboid == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7Try selecting it again!"));
                    return true;
                }

                portal = new PortalData();
                portal.setCuboid(cuboid);
                cw.portalCfg.setPortal(args[1], portal);
                player.sendMessage(Util.formatMsg("&6&lNew portal created!"));
                player.sendMessage(Util.formatMsg("&7Don't forget to set the portal target!"));
                return true;
            }

            //Set the portal target/destination
            if (args[0].equalsIgnoreCase("target")) {
                if (args.length < 2) {
                    player.sendMessage(Util.formatMsg("&cNo portal ID specified! &7/portal target {ID} [server]"));
                    return true;
                }
                PortalData portal = cw.portalCfg.getPortal(args[1]);
                if (portal == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid portal ID specified! &7/portal list for a list of all portals!"));
                    return true;
                }

                if (args.length > 2) {
                    portal.setTargetServer(args[2]);
                    cw.portalCfg.setPortal(args[1], portal);
                    player.sendMessage(Util.formatMsg("&6&lPortal target set to server &a&l" + args[2] + "&6&l!"));
                } else {
                    portal.setTarget(player.getLocation());
                    cw.portalCfg.setPortal(args[1], portal);
                    player.sendMessage(Util.formatMsg("&6&lPortal target set at your location!"));
                }
                return true;
            }

            //Redefine the portal cuboid
            if (args[0].equalsIgnoreCase("redefine")) {
                if (args.length < 2) {
                    player.sendMessage(Util.formatMsg("&cNo portal ID specified! &7/portal redefine {ID}"));
                    return true;
                }
                PortalData portal = cw.portalCfg.getPortal(args[1]);
                if (portal == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid portal ID specified! &7/portal list for a list of all portals!"));
                    return true;
                }

                Selection selection = cw.getCore().getSel();
                SelectionStatus status = selection.getStatus(player);
                if (status == SelectionStatus.NONE) {
                    player.sendMessage(Util.formatMsg("&cNo cuboid selected! &7Use &c/cww &7to get the wand and select two points."));
                    return true;
                }

                if (status == SelectionStatus.POS2) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7You are missing &cposition 1&7!"));
                    return true;
                }

                if (status == SelectionStatus.POS1) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7You are missing &cposition 2&7!"));
                    return true;
                }

                Cuboid cuboid = selection.getSelection(player);
                if (cuboid == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid cuboid! &7Try selecting it again!"));
                    return true;
                }

                portal.setCuboid(cuboid);
                cw.portalCfg.setPortal(args[1], portal);
                player.sendMessage(Util.formatMsg("&6&lPortal cuboid redefined!"));
                return true;
            }

            //Remove the portal
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
                if (args.length < 2) {
                    player.sendMessage(Util.formatMsg("&cNo portal ID specified! &7/portal remove {ID}"));
                    return true;
                }
                PortalData portal = cw.portalCfg.getPortal(args[1]);
                if (portal == null) {
                    player.sendMessage(Util.formatMsg("&cInvalid portal ID specified! &7/portal list for a list of all portals!"));
                    return true;
                }

                cw.portalCfg.removePortal(args[1]);
                player.sendMessage(Util.formatMsg("&6&lPortal removed!"));
                return true;
            }

            showPortalHelp(player);
            return true;
        }


        return false;
    }

    private void showPortalHelp(Player player) {
        player.sendMessage(CWUtil.integrateColor("&8===== &4&lPortal Commands &8====="));
        player.sendMessage(CWUtil.integrateColor("&6/portal list [page] &8- &5List all the portals"));
        player.sendMessage(CWUtil.integrateColor("&6/portal info {ID} &8- &5Show portal info."));
        player.sendMessage(CWUtil.integrateColor("&6/portal create {ID} &8- &5Create a new portal"));
        player.sendMessage(CWUtil.integrateColor("&6/portal target {ID} [server] &8- &5Set the portal target"));
        player.sendMessage(CWUtil.integrateColor("&6/portal redefine {ID} &8- &5Redefine the portal cuboid"));
        player.sendMessage(CWUtil.integrateColor("&6/portal remove {ID} &8- &5Remove the portal"));
    }
}
