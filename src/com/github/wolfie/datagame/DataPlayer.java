package com.github.wolfie.datagame;

import com.github.wolfie.datagame.entities.Player;

public class DataPlayer extends Player {
	public DataPlayer(final double x, final double y, final DataLevel level) {
		super(x, y, level);
	}

	public int getVisionRange() {
		return 5;
	}
}
