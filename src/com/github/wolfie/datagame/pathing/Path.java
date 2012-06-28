package com.github.wolfie.datagame.pathing;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import rlforj.los.BresOpportunisticLos;
import rlforj.los.ILosAlgorithm;

import com.github.wolfie.datagame.DataLevel;

public class Path {
	private final LinkedList<Node> nodes = new LinkedList<>();
	private boolean hasBeenOptimized = false;
	private final DataLevel level;

	public Path(final Node node, final DataLevel level) {
		this.level = level;
		nodes.add(node);
	}

	public void add(final Node node) {
		nodes.add(node);
	}

	public List<Point> getWaypoints() {
		optimizePathIfNeeded();
		final List<Point> waypoints = new ArrayList<>();
		for (final Node node : nodes) {
			waypoints.add(new Point(node.x, node.y));
		}
		return waypoints;
	}

	@Override
	public String toString() {
		optimizePathIfNeeded();
		final StringBuilder sb = new StringBuilder();
		for (final Node node : nodes) {
			sb.append(String.format("-> [%s,%s] ", node.x, node.y));
		}
		return sb.toString();
	}

	private void optimizePathIfNeeded() {
		if (!hasBeenOptimized) {
			final ILosAlgorithm a = new BresOpportunisticLos();

			Iterator<Node> endIter = nodes.descendingIterator();
			Node start = nodes.getFirst();
			Node end = endIter.next();
			final LinkedList<Node> optimizedNodes = new LinkedList<>();

			while (!start.equals(end)) {
				if (a.existsLineOfSight(level, start.x, start.y, end.x, end.y,
						false)) {
					optimizedNodes.add(end);
					start = end;
					endIter = nodes.descendingIterator();
				}
				end = endIter.next();
			}

			nodes.clear();
			nodes.addAll(optimizedNodes);
			hasBeenOptimized = true;
		}
	}
}
