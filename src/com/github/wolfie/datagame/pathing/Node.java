package com.github.wolfie.datagame.pathing;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.github.wolfie.datagame.DataLevel;

public class Node {
	public final int x;
	public final int y;
	private final DataLevel level;

	private Node(final int x, final int y, final DataLevel level) {
		this.x = x;
		this.y = y;
		this.level = level;
	}

	public static Node from(final Point point, final DataLevel level) {
		return new Node(point.x, point.y, level);
	}

	public Set<Node> getNeighbors() {
		final Set<Node> neighbors = new HashSet<>();
		for (int xx = x - 1; xx <= x + 1; xx++) {
			for (int yy = y - 1; yy <= y + 1; yy++) {
				if (xx == x && yy == y) {
					continue;
				}
				if (!level.isObstacle(xx, yy) && isInsideLevel(xx, yy)) {
					neighbors.add(new Node(xx, yy, level));
				}
			}
		}
		return neighbors;
	}

	private boolean isInsideLevel(final int xx, final int yy) {
		return xx >= 0 && yy >= 0 && xx < level.widthInTiles
				&& yy < level.widthInTiles;
	}

	public static double navigationCost(final Node from, final Node to) {
		return Math.hypot(from.x - to.x, from.y - to.y);
	}

	public static double guesstimateCost(final Node from, final Node to) {
		return navigationCost(from, to);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Node other = (Node) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Node [x=" + x + ", y=" + y + "]";
	}

}
