package com.github.wolfie.datagame;

import java.util.Collection;

import com.github.wolfie.datagame.entities.Creep;
import com.github.wolfie.datagame.entities.Player;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.engine.Colors;
import com.mojang.mojam.screen.Bitmap;

public class Minimap {
	private final DataLevel level;
	private Bitmap bitmap;

	public Minimap(final DataLevel level) {
		this.level = level;
		bitmap = new Bitmap(level.widthInTiles, level.heightInTiles);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void updateMinimap(final Player player,
			final Collection<Creep> creeps, final int scrollLeft,
			final int scrollTop) {
		final Bitmap minimap = new Bitmap(level.widthInTiles,
				level.heightInTiles);
		final int obstacleColor = 0xFF005500;
		final int groundColor = 0xFF000000;
		final int playerColor = 0xFFFFFFFF;
		final int creepColor = 0xFFAAAAAA;

		for (int y = 0; y < level.heightInTiles; y++) {
			for (int x = 0; x < level.widthInTiles; x++) {
				if (level.isObstacle(x, y)) {
					minimap.setPixel(x, y, obstacleColor);
				} else {
					minimap.setPixel(x, y, groundColor);
				}
			}
		}

		for (int y = 0; y < level.heightInTiles; y++) {
			for (int x = 0; x < level.widthInTiles; x++) {
				if (level.isVisibleTile(x, y)) {
					final int pixel = minimap.getPixel(x, y);
					minimap.setPixel(x, y,
							Bitmap.blendPixels(pixel, 0x55FFFFFF));
				}
			}
		}

		minimap.setPixel(player.getTileX(), player.getTileY(), playerColor);

		for (final Creep creep : creeps) {
			minimap.setPixel(creep.getTileX(), creep.getTileY(), creepColor);
		}

		final int windowX = scrollLeft / DataTile.WIDTH;
		final int windowY = scrollTop / DataTile.HEIGHT;
		final int windowWidth = DataGame.WIDTH / DataTile.WIDTH;
		final int windowHeight = DataGame.HEIGHT / DataTile.HEIGHT;
		minimap.rectangle(windowX, windowY, windowWidth, windowHeight,
				Colors.WHITE);

		bitmap = minimap;
	}

	public int getWidth() {
		return level.widthInTiles;
	}

	public int getHeight() {
		return level.heightInTiles;
	}

	public double getScale() {
		return DataTile.WIDTH;
	}
}
