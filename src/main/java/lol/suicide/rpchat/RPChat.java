package lol.suicide.rpchat;

import com.google.common.collect.Maps;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class RPChat extends JavaPlugin implements Listener, CommandExecutor {

    @Getter
    private static RPChat instance;
    private HashMap<UUID, ChatChannel> chatChannels;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.chatChannels = Maps.newHashMap();
        getCommand("rp").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        ChatChannel channel = chatChannels.getOrDefault(player.getUniqueId(), ChatChannel.GLOBAL);
        if(channel == ChatChannel.GLOBAL) {
            event.setFormat(channel.getPrefix() + event.getFormat());
        } else {
            event.setCancelled(true);
            getServer().getOnlinePlayers().stream().filter(pl -> chatChannels.getOrDefault(pl.getUniqueId(), ChatChannel.GLOBAL) == ChatChannel.RP &&
                    player.getLocation().distance(pl.getLocation()) <= getConfig().getDouble("rp-chat-distance")).forEach(pl ->
                pl.sendMessage(String.format(channel.getPrefix() + FPlayers.getInstance().getByPlayer(pl).getFaction().getRelationTo(fPlayer.getFaction()).getColor() + fPlayer.getRole().getPrefix() + fPlayer.getFaction().getTag() + ChatColor.RESET + " " + event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()))
            );
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        } else {
            Player player = (Player) sender;
            if(chatChannels.getOrDefault(player.getUniqueId(), ChatChannel.GLOBAL) == ChatChannel.GLOBAL) {
                chatChannels.put(player.getUniqueId(), ChatChannel.RP);

            } else if(chatChannels.getOrDefault(player.getUniqueId(), ChatChannel.GLOBAL) == ChatChannel.RP) {
                chatChannels.put(player.getUniqueId(), ChatChannel.GLOBAL);
            }
            player.sendMessage(ChatColor.GRAY + "You have switched your chat channel to " + chatChannels.get(player.getUniqueId()).name() + ".");
        }
        return true;
    }

    @Override
    public void onDisable() {
        chatChannels.clear();
    }
}
