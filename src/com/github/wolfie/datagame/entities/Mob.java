package com.github.wolfie.datagame.entities;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.github.wolfie.datagame.DataLevel;
import com.github.wolfie.datagame.pathing.AStar;
import com.github.wolfie.datagame.pathing.Path;
import com.github.wolfie.datagame.tiles.DataTile;
import com.github.wolfie.engine.objects.GameObject;

public abstract class Mob extends GameObject implements MovableGameObject {

	public interface TileChangeListener {
		void tileChanged(Mob mob);
	}

	private static final int MOB_BASE_SPEED = 100;
	protected int speed = MOB_BASE_SPEED;

	protected Queue<Point2D.Double> waypoints = new ArrayDeque<>();
	protected Point2D.Double currentWaypoint = null;
	private TileChangeListener tileChangeListener;
	private int prevTileY = -1;
	private int prevTileX = -1;
	protected double distanceToWalkX = 0;
	protected double distanceToWalkY = 0;
	private final DataLevel level;

	public Mob(final double x, final double y, final double w, final double h,
			final DataLevel level) {
		super(x, y, w, h);
		this.level = level;
	}

	public void addWaypointPx(final double x, final double y) {
		waypoints.add(new Point2D.Double(x, y));
		processWaypoints();
	}

	public void setWaypointPx(final double x, final double y) {
		waypoints.clear();
		currentWaypoint = null;
		addWaypointPx(x, y);
	}

	protected void processWaypoints() {
		if (distanceToWalkX == 0 && distanceToWalkY == 0) {
			currentWaypoint = null;
		}

		if (currentWaypoint == null && !waypoints.isEmpty()) {
			currentWaypoint = waypoints.remove();
			final Point2D.Double base = getBasePointPx();
			final double angle = Math.atan2(currentWaypoint.y - base.y,
					currentWaypoint.x - base.x);
			velocityXPPS = Math.cos(angle) * speed;
			velocityYPPS = Math.sin(angle) * speed;
			distanceToWalkX = currentWaypoint.x - base.x;
			distanceToWalkY = currentWaypoint.y - base.y;
		}
	}

	public Point2D.Double getBasePointPx() {
		final double x = topLeftX + getTileWidth() * (DataTile.WIDTH / 2);
		final double y = topLeftY + getTileHeight() * DataTile.HEIGHT;
		return new Point.Double(x, y);
	}

	abstract public int getTileHeight();

	abstract public int getTileWidth();

	@Override
	protected void moveX(final double velocityX) {
		if (distanceToWalkX != 0) {
			topLeftX += velocityX;
			distanceToWalkX -= velocityX;

			if (Math.signum(velocityX) != Math.signum(distanceToWalkX)) {
				topLeftX += distanceToWalkX;
				distanceToWalkX = 0;
			}
		}
	}

	@Override
	protected void moveY(final double velocityY) {
		if (distanceToWalkY != 0) {
			topLeftY += velocityY;
			distanceToWalkY -= velocityY;

			if (Math.signum(velocityY) != Math.signum(distanceToWalkY)) {
				topLeftY += distanceToWalkY;
				distanceToWalkY = 0;
			}
		}
	}

	@Override
	protected void _postMove() {
		super._postMove();
		final int tileX = getTileX();
		final int tileY = getTileY();
		if (tileX != prevTileX || tileY != prevTileY) {
			tileChangeListener.tileChanged(this);
			prevTileX = tileX;
			prevTileY = tileY;
		}

		processWaypoints();
	}

	public void setTileChangeListener(
			final TileChangeListener tileChangeListener) {
		this.tileChangeListener = tileChangeListener;
	}

	public int getTileX() {
		return (int) Math.round(topLeftX / DataTile.WIDTH) + getTileWidth() / 2;
	}

	public int getTileY() {
		return (int) Math.floor(topLeftY / DataTile.HEIGHT) + getTileHeight();
	}

	public void walkTo(final int x, final int y) {
		final AStar pathing = new AStar(level,
				new Point(getTileX(), getTileY()), new Point(
						x / DataTile.WIDTH, y / DataTile.HEIGHT));
		waypoints.clear();
		currentWaypoint = null;
		final Path path = pathing.getPath();
		if (path != null) {
			final List<Point> waypoints = path.getWaypoints();
			for (final Point point : waypoints) {
				addWaypointPx(point.x * DataTile.WIDTH + DataTile.WIDTH / 2,
						point.y * DataTile.HEIGHT + DataTile.HEIGHT / 2);
			}
		}
	}
}
