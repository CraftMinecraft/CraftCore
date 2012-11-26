package com.craftminecraft.craftsuite;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//import net.obnoxint.mcdev.feature.FeatureManager;
//import net.obnoxint.mcdev.omclib.OmcLibPlugin;

import com.craftminecraft.craftsuite.features.SwarmAPIFeature;

public class CraftSuitePlugin extends JavaPlugin {
//    private OmcLibPlugin omclib;
    private static CraftSuitePlugin plug;
    public static CraftSuitePlugin getInstance() {
        return plug;
    }

    private SwarmAPIFeature api;
    @Override
    public void onEnable() {
        // Setup omclib
        /*omclib = (OmcLibPlugin) getServer().getPluginManager().getPlugin("omc-lib");
        omclib.getFeatureManager().addFeature(new SwarmAPIFeature(this));   // Adds the Swarm api.
        */
        api = new SwarmAPIFeature(this);
        plug = this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
            player.canSee(this.getServer().getPlayer("hi"));
        } else {
            return false;
        }
        // Start the event.
        // 
        return false;
    }

    @Override
    public void onDisable() {
        plug = null;    
    }
}
