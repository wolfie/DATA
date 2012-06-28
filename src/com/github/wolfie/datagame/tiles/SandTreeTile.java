package com.github.wolfie.datagame.tiles;

import com.github.wolfie.datagame.DataArt;
import com.github.wolfie.datagame.DataGame;
import com.mojang.mojam.screen.Bitmap;

public class SandTreeTile extends DataTile implements Obstacle {

	private final Bitmap bitmap;

	public SandTreeTile() {
		bitmap = new Bitmap(DataTile.WIDTH, DataTile.HEIGHT * 2);
		bitmap.blit(new SandTile().getBitmap(), 0, 16);
		bitmap.blit(DataArt.TILE_TREE[DataGame.RANDOM
				.nextInt(DataArt.TILE_TREE.length)][DataGame.RANDOM
				.nextInt(DataArt.TILE_TREE[0].length)], 0, 0);
	}

	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public int getOffsetPixelsY() {
		return -16;
	}
}
