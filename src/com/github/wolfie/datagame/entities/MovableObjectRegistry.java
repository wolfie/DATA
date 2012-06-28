package com.github.wolfie.datagame.entities;

import java.util.HashSet;
import java.util.Set;

import rlforj.los.PrecisePermissive;

import com.github.wolfie.datagame.DataLevel;
import com.github.wolfie.datagame.DataPlayer;
import com.github.wolfie.datagame.entities.Mob.TileChangeListener;
import com.github.wolfie.engine.TickData;

public class MovableObjectRegistry implements TileChangeListener {

	private final DataLevel level;
	private final Set<DataPlayer> players = new HashSet<>();
	private final Set<Mob> mobs = new HashSet<>();
	private final PrecisePermissive losAlgorithm = new PrecisePermissive();
	private boolean losNeedsRecalculation = true;

	public MovableObjectRegistry(final DataLevel level) {
		this.level = level;
	}

	public void register(final DataPlayer player) {
		players.add(player);
		player.setTileChangeListener(this);
	}

	public void postTick() {
		if (losNeedsRecalculation) {
			level.clearVisibility();
			for (final DataPlayer player : players) {
				losAlgorithm.visitFieldOfView(level, player.getTileX(),
						player.getTileY(), player.getVisionRange());
			}
			for (final Mob mob : mobs) {
				losAlgorithm.visitFieldOfView(level, mob.getTileX(),
						mob.getTileY(), mob.getVisionRange());
			}
			losNeedsRecalculation = false;
		}
	}

	public void tick(final long nsBetweenTicks, final TickData tickData) {
		for (final DataPlayer player : players) {
			player.tick(nsBetweenTicks, tickData);
		}
	}

	@Override
	public void tileChanged(final Mob mob) {
		losNeedsRecalculation = true;
	}

	public void register(final Mob mob) {
		mobs.add(mob);
		mob.setTileChangeListener(this);
	}
}
