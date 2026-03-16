package com.caiorosadev.graph.builder;

import com.caiorosadev.graph.core.Graph;
import com.caiorosadev.graph.core.Node;
import com.caiorosadev.graph.enums.GraphType;
import lombok.Setter;
import lombok.var;

import java.util.*;

public class GraphBuilder {
    @Setter private GraphType type = GraphType.DIRECTED;
    private Set<Integer> nodeIds;
    private List<Edge> edges;

    public GraphBuilder() {
        nodeIds = new HashSet<>();
        edges = new ArrayList<>();
    }

    public void setupNodes(List<Integer> ids) {
        nodeIds.addAll(ids);
    }

    public void addEdge(int from, int to) {
        edges.add(new Edge(from, to));
    }

    public Graph build() {
        Graph graph = new Graph();
        Map<Integer, Node> nodesMap = new HashMap<>();

        for (Integer id : nodeIds) {
            var node = new Node(id.toString());

            nodesMap.put(id, node);

            graph.append(node);
        }

        for (Edge edge : edges) {
            var fromNode = nodesMap.get(edge.from);
            var toNode = nodesMap.get(edge.to);

            if (fromNode == null) {
                System.out.println("Invalid FROM node in provided edge.");
                continue;
            }

            fromNode.appendEdge(toNode);
            if (type == GraphType.UNDIRECTED) {
                toNode.appendEdge(fromNode);
            }
        }

        return graph;
    }
}
