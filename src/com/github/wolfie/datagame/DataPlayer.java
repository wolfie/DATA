package com.github.wolfie.datagame;

import com.github.wolfie.datagame.entities.Player;

public class DataPlayer extends Player {
	public DataPlayer(final double x, final double y) {
		super(x, y);
	}

	public int getTileX() {
		return (int) Math.floor(topLeftX + 0.5);
	}

	public int getTileY() {
		return (int) Math.floor(topLeftY + 0.5);
	}

	public int getVisionRange() {
		return 5;
	}
}
