package lol.suicide.rpchat;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ChatChannel {
    GLOBAL("global-chat-prefix"),
    RP("rp-chat-prefix");

    private String key;

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', RPChat.getInstance().getConfig().getString(this.key));
    }
}
