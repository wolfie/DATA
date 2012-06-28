package com.github.wolfie.datagame;

import com.github.wolfie.engine.Art;
import com.github.wolfie.engine.Font;
import com.mojang.mojam.screen.Bitmap;

public class DataArt extends Art {

	// @formatter:off
	
	public static final Bitmap[][] TILE_SAND = cut("/sprites/tile_sand.png", 16, 16);
	public static final Bitmap[][] TILE_TREE = cut("/sprites/tile_tree.gif", 16, 32);
	public static final Bitmap     PLAYER = load("/sprites/player.png");
	public static final Bitmap     CREEP = load("/sprites/creep.gif");
	public static final Bitmap     PAUSED = load("/sprites/paused.gif");

	public static final Font.CaseInsensitive DEFAULT_FONT = new Font.CaseInsensitive(
			cut("/fonts/default.gif", 4, 6), 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ",
			"0123456789.,!?()-=/\\*");
	
	// @formatter:on

}
