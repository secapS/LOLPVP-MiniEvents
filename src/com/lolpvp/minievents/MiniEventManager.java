package com.lolpvp.minievents;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lolpvp.minievents.classes.MeteorLanding;
import com.lolpvp.minievents.classes.PirateInvasion;
import com.lolpvp.minievents.classes.Purge;
import com.lolpvp.minievents.core.MiniEvents;
import com.lolpvp.minievents.events.CountdownStartEvent;
import com.lolpvp.minievents.events.MiniEventStopEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class MiniEventManager
{
	private static MiniEventManager miniEventManager;
	private static MiniEvents plugin;
	private List<MiniEvent> events = new ArrayList<>();
	
	public MiniEventManager()
	{
		this.events.add(new Purge(plugin));
		this.events.add(new MeteorLanding(plugin));
		this.events.add(new PirateInvasion(plugin));
	}
	
	public List<MiniEvent> getEvents()
	{
		return this.events;
	}
	
	public static void setup(MiniEvents instance)
	{
		plugin = instance;
		getWorld();
	}

	public MiniEvent getMiniEventByName(String eventName)
	{
		for(MiniEvent event : events)
		{
			if(event.getName().equalsIgnoreCase(eventName))
			{
				return event;
			}
		}
		return null;
	}
	
	public MiniEvent getMiniEventByClass(Class<? extends MiniEvent> clazz)
	{
		for(MiniEvent event : events)
		{
			if(event.getClass().equals(clazz))
			{
				return event;
			}
		}
		return null;
	}
	
	public boolean isEventRunning(MiniEvent event)
	{
		return event.isRunning();
	}
	
	public void startMiniEvent(MiniEvent event)
	{
		boolean anyRunning = false;
		
		for(MiniEvent miniEvent : this.getEvents())
		{
			if(miniEvent.isRunning())
				anyRunning = true;
		}
		
		if(!anyRunning)
		{
			Countdown countdown = new Countdown(event, event.getCountdown(), plugin);
			BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, countdown, 0L, 20L);
			countdown.setId(task.getTaskId());
			CountdownStartEvent countdownStartEvent = new CountdownStartEvent(countdown, event);
			plugin.getServer().getPluginManager().callEvent(countdownStartEvent);
			event.setState(MiniEvent.State.SETUP);
			event.setRunning(true);		
		}
	}
	
	public void endMiniEvent(MiniEvent event)
	{
		if(event.isRunning())
		{
			plugin.getServer().getScheduler().cancelTask(event.getId());
			event.setState(MiniEvent.State.END);
			event.setRunning(false);
			MiniEventStopEvent callEvent = new MiniEventStopEvent(event);
			plugin.getServer().getPluginManager().callEvent(callEvent);
		}
	}
	
	public WorldGuardPlugin getWorldGuard() 
	{
		final Plugin plugin = Bukkit.getServer().getPluginManager()
				.getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) 
		{
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}
	
	public static World getWorld()
	{
		World world = plugin.getServer().getWorld(plugin.getConfig().getString("worldName"));
		if(world == null)
		{
			WorldCreator worldCreator = new WorldCreator(plugin.getConfig().getString("worldName"));
			world = plugin.getServer().createWorld(worldCreator);
		}
		return world;
	}
	
	public RegionManager getRegionManager()
	{
		return this.getWorldGuard().getRegionManager(getWorld());
	}
	
	public static MiniEventManager getInstance()
	{
		if(miniEventManager == null)
		{
			miniEventManager = new MiniEventManager();
		}
		return miniEventManager;
	}
}