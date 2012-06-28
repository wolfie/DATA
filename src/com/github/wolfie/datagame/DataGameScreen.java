package com.github.wolfie.datagame;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.wolfie.datagame.entities.Creep;
import com.github.wolfie.datagame.entities.MovableObjectRegistry;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.datagame.tiles.SandTile;
import com.github.wolfie.datagame.tiles.SandTreeTile;
import com.github.wolfie.engine.Colors;
import com.github.wolfie.engine.GameScreen;
import com.github.wolfie.engine.KeyData;
import com.github.wolfie.engine.MouseData;
import com.github.wolfie.engine.TickData;
import com.github.wolfie.engine.Util;
import com.github.wolfie.engine.level.AbstractTile;
import com.github.wolfie.engine.level.LevelBuilder;
import com.mojang.mojam.screen.Alignment;
import com.mojang.mojam.screen.Bitmap;

public class DataGameScreen extends GameScreen {

	private final Bitmap bitmap;
	private final DataGame game;
	private DataLevel level;
	private final DataPlayer player;
	private final Set<Creep> creeps = new HashSet<>();
	private final MovableObjectRegistry registry;

	private int scrollLeft = 0;
	private int scrollTop = 0;
	private final int maxScrollTop;
	private final int maxScrollLeft;
	private Bitmap minimapBitmap;
	private final int[] selectBox = new int[] { -1, -1, -1, -1 };

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

		generateCreep();
	}

	private void generateCreep() {
		for (int i = 0; i < 20; i++) {
			int x = 0;
			int y = 0;
			do {
				x = DataGame.RANDOM.nextInt(level.widthInTiles);
				y = DataGame.RANDOM.nextInt(level.heightInTiles);
			} while (level.isObstacle(x, y));
			final Creep creep = new Creep(x * DataTile.WIDTH - 8, y
					* DataTile.HEIGHT - 8, level);
			creeps.add(creep);
			registry.register(creep);
		}
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

			for (final Creep creep : creeps) {
				if (creep.getTileY() == y) {
					bitmap.blit(creep.getBitmap(nsSinceLastFrame),
							(int) creep.topLeftX - scrollLeft,
							(int) creep.topLeftY - scrollTop);
				}
			}

			if (player.getTileY() == y) {
				bitmap.blit(player.getBitmap(nsSinceLastFrame),
						(int) player.topLeftX - scrollLeft,
						(int) player.topLeftY - scrollTop);
			}
		}

		if (selectBox[0] > -1) {
			bitmap.rectangle(selectBox[0], selectBox[1], selectBox[2],
					selectBox[3], Colors.GREEN);
		}

		bitmap.blit(minimapBitmap, 10, 10, Alignment.BOTTOM_LEFT);

		bitmap.blit(
				DataArt.DEFAULT_FONT.textToBitmap(getFps(nsSinceLastFrame)
						+ "fps"), 0, 0, Alignment.TOP_RIGHT);

		if (DataGame.getInstance().isPaused()) {
			bitmap.blendSelf(0x55000000);
			bitmap.blit(DataArt.PAUSED, 0, 10, Alignment.TOP_CENTER);
		}

		return bitmap;
	}

	private Bitmap getMinimapBitmap() {
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

		return minimap;
	}

	private int getFps(final long nsSinceLastFrame) {
		final double fps = 1.0d / Util.nanoSecondsToSeconds(nsSinceLastFrame);
		return (int) (fps + 0.5);
	}

	@Override
	public void tick(final long nsBetweenTicks, final TickData tickData) {
		final KeyData keys = tickData.keys;
		final MouseData mouseData = tickData.mouseData;

		if (((DataKeys) keys).pause.wasPressed()) {
			DataGame.getInstance().togglePause();
		}

		if (DataGame.getInstance().isPaused()) {
			return;
		}

		if (mouseData.mouseButtonWasPressed[MouseData.RIGHT_MOUSE]) {
			final int x = mouseData.x + scrollLeft;
			final int y = mouseData.y + scrollTop;

			player.walkTo(x, y);
		}

		if (mouseData.mouseButtonIsDragged[MouseData.MIDDLE_MOUSE]) {
			scrollTop = Math.max(0, scrollTop + mouseData.prevY - mouseData.y);
			scrollTop = Math.min(scrollTop, maxScrollTop);
			scrollLeft = Math
					.max(0, scrollLeft + mouseData.prevX - mouseData.x);
			scrollLeft = Math.min(scrollLeft, maxScrollLeft);
		}

		if (mouseData.mouseButtonIsDragged[MouseData.LEFT_MOUSE]) {
			if (!mouseData.mouseButtonWasDragged[MouseData.LEFT_MOUSE]) {
				selectBox[0] = mouseData.x;
				selectBox[1] = mouseData.y;
			}
			selectBox[2] = mouseData.x - selectBox[0];
			selectBox[3] = mouseData.y - selectBox[1];
		} else {
			if (!mouseData.mouseButtonWasDragged[MouseData.LEFT_MOUSE]) {
				Arrays.fill(selectBox, -1);
			}
		}

		for (final Creep creep : creeps) {
			creep.tick(nsBetweenTicks, tickData);
		}

		minimapBitmap = getMinimapBitmap();

		registry.tick(nsBetweenTicks, tickData);
		registry.postTick();
	}
}
