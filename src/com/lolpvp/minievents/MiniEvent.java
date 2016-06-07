package com.lolpvp.minievents;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.lolpvp.minievents.events.MiniEventRunningEvent;

public class MiniEvent implements Listener, Runnable
{
	private String name;
	private int id, duration, countdown, timeLeft;
	private boolean running;
	private boolean broadcastCountdown;
	private State state;
	
	public MiniEvent(String name, int countdown, int duration, boolean broadcastCountdown)
	{
		this.name = name;
		this.countdown = countdown;
		this.duration = duration;
		this.timeLeft = duration;
		this.broadcastCountdown = broadcastCountdown;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(int newId)
	{
		this.id = newId;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getCountdown()
	{
		return this.countdown;
	}
	
	public int getDuration()
	{
		return this.duration;
	}
	
	public int getTimeLeft()
	{
		return this.timeLeft;
	}
	
	public boolean shouldBroadcastCountdown()
	{
		return this.broadcastCountdown;
	}
	
	public boolean isRunning()
	{
		return this.running;
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public void setState(State state)
	{
		this.state = state;
	}
	
	public void setRunning(boolean value)
	{
		this.running = value;
	}
	
	public static enum State
	{
		SETUP,
		PLAYING,
		END;
	}
	
	@Override
	public void run() 
	{	
		MiniEventRunningEvent event = new MiniEventRunningEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		
		if(timeLeft == 0)
			MiniEventManager.getInstance().endMiniEvent(this);
		timeLeft--;
	}
}