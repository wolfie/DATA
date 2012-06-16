package com.github.wolfie.datagame.tiles;

import com.github.wolfie.datagame.DataArt;
import com.github.wolfie.datagame.DataGame;
import com.mojang.mojam.screen.Bitmap;

public class SandTile extends DataTile {

	private final Bitmap bitmap;

	public SandTile() {
		bitmap = DataArt.TILE_SAND[DataGame.RANDOM
				.nextInt(DataArt.TILE_SAND.length)][0];
	}

	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}

}
