package com.caiorosadev.graph.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {

    private String from;
    private String to;
    private double weight;

    public static Edge between(Node from, Node to, double weight) {
        return new Edge(from.getId(), to.getId(), weight);
    }
}
