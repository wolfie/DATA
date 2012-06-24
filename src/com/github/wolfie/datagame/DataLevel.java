package com.github.wolfie.datagame;

import java.util.HashSet;
import java.util.Set;

import rlforj.los.ILosBoard;

import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.datagame.tiles.Obstacle;
import com.github.wolfie.engine.level.Level;
import com.github.wolfie.engine.level.Tile;

public class DataLevel extends Level implements ILosBoard {
	private final Set<Integer> visibleTileIndexes = new HashSet<>();

	public DataLevel(final Level level) {
		super(level);
	}

	public static DataLevel createFrom(final Level level) {
		return new DataLevel(level);
	}

	@Override
	public boolean contains(final int x, final int y) {
		return x >= 0 && x <= widthInTiles && y >= 0 && y <= heightInTiles;
	}

	@Override
	public boolean isObstacle(final int x, final int y) {
		final Tile tile = getTile(x, y);
		if (tile instanceof Obstacle) {
			return ((Obstacle) tile).isBlocking();
		} else {
			return false;
		}
	}

	@Override
	public void visit(final int x, final int y) {
		visibleTileIndexes.add(y * widthInTiles + x);
	}

	public void clearVisibility() {
		visibleTileIndexes.clear();
	}

	public boolean isVisibleTile(final int x, final int y) {
		return visibleTileIndexes.contains(y * widthInTiles + x);
	}

	@Override
	public DataTile getTile(final int x, final int y) {
		return (DataTile) super.getTile(x, y);
	}
}
