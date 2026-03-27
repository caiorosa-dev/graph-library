package com.caiorosadev.graph.ui.preview;

import com.caiorosadev.graph.core.Graph;
import com.caiorosadev.graph.core.Node;
import com.caiorosadev.graph.enums.GraphType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class AsciiGraphPreview {

    private AsciiGraphPreview() {
    }

    public static String render(Graph graph) {
        if (graph == null) {
            return "(sem grafo)";
        }
        StringBuilder sb = new StringBuilder();
        String typeLabel = graph.getGraphType() == GraphType.DIRECTED
                ? "direcionado"
                : "não direcionado";
        sb.append("Tipo: ").append(typeLabel).append('\n');
        sb.append("Nós: ").append(graph.getNodes().size())
                .append("  |  Arestas: ").append(graph.edgeCount()).append('\n');
        sb.append("Nós isolados (grau 0): ").append(countIsolated(graph)).append('\n');
        sb.append("---\n");

        List<Node> sorted = new ArrayList<>(graph.getNodes());
        sorted.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));

        if (graph.getGraphType() == GraphType.DIRECTED) {
            for (Node node : sorted) {
                List<Node> targets = node.neighborsSortedById();
                sb.append(node.getId());
                if (targets.isEmpty()) {
                    sb.append("  (sem saídas)");
                } else {
                    sb.append(" -> ");
                    sb.append(targets.stream()
                            .map(target -> target.getId() + "(" + formatWeight(node.getEdgeWeight(target).orElse(0)) + ")")
                            .collect(Collectors.joining(", ")));
                }
                sb.append('\n');
            }

            return sb.toString();
        }

        Set<String> printed = new HashSet<>();
        List<String> lines = new ArrayList<>();
        for (Node node : sorted) {
            String nodeId = node.getId();
            for (Node neighbor : node.getNeighbors()) {
                String neighborId = neighbor.getId();
                String smallerId = nodeId.compareTo(neighborId) <= 0 ? nodeId : neighborId;
                String largerId = nodeId.compareTo(neighborId) <= 0 ? neighborId : nodeId;
                String key = smallerId + "\0" + largerId;
                if (printed.add(key)) {
                    double weight = node.getEdgeWeight(neighbor).orElse(0);
                    lines.add(smallerId + " --(" + formatWeight(weight) + ")-- " + largerId);
                }
            }
        }

        Collections.sort(lines);
        if (lines.isEmpty()) {
            sb.append("(nenhuma aresta)\n");
        } else {
            for (String line : lines) {
                sb.append(line).append('\n');
            }
        }

        return sb.toString();
    }

    public static String renderAdjacencyMatrix(Graph graph) {
        if (graph == null || graph.getNodes().isEmpty()) {
            return "(grafo vazio)\n";
        }

        List<Node> columnOrder = new ArrayList<>(graph.getNodes());
        columnOrder.sort(Comparator.comparing(Node::getId, Comparator.naturalOrder()));
        int labelW = columnOrder.stream().mapToInt(node -> node.getId().length()).max().orElse(1);
        StringBuilder sb = new StringBuilder();
        
        sb.append(" ".repeat(Math.max(0, labelW + 2)));
        for (Node columnNode : columnOrder) {
            sb.append(String.format(Locale.ROOT, "%" + Math.max(4, columnNode.getId().length() + 2) + "s", columnNode.getId()));
        }
        
        sb.append('\n');
        
        for (Node rowNode : columnOrder) {
            sb.append(String.format(Locale.ROOT, "%-" + (labelW + 2) + "s", rowNode.getId()));
        
            for (Node columnNode : columnOrder) {
                String cell;

                if (rowNode.hasEdgeTo(columnNode)) {
                    cell = formatWeight(rowNode.getEdgeWeight(columnNode).orElse(0));
                } else {
                    cell = "—";
                }
                
                int colWidth = Math.max(4, columnNode.getId().length() + 2);
                sb.append(String.format(Locale.ROOT, "%" + colWidth + "s", cell));
            }
            
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String formatWeight(double weight) {
        if (weight == (long) weight) {
            return String.format(Locale.ROOT, "%d", (long) weight);
        }

        return String.format(Locale.ROOT, "%.2f", weight);
    }

    private static int countIsolated(Graph graph) {
        int count = 0;

        for (Node node : graph.getNodes()) {
            if (graph.getGraphType() == GraphType.UNDIRECTED) {
                if (node.getEdges().isEmpty()) {
                    count++;
                }
            } else {
                boolean hasOutgoingEdge = !node.getEdges().isEmpty();
                boolean hasIncomingEdge = false;
                for (Node candidate : graph.getNodes()) {
                    if (candidate.hasEdgeTo(node)) {
                        hasIncomingEdge = true;
                        break;
                    }
                }
                if (!hasOutgoingEdge && !hasIncomingEdge) {
                    count++;
                }
            }
        }

        return count;
    }
}
