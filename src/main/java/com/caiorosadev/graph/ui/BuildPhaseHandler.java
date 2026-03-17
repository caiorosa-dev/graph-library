package com.caiorosadev.graph.ui;

import com.caiorosadev.graph.builder.GraphBuilder;
import com.caiorosadev.graph.core.Graph;
import com.caiorosadev.graph.enums.GraphType;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BuildPhaseHandler {

    private static final String PROMPT = "grafo> ";

    public String getPrompt() {
        return PROMPT;
    }

    public Optional<Graph> process(GraphBuilder builder, String line) {
        if (line == null || (line = line.trim()).isEmpty()) {
            return Optional.empty();
        }

        Scanner sc = new Scanner(line);
        String cmd = sc.hasNext() ? sc.next().toLowerCase() : "";

        switch (cmd) {
            case "tipo":
                handleTipo(builder, sc);
                return Optional.empty();
            case "no":
            case "n":
                handleNo(builder, sc);
                return Optional.empty();
            case "nos":
                handleNos(builder, sc);
                return Optional.empty();
            case "aresta":
            case "a":
            case "conectar":
            case "c":
                handleAresta(builder, sc);
                return Optional.empty();
            case "construir":
            case "build":
            case "pronto":
                return handleConstruir(builder, sc);
            case "tutorial":
                printTutorial();
                return Optional.empty();
            case "ajuda":
            case "help":
            case "?":
                printAjuda();
                return Optional.empty();
            default:
                System.out.println("Comando desconhecido: '" + cmd + "'. Digite  ?  ou  ajuda  para ver os comandos.");
                return Optional.empty();
        }
    }

    private void handleTipo(GraphBuilder builder, Scanner sc) {
        if (!sc.hasNext()) {
            System.out.println("Uso: tipo d  ou  tipo nd  (direcionado / não direcionado)");
            return;
        }
        String t = sc.next().toLowerCase();
        if (t.equals("d") || t.equals("direto") || t.equals("direcionado")) {
            builder.setType(GraphType.DIRECTED);
            System.out.println("Ok. Grafo definido como direcionado.");
        } else if (t.equals("nd") || t.equals("nao-direto") || t.equals("nao-direcionado")) {
            builder.setType(GraphType.UNDIRECTED);
            System.out.println("Ok. Grafo definido como não direcionado.");
        } else {
            System.out.println("Tipo inválido. Use: tipo d  ou  tipo nd");
        }
    }

    private boolean handleNo(GraphBuilder builder, Scanner sc) {
        if (!sc.hasNext()) {
            System.out.println("Uso: no <id>  (ex: no 1)");
            return false;
        }
        int id = parseNodeId(sc.next());
        if (id < 0) return false;
        builder.setupNodes(List.of(id));
        System.out.println("Nó " + id + " adicionado.");
        return true;
    }

    private boolean handleNos(GraphBuilder builder, Scanner sc) {
        List<Integer> ids = new java.util.ArrayList<>();
        while (sc.hasNext()) {
            int id = parseNodeId(sc.next());
            if (id < 0) return false;
            ids.add(id);
        }
        if (ids.isEmpty()) {
            System.out.println("Uso: nos <id1> [id2 id3 ...]  (ex: nos 1 2 3 4)");
            return false;
        }
        builder.setupNodes(ids);
        System.out.println("Nós adicionados: " + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        return true;
    }

    private boolean handleAresta(GraphBuilder builder, Scanner sc) {
        if (!sc.hasNext()) {
            System.out.println("Uso: aresta <de> <para>  (ex: aresta 1 2)");
            return false;
        }
        int from = parseNodeId(sc.next());
        if (from < 0) return false;
        if (!sc.hasNext()) {
            System.out.println("Uso: aresta <de> <para>  (ex: aresta 1 2)");
            return false;
        }
        int to = parseNodeId(sc.next());
        if (to < 0) return false;
        builder.addEdge(from, to);
        System.out.println("Aresta " + from + " -> " + to + " adicionada.");
        return true;
    }

    private Optional<Graph> handleConstruir(GraphBuilder builder, Scanner sc) {
        try {
            Graph graph = builder.build();
            System.out.println("Grafo construído com sucesso. Entrando na Fase 2 (exploração)...");
            return Optional.of(graph);
        } catch (Exception e) {
            System.out.println("Erro ao construir: " + e.getMessage());
            return Optional.empty();
        }
    }

    private int parseNodeId(String s) {
        try {
            int id = Integer.parseInt(s.trim());
            if (id < 0) {
                System.out.println("ID de nó deve ser um número não negativo.");
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("ID de nó inválido: '" + s + "'. Use um número.");
            return -1;
        }
    }

    private void printAjuda() {
        System.out.println();
        System.out.println("  Comandos (Fase 1 — construção):");
        System.out.println("  - tipo d         grafo direcionado");
        System.out.println("  - tipo nd        grafo não direcionado");
        System.out.println("  - no <id>        adiciona um nó (ex: no 1)");
        System.out.println("  - nos <id> ...   adiciona vários (ex: nos 1 2 3 4)");
        System.out.println("  - aresta <de> <para>  ou  a <de> <para>  (ex: aresta 1 2)");
        System.out.println("  - build         finaliza e vai para a Fase 2");
        System.out.println("  - tutorial       mostra o passo a passo de novo");
        System.out.println("  - ?  ou  ajuda   esta mensagem");
        System.out.println();
    }

    public void printTutorial() {
        System.out.println();
        System.out.println("  Passos na Fase 1:");
        System.out.println("  1. tipo d  ou  tipo nd");
        System.out.println("  2. no 1  ou  nos 1 2 3 4");
        System.out.println("  3. aresta 1 2  (repetir para cada conexão)");
        System.out.println("  4. build  (quando terminar)");
        System.out.println();
    }
}
