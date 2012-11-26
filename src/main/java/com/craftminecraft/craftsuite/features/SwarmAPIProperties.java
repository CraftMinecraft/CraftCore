// WILL NOT USE THIS UNTIL MOSAIC COMES OUT.

package com.craftminecraft.craftsuite.features;

import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

/*import net.obnoxint.mcdev.feature.FeatureProperties;
import net.obnoxint.mcdev.feature.event.FeaturePropertiesLoadedEvent;
import net.obnoxint.mcdev.feature.event.FeaturePropertiesStoredEvent;*/

// Since omc-lib is broken ATM, I'll just put all the FeatureProperties funcs here..
public class SwarmAPIProperties /*extends FeatureProperties*/ {
    protected static final String PROPERTY_FILE_EXTENSION = ".yml";
    private static final String PROPERTY_NAME_PORT = "port";
    private static final String PROPERTY_NAME_SALT = "salt";

    // hopefully omc-lib fixes this
    private String comment = null;
    private boolean dirty = false;
    private File propertiesDirectory = null;
    private File propertiesFile = null;
    
    private int port = 25888;
    private String salt = "anormalsalt";
    
    private YamlConfiguration config = new YamlConfiguration(); 

    public SwarmAPIProperties(final Plugin plugin) {
        //super(feature);
        
    }

    // ALL THE FEATUREPROPERTIES FUNCTIONS HERE
    public final boolean isDirty() {
        return dirty;
    }

    public boolean store() {
        boolean r = false;
        if (isDirty()) {
            try {
                onStore();
                final FileWriter fw = new FileWriter(getPropertiesFile());
                fw.write(getConfig().saveToString());
                fw.flush();
                fw.close();
                setDirty(false);
                onStored();
                //Bukkit.getPluginManager().callEvent(new FeaturePropertiesStoredEvent(feature));
                r = true;
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return r;
    }

    public boolean load() {
        boolean r = false;
        if (isDirty()) {
            try {
                onLoad();
                properties = YamlConfiguration.loadConfiguration(getPropertiesFile());
                onLoaded();
                Bukkit.getPluginManager().callEvent(new FeaturePropertiesLoadedEvent(feature));
                r = true;
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return r;
        }
    }

    protected Plugin getPlugin() {
        return plugin;
    }

    protected final FileConfiguration getProperties() {
        return config;
    }

    protected final File getPropertiesDirectory() {
        if (propertiesDirectory != null) {
            if (!propertiesDirectory.exists()) {
                propertiesDirectory.mkdir();
            }
        }
        return propertiesDirectory;
    }

    protected final File getPropertiesFile() {
        if (propertiesFile == null) {
            final File f = new File(((getPropertiesDirectory() == null) ? getPlugin().getDataFolder() : getPropertiesDirectory()),"config.yml");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    setDirty();
                    onFileCreated();
                } catch (final IOException e) {
                    e.printStackTrace();
                    this.getPlugin().getPluginLoader().disablePlugin(this.getPlugin());
                }
            }
            propertiesFile = f;
        }
        return propertiesFile;
    }

    // OTHER USELESS FUNCTIONS //
    public int getPort() {
        return port;
    }

    public String getSalt() {
        return salt;
    }

    public void setPort(int newport) {
        port = newport;
        setDirty();
    }
    
    public void setSalt(String newsalt) {
        salt = newsalt;
        setDirty();
    }

  //  @Override
    protected void onFileCreated() {
        onStore();
    }

  //  @Override
    protected void onLoaded() {
        final YamlConfiguration p = getConfig();
        port = p.getInt(PROPERTY_NAME_PORT);
    }

 //   @Override
    protected void onStore() {
        final YamlConfiguration p = getConfig();
        p.set(PROPERTY_NAME_PORT, port);
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public void setDirty() {
        if (!isDirty()) {
            setDirty(true);
        }
    }

    public final YamlConfiguration getConfig() {
        return config;
    }
}
