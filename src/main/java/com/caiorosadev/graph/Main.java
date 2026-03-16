package com.caiorosadev.graph;

import com.caiorosadev.graph.builder.GraphBuilder;
import com.caiorosadev.graph.enums.GraphType;
import lombok.var;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var builder = new GraphBuilder();

        builder.setType(GraphType.UNDIRECTED);
        builder.setupNodes(List.of(1, 2, 3, 4));

        builder.addEdge(1, 2);
        builder.addEdge(2, 3);
        builder.addEdge(3, 1);

        var graph = builder.build();

        graph.print();
    }
}