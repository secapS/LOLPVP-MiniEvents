package com.lolpvp.minievents;

import org.bukkit.entity.Player;

public interface Winnable
{
	Player getWinner();
	void setWinner(Player player);
	String getReward();
	boolean hasWinner();
}