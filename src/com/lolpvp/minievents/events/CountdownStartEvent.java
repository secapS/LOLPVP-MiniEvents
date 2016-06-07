package com.lolpvp.minievents.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.lolpvp.minievents.Countdown;
import com.lolpvp.minievents.MiniEvent;

public class CountdownStartEvent extends Event implements CustomMiniEvent
{
	private MiniEvent event;
	private Countdown countdown;
	
	public CountdownStartEvent(Countdown countdown, MiniEvent event)
	{
		this.countdown = countdown;
		this.event = event;
	}
	
	public Countdown getCountdown()
	{
		return this.countdown;
	}
	
	public MiniEvent getEvent()
	{
		return this.event;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() 
	{
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() 
	{
	    return handlers;
	}
}
