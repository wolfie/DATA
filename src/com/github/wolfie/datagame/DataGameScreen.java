package com.github.wolfie.datagame;

import java.net.URL;

import com.github.wolfie.datagame.entities.MovableObjectRegistry;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.datagame.tiles.SandTile;
import com.github.wolfie.datagame.tiles.SandTreeTile;
import com.github.wolfie.engine.Colors;
import com.github.wolfie.engine.GameScreen;
import com.github.wolfie.engine.TickData;
import com.github.wolfie.engine.level.AbstractTile;
import com.github.wolfie.engine.level.LevelBuilder;
import com.mojang.mojam.screen.Bitmap;

public class DataGameScreen extends GameScreen {

	private final Bitmap bitmap;
	private final DataGame game;
	private DataLevel level;
	private final DataPlayer player = new DataPlayer(0, 0);
	private final MovableObjectRegistry registry;

	protected DataGameScreen(final int width, final int height,
			final DataGame dataGame) {
		super(width, height);
		this.game = dataGame;
		bitmap = new Bitmap(width, height);
		bitmap.clear(Colors.BLACK);
		level = DataLevel.createFrom(new LevelBuilder() {
			@Override
			protected AbstractTile getTileForColor(final int hexColor) {
				switch (hexColor) {
				case Colors.GREEN:
					return new SandTreeTile();
				default:
					return new SandTile();
				}
			}

			@Override
			protected URL getLevelImage() {
				return DataGame.class.getResource("/level/default.png");
			}
		}.build());

		registry = new MovableObjectRegistry(level);
		registry.register(player);
	}

	@Override
	public Bitmap getBitmap(final long nsSinceLastFrame) {
		bitmap.clear(Colors.BLACK);

		for (int y = 0; y < level.height; y++) {
			for (int x = 0; x < level.width; x++) {
				final DataTile tile = level.getTile(x, y);
				Bitmap tileBitmap;
				if (level.isVisibleTile(x, y)) {
					tileBitmap = tile.getBitmap();
				} else {
					tileBitmap = tile.getBitmap().blend(0xAA000000);
				}
				bitmap.blit(tileBitmap,
						x * DataTile.WIDTH + tile.getOffsetPixelsX(), y
								* DataTile.HEIGHT + tile.getOffsetPixelsY());
			}
			if ((int) player.topLeftY == y) {
				bitmap.blit(player.getBitmap(nsSinceLastFrame),
						(int) player.topLeftX, (int) player.topLeftY);
			}
		}

		return bitmap;
	}

	@Override
	public void tick(final long nsBetweenTicks, final TickData tickData) {
		registry.postTick();
		tickData.mouseData.postTick();
	}

}
