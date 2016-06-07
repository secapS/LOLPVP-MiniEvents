package com.lolpvp.minievents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import com.lolpvp.minievents.core.MiniEvents;
import com.lolpvp.minievents.events.CountdownEndEvent;
import com.lolpvp.minievents.events.CountdownRunningEvent;
import com.lolpvp.minievents.events.MiniEventStartEvent;

public class Countdown implements Listener, Runnable
{
	private int id;
	private MiniEvent event;
	private int countdown;
	private int timeLeft;
	private boolean countdownFinished = false;
	private MiniEvents plugin;
	
	public Countdown(MiniEvent event, int duration, MiniEvents instance)
	{
		this.event = event;
		this.countdown = duration;
		this.timeLeft = duration;
		this.plugin = instance;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public MiniEvent getEvent()
	{
		return this.event;
	}
	
	public int getDuration()
	{
		return this.countdown;
	}

	public void setId(int newId)
	{
		this.id = newId;
	}
	
	public int getTimeLeft()
	{
		return this.timeLeft;
	}
	
	public boolean isCountdownFinished()
	{
		return this.countdownFinished;
	}
	
	public int getId()
	{
		return this.id;
	}

	@Override
	public void run() 
	{
		CountdownRunningEvent countdownRunningEvent = new CountdownRunningEvent(this, this.event);
		this.plugin.getServer().getPluginManager().callEvent(countdownRunningEvent);
		
		if(this.event.shouldBroadcastCountdown())
		{
			if((timeLeft % 60) == 0 && timeLeft != 0)
				Bukkit.broadcastMessage(ChatColor.AQUA + this.getEvent().getName() + ChatColor.GRAY + " starts in " + ChatColor.AQUA + timeLeft/60 + ChatColor.GRAY + " minute(s)!");
			
			if(timeLeft <= 60 && (timeLeft % 10) == 0 && (timeLeft % 60) != 0 && timeLeft != 0)
				Bukkit.broadcastMessage(ChatColor.AQUA + this.getEvent().getName() + ChatColor.GRAY + " starts in " + ChatColor.AQUA + timeLeft + ChatColor.GRAY + " second(s)!");
			
			if(timeLeft <= 5 && timeLeft != 0)
				Bukkit.broadcastMessage(ChatColor.AQUA + this.getEvent().getName() + ChatColor.GRAY + " starts in " + ChatColor.AQUA + timeLeft + ChatColor.GRAY + " second(s)!");	
		}
		
		if(timeLeft > 0)
			timeLeft--;
		else
		{
			countdownFinished = true;
			event.setState(MiniEvent.State.PLAYING);
			BukkitTask task = this.plugin.getServer().getScheduler().runTask(plugin, event);
			event.setId(task.getTaskId());
			CountdownEndEvent countdownEndEvent = new CountdownEndEvent(this, this.event);
			this.plugin.getServer().getPluginManager().callEvent(countdownEndEvent);
			this.plugin.getServer().getScheduler().cancelTask(this.id);
			MiniEventStartEvent callEvent = new MiniEventStartEvent(event);
			plugin.getServer().getPluginManager().callEvent(callEvent);
		}
	}
}