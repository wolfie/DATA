package com.github.wolfie.datagame.tiles;

import com.github.wolfie.engine.level.AbstractTile;

public abstract class DataTile extends AbstractTile {
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	public int getOffsetPixelsY() {
		return 0;
	}

	public int getOffsetPixelsX() {
		return 0;
	}
}
