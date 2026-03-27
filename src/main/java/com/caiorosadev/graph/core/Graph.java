package com.caiorosadev.graph.core;

import com.caiorosadev.graph.enums.GraphType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
            int total = 0;
            for (Node node : nodes) {
                total += node.getEdges().size();
            }
            return total;
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

        return order;
    }

    private void dfsVisit(Node current, Set<Node> visited, List<Node> result) {
        visited.add(current);
        result.add(current);
        for (Node neighbor : current.neighborsSortedById()) {
            if (!visited.contains(neighbor)) {
                dfsVisit(neighbor, visited, result);
            }
        }
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

        return order;
    }

    public List<Node> nodesWithEdges() {
        List<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (!node.getEdges().isEmpty()) {
                result.add(node);
            }
        }
        return result;
    }

    public List<Node> nodesExcept(Node exclude) {
        List<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (exclude == null || !node.equals(exclude)) {
                result.add(node);
            }
        }
        result.sort(Comparator.comparing(Node::getId));
        return result;
    }

    public List<Node> validTargetsForNewEdge(Node from) {
        if (from == null || !nodes.contains(from)) {
            return new ArrayList<>();
        }

        List<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (!node.equals(from) && !from.hasEdgeTo(node)) {
                result.add(node);
            }
        }
        result.sort(Comparator.comparing(Node::getId));
        return result;
    }

    public List<Node> transitiveClosureForward(Node start) {
        if (start == null || !nodes.contains(start)) {
            return new ArrayList<>();
        }

        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new ArrayDeque<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Node neighbor : current.neighborsSortedById()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        List<Node> result = new ArrayList<>(visited);
        result.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
        return result;
    }

    public List<Node> transitiveClosureBackward(Node target) {
        if (target == null || !nodes.contains(target)) {
            return new ArrayList<>();
        }

        Map<Node, List<Node>> predecessors = buildPredecessorLists();

        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new ArrayDeque<>();

        queue.add(target);
        visited.add(target);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            List<Node> currentPredecessors = predecessors.get(current);
            if (currentPredecessors != null) {
                for (Node predecessor : currentPredecessors) {
                    if (!visited.contains(predecessor)) {
                        visited.add(predecessor);
                        queue.add(predecessor);
                    }
                }
            }
        }

        List<Node> result = new ArrayList<>(visited);
        result.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
        return result;
    }

    public boolean isStronglyConnected() {
        if (nodes.isEmpty()) {
            return true;
        }

        List<List<Node>> components = stronglyConnectedComponents();

        return components.size() == 1 && components.get(0).size() == nodes.size();
    }

    public List<List<Node>> stronglyConnectedComponents() {
        List<List<Node>> result = new ArrayList<>();

        if (nodes.isEmpty()) {
            return result;
        }

        // DFS no grafo original empilhando na ordem de término
        Set<Node> visited = new HashSet<>();
        Deque<Node> finishOrder = new ArrayDeque<>();

        List<Node> sorted = new ArrayList<>(nodes);
        sorted.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));

        for (Node node : sorted) {
            if (!visited.contains(node)) {
                dfsCollectFinishOrder(node, visited, finishOrder);
            }
        }

        // DFS no grafo reverso na ordem inversa de término, cada profundidade é um CFC (componente fortemente conectado)
        Map<Node, List<Node>> predecessors = buildPredecessorLists();
        visited.clear();

        while (!finishOrder.isEmpty()) {
            Node node = finishOrder.pop();
            if (!visited.contains(node)) {
                List<Node> component = new ArrayList<>();
                dfsVisitReversed(node, predecessors, visited, component);
                component.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
                result.add(component);
            }
        }

        result.sort(Comparator.comparing(comp -> comp.get(0).getId(), Comparator.naturalOrder()));

        return result;
    }

    private void dfsCollectFinishOrder(Node current, Set<Node> visited, Deque<Node> finishOrder) {
        visited.add(current);
        for (Node neighbor : current.neighborsSortedById()) {
            if (!visited.contains(neighbor)) {
                dfsCollectFinishOrder(neighbor, visited, finishOrder);
            }
        }
        finishOrder.push(current);
    }

    private void dfsVisitReversed(Node current, Map<Node, List<Node>> predecessors, Set<Node> visited, List<Node> result) {
        visited.add(current);
        result.add(current);

        List<Node> currentPredecessors = predecessors.get(current);

        if (currentPredecessors != null) {
            for (Node predecessor : currentPredecessors) {
                if (!visited.contains(predecessor)) {
                    dfsVisitReversed(predecessor, predecessors, visited, result);
                }
            }
        }
    }

    // Roda cada nó do grafo, e cria uma lista das arestas invertidas (do destino para a origem).
    private Map<Node, List<Node>> buildPredecessorLists() {
        Map<Node, List<Node>> predecessors = new HashMap<>();
        for (Node source : nodes) {
            for (Node destination : source.getNeighbors()) {
                if (!predecessors.containsKey(destination)) {
                    predecessors.put(destination, new ArrayList<>());
                }
                predecessors.get(destination).add(source);
            }
        }

        for (List<Node> predecessorList : predecessors.values()) {
            predecessorList.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
        }

        return predecessors;
    }
}
