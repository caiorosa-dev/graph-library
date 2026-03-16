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
        System.out.print("  | ");
        for (Node node : nodes) {
            System.out.print(node.getId() + " | ");
        }
        System.out.println();
        for (Node x : nodes) {
            System.out.print(x.getId() + " | ");
            for (Node y : nodes) {
                System.out.print((x.hasEdgeWith(y) ? "X" : " ") + " | ");
            }
            System.out.println();
        }
    }
}
