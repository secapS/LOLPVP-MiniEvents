package com.lolpvp.minievents.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.lolpvp.minievents.MiniEvent;

public class MiniEventStartEvent extends Event implements CustomMiniEvent
{
	private MiniEvent event;
	
	public MiniEventStartEvent(MiniEvent event)
	{
		this.event = event;
	}
	
	public MiniEvent getEvent()
	{
		return this.event;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
