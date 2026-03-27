package com.caiorosadev.graph.core;

import com.caiorosadev.graph.enums.GraphType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph {

    @Getter
    @Setter
    private GraphType graphType = GraphType.DIRECTED;

    @Getter
    private final List<Node> nodes = new ArrayList<>();

    public Graph() {
    }

    public Graph(GraphType graphType) {
        this.graphType = Objects.requireNonNull(graphType);
    }

    public Optional<Node> findNode(String id) {
        if (id == null) {
            return Optional.empty();
        }
        for (Node node : nodes) {
            if (node.getId().equals(id)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public void append(Node node) {
        nodes.add(node);
        
        sortNodesById();
    }

    public boolean addNode(String id) {
        if (findNode(id).isPresent()) {
            return false;
        }
        
        append(new Node(id));
        
        return true;
    }

    public boolean removeNode(String id) {
        Optional<Node> opt = findNode(id);
        
        if (opt.isEmpty()) {
            return false;
        }
        
        Node target = opt.get();
        
        for (Node node : nodes) {
            node.removeEdge(target);
        }
        
        nodes.remove(target);
        
        return true;
    }

    public boolean addEdge(String fromId, String toId, double weight) {
        Optional<Node> a = findNode(fromId);
        Optional<Node> b = findNode(toId);
        
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        
        Node from = a.get();
        Node to = b.get();
        
        from.addEdge(to, weight);
        if (graphType == GraphType.UNDIRECTED) {
            to.addEdge(from, weight);
        }
        
        return true;
    }

    public boolean removeEdge(String fromId, String toId) {
        Optional<Node> a = findNode(fromId);
        Optional<Node> b = findNode(toId);

        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        
        Node from = a.get();
        Node to = b.get();
        
        boolean removed = from.removeEdge(to);
        
        if (graphType == GraphType.UNDIRECTED) {
            to.removeEdge(from);
        }
        
        return removed;
    }

    private void sortNodesById() {
        nodes.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
    }

    public int edgeCount() {
        if (graphType == GraphType.DIRECTED) {
            return nodes.stream().mapToInt(node -> node.getEdges().size()).sum();
        }

        Set<String> pairs = new HashSet<>();

        for (Node node : nodes) {
            String nodeId = node.getId();
            for (Node neighbor : node.getNeighbors()) {
                String neighborId = neighbor.getId();
                String smallerId = nodeId.compareTo(neighborId) <= 0 ? nodeId : neighborId;
                String largerId = nodeId.compareTo(neighborId) <= 0 ? neighborId : nodeId;
                pairs.add(smallerId + "\0" + largerId);
            }
        }

        return pairs.size();
    }

    public List<Node> depthFirstTraversal(Node start) {
        List<Node> order = new ArrayList<>();
        if (start == null || !nodes.contains(start)) {
            return order;
        }

        Set<Node> visited = new HashSet<>();
        
        dfsVisit(start, visited, order);
        
        return List.copyOf(order);
    }

    private void dfsVisit(Node current, Set<Node> visited, List<Node> order) {
        visited.add(current);
        order.add(current);
        current.neighborsSortedById().stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .forEach(neighbor -> dfsVisit(neighbor, visited, order));
    }

    public List<Node> breadthFirstTraversal(Node start) {
        List<Node> order = new ArrayList<>();

        if (start == null || !nodes.contains(start)) {
            return order;
        }
        
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new ArrayDeque<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            order.add(current);
        
            for (Node neighbor : current.neighborsSortedById()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return List.copyOf(order);
    }

    public List<Node> nodesWithEdges() {
        return nodes.stream()
                .filter(node -> !node.getEdges().isEmpty())
                .collect(Collectors.toList());
    }

    public List<Node> nodesExcept(Node exclude) {
        return nodes.stream()
                .filter(node -> exclude == null || !node.equals(exclude))
                .sorted(Comparator.comparing(Node::getId))
                .collect(Collectors.toList());
    }

    public List<Node> validTargetsForNewEdge(Node from) {
        if (from == null || !nodes.contains(from)) {
            return List.of();
        }

        return nodes.stream()
                .filter(node -> !node.equals(from))
                .filter(node -> !from.hasEdgeTo(node))
                .sorted(Comparator.comparing(Node::getId))
                .collect(Collectors.toList());
    }
}
