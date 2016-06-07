package com.lolpvp.minievents.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import com.lolpvp.minievents.MiniEvent;
import com.lolpvp.minievents.MiniEventManager;
import com.lolpvp.minievents.core.MiniEvents;
import com.lolpvp.minievents.events.MiniEventStartEvent;
import com.lolpvp.minievents.events.MiniEventStopEvent;
import com.lolpvp.minievents.utils.ChatUtils;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Purge extends MiniEvent
{
	MiniEvents plugin;
	public Purge(MiniEvents instance) 
	{
		super("Purge", MiniEvents.getInstance().getConfig().getInt("purge.countdown"), MiniEvents.getInstance().getConfig().getInt("purge.duration"), false);
		this.plugin = instance;
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		if(this.isRunning())
		{
			event.setMotd(ChatColor.RED + "" + ChatColor.BOLD + "***WARNING*** THERE IS A PURGE OCCURING! JOIN AT YOUR OWN RISK!");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onStart(MiniEventStartEvent event)
	{
		if(event.getEvent().equals(this))
		{
			ChatUtils.broadcast(this, "has started! PVP has been allowed in all regions!");
			this.removePVPRegions();
			
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1F, 0.5F);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onStop(MiniEventStopEvent event)
	{
		if(event.getEvent().equals(this))
		{
			ChatUtils.broadcast(this, "has ended. Thank you for your cooperation.");
			this.resetPVPRegions();
			
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1F, 0.5F);
			}
		}
	}
	
	private List<ProtectedRegion> editedRegions = new ArrayList<>();
	
	public void removePVPRegions()
	{
		for(ProtectedRegion region : MiniEventManager.getInstance().getRegionManager().getRegions().values())
		{
			if(region.getFlag(DefaultFlag.PVP) != null && region.getFlag(DefaultFlag.PVP).equals(StateFlag.State.DENY))
			{
				region.setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
				editedRegions.add(region);
			}
			else if(region.getFlag(DefaultFlag.INVINCIBILITY) != null && region.getFlag(DefaultFlag.INVINCIBILITY).equals(StateFlag.State.ALLOW))
			{
				region.setFlag(DefaultFlag.INVINCIBILITY, StateFlag.State.DENY);
				editedRegions.add(region);
			}
		}
	}
	
	public void resetPVPRegions()
	{
		for(ProtectedRegion region : MiniEventManager.getInstance().getRegionManager().getRegions().values())
		{
			if(editedRegions.contains(region))
			{
				if(region.getFlag(DefaultFlag.PVP) != null)
					region.setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
				else if(region.getFlag(DefaultFlag.INVINCIBILITY) != null)
					region.setFlag(DefaultFlag.INVINCIBILITY, StateFlag.State.ALLOW);
			}
		}
		editedRegions.clear();
	}
}