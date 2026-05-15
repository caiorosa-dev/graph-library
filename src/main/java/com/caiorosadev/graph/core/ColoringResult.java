package com.caiorosadev.graph.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ColoringResult {
    private final Map<Node, Integer> colors;
    private final List<Node> coloringSequence;
    private final int chromaticNumber;
}
