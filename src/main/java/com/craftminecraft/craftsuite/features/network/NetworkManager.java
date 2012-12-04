package com.craftminecraft.craftsuite.features.network;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.BindException;
import java.io.File;
//import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.craftminecraft.craftsuite.CraftSuitePlugin;
import com.craftminecraft.craftsuite.features.network.ConnectionHandler;

public class NetworkManager {
    /* Singleton */
    private static NetworkManager netman = null;

    public static NetworkManager getInstance() {
        return netman;
    }
    private static void setInstance(NetworkManager newnetman) {
        netman = newnetman;
    }
    /* Start of actual class */

    private boolean active = false;
    private YamlConfiguration config = null;
    private ServerRunner server;
    /*
     * The class that runs the SocketServer and accepts connection.
     * On each successful connection, it will spawn a ConnectionHandler thread
     * that will take care of handling the new connection from there.
     */
    private class ServerRunner implements Runnable {
        private ServerSocket server;
        private boolean running = false;
        private List<ConnectionHandler> connections;
        public ServerRunner() {
        }

        public void run() {
            connections = new ArrayList<ConnectionHandler>();
            try {
                // IOException if unix and bind to port between 1 and 1023 without root
                // BindException if port already in use
                server = new ServerSocket(NetworkManager.getInstance().getConfig().getInt("port"));
                running = true;
                while (running) {
                    // IOException if out of ressources. In this case, it should stop the loop, etc.
                    Socket newsocket = server.accept();

                    ConnectionHandler handler = new ConnectionHandler(newsocket);
                    connections.add(handler);

                    // Will this throw ? I have noooo idea. 
                    new Thread(handler).start();
                }
            } catch (BindException ex) {
                CraftSuitePlugin.getInstance().getLogger().severe("Failed to bind to port. Is it already used ?");
            } catch (IOException ex) {
                CraftSuitePlugin.getInstance().getLogger().severe("Could not clos connection to server. Please send the following stacktrace on bukkit : ");
                ex.printStackTrace();
            }
        }

        public void stopit() {
            running = false;
            for (ConnectionHandler handler : connections) {
                handler.stop();
            }
            connections.clear();
            connections = null;
        }
        
        @SuppressWarnings({ "unused" })
        public synchronized ConnectionHandler getConnectionByName(String name) {
            for (ConnectionHandler handler : connections) {
                if ((String)handler.getSettings().get("name") == name) {
                    return handler;
                }
            }
            return null;
        }

        public synchronized ConnectionHandler getConnectionByIp(String ip) {
            //throw new NotIm
            return null;
        }

        // NEED TO GET THIS TO RUN IN ANOTHER THREAD IMPERATIVELY
        // DO NOT CALL THIS METHOD FROM MAIN BUKKIT THREAD OR THE
        // SERVER MAY GET INSANE AMOUNTS OF LAG.
        //@SuppressWarnings({"unchecked")}
        public void send(final String opcode, final Map object) {
            
        } 
    }

    public NetworkManager() {
        NetworkManager.setInstance(this);
        this.config = YamlConfiguration.loadConfiguration(new File(CraftSuitePlugin.getInstance().getDataFolder(),"core.yml"));
        this.server = new ServerRunner();
        new Thread(this.server);
    }

    public ServerRunner getRunner() {
        return this.server;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    /* Should stop and restart the server */
    public void setActive(final boolean active) {
        if(this.active != active) {
            this.active = active;
            if (active == true) {
                this.start();
            } else {
                this.stop();
            }
        }
    }

    private void start() {
        new Thread(this.server).start();
    }
    private void stop() {
        this.server.stopit();
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
