package com.craftminecraft.craftsuite.features;

import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.BindException;
import java.io.File;
//import java.net.InetAddress;

import org.bukkit.event.Listener;
import org.bukkit.configuration.file.YamlConfiguration;

import com.craftminecraft.craftsuite.CraftSuitePlugin;
import com.craftminecraft.craftsuite.utils.ConnectionHandler;

public class SwarmAPIFeature implements Listener {
    private final CraftSuitePlugin plugin;
    private boolean active = false;
    private YamlConfiguration config;
    private ServerRunner server;
    /*
     * The class that runs the SocketServer and accepts connection.
     * On each successful connection, it will spawn a ConnectionHandler thread
     * that will take care of handling the new connection from there.
     */
    private class ServerRunner implements Runnable {
        private ServerSocket server;
        private boolean running = false;
        private CraftSuitePlugin plugin;
        private int port;
        private YamlConfiguration config;
        private ArrayList<ConnectionHandler> connections;
        public ServerRunner(YamlConfiguration config, CraftSuitePlugin plugin) {
            this.port = config.getInt("port",25566);
            this.config = config;
            this.plugin = plugin;
        }

        public void run() {
            try {
                // IOException if unix and bind to port between 1 and 1023 without root
                // BindException if port already in use
                server = new ServerSocket(port);
                running = true;
                while (running) {
                    // IOException if out of ressources. In this case, it should stop the loop, etc.
                    Socket newsocket = server.accept();

                    ConnectionHandler handler = new ConnectionHandler(newsocket, plugin, config);
                    connections.add(handler);

                    // Will this throw ? I have noooo idea. 
                    new Thread(handler).start();
                }
            } catch (BindException ex) {
                CraftSuitePlugin.getInstance().getLogger().severe("Failed to bind to port. Is it already used ?");
            } catch (IOException ex) {
                CraftSuitePlugin.getInstance().getLogger().severe("Could not close connection to server. Please send the following stacktrace on bukkit : ");
                ex.printStackTrace();
            }
        }

        public void stop() {
            running = false;
            for (ConnectionHandler handler : connections) {
                handler.stop();
            }
        }
        
        @SuppressWarnings({ "unused" })
        public synchronized ConnectionHandler getConnection(String name) {
            for (ConnectionHandler handler : connections) {
                if ((String)handler.getSettings().get("name") == name) {
                    return handler;
                }
            }
            return null;
        }
    }

    public SwarmAPIFeature(CraftSuitePlugin plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"core.yml"));
        this.server = new ServerRunner(config,plugin);
        new Thread(this.server);
    }

    public ServerRunner getRunner() {
        return this.server;
    }

    /* Should stop and restart the server */
    public void setActive(final boolean active) {
        if(this.active != active) {
            this.active = active;
            if (active == true) {
            } else {
                this.stop();
            }
        }
    }

    private void stop() {
        this.server.stop();
    }


    // Feature stuff
    // No longer relevant since omc-lib is broken...
    // Keeping it there in anticipation for mosaic.
    /* @Override
    public String getFeatureName(){
        // There ought to be a better way to do this...
        return plugin.getName() + "_swarmapifeature";
    } 
    
    @Override
    public Plugin getFeaturePlugin(){
        return this.plugin;
    }

    @Override
    public SwarmAPIProperties getFeatureProperties(){
        return this.properties;
    }       

    @Override
    public boolean isFeatureActive(){
        return this.active;
    }   

    @Override
    public void setFeatureActive(boolean active){
        this.active = active;
    }*/
}
