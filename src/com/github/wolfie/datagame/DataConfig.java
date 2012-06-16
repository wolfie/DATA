package com.github.wolfie.datagame;

import com.github.wolfie.engine.Config;
import com.github.wolfie.engine.EngineCanvas;

public class DataConfig implements Config {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;
	public static final String GAME_TITLE = "DATA";
	public static final int SCALE = 2;

	@Override
	public String getGameTitle() {
		return GAME_TITLE;
	}

	@Override
	public EngineCanvas getGameInstance() {
		return new DataGame();
	}

	@Override
	public int getScale() {
		return SCALE;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public double getGravityPPSS() {
		return 0;
	}

}
