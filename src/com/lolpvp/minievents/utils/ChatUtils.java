package com.lolpvp.minievents.utils;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.lolpvp.minievents.MiniEvent;

public class ChatUtils
{
	public static void broadcast(Player player, String msg)
	{
		player.sendMessage(msg);
	}
	
	@SuppressWarnings("deprecation")
	public static void broadcast(String msg)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			broadcast(player, msg);
		}
	}
	
	public static void broadcast(MiniEvent event, Player player, String msg)
	{
		player.sendMessage(AQUA + event.getName() + GRAY + " " + msg);
	}
	
	@SuppressWarnings("deprecation")
	public static void broadcast(MiniEvent event, String msg)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			broadcast(event, player, msg);
		}
	}
}
