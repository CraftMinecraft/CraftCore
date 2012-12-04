package com.craftminecraft.craftsuite.features.network;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.craftminecraft.craftsuite.utils.json.JSONObject;
import com.craftminecraft.craftsuite.utils.json.parser.ParseException;
import com.craftminecraft.craftsuite.utils.json.parser.JSONParser;
import com.craftminecraft.craftsuite.CraftSuitePlugin;

/* Handles all the socket connections from other servers. */
public class ConnectionHandler implements Runnable {

    // Accessible from outside
    private boolean running = false;
    private Map<String,String> settings = null;
    private List<String> toWrite;
    
    // Unaccessible inner variables
    private CraftSuitePlugin plugin;
    private YamlConfiguration config;
    private Socket connection;
    private JSONParser parser;
    
    public ConnectionHandler(Socket socket) {
        connection = socket;
        parser = new JSONParser();
        settings = new HashMap<String,String>();
        toWrite = new ArrayList<String>();
    }
    public void run() {
        BufferedReader in;
        PrintWriter out;

        // Do the login.

        try {
            // Might want to implement my own reader that decyphers everything.
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"), false);

            JSONObject jsobj = (JSONObject)parser.parse(in); // Blocking. Does it matter ?

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
                if (in.ready()) {
                    jsonobj = parser.parse(in);
                    JSONObject map = (JSONObject)jsonobj;

                    /*Bukkit.getScheduler().callSyncMethod(CraftSuitePlugin.getInstance(), new Callable<String> {
                        // Start another event. Use Future and move to main thread
                        // to avoid threading problems. 
                        return "hi";
                    });*/
            
                }


                // Avoids threading problems with the write() function
                synchronized (toWrite) {
                    if (!toWrite.isEmpty()) {
                        for (String item : toWrite) {
                            out.print(item);
                        }
                        toWrite.clear();
                    } 
                }
                // Called even if out is empty. Poorly optimized ? 
                out.flush();
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

    public void write(String message) {
    /*    JSONObject jsonstr = new JSONObject();
        jsonstr.put("opcode", opcode);
        jsonstr.putAll(args);
        Use the parser to check if it's valid. */
        try {
            parser.parse(message);
        } catch (ParseException ex) {
            return;
        }
        synchronized (toWrite) {
            toWrite.add(message);
        }
        return;
    }

    // Theorically, it would be best to ask for the plugin name as well. Hmm...
    // I'd like pluginName to be automatic, but then I'd need to figure the caller out.
    public void write(String opcode, Map<String,String> args) {
        // 
    }

    // @Deprecated
    public Socket getSocket() {
        return connection;
    }

    public Map<String,String> getSettings() {
        return settings;
    }
}
