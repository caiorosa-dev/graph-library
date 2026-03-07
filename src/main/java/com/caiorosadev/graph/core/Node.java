package com.caiorosadev.graph.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Node {
    @Getter private final String id;
    @Getter private final List<Node> edges;

    public Node(String id) {
        this.id = id;
        this.edges = new ArrayList<>();
    }

    void appendEdge(Node node) {
        edges.add(node);
    }
}
