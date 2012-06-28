package com.github.wolfie.datagame.entities;

import com.github.wolfie.datagame.DataArt;
import com.github.wolfie.datagame.DataGame;
import com.github.wolfie.datagame.DataLevel;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.engine.TickData;
import com.mojang.mojam.screen.Bitmap;

public class Creep extends Mob {

	public static final int HEIGHT = 1;
	public static final int WIDTH = 1;
	private final Bitmap bitmap;
	private final DataLevel level;

	public Creep(final double x, final double y, final DataLevel level) {
		super(x, y, WIDTH, HEIGHT, level);
		this.level = level;
		bitmap = DataArt.CREEP.copy();
		speed = 40;
	}

	@Override
	public Bitmap getBitmap(final long nsSinceLastFrame) {
		return bitmap;
	}

	@Override
	public int getTileHeight() {
		return HEIGHT;
	}

	@Override
	public int getTileWidth() {
		return WIDTH;
	}

	@Override
	public int getVisionRange() {
		return 3;
	}

	@Override
	protected void _tick(final long nsBetweenTicks, final TickData tickData) {
		if (waypoints.isEmpty()) {
			walkTo(DataGame.RANDOM.nextInt(level.widthInTiles * DataTile.WIDTH),
					DataGame.RANDOM.nextInt(level.heightInTiles
							* DataTile.HEIGHT));
		}
	}

}
