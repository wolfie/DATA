package com.github.wolfie.datagame.entities;

import com.github.wolfie.datagame.DataArt;
import com.github.wolfie.engine.TickData;
import com.github.wolfie.engine.objects.GameObject;
import com.mojang.mojam.screen.Bitmap;

public class Player extends GameObject implements MovableGameObject {

	private static int HEIGHT = 2;
	private static int WIDTH = 1;
	private final Bitmap bitmap;

	public Player(final double x, final double y) {
		super(Math.max(WIDTH - 1, x), Math.max(HEIGHT - 1, y), WIDTH, HEIGHT);
		bitmap = DataArt.PLAYER;
	}

	@Override
	public Bitmap getBitmap(final long nsSinceLastFrame) {
		return bitmap;
	}

	@Override
	protected void moveX(final double velocityX) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void moveY(final double velocityY) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void _tick(final long nsBetweenTicks, final TickData tickData) {
		// TODO Auto-generated method stub

	}
}
