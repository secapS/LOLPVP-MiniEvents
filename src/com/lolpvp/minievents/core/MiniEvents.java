package com.lolpvp.minievents.core;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.lolpvp.minievents.MiniEvent;
import com.lolpvp.minievents.MiniEventManager;

public class MiniEvents extends JavaPlugin
{	
	private static MiniEvents instance; 
	Random random = new Random();
	@Override
	public void onEnable()
	{
		instance = this;
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		MiniEventManager.setup(this);
		for(MiniEvent event : MiniEventManager.getInstance().getEvents())
		{
			this.getServer().getPluginManager().registerEvents(event, this);
		}
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				for(MiniEvent event : MiniEventManager.getInstance().getEvents())
				{
					if(!event.isRunning())
					{
						MiniEventManager.getInstance().startMiniEvent(MiniEventManager.getInstance().getEvents().get(random.nextInt(MiniEventManager.getInstance().getEvents().size())));
					}
				}
			}
		}, 0L, (this.getConfig().getInt("time-between-events-minutes") * 60) * 20L);
	}
	
	public static MiniEvents getInstance()
	{
		return instance;
	}
	
	@Override
	public void onDisable()
	{
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(command.getName().equalsIgnoreCase("minievents"))
		{
			if(sender.isOp() || sender.hasPermission("lolpvp.minievents"))
			{
				if(args.length > 0)
				{
					if(args[0].equalsIgnoreCase("start"))
					{
						if(args.length == 2)
						{
							if(MiniEventManager.getInstance().getMiniEventByName(args[1]) != null)
							{
								MiniEventManager.getInstance().startMiniEvent(MiniEventManager.getInstance().getMiniEventByName(args[1]));
							}
							else
							{
								sender.sendMessage("MiniEvent is null");
							}
						}
						else
						{
							sender.sendMessage("MiniEvents: Purge, Meteor Landing.");
						}
					}
					else if(args[0].equalsIgnoreCase("stop"))
					{
						if(args.length == 2)
						{
							if(MiniEventManager.getInstance().getMiniEventByName(args[1]) != null)
							{
								MiniEventManager.getInstance().endMiniEvent(MiniEventManager.getInstance().getMiniEventByName(args[1]));
							}
							else
							{
								sender.sendMessage("MiniEvent is null");
							}
						}
						else
						{
							sender.sendMessage("MiniEvents: Purge, Meteor Landing, Pirate Invasion");
						}
					}
					else if(args[0].equalsIgnoreCase("reload"))
					{
						sender.sendMessage("Reloaded Config");
						this.saveConfig();
						this.reloadConfig();
					}
				}	
				else
				{
					sender.sendMessage("Commands: stop <MiniEvent>, start <MiniEvent>, reload");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			}
		}
		return false;
	}
}