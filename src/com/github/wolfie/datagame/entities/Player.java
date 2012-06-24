package com.github.wolfie.datagame.entities;

import com.github.wolfie.datagame.DataArt;
import com.github.wolfie.datagame.DataLevel;
import com.github.wolfie.engine.TickData;
import com.mojang.mojam.screen.Bitmap;

public class Player extends Mob {

	private static int HEIGHT = 2;
	private static int WIDTH = 1;
	private final Bitmap bitmap;

	public Player(final double x, final double y, final DataLevel level) {
		super(Math.max(WIDTH - 1, x), Math.max(HEIGHT - 1, y), WIDTH, HEIGHT,
				level);
		bitmap = DataArt.PLAYER;
	}

	@Override
	public Bitmap getBitmap(final long nsSinceLastFrame) {
		return bitmap;
	}

	@Override
	protected void _tick(final long nsBetweenTicks, final TickData tickData) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getTileHeight() {
		return HEIGHT;
	}

	@Override
	public int getTileWidth() {
		return WIDTH;
	}
}
