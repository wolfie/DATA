package com.github.wolfie.datagame.entities;

import java.util.HashSet;
import java.util.Set;

import rlforj.los.PrecisePermissive;

import com.github.wolfie.datagame.DataLevel;
import com.github.wolfie.datagame.DataPlayer;

public class MovableObjectRegistry {

	private final DataLevel level;
	private final Set<DataPlayer> players = new HashSet<>();
	private final PrecisePermissive losAlgorithm = new PrecisePermissive();
	private boolean losNeedsRecalculation = true;

	public MovableObjectRegistry(final DataLevel level) {
		this.level = level;
	}

	public void register(final DataPlayer player) {
		players.add(player);
	}

	public void postTick() {
		if (losNeedsRecalculation) {
			for (final DataPlayer player : players) {
				losAlgorithm.visitFieldOfView(level, player.getTileX(),
						player.getTileY(), player.getVisionRange());
			}
			losNeedsRecalculation = false;
		}
	}
}
