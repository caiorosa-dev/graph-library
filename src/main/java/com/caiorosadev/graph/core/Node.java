package com.caiorosadev.graph.core;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

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
}
