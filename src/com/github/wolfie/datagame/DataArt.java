package com.github.wolfie.datagame;

import com.github.wolfie.engine.Art;
import com.mojang.mojam.screen.Bitmap;

public class DataArt extends Art {

	public static final Bitmap[][] TILE_SAND = cut("/sprites/tile_sand.png",
			16, 16);
	public static final Bitmap[][] TILE_TREE = cut("/sprites/tile_tree.png",
			16, 32);
	public static final Bitmap PLAYER = load("/sprites/player.png");

}
