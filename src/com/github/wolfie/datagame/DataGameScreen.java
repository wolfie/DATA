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

	private static final Alignment MINIMAP_ALIGN = Alignment.BOTTOM_LEFT;
	private static final int MINIMAP_POS_Y = 10;
	private static final int MINIMAP_POS_X = 10;

	private static final int LEFT_DRAG_MODE_NONE = 0;
	private static final int LEFT_DRAG_MODE_MINIMAP = 1;
	private static final int LEFT_DRAG_MODE_SELECTION_BOX = 2;
	private int leftDragMode;

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
	private final Minimap minimapBitmap;
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

		minimapBitmap = new Minimap(level);

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

		bitmap.blit(minimapBitmap.getBitmap(), MINIMAP_POS_X, MINIMAP_POS_Y,
				MINIMAP_ALIGN);

		bitmap.blit(
				DataArt.DEFAULT_FONT.textToBitmap(getFps(nsSinceLastFrame)
						+ "fps"), 0, 0, Alignment.TOP_RIGHT);

		if (DataGame.getInstance().isPaused()) {
			bitmap.blendSelf(0x55000000);
			bitmap.blit(DataArt.PAUSED, 0, 10, Alignment.TOP_CENTER);
		}

		return bitmap;
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

			// move player with mouse //

			final int x;
			final int y;
			if (!cursorIsInsideMinimap(mouseData)) {
				x = mouseData.x + scrollLeft;
				y = mouseData.y + scrollTop;
			} else {
				x = (int) ((mouseData.x - MINIMAP_POS_X)
						* minimapBitmap.getScale() + 0.5);
				y = (int) ((mouseData.y - height + MINIMAP_POS_Y + minimapBitmap
						.getHeight()) * minimapBitmap.getScale() + 0.5);
			}
			player.walkTo(x, y);
		}

		if (mouseData.mouseButtonIsDragged[MouseData.MIDDLE_MOUSE]) {

			// scroll camera with middle-mouse-drag //

			scrollTop = Math.max(0, scrollTop + mouseData.prevY - mouseData.y);
			scrollTop = Math.min(scrollTop, maxScrollTop);
			scrollLeft = Math
					.max(0, scrollLeft + mouseData.prevX - mouseData.x);
			scrollLeft = Math.min(scrollLeft, maxScrollLeft);
		}

		/*
		 * HOLY SHIT THIS CODE SUCKS AND IS ALSO MESSY
		 */

		if (mouseData.mouseButtonIsPressed[MouseData.LEFT_MOUSE]
				&& (cursorIsInsideMinimap(mouseData) && leftDragMode == LEFT_DRAG_MODE_NONE)
				|| leftDragMode == LEFT_DRAG_MODE_MINIMAP) {
			// move camera with minimap //
			moveCameraWithMinimap(mouseData);
		}

		if (mouseData.mouseButtonIsDragged[MouseData.LEFT_MOUSE]) {
			if ((cursorIsInsideMinimap(mouseData) && leftDragMode == LEFT_DRAG_MODE_NONE)
					|| leftDragMode == LEFT_DRAG_MODE_MINIMAP) {
				// move camera with minimap //
				moveCameraWithMinimap(mouseData);

			} else if (leftDragMode == LEFT_DRAG_MODE_NONE
					|| leftDragMode == LEFT_DRAG_MODE_SELECTION_BOX) {
				leftDragMode = LEFT_DRAG_MODE_SELECTION_BOX;

				// drag selection box //

				if (!mouseData.mouseButtonWasDragged[MouseData.LEFT_MOUSE]) {
					selectBox[0] = mouseData.x;
					selectBox[1] = mouseData.y;
				}
				selectBox[2] = mouseData.x - selectBox[0];
				selectBox[3] = mouseData.y - selectBox[1];
			}
		} else {
			// dragging stopped
			leftDragMode = 0;
			if (!mouseData.mouseButtonWasDragged[MouseData.LEFT_MOUSE]) {
				Arrays.fill(selectBox, -1);
			}
		}

		for (final Creep creep : creeps) {
			creep.tick(nsBetweenTicks, tickData);
		}

		minimapBitmap.updateMinimap(player, creeps, scrollLeft, scrollTop);

		registry.tick(nsBetweenTicks, tickData);
		registry.postTick();
	}

	private void moveCameraWithMinimap(final MouseData mouseData) {
		leftDragMode = LEFT_DRAG_MODE_MINIMAP;

		int translatedX = (int) ((mouseData.x - MINIMAP_POS_X)
				* minimapBitmap.getScale() + 0.5);
		int translatedY = (int) ((mouseData.y - height + MINIMAP_POS_Y + minimapBitmap
				.getHeight()) * minimapBitmap.getScale() + 0.5);

		translatedX -= width / 2;
		translatedX = Math.max(0, translatedX);
		translatedX = Math.min(translatedX, level.widthInTiles * DataTile.WIDTH
				- width);
		scrollLeft = translatedX;

		translatedY -= height / 2;
		translatedY = Math.max(0, translatedY);
		translatedY = Math.min(translatedY, level.heightInTiles
				* DataTile.HEIGHT - height);
		scrollTop = translatedY;
	}

	private boolean cursorIsInsideMinimap(final MouseData mouseData) {
		final int x = mouseData.x;
		final int y = mouseData.y;

		// assuming MINIMAP_ALIGN = BOTTOM_LEFT
		final boolean isWithinWidth = MINIMAP_POS_X < x
				&& x < MINIMAP_POS_X + minimapBitmap.getWidth();
		final boolean isWithinHeight = height - MINIMAP_POS_Y
				- minimapBitmap.getHeight() < y
				&& y < height - MINIMAP_POS_Y;
		return isWithinWidth && isWithinHeight;
	}
}
