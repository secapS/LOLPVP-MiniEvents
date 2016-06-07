package com.lolpvp.minievents.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import com.lolpvp.minievents.MiniEvent;
import com.lolpvp.minievents.MiniEventManager;
import com.lolpvp.minievents.core.MiniEvents;
import com.lolpvp.minievents.events.CountdownStartEvent;
import com.lolpvp.minievents.events.MiniEventStartEvent;
import com.lolpvp.minievents.events.MiniEventStopEvent;
import com.lolpvp.minievents.utils.LocationUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MeteorLanding extends MiniEvent
{
	MiniEvents plugin;
	public MeteorLanding(MiniEvents instance)
	{
		super("MeteorLanding", MiniEvents.getInstance().getConfig().getInt("meteorlanding.countdown"), MiniEvents.getInstance().getConfig().getInt("meteorlanding.duration"), false);
		this.plugin = instance;
	}
	
	Map<Location, BlockData> editedBlocks = new HashMap<>();
	List<Block> blocks = new ArrayList<>();

	public Location spawn;
	
	public String getRandomRegion()
	{
		Random random = new Random();
		List<String> regions = this.plugin.getConfig().getStringList("meteorlanding.regions");
		String region = regions.get(random.nextInt(regions.size()));
		return region;
	}
	
	@EventHandler
	public void onCountdownStart(CountdownStartEvent event)
	{
		if(event.getEvent().equals(this))
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "Brace yourselves a " + ChatColor.AQUA + "Meteor" + ChatColor.GRAY + " is incoming!");
			
			for(ProtectedRegion region : MiniEventManager.getInstance().getRegionManager().getRegions().values())
			{
				if(region.getId().equals(this.getRandomRegion()))
				{
					spawn = new Location(MiniEventManager.getWorld(), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEventStart(MiniEventStartEvent event)
	{
		if(event.getEvent().equals(this))
		{			
			for(Location loc : LocationUtils.sphere(spawn, this.plugin.getConfig().getInt("meteorlanding.crater-radius"), false))
			{
				editedBlocks.put(loc, new BlockData(loc.getBlock().getType(), loc.getBlock().getData()));
				loc.getBlock().setType(Material.AIR);
			}
			
			for(Location loc : LocationUtils.sphere(spawn, this.plugin.getConfig().getInt("meteorlanding.meteor-radius"), false))
			{
				loc.getBlock().setType(Material.ENDER_STONE);
				blocks.add(loc.getBlock());
			}
			
			for(Player player : Bukkit.getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.EXPLODE, 1F, 1F);
			}
			Bukkit.broadcastMessage(ChatColor.GRAY + "A " + ChatColor.AQUA + "Meteor " + ChatColor.GRAY + "has landed near: " + ChatColor.AQUA + "X: " + (spawn.getBlockX()) + " Z: " + (spawn.getBlockZ()));
			Bukkit.broadcastMessage(ChatColor.GRAY + "Collect the resources from the meteor and sell it in the shops!");
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(blocks.contains(event.getBlock()))
		{
			if(blocks.size() > 1)
				blocks.remove(event.getBlock());
			else
			{
				blocks.clear();
				MiniEventManager.getInstance().endMiniEvent(this);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEnd(MiniEventStopEvent event)
	{
		if(event.getEvent().equals(this))
		{
			Bukkit.broadcastMessage(ChatColor.GRAY + "All the resources has been taken!");
			Bukkit.broadcastMessage(ChatColor.GRAY + "Resetting blocks in " + this.plugin.getConfig().getInt("meteorlanding.reset-delay") + " seconds.");
			Bukkit.broadcastMessage("" + this.editedBlocks.size());
			this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable()
			{
				@Override
				public void run()
				{
					for(Location block : MeteorLanding.this.editedBlocks.keySet())
					{
						block.getBlock().setType(MeteorLanding.this.editedBlocks.get(block).getMaterial());
						block.getBlock().setData(MeteorLanding.this.editedBlocks.get(block).getData());
					}	
					editedBlocks.clear();
				}
			}, this.plugin.getConfig().getInt("meteorlanding.reset-delay") * 20L);
		}
	}
}

class BlockData
{
	private Material material;
	private byte data;
	
	public BlockData(Material material, byte data)
	{
		this.material = material;
		this.data = data;
	}
	
	public Material getMaterial()
	{
		return this.material;
	}
	
	public byte getData()
	{
		return this.data;
	}
}