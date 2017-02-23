package me.dadus33.chatitem;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import me.dadus33.chatitem.commands.CIReload;
import me.dadus33.chatitem.filters.Log4jFilter;
import me.dadus33.chatitem.json.JSONManipulator;
import me.dadus33.chatitem.json.JSONManipulatorCurrent;
import me.dadus33.chatitem.listeners.ChatEventListener;
import me.dadus33.chatitem.listeners.ChatPacketListener;
import me.dadus33.chatitem.listeners.ChatPacketValidator;
import me.dadus33.chatitem.listeners.HandshakeListener;
import me.dadus33.chatitem.utils.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.IOException;
import java.util.logging.Level;

public class ChatItem extends JavaPlugin {

    public final static int CFG_VER = 11;
    private static ChatItem instance;
    private ChatEventListener chatEventListener;
    private Log4jFilter filter;
    private Storage storage;
    private ProtocolManager pm;
    private ChatPacketListener packetListener;
    private ChatPacketValidator packetValidator;
    private static boolean post17 = false;
    private static boolean post111 = false;
    private static boolean baseComponentAvailable = true;
    private static boolean viaVersion = false;
    private static boolean protocolSupport = false;

    private static Plugin viaPlugin;
    private static Plugin protocolPlugin;

    public static void reload(CommandSender sender) {
        ChatItem obj = getInstance();
        obj.pm = ProtocolLibrary.getProtocolManager();
        obj.saveDefaultConfig();
        obj.reloadConfig();
        obj.storage = new Storage(obj.getConfig());
        obj.packetListener.setStorage(obj.storage);
        obj.packetValidator.setStorage(obj.storage);
        obj.chatEventListener.setStorage(obj.storage);
        obj.filter.setStorage(obj.storage);
        if (!obj.storage.RELOAD_MESSAGE.isEmpty())
            sender.sendMessage(obj.storage.RELOAD_MESSAGE);
    }

    public static ChatItem getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        pm = ProtocolLibrary.getProtocolManager();
        saveDefaultConfig();
        storage = new Storage(getConfig());
        if(isMc18OrLater()) {
            post17 = true; //for actionbar messages ignoring
        }
        if(isMc111OrLater()){
            post111 = true; //for shulker box filtering
        }

        packetListener = new ChatPacketListener(this, ListenerPriority.LOW, storage, PacketType.Play.Server.CHAT);
        packetValidator = new ChatPacketValidator(this, ListenerPriority.LOWEST, storage, PacketType.Play.Server.CHAT);
        pm.addPacketListener(packetValidator);
        if((viaPlugin = Bukkit.getPluginManager().getPlugin("ViaVersion")) != null){
            viaVersion = true;
        }else if((protocolPlugin = Bukkit.getPluginManager().getPlugin("ProtocolSupport")) != null){
            protocolSupport = true;
        }
        if(!protocolSupport && !viaVersion) {
            //We only implement our own way of getting protocol versions if we have no other choice
            pm.addPacketListener(new HandshakeListener(this, ListenerPriority.LOWEST, PacketType.Handshake.Client.SET_PROTOCOL));
        }
        pm.addPacketListener(packetListener);
        CIReload rld = new CIReload();
        Bukkit.getPluginCommand("cireload").setExecutor(rld);
        chatEventListener = new ChatEventListener(storage);
        Bukkit.getPluginManager().registerEvents(chatEventListener, this);
        try {
            Class.forName("net.md_5.bungee.api.chat.BaseComponent");
        } catch (ClassNotFoundException e) {
            baseComponentAvailable = false;
        }

        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, ChatColor.RED + "Couldn't start metrics!");
        }
        filter = new Log4jFilter(storage);
    }


    public void onDisable() {
        instance = null;
        post17 = false;
    }

    private boolean isMc18OrLater(){
        switch(getVersion(Bukkit.getServer())){
            case "v1_7_R1": return false;
            case "v1_7_R2": return false;
            case "v1_7_R3": return false;
            case "v1_8_R1": return true;
            case "v1_8_R2": return true;
            case "v1_8_R3": return true;
            case "v1_9_R1": return true;
            case "v1_9_R2": return true;
            case "v1_10_R1": return true;
            case "v1_10_R2": return true;
            case "v1_11_R1": return true;
            case "v1_11_R2": return true;
            default: return true;
        }
    }

    private boolean isMc111OrLater(){
        switch(getVersion(Bukkit.getServer())){
            case "v1_7_R1": return false;
            case "v1_7_R2": return false;
            case "v1_7_R3": return false;
            case "v1_8_R1": return false;
            case "v1_8_R2": return false;
            case "v1_8_R3": return false;
            case "v1_9_R1": return false;
            case "v1_9_R2": return false;
            case "v1_10_R1": return false;
            case "v1_10_R2": return false;
            case "v1_11_R1": return true;
            case "v1_11_R2": return true;
            default: return true;
        }
    }


    public static String getVersion(Server server) {
        final String packageName = server.getClass().getPackage().getName();

        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static boolean mcSupportsActionBar(){
        return post17;
    }

    public static boolean mcSupportsShulkerBoxes(){
        return post111;
    }

    public static boolean supportsChatComponentApi(){
        return baseComponentAvailable;
    }

    public static JSONManipulator getManipulator(){
        return new JSONManipulatorCurrent();
    }

    public static boolean usesViaVersion(){
        return viaVersion;
    }

    public static boolean usesProtocolSupport(){
        return protocolSupport;
    }

    public static ChatItem instance(){
        return instance;
    }

}
