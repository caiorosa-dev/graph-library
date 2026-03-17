package com.caiorosadev.graph;

import com.caiorosadev.graph.ui.GraphTerminalUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GraphTerminalUI ui = new GraphTerminalUI(scanner);
        ui.run();
    }
}