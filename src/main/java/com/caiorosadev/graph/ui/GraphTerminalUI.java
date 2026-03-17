package com.caiorosadev.graph.ui;

import com.caiorosadev.graph.builder.GraphBuilder;
import com.caiorosadev.graph.core.Graph;

import java.util.Optional;
import java.util.Scanner;

/**
 * UI em terminal para construir e explorar um grafo.
 * Sempre começa na Fase 1 (construção); só após "pronto" vai para a Fase 2 (exploração).
 */
public class GraphTerminalUI {

    private final Scanner scanner;
    private final BuildPhaseHandler buildHandler;
    private final ExplorePhaseHandler exploreHandler;

    private GraphBuilder builder;
    private Graph graph;
    private boolean tutorialFase1JaMostrado;

    public GraphTerminalUI(Scanner scanner) {
        this.scanner = scanner;
        this.buildHandler = new BuildPhaseHandler();
        this.exploreHandler = new ExplorePhaseHandler();
        this.builder = new GraphBuilder();
        this.graph = null;
        this.tutorialFase1JaMostrado = false;
    }

    public void run() {
        printTutorialFase1();

        while (true) {
            if (graph == null) {
                runBuildPhase();
            } else {
                boolean quit = runExplorePhase();
                if (quit) break;
            }
        }

        System.out.println("Adeus :)");
    }

    private void printTutorialFase1() {
        if (tutorialFase1JaMostrado) return;
        tutorialFase1JaMostrado = true;
        System.out.println("  ┌─ FASE 1: CONSTRUÇÃO ──────────────────────────────────┐");
        System.out.println("  │  Siga estes passos (em qualquer ordem, exceto o 4):   │");
        System.out.println("  │                                                       │");
        System.out.println("  │  1. Definir o tipo do grafo:                          │");
        System.out.println("  │  'tipo d' → direcionado  'tipo nd' → não direcionado  |");
        System.out.println("  │                                                       │");
        System.out.println("  │  2. Adicionar nós (use números como identificador):   │");
        System.out.println("  │  'no 1' → um nó ou utilize 'nos 1 2 3 4' para vários  |");
        System.out.println("  │                                                       │");
        System.out.println("  │  3. Adicionar conexões entre nós:                     │");
        System.out.println("  │  'aresta 1 2' → liga nó 1 ao nó 2                     │");
        System.out.println("  │                                                       │");
        System.out.println("  │  4. Quando terminar, finalize a construção:           │");
        System.out.println("  │  'build' → Programa entra na fase de exploração       │");
        System.out.println("  └───────────────────────────────────────────────────────┘");
        System.out.println();
    }

    private void runBuildPhase() {
        System.out.print(buildHandler.getPrompt());
        if (!scanner.hasNextLine()) return;
        String line = scanner.nextLine().trim();

        Optional<Graph> built = buildHandler.process(builder, line);
        built.ifPresent(g -> {
            graph = g;
            printEntradaFase2();
        });
    }

    private void printEntradaFase2() {
        System.out.println();
        System.out.println("  ┌─ FASE 2: EXPLORAÇÃO ──────────────────────────┐");
        System.out.println("  │  O grafo está pronto. Agora você só pode:     │");
        System.out.println("  │                                               │");
        System.out.println("  │  • matriz      → ver matriz de adjacência     │");
        System.out.println("  │  • dfs <id>    → busca em profundidade        │");
        System.out.println("  │  • bfs <id>    → busca em largura             │");
        System.out.println("  │  • sair        → encerrar o programa          │");
        System.out.println("  │                                               │");
        System.out.println("  │  Digite  ?  para ver todos os comandos.       │");
        System.out.println("  └─────────────────────────────────────────── ───┘");
        System.out.println();
    }

    private boolean runExplorePhase() {
        System.out.print(exploreHandler.getPrompt());
        if (!scanner.hasNextLine()) return false;
        String line = scanner.nextLine().trim();

        return exploreHandler.process(graph, line);
    }
}
