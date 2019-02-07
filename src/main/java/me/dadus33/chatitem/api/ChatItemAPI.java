package me.dadus33.chatitem.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author sarhatabaot
 */
public interface ChatItemAPI {
    String getJSONFromItem(ItemStack var1);

    String getJSONFromItem(ItemStack var1, String var2);

    String getJSONFromItem(ItemStack var1, Player var2);

    String getJSONFromItem(ItemStack var1, String var2, Player var3);

    String getJSONFromInlineItem(String var1, ItemStack... var2);

    String getJSONFromInlineItem(String var1, String[] var2, ItemStack... var3);

    String getJSONFromInlineItem(String var1, Player var2, ItemStack... var3);

    String getJSONFromInlineItem(String var1, Player var2, String[] var3, ItemStack... var4);
}
