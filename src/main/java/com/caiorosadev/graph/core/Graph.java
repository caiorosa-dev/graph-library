package com.caiorosadev.graph.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    @Getter
    private final List<Node> nodes;

    public Graph() {
        this.nodes = new ArrayList<>();
    }

    public void append(Node node) {
        this.nodes.add(node);
    }

    public void print() {

    }
}
