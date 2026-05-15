package com.caiorosadev.graph.core;

import com.caiorosadev.graph.enums.GraphType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class GraphColoringExporter {

    private GraphColoringExporter() {}

    public static String export(Graph graph, ColoringResult result) throws IOException {
        String json = buildJson(graph, result);
        Files.write(Paths.get("graph-coloring.json"), json.getBytes(StandardCharsets.UTF_8));
        return Paths.get("graph-coloring.json").toAbsolutePath().toString();
    }

    static String buildJson(Graph graph, ColoringResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"graphType\": \"").append(graph.getGraphType().name()).append("\",\n");
        sb.append("  \"chromaticNumber\": ").append(result.getChromaticNumber()).append(",\n");

        sb.append("  \"coloringSequence\": [");
        List<Node> seq = result.getColoringSequence();
        for (int i = 0; i < seq.size(); i++) {
            sb.append("\"").append(escape(seq.get(i).getId())).append("\"");
            if (i < seq.size() - 1) sb.append(", ");
        }
        sb.append("],\n");

        sb.append("  \"nodes\": [\n");
        List<Node> sortedNodes = graph.getNodes();
        Map<Node, Integer> colors = result.getColors();
        for (int i = 0; i < sortedNodes.size(); i++) {
            Node node = sortedNodes.get(i);
            sb.append("    { \"id\": \"").append(escape(node.getId()))
              .append("\", \"color\": ").append(colors.getOrDefault(node, 0)).append(" }");
            if (i < sortedNodes.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"edges\": [\n");
        List<String[]> edges = collectEdges(graph);
        for (int i = 0; i < edges.size(); i++) {
            String[] e = edges.get(i);
            sb.append("    { \"from\": \"").append(escape(e[0]))
              .append("\", \"to\": \"").append(escape(e[1]))
              .append("\", \"weight\": ").append(e[2]).append(" }");
            if (i < edges.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    private static List<String[]> collectEdges(Graph graph) {
        List<String[]> result = new ArrayList<>();
        if (graph.getGraphType() == GraphType.DIRECTED) {
            for (Node node : graph.getNodes()) {
                for (Node neighbor : node.neighborsSortedById()) {
                    result.add(new String[]{ node.getId(), neighbor.getId(),
                            formatWeight(node.getEdgeWeight(neighbor).orElse(0)) });
                }
            }
        } else {
            Set<String> seen = new HashSet<>();
            for (Node node : graph.getNodes()) {
                String nodeId = node.getId();
                for (Node neighbor : node.neighborsSortedById()) {
                    String neighborId = neighbor.getId();
                    String smallerId = nodeId.compareTo(neighborId) <= 0 ? nodeId : neighborId;
                    String largerId  = nodeId.compareTo(neighborId) <= 0 ? neighborId : nodeId;
                    String key = smallerId + "\0" + largerId;
                    if (seen.add(key)) {
                        result.add(new String[]{ smallerId, largerId,
                                formatWeight(node.getEdgeWeight(neighbor).orElse(0)) });
                    }
                }
            }
        }
        return result;
    }

    private static String formatWeight(double w) {
        if (w == (long) w) {
            return (long) w + ".0";
        }
        return String.format(Locale.ROOT, "%.6g", w).replaceAll("0+$", "").replaceAll("\\.$", ".0");
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
