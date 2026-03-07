package com.caiorosadev.graph;

import com.caiorosadev.graph.builder.GraphBuilder;
import lombok.var;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        var builder = new GraphBuilder();

        builder.setupNodes(List.of(1, 2, 3));

        var graph = builder.build();
    }
}