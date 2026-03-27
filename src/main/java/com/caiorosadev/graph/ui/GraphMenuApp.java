package com.caiorosadev.graph.ui;

import com.caiorosadev.graph.core.Graph;
import com.caiorosadev.graph.core.Node;
import com.caiorosadev.graph.enums.GraphType;
import com.caiorosadev.graph.ui.preview.AsciiGraphPreview;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.consoleui.prompt.InputResult;
import org.jline.consoleui.prompt.ListResult;
import org.jline.consoleui.prompt.PromptResultItemIF;
import org.jline.consoleui.prompt.builder.PromptBuilder;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class GraphMenuApp {

    private static final AttributedStyle TITLE_STYLE = AttributedStyle.DEFAULT.bold().foreground(6);
    private static final AttributedStyle DIM_STYLE = AttributedStyle.DEFAULT.foreground(245);
    private static final AttributedStyle OK_STYLE = AttributedStyle.DEFAULT.foreground(2);
    private static final AttributedStyle ERR_STYLE = AttributedStyle.DEFAULT.foreground(1);

    private final Terminal terminal;
    private final LineReader lineReader;
    private Graph graph;

    public GraphMenuApp(Terminal terminal, LineReader lineReader) {
        this.terminal = terminal;
        this.lineReader = lineReader;
    }


    public void run() throws IOException {
        while (true) {
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Grafos — menu principal", graphSummary()),
                    "main",
                    "O que deseja fazer?",
                    listItems(
                            item("new", "Novo grafo"),
                            item("edit", "Editar grafo", graph != null),
                            item("preview", "Visualização geral", graph != null),
                            item("explore", "Explorar", graph != null),
                            item("quit", "Sair")
                    )
            );

            if (choice == null) {
                continue;
            }

            switch (choice) {
                case "new":
                    wizardNewGraph();
                    break;
                case "edit":
                    loopEditMenu();
                    break;
                case "preview":
                    screenPreview();
                    break;
                case "explore":
                    loopExploreMenu();
                    break;
                case "quit":
                    clearScreen();
                    styled("Até logo :)", AttributedStyle.DEFAULT.bold().foreground(5));
                    return;
                default:
                    break;
            }
        }
    }


    private void wizardNewGraph() throws IOException {
        clearScreen();
        String typeChoice = showListPrompt(
                buildHeader("Novo grafo", null),
                "gtype",
                "Tipo do grafo:",
                listItems(
                        item("d", "Direcionado"),
                        item("nd", "Não direcionado")
                )
        );
        if (typeChoice == null) {
            return;
        }
        GraphType gt = "nd".equals(typeChoice) ? GraphType.UNDIRECTED : GraphType.DIRECTED;
        this.graph = new Graph(gt);
        loopEditMenu();
    }


    private void loopEditMenu() throws IOException {
        if (graph == null) {
            return;
        }
        while (true) {
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Editar grafo", graphSummary()),
                    "edit",
                    "Escolha:",
                    listItems(
                            item("addn", "Adicionar nós…"),
                            item("remn", "Remover nós…", !graph.getNodes().isEmpty()),
                            item("adde", "Adicionar arestas…", graph.getNodes().size() >= 2),
                            item("reme", "Remover arestas…", graph.edgeCount() > 0),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            switch (choice) {
                case "addn":
                    loopAddNodesSubmenu();
                    break;
                case "remn":
                    loopRemoveNodesSubmenu();
                    break;
                case "adde":
                    loopAddEdgesSubmenu();
                    break;
                case "reme":
                    loopRemoveEdgesSubmenu();
                    break;
                default:
                    break;
            }
        }
    }


    private void loopAddNodesSubmenu() throws IOException {
        while (true) {
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Adicionar nós", nodeListSummary()),
                    "addn",
                    "Escolha:",
                    listItems(
                            item("add", "Adicionar nó"),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            if ("add".equals(choice)) {
                clearScreen();
                String raw = showInputPrompt(
                        buildHeader("Novo nó", null),
                        "nid",
                        "ID do nó (letras e números):",
                        ""
                );
                if (raw == null) {
                    continue;
                }
                String id = NodeIdValidator.normalize(raw);
                if (!NodeIdValidator.isValid(id)) {
                    showMessage("Adicionar nó", styledLine("ID inválido. Use apenas letras e números.", ERR_STYLE));
                } else if (!graph.addNode(id)) {
                    showMessage("Adicionar nó", styledLine("Nó «" + id + "» já existe.", ERR_STYLE));
                } else {
                    showMessage("Adicionar nó", styledLine("Nó «" + id + "» adicionado.", OK_STYLE));
                }
            }
        }
    }


    private void loopRemoveNodesSubmenu() throws IOException {
        while (true) {
            if (graph.getNodes().isEmpty()) {
                showMessage("Remover nós", styledLine("Não há nós.", ERR_STYLE));
                return;
            }
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Remover nós", nodeListSummary()),
                    "rmn",
                    "Escolha:",
                    listItems(
                            item("rm", "Remover um nó"),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            if ("rm".equals(choice)) {
                clearScreen();
                String id = showListPrompt(
                        buildHeader("Remover nó", null),
                        "which",
                        "Nó a remover:",
                        nodeItems(sortedNodes())
                );
                if (id == null) {
                    continue;
                }
                if (graph.removeNode(id)) {
                    showMessage("Remover nós", styledLine("Nó «" + id + "» removido.", OK_STYLE));
                } else {
                    showMessage("Remover nós", styledLine("Não foi possível remover.", ERR_STYLE));
                }
            }
        }
    }


    private void loopAddEdgesSubmenu() throws IOException {
        while (true) {
            if (graph.getNodes().size() < 2) {
                showMessage("Adicionar arestas", styledLine("São necessários pelo menos dois nós.", ERR_STYLE));
                return;
            }
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Adicionar arestas", edgeListSummary()),
                    "adde",
                    "Escolha:",
                    listItems(
                            item("add", "Adicionar aresta"),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            if ("add".equals(choice)) {
                clearScreen();
                String fromId = showListPrompt(
                        buildHeader("Nova aresta — origem", null),
                        "from",
                        "Nó de origem:",
                        nodeItems(sortedNodes())
                );
                if (fromId == null) {
                    continue;
                }
                Optional<Node> fromNode = graph.findNode(fromId);
                if (fromNode.isEmpty()) {
                    continue;
                }
                List<Node> toOpts = graph.validTargetsForNewEdge(fromNode.get());
                if (toOpts.isEmpty()) {
                    showMessage("Nova aresta", styledLine("Não há destinos válidos a partir de «" + fromId + "».", ERR_STYLE));
                    continue;
                }
                clearScreen();
                String toId = showListPrompt(
                        buildHeader("Nova aresta — destino (de «" + fromId + "»)", null),
                        "to",
                        "Nó de destino:",
                        nodeItems(toOpts)
                );
                if (toId == null) {
                    continue;
                }
                clearScreen();
                String wRaw = showInputPrompt(
                        buildHeader("Nova aresta — peso", null),
                        "w",
                        "Peso da aresta (Enter = 1):",
                        "1"
                );
                double weight;
                try {
                    weight = parseWeight(wRaw);
                } catch (NumberFormatException e) {
                    showMessage("Adicionar arestas", styledLine("Peso inválido.", ERR_STYLE));
                    continue;
                }
                if (graph.addEdge(fromId, toId, weight)) {
                    showMessage("Adicionar arestas",
                            styledLine("Aresta «" + fromId + "» → «" + toId + "» (peso " + formatWeight(weight) + ") adicionada.", OK_STYLE));
                } else {
                    showMessage("Adicionar arestas", styledLine("Não foi possível adicionar.", ERR_STYLE));
                }
            }
        }
    }


    private void loopRemoveEdgesSubmenu() throws IOException {
        while (true) {
            List<Node> nodesWithEdges = graph.nodesWithEdges();
            if (nodesWithEdges.isEmpty()) {
                showMessage("Remover arestas", styledLine("Não há arestas.", ERR_STYLE));
                return;
            }
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Remover arestas", edgeListSummary()),
                    "reme",
                    "Escolha:",
                    listItems(
                            item("rm", "Remover aresta"),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            if ("rm".equals(choice)) {
                nodesWithEdges.sort(Comparator.comparing(Node::getId));
                clearScreen();
                String fromId = showListPrompt(
                        buildHeader("Remover aresta — origem", null),
                        "from",
                        "Nó de origem:",
                        nodeItems(nodesWithEdges)
                );
                if (fromId == null) {
                    continue;
                }
                Optional<Node> fromNode = graph.findNode(fromId);
                if (fromNode.isEmpty()) {
                    continue;
                }
                List<Node> neighbors = fromNode.get().neighborsSortedById();
                if (neighbors.isEmpty()) {
                    showMessage("Remover aresta", styledLine("Este nó não tem vizinhos de saída.", ERR_STYLE));
                    continue;
                }
                clearScreen();
                String toId = showListPrompt(
                        buildHeader("Remover aresta de «" + fromId + "»", null),
                        "to",
                        "Nó de destino:",
                        nodeItems(neighbors)
                );
                if (toId == null) {
                    continue;
                }
                if (graph.removeEdge(fromId, toId)) {
                    showMessage("Remover arestas", styledLine("Aresta removida.", OK_STYLE));
                } else {
                    showMessage("Remover arestas", styledLine("Aresta não encontrada.", ERR_STYLE));
                }
            }
        }
    }


    private void loopExploreMenu() throws IOException {
        if (graph == null) {
            return;
        }
        while (true) {
            if (graph.getNodes().isEmpty()) {
                showMessage("Explorar", styledLine("Grafo sem nós.", ERR_STYLE));
                return;
            }
            clearScreen();
            String choice = showListPrompt(
                    buildHeader("Explorar", graphSummary()),
                    "ex",
                    "Escolha:",
                    listItems(
                            item("matrix", "Matriz de adjacência"),
                            item("closureFwd", "Fecho transitivo direto…"),
                            item("closureBwd", "Fecho transitivo inverso…"),
                            item("connectivity", "Conectividade forte e CFCs…"),
                            item("dfsSearch", "DFS — procurar nó"),
                            item("bfsSearch", "BFS — procurar nó"),
                            item("dfsFull", "DFS — exploração completa"),
                            item("bfsFull", "BFS — exploração completa"),
                            item("back", "← Voltar")
                    )
            );
            if (choice == null || "back".equals(choice)) {
                return;
            }
            switch (choice) {
                case "matrix":
                    showContentScreen("Matriz de adjacência", AsciiGraphPreview.renderAdjacencyMatrix(graph));
                    break;
                case "closureFwd":
                    exploreTransitiveClosureForward();
                    break;
                case "closureBwd":
                    exploreTransitiveClosureBackward();
                    break;
                case "connectivity":
                    exploreStrongConnectivityAndSccs();
                    break;
                case "dfsSearch":
                    exploreSearch(true);
                    break;
                case "bfsSearch":
                    exploreSearch(false);
                    break;
                case "dfsFull":
                    exploreFullTraversal(true);
                    break;
                case "bfsFull":
                    exploreFullTraversal(false);
                    break;
                default:
                    break;
            }
        }
    }

    private void exploreTransitiveClosureForward() throws IOException {
        clearScreen();
        List<Node> all = sortedNodes();
        String vertexId = showListPrompt(
                buildHeader("Fecho transitivo direto", null),
                "vertex",
                "Vértice:",
                nodeItems(all)
        );
        if (vertexId == null) {
            return;
        }
        Optional<Node> opt = graph.findNode(vertexId);
        if (opt.isEmpty()) {
            return;
        }
        List<Node> closure = graph.transitiveClosureForward(opt.get());
        String ids = closure.stream().map(Node::getId).collect(Collectors.joining(", "));
        List<AttributedString> content = new ArrayList<>();
        content.add(styledLine("Vértice: «" + vertexId + "»", DIM_STYLE));
        content.add(AttributedString.EMPTY);
        content.add(styledLine(
                "Fecho transitivo direto (" + closure.size() + " vértice(s)): " + ids,
                OK_STYLE));
        showMessage("Fecho transitivo direto", content);
    }

    private void exploreTransitiveClosureBackward() throws IOException {
        clearScreen();
        List<Node> all = sortedNodes();
        String vertexId = showListPrompt(
                buildHeader("Fecho transitivo inverso", null),
                "vertex",
                "Vértice:",
                nodeItems(all)
        );
        if (vertexId == null) {
            return;
        }
        Optional<Node> opt = graph.findNode(vertexId);
        if (opt.isEmpty()) {
            return;
        }
        List<Node> closure = graph.transitiveClosureBackward(opt.get());
        String ids = closure.stream().map(Node::getId).collect(Collectors.joining(", "));
        List<AttributedString> content = new ArrayList<>();
        content.add(styledLine("Vértice: «" + vertexId + "»", DIM_STYLE));
        content.add(AttributedString.EMPTY);
        content.add(styledLine(
                "Fecho transitivo inverso (" + closure.size() + " vértice(s)): " + ids,
                OK_STYLE));
        showMessage("Fecho transitivo inverso", content);
    }

    private void exploreStrongConnectivityAndSccs() throws IOException {
        boolean stronglyConnected = graph.isStronglyConnected();
        List<List<Node>> sccs = graph.stronglyConnectedComponents();
        List<AttributedString> content = new ArrayList<>();
        String typeLabel = graph.getGraphType() == GraphType.DIRECTED ? "direcionado" : "não direcionado";
        content.add(styledLine("Tipo: " + typeLabel, DIM_STYLE));
        content.add(AttributedString.EMPTY);
        if (stronglyConnected) {
            content.add(styledLine("O grafo é fortemente conexo.", OK_STYLE));
        } else {
            content.add(styledLine("O grafo não é fortemente conexo.", ERR_STYLE));
            content.add(AttributedString.EMPTY);
            content.add(styledLine("Componentes fortemente conexos máximos (CFCs):", DIM_STYLE));
            int n = 1;
            for (List<Node> comp : sccs) {
                String ids = comp.stream().map(Node::getId).collect(Collectors.joining(", "));
                content.add(styledLine("  " + n + ". { " + ids + " }", OK_STYLE));
                n++;
            }
        }
        showMessage("Conectividade e CFCs", content);
    }

    private void exploreSearch(boolean dfs) throws IOException {
        clearScreen();
        List<Node> all = sortedNodes();
        String startId = showListPrompt(
                buildHeader(dfs ? "DFS — procurar" : "BFS — procurar", null),
                "start",
                "Nó inicial:",
                nodeItems(all)
        );
        if (startId == null) {
            return;
        }
        Optional<Node> startOpt = graph.findNode(startId);
        if (startOpt.isEmpty()) {
            return;
        }
        Node start = startOpt.get();
        List<Node> targets = graph.nodesExcept(start);
        if (targets.isEmpty()) {
            showMessage(dfs ? "DFS" : "BFS", styledLine("Não há outro nó para procurar.", ERR_STYLE));
            return;
        }
        clearScreen();
        String targetId = showListPrompt(
                buildHeader("Nó alvo (partindo de «" + startId + "»)", null),
                "target",
                "Nó alvo:",
                nodeItems(targets)
        );
        if (targetId == null) {
            return;
        }

        List<Node> traversal = dfs
                ? graph.depthFirstTraversal(start)
                : graph.breadthFirstTraversal(start);
        boolean found = traversal.stream().anyMatch(n -> n.getId().equals(targetId));

        String pathStr = traversal.stream().map(Node::getId).collect(Collectors.joining(" → "));

        List<AttributedString> content = new ArrayList<>();
        content.add(styledLine("Percurso: " + pathStr, DIM_STYLE));
        content.add(AttributedString.EMPTY);
        if (found) {
            content.add(styledLine("Nó «" + targetId + "» ENCONTRADO a partir de «" + startId + "».", OK_STYLE));
        } else {
            content.add(styledLine("Nó «" + targetId + "» NÃO alcançável a partir de «" + startId + "».", ERR_STYLE));
        }
        showMessage(dfs ? "DFS — resultado" : "BFS — resultado", content);
    }

    private void exploreFullTraversal(boolean dfs) throws IOException {
        clearScreen();
        List<Node> all = sortedNodes();
        String startId = showListPrompt(
                buildHeader(dfs ? "DFS — exploração completa" : "BFS — exploração completa", null),
                "start",
                "Nó inicial:",
                nodeItems(all)
        );
        if (startId == null) {
            return;
        }
        Optional<Node> startOpt = graph.findNode(startId);
        if (startOpt.isEmpty()) {
            return;
        }
        List<Node> order = dfs
                ? graph.depthFirstTraversal(startOpt.get())
                : graph.breadthFirstTraversal(startOpt.get());

        String pathStr = order.stream().map(Node::getId).collect(Collectors.joining(" → "));

        List<AttributedString> content = new ArrayList<>();
        content.add(styledLine("Nós visitados: " + order.size() + " / " + graph.getNodes().size(), DIM_STYLE));
        content.add(AttributedString.EMPTY);
        content.add(styledLine("Percurso: " + pathStr, OK_STYLE));

        showMessage(dfs ? "DFS — resultado" : "BFS — resultado", content);
    }


    private void screenPreview() throws IOException {
        if (graph == null) {
            return;
        }
        showContentScreen("Visualização geral", AsciiGraphPreview.render(graph));
    }


    private void showContentScreen(String title, String multilineContent) throws IOException {
        List<AttributedString> h = buildHeader(title, null);
        for (String line : multilineContent.split("\n", -1)) {
            h.add(new AttributedString(line));
        }
        h.add(AttributedString.EMPTY);
        clearScreen();
        showInputPrompt(h, "cont", "(Enter para continuar)", "");
    }

    private void showMessage(String title, AttributedString line) throws IOException {
        showMessage(title, List.of(line));
    }

    private void showMessage(String title, List<AttributedString> lines) throws IOException {
        List<AttributedString> h = buildHeader(title, null);
        h.addAll(lines);
        h.add(AttributedString.EMPTY);
        clearScreen();
        showInputPrompt(h, "cont", "(Enter para continuar)", "");
    }


    private String graphSummary() {
        if (graph == null) {
            return null;
        }
        String type = graph.getGraphType() == GraphType.DIRECTED ? "direcionado" : "não direcionado";
        return type + " · " + graph.getNodes().size() + " nós · " + graph.edgeCount() + " arestas";
    }

    private String nodeListSummary() {
        if (graph == null || graph.getNodes().isEmpty()) {
            return "Nenhum nó ainda";
        }
        return "Nós: " + graph.getNodes().stream()
                .sorted(Comparator.comparing(Node::getId))
                .map(Node::getId)
                .collect(Collectors.joining(", "));
    }

    private String edgeListSummary() {
        if (graph == null) {
            return null;
        }
        return graph.edgeCount() + " aresta(s)";
    }


    private String showListPrompt(List<AttributedString> header, String name, String message,
                                  List<ListItemSpec> items) throws IOException {
        ConsolePrompt prompt = new ConsolePrompt(lineReader, terminal, new ConsolePrompt.UiConfig());
        PromptBuilder pb = prompt.getPromptBuilder();
        var listBuilder = pb.createListPrompt().name(name).message(message);
        for (ListItemSpec spec : items) {
            if (!spec.enabled) {
                continue;
            }
            listBuilder.newItem().name(spec.id).text(spec.label).add();
        }
        pb = listBuilder.addPrompt();
        @SuppressWarnings("rawtypes")
        Map result = prompt.prompt(header, pb.build());
        if (result == null || result.isEmpty()) {
            return null;
        }
        PromptResultItemIF item = (PromptResultItemIF) result.get(name);
        if (item instanceof ListResult) {
            return ((ListResult) item).getSelectedId();
        }
        return null;
    }

    private String showInputPrompt(List<AttributedString> header, String name, String message, String def) throws IOException {
        ConsolePrompt prompt = new ConsolePrompt(lineReader, terminal, new ConsolePrompt.UiConfig());
        PromptBuilder pb = prompt.getPromptBuilder();
        pb.createInputPrompt().name(name).message(message).defaultValue(def).addPrompt();
        @SuppressWarnings("rawtypes")
        Map result = prompt.prompt(header, pb.build());
        if (result == null || result.isEmpty()) {
            return null;
        }
        PromptResultItemIF item = (PromptResultItemIF) result.get(name);
        if (item instanceof InputResult) {
            return ((InputResult) item).getResult();
        }
        return null;
    }


    private static List<AttributedString> buildHeader(String title, String subtitle) {
        List<AttributedString> lines = new ArrayList<>();
        lines.add(styledLine("  ____                 _       _     _ _                          ", TITLE_STYLE));
        lines.add(styledLine(" / ___|_ __ __ _ _ __ | |__   | |   (_) |__  _ __ __ _ _ __ _   _ ", TITLE_STYLE));
        lines.add(styledLine("| |  _| '__/ _` | '_ \\| '_ \\  | |   | | '_ \\| '__/ _` | '__| | | |", TITLE_STYLE));
        lines.add(styledLine("| |_| | | | (_| | |_) | | | | | |___| | |_) | | | (_| | |  | |_| |", TITLE_STYLE));
        lines.add(styledLine(" \\____|_|  \\__,_| .__/|_| |_| |_____|_|_.__/|_|  \\__,_|_|   \\__, |", TITLE_STYLE));
        lines.add(styledLine("                |_|                                         |___/", TITLE_STYLE));
        lines.add(styledLine(" ", TITLE_STYLE));
        lines.add(styledLine("----> " + title, TITLE_STYLE));
        if (subtitle != null && !subtitle.isBlank()) {
            lines.add(styledLine(subtitle, DIM_STYLE));
        }
        lines.add(AttributedString.EMPTY);
        return lines;
    }

    private static AttributedString styledLine(String text, AttributedStyle style) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.style(style);
        asb.append(text);
        return asb.toAttributedString();
    }

    private void styled(String text, AttributedStyle style) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.style(style);
        asb.append(text);
        terminal.writer().println(asb.toAnsi(terminal));
        terminal.writer().flush();
    }


    private List<Node> sortedNodes() {
        List<Node> list = new ArrayList<>(graph.getNodes());
        list.sort(Comparator.comparing(Node::getId));
        return list;
    }

    private static double parseWeight(String raw) {
        if (raw == null || raw.isBlank()) {
            return 1.0;
        }
        return Double.parseDouble(raw.trim());
    }

    private static String formatWeight(double w) {
        if (w == (long) w) {
            return String.format(Locale.ROOT, "%d", (long) w);
        }
        return String.format(Locale.ROOT, "%.2f", w);
    }


    private static ListItemSpec item(String id, String label) {
        return new ListItemSpec(id, label, true);
    }

    private static ListItemSpec item(String id, String label, boolean enabled) {
        return new ListItemSpec(id, label, enabled);
    }

    private static List<ListItemSpec> listItems(ListItemSpec... items) {
        List<ListItemSpec> list = new ArrayList<>();
        for (ListItemSpec s : items) {
            list.add(s);
        }
        return list;
    }

    private static List<ListItemSpec> nodeItems(List<Node> nodes) {
        List<ListItemSpec> list = new ArrayList<>();
        for (Node n : nodes) {
            list.add(item(n.getId(), n.getId()));
        }
        return list;
    }

    private static final class ListItemSpec {
        final String id;
        final String label;
        final boolean enabled;

        ListItemSpec(String id, String label, boolean enabled) {
            this.id = id;
            this.label = label;
            this.enabled = enabled;
        }
    }


    private void clearScreen() {
        terminal.writer().print("\u001B[2J\u001B[H");
        terminal.writer().flush();
    }
}
