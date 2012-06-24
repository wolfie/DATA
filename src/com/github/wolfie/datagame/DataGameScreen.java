package com.github.wolfie.datagame;

import java.net.URL;

import com.github.wolfie.datagame.entities.MovableObjectRegistry;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.datagame.tiles.SandTile;
import com.github.wolfie.datagame.tiles.SandTreeTile;
import com.github.wolfie.engine.Colors;
import com.github.wolfie.engine.GameScreen;
import com.github.wolfie.engine.MouseData;
import com.github.wolfie.engine.TickData;
import com.github.wolfie.engine.level.AbstractTile;
import com.github.wolfie.engine.level.LevelBuilder;
import com.mojang.mojam.screen.Bitmap;

public class DataGameScreen extends GameScreen {

	private final Bitmap bitmap;
	private final DataGame game;
	private DataLevel level;
	private final DataPlayer player;
	private final MovableObjectRegistry registry;

	private int scrollLeft = 0;
	private int scrollTop = 0;
	private final int maxScrollTop;
	private final int maxScrollLeft;

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

		maxScrollLeft = level.widthInTiles * DataTile.WIDTH - width;
		maxScrollTop = level.heightInTiles * DataTile.HEIGHT - height;

		player = new DataPlayer(0, 0, level);
		registry = new MovableObjectRegistry(level);
		registry.register(player);
	}

	@Override
	public Bitmap getBitmap(final long nsSinceLastFrame) {
		bitmap.clear(Colors.BLACK);

		for (int y = 0; y < level.heightInTiles; y++) {
			for (int x = 0; x < level.widthInTiles; x++) {
				final DataTile tile = level.getTile(x, y);
				Bitmap tileBitmap;
				if (level.isVisibleTile(x, y)) {
					tileBitmap = tile.getBitmap();
				} else {
					tileBitmap = tile.getBitmap().blend(0x55000000);
				}
				final int x2 = x * DataTile.WIDTH + tile.getOffsetPixelsX()
						- scrollLeft;
				final int y2 = y * DataTile.HEIGHT + tile.getOffsetPixelsY()
						- scrollTop;
				bitmap.blit(tileBitmap, x2, y2);
			}
			if (player.getTileY() == y) {
				bitmap.blit(player.getBitmap(nsSinceLastFrame),
						(int) player.topLeftX - scrollLeft,
						(int) player.topLeftY - scrollTop);
			}
		}

		return bitmap;
	}

	@Override
	public void tick(final long nsBetweenTicks, final TickData tickData) {
		final MouseData mouseData = tickData.mouseData;

		if (mouseData.mouseButtonWasPressed[MouseData.RIGHT_MOUSE]) {
			final int x = mouseData.x + scrollLeft;
			final int y = mouseData.y + scrollTop;

			player.walkTo(x, y);
		}

		if (mouseData.isBeingDragged(MouseData.MIDDLE_MOUSE)) {
			scrollTop = Math.max(0, scrollTop + mouseData.prevY - mouseData.y);
			scrollTop = Math.min(scrollTop, maxScrollTop);
			scrollLeft = Math
					.max(0, scrollLeft + mouseData.prevX - mouseData.x);
			scrollLeft = Math.min(scrollLeft, maxScrollLeft);
		}

		registry.tick(nsBetweenTicks, tickData);

		registry.postTick();
	}

}
