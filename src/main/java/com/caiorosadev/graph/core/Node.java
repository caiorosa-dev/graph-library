package com.caiorosadev.graph.core;

import lombok.Getter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

public final class Node {

    @Getter
    private final String id;

    private final Map<Node, Edge> edges = new HashMap<>();

    public Node(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public void addEdge(Node target, double weight) {
        Objects.requireNonNull(target, "target");

        edges.put(target, Edge.between(this, target, weight));
    }

    public boolean removeEdge(Node target) {
        return edges.remove(target) != null;
    }

    public boolean hasEdgeTo(Node node) {
        return edges.containsKey(node);
    }

    public OptionalDouble getEdgeWeight(Node target) {
        Edge edge = edges.get(target);

        return edge == null ? OptionalDouble.empty() : OptionalDouble.of(edge.getWeight());
    }

    public Map<Node, Edge> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    public Set<Node> getNeighbors() {
        return Collections.unmodifiableSet(edges.keySet());
    }

    public List<Node> neighborsSortedById() {
        return edges.keySet().stream()
                .sorted(Comparator.comparing(Node::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Node)) {
            return false;
        }

        Node that = (Node) other;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
