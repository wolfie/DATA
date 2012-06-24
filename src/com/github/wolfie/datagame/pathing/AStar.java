package com.github.wolfie.datagame.pathing;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.wolfie.datagame.DataLevel;

public class AStar {
	private final DataLevel level;
	private final Point start;
	private final Point end;

	private final Set<Node> closedSet = new HashSet<>();
	private final Set<Node> openSet = new HashSet<>();
	private final Map<Node, Node> cameFrom = new HashMap<>();
	private final Map<Node, Double> gScore = new HashMap<>();
	private final Map<Node, Double> fScore = new HashMap<>();
	private final Node startNode;
	private final Node endNode;

	public AStar(final DataLevel level, final Point start, final Point end) {
		this.level = level;
		this.start = start;
		this.end = end;

		startNode = Node.from(start, level);
		endNode = Node.from(end, level);

		openSet.add(startNode);
		gScore.put(startNode, 0.0d);
		fScore.put(startNode, Node.guesstimateCost(startNode, endNode));
	}

	public Path getPath() {
		while (!openSet.isEmpty()) {
			final Node current = getLowestFScoreNodeInOpenSet();
			if (current.equals(endNode)) {
				return reconstructPath(cameFrom, endNode);
			}

			openSet.remove(current);
			closedSet.add(current);

			for (final Node neighbor : current.getNeighbors()) {
				if (closedSet.contains(neighbor)) {
					continue;
				}
				final double maybeGScore = gScore.get(current)
						+ Node.navigationCost(current, neighbor);

				if (!openSet.contains(neighbor)
						|| maybeGScore < gScore.get(neighbor)) {
					openSet.add(neighbor);
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, maybeGScore);
					fScore.put(
							neighbor,
							maybeGScore
									+ Node.guesstimateCost(neighbor, endNode));
				}
			}
		}

		return null;
	}

	private Path reconstructPath(final Map<Node, Node> cameFrom,
			final Node currentNode) {
		if (cameFrom.containsKey(currentNode)) {
			final Path path = reconstructPath(cameFrom,
					cameFrom.get(currentNode));
			path.add(currentNode);
			return path;
		} else {
			return new Path(currentNode, level);
		}
	}

	private Node getLowestFScoreNodeInOpenSet() {
		Double lowestScore = Double.MAX_VALUE;
		Node lowestNode = null;
		for (final Node openNode : openSet) {
			if (fScore.containsKey(openNode)) {
				final Double nodeScore = fScore.get(openNode);
				if (nodeScore < lowestScore) {
					lowestScore = nodeScore;
					lowestNode = openNode;
				}
			}
		}
		return lowestNode;
	}
}
