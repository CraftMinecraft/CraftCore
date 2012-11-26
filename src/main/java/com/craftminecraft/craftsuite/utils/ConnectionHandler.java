package com.craftminecraft.craftsuite.utils;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.craftminecraft.craftsuite.events.*;
import com.craftminecraft.craftsuite.utils.json.*;
import com.craftminecraft.craftsuite.utils.json.parser.*;
import com.craftminecraft.craftsuite.CraftSuitePlugin;
/* Handles all the socket connections from other servers. */
public class ConnectionHandler implements Runnable {

    // Accessible from outside
    private boolean running = false;
    private Map<String,String> settings = null;
    
    // Unaccessible inner variables
    private CraftSuitePlugin plugin;
    private YamlConfiguration config;
    private Socket connection;
    private JSONParser parser;
    private BufferedReader in;
    private PrintWriter out;
    
    public ConnectionHandler(Socket socket, CraftSuitePlugin plugin, YamlConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        connection = socket;
        parser = new JSONParser();
    }
    public void run() {
        // Do the login.
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"), true);
            String line = in.readLine();    // Blocking
            JSONObject jsobj = (JSONObject)parser.parse(line);
            plugin.getLogger().info((String)jsobj.get("salt"));    //I'll make it actually encrypt using the salt as a key.
            if (config.getConfigurationSection("servers").get("salt") != (String)jsobj.get("salt")) {
                return;
            }
            // Check if logged in, etc...
        } catch (ParseException ex) {
            return;
        } catch (IOException ex) {
            return;
        }

        //Escape the above try because I don't want it to exit the function if subsequent json parsing fails.
        Object jsonobj;
        while (running) {
            try {
                while(running && (jsonobj = parser.parse(in)) != null) {
                    JSONObject map = (JSONObject)jsonobj;
                    
                    // Start another event. Use Future and move to main thread
                    // to avoid threading problems. 
                }
                // Reached EOS
                stop();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }
   
    public void stop() {
        this.running = false;
    }

    @SuppressWarnings({ "unchecked" })
    public synchronized void write(String opcode, Map<String, String> args) {
        JSONObject jsonstr = new JSONObject();
        jsonstr.put("opcode", opcode);
        jsonstr.putAll(args);
        this.out.append(jsonstr.toString());
        this.out.flush();
    }

    // @Deprecated
    public Socket getSocket() {
        return connection;
    }

    public Map<String,String> getSettings() {
        return settings;
    }
}
