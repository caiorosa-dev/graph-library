package com.caiorosadev.graph.core;

import lombok.Getter;
import lombok.var;

import java.util.*;

public class Node {
    @Getter private final String id;
    @Getter private final Set<Node> edges;

    public Node(String id) {
        this.id = id;
        this.edges = new HashSet<>();
    }

    public void appendEdge(Node node) {
        edges.add(node);
    }

    public boolean hasEdgeWith(Node node) {
        return edges.contains(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private void printPath(Set<Node> path) {
        for (var node : path) {
            System.out.print(node.id + " -> ");
        }

        System.out.print("Finalizado");
        System.out.println();
    }

    public boolean deepSearch(String searchId, Set<Node> visited) {
        if (visited == null) {
            visited = new HashSet<>();
        }

        visited.add(this);

        if (this.id.equals(searchId)) {
            printPath(visited);
            return true;
        }

        for (var edgeNode : edges) {
            if (visited.contains(edgeNode)) {
                continue;
            }

            if (edgeNode.deepSearch(searchId, visited)) {
                return true;
            }
        }

        return false;
    }

    public boolean breadthSearch(String searchId) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(this);
        visited.add(this);

        while (!queue.isEmpty()) {
            var current = queue.poll();

            if (current.id.equals(searchId)) {
                printPath(visited);
                return true;
            }

            for (Node edge : current.getEdges()) {
                if (!visited.contains(edge)) {
                    visited.add(edge);
                    queue.add(edge);
                }
            }
        }

        return false;
    }
}
