package com.craftminecraft.craftsuite;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//import net.obnoxint.mcdev.feature.FeatureManager;
//import net.obnoxint.mcdev.omclib.OmcLibPlugin;

import com.craftminecraft.craftsuite.features.network.NetworkManager;

public class CraftSuitePlugin extends JavaPlugin {
    private static CraftSuitePlugin plug;
    public static CraftSuitePlugin getInstance() {
        return plug;
    }

    private NetworkManager api;
    @Override
    public void onEnable() {
        // Setup omclib
        /*omclib = (OmcLibPlugin) getServer().getPluginManager().getPlugin("omc-lib");
        omclib.getFeatureManager().addFeature(new SwarmAPIFeature(this));   // Adds the Swarm api.
        */
        api = new NetworkManager();
        plug = this;
    }

    @Override
    public void onDisable() {
        plug = null;
        api.setActive(false);
    }
}
