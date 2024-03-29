package com.github.wolfie.datagame;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.github.wolfie.engine.EngineCanvas;
import com.github.wolfie.engine.GameScreen;

public class DataGame extends EngineCanvas {
	private static final long serialVersionUID = 1448039885235835630L;
	public static final Random RANDOM = new Random();

	public static void main(final String[] args) {
		init();
	}

	private boolean running;
	private boolean paused;
	public DataKeys keyData;
	private DataGameScreen gameScreen;

	public DataGame() {
	}

	@Override
	protected WindowListener getWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				running = false;
			}

			@Override
			public void windowLostFocus(final WindowEvent e) {
				pauseGame();
			}
		};
	}

	@Override
	protected GameScreen getGameScreen() {
		return gameScreen = new DataGameScreen(DataConfig.WIDTH,
				DataConfig.HEIGHT, this);
	}

	@Override
	protected DataKeys getKeyData() {
		return keyData = new DataKeys();
	}

	@Override
	protected void startGame() {
		running = true;
	}

	@Override
	protected boolean gameIsRunning() {
		return running;
	}

	@Override
	public void pauseGame() {
		paused = true;
		mouseData.confineMouse = false;
	}

	@Override
	public void unpauseGame() {
		paused = false;
		mouseData.confineMouse = true;
	}

	@Override
	protected Cursor createCursor() {
		try {
			return createCursorFrom(ImageIO.read(DataArt.class
					.getResource("/sprites/cursor.png")));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return createBlankCursor();
	}

	public static DataGame getInstance() {
		return (DataGame) EngineCanvas.getInstance();
	}

	public void togglePause() {
		if (paused) {
			unpauseGame();
		} else {
			pauseGame();
		}
	}

	public boolean isPaused() {
		return paused;
	}
}
