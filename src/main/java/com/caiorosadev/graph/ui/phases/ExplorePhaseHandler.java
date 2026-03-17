package com.caiorosadev.graph.ui.phases;

import com.caiorosadev.graph.core.Graph;

import java.util.Scanner;

public class ExplorePhaseHandler {

    private static final String PROMPT = "explorar> ";

    public String getPrompt() {
        return PROMPT;
    }

    public boolean process(Graph graph, String line) {
        if (line == null || (line = line.trim()).isEmpty()) {
            return false;
        }

        Scanner sc = new Scanner(line);
        String cmd = sc.hasNext() ? sc.next().toLowerCase() : "";

        switch (cmd) {
            case "matriz":
            case "m":
                graph.print();
                return false;
            case "dfs":
            case "profundidade":
            case "p":
                handleDfs(graph, sc);
                return false;
            case "bfs":
            case "largura":
            case "l":
                handleBfs(graph, sc);
                return false;
            case "sair":
            case "quit":
            case "q":
            case "exit":
                return true;
            case "ajuda":
            case "help":
            case "?":
                printAjuda();
                return false;
            default:
                System.out.println("Comando desconhecido: '" + cmd + "'. Digite ajuda ou ? para ver os comandos.");
                return false;
        }
    }

    private void handleDfs(Graph graph, Scanner sc) {
        String id = sc.hasNext() ? sc.next() : null;
        if (id == null) {
            System.out.println("Uso: dfs <id>  (ex: dfs 3)");
            return;
        }
        boolean found = graph.deepSearch(id, null);
        if (found) {
            System.out.println("Nó '" + id + "' encontrado (busca em profundidade).");
        } else {
            System.out.println("Nó '" + id + "' não encontrado (busca em profundidade).");
        }
    }

    private void handleBfs(Graph graph, Scanner sc) {
        String id = sc.hasNext() ? sc.next() : null;
        if (id == null) {
            System.out.println("Uso: bfs <id>  (ex: bfs 3)");
            return;
        }
        boolean found = graph.breadthSearch(id, null);
        if (found) {
            System.out.println("Nó '" + id + "' encontrado (busca em largura).");
        } else {
            System.out.println("Nó '" + id + "' não encontrado (busca em largura).");
        }
    }

    private void printAjuda() {
        System.out.println();
        System.out.println("  Comandos (fase de exploração):");
        System.out.println("    matriz | m     — imprime a matriz de adjacência");
        System.out.println("    dfs <id> | p <id>   — busca em profundidade pelo nó");
        System.out.println("    bfs <id> | l <id>   — busca em largura pelo nó");
        System.out.println("    sair | q       — encerra o programa");
        System.out.println("    ajuda | ?       — esta mensagem");
        System.out.println();
    }
}
