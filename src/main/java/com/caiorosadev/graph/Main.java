package com.caiorosadev.graph;

import com.caiorosadev.graph.ui.GraphMenuApp;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
            new GraphMenuApp(terminal, reader).run();
        } catch (UserInterruptException e) {
            System.out.println();
            System.out.println("Interrompido. Até logo :)");
        } catch (IOException e) {
            System.err.println("Erro de terminal: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
