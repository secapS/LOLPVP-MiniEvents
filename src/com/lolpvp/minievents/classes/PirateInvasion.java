package com.lolpvp.minievents.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.lolpvp.minievents.MiniEvent;
import com.lolpvp.minievents.MiniEventManager;
import com.lolpvp.minievents.Winnable;
import com.lolpvp.minievents.core.MiniEvents;
import com.lolpvp.minievents.events.CountdownStartEvent;
import com.lolpvp.minievents.events.MiniEventStartEvent;
import com.lolpvp.minievents.events.MiniEventStopEvent;
import com.lolpvp.minievents.utils.ChatUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PirateInvasion extends MiniEvent implements Winnable
{
	private Player winner;
	
	MiniEvents plugin;
	
	public PirateInvasion(MiniEvents instance)
	{
		super("PirateInvasion", MiniEvents.getInstance().getConfig().getInt("pirateinvasion.countdown"), MiniEvents.getInstance().getConfig().getInt("pirateinvasion.duration"), false);
		this.plugin = instance;
	}
	
	World world = MiniEventManager.getWorld();
	//ship1
	//ship2
	//ship3
	@EventHandler
	public void onCountdownStart(CountdownStartEvent event)
	{
		if(event.getEvent().equals(this))
		{
			for(ProtectedRegion region : MiniEventManager.getInstance().getRegionManager().getRegions().values())
			{
				if(region.getId().equalsIgnoreCase("ship1") || region.getId().equalsIgnoreCase("ship2") || region.getId().equalsIgnoreCase("ship3"))
				{
					int minX = region.getMinimumPoint().getBlockX();
					int minY = region.getMinimumPoint().getBlockY();
					int minZ = region.getMinimumPoint().getBlockZ();
					int maxX = region.getMaximumPoint().getBlockX();
					int maxY = region.getMaximumPoint().getBlockY();
					int maxZ = region.getMaximumPoint().getBlockZ();
					for (int x = minX; x <= maxX; x++)
					{
						for (int z = minZ; z <= maxZ; z++)
						{
							for (int y = minY; y <= maxY; y++)
							{
								if((world.getBlockAt(x, y, z).getType().equals(Material.WOOD) || world.getBlockAt(x, y, z).getType().equals(Material.LOG)) && world.getBlockAt(x, y + 1, z).getType().equals(Material.AIR))
								{
									spawnPoints.add(world.getBlockAt(x, y+1, z));
								}
							}
						}
					}
				}
			}
		}
	}
	
	List<Block> spawnPoints = new ArrayList<>();
	List<Entity> pirates = new ArrayList<>();

	Random random = new Random();
	
	@EventHandler
	public void onEventStart(MiniEventStartEvent event)
	{
		if(event.getEvent().equals(this))
		{		
			while(this.plugin.getConfig().getInt("pirateinvasion.max-pirates") > pirates.size())
			{
				System.out.println("Spawn Point Size " + spawnPoints.size());
				Location location = spawnPoints.get(random.nextInt(spawnPoints.size())).getLocation();
				Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
				skeleton.setMaxHealth(40.00);
				skeleton.setHealth(40.00);
				skeleton.setSkeletonType(SkeletonType.WITHER);
				skeleton.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
				skeleton.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
				skeleton.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
				skeleton.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
				skeleton.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
				skeleton.setCustomName("Pirate");
				skeleton.setCustomNameVisible(true);
				pirates.add(skeleton);
			}
			
			ChatUtils.broadcast(ChatColor.AQUA + "Pirates" + ChatColor.GRAY + " have taken over our ships! Kill them all and claim their booty!");
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(this.pirates.contains(event.getEntity()))
		{
			if(this.pirates.size() > 1)
			{
				this.pirates.remove(event.getEntity());

			}
			else
			{
				this.pirates.remove(event.getEntity());
				this.setWinner(event.getEntity().getKiller());
				MiniEventManager.getInstance().endMiniEvent(this);
			}
		}
	}
	
	@EventHandler
	public void onEventEnd(MiniEventStopEvent event)
	{
		if(event.getEvent().equals(this))
		{
			for(Entity entity : pirates)
			{
				entity.remove();
			}
			
			this.spawnPoints.clear();
			this.pirates.clear();
			
			if(hasWinner())
			{
				Bukkit.broadcastMessage(ChatColor.AQUA + "The Pirate Invasion " + ChatColor.GRAY + "has ended! " + ChatColor.AQUA + this.getWinner().getName() + ChatColor.GRAY + " has found the pirates booty!");
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), this.getReward());
			}
			else
			{
				Bukkit.broadcastMessage(ChatColor.GRAY + "The pirates have fled with all of their booty!");
			}
		}
	}
	
	@Override
	public Player getWinner()
	{
		return this.winner;
	}
	
	@Override
	public void setWinner(Player player)
	{
		this.winner = player;
	}
	
	@Override
	public boolean hasWinner()
	{
		return this.getWinner() != null;
	}

	@Override
	public String getReward() 
	{
		Random random = new Random();
		List<String> rewards = this.plugin.getConfig().getStringList("pirateinvasion.rewards");
		String reward = rewards.get(random.nextInt(rewards.size()));
		return reward.replaceAll("\\{playerName\\}", this.getWinner().getName());
	}
}
