package com.lolpvp.minievents.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class LocationUtils
{	
	public static List<Location> sphere(Location center, int radius, boolean hollow)
	{
		List<Location> blocks = new ArrayList<>();
		int blockX = center.getBlockX();
		int blockY = center.getBlockY();
		int blockZ = center.getBlockZ();
		
		for(int x = blockX - radius; x <= blockX + radius; x++)
		{
			for(int y = blockY - radius; y <= blockY + radius; y++)
			{
				for(int z = blockZ - radius; z <= blockZ + radius; z++)
				{
					double distance = Math.pow((double) blockX - x, 2) + Math.pow((double) blockY - y, 2) + Math.pow((double) blockZ - z, 2);
					
					if(distance < Math.pow(radius, 2) && !(hollow && distance < Math.pow(radius - 1, 2)))
					{
						Location location = new Location(center.getWorld(), x, y, z);
						blocks.add(location);
					}
				}
			}
		}
		return blocks;  
	}
}