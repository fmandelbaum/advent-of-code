package org.duckdns.daufsys;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * The engine schematic (your puzzle input) consists of a visual representation of the engine.
 * There are lots of numbers and symbols you don't really understand,
 * but apparently any number adjacent to a symbol, even diagonally,
 * is a "part number" and should be included in your sum.
 * (Periods (.) do not count as a symbol.)
 *
 * What is the sum of all of the part numbers in the engine schematic?
 *
 *
 */
public class Day03 {

    static final List<Character> NUMBERS = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    public static void main(String... args) throws Exception {
        List<String> lines;
        try (var stream = Files.lines(Paths.get("03.txt"), StandardCharsets.UTF_8)) {
            lines = stream.collect(Collectors.toList());
        }
        var allNumbers = new ArrayList<List<Number>>();
        var allSymbols = new ArrayList<List<Symbol>>();
        for (var line : lines) {
            var numbers = new ArrayList<Number>();
            var symbols = new ArrayList<Symbol>();
            parseLine(line, numbers, symbols);
            allNumbers.add(numbers);
            allSymbols.add(symbols);
        }

        // Mark parts
        var l = 0;
        var linesSize = lines.size();
        while (l < linesSize) {
            //System.out.println(">>> Processing line " + (l + 1));
            var symbols = allSymbols.get(l);
            markParts(l, symbols, allNumbers.get(l));
            if (l == 0) {
                // First line
                markParts(l, symbols, allNumbers.get(1));
            } else if (l == linesSize - 1) {
                // Last line
                markParts(l, symbols, allNumbers.get(linesSize - 2));
            } else {
                // "Middle" line
                markParts(l, symbols, allNumbers.get(l - 1));
                markParts(l, symbols, allNumbers.get(l + 1));
            }
            l++;
        }

        var total = 0;
        for (var i = 0; i < allNumbers.size(); i++) {
            var numbers = allNumbers.get(i);
            total += numbers.stream().reduce(0, (subtotal, number) -> subtotal + (number.isPart ? number.value : 0), Integer::sum);
        }
        System.out.println("Parts total: " + total);
    }

    static void markParts(int l, List<Symbol> symbols, List<Number> numbers) {
        // If no symbols, or no numbers, or all numbers are already known parts, skip
        if (symbols.isEmpty() || numbers.isEmpty() || numbers.stream().allMatch(number -> number.isPart)) {
            //System.out.println("Skipped line " + (l + 1) + " with no symbols, no numbers, or all already known parts");
            return;
        }
        //System.out.println("Analizing line " + (l + 1));
        for (var symbol: symbols) {
            for (var number: numbers) {
                if (!number.isPart && number.start - 1 <= symbol.column && symbol.column <= number.end + 1) {
                    number.isPart = true;
                }
            }
        }
        //System.out.println("Symbols: " + symbols);
        //System.out.println("Numbers: " + numbers);
    }

    // ......726...811...........................+..91..980..*........................$..........*.......639..................193.%............403.
    // 952.........................................................793......583..........623............11........730............50.116.........446
    // .....*....*........./227..-113........@...825/.....348...881......603...........%....793...=............235*..............472.........82.941
    static void parseLine(String line, List<Number> numbers, List<Symbol> symbols) {
        var i = 0;
        var sb = new StringBuilder();
        var number = new Number();
        while (i < line.length()) {
            var c = line.charAt(i);
            if (c == '.') {
                if (!sb.isEmpty()) {
                    number.end = i - 1;
                    number.value = Integer.parseInt(sb.toString());
                    numbers.add(number);
                    sb = new StringBuilder();
                    number = new Number();
                }
                i++;
                continue;
            }
            if (NUMBERS.contains(c)) {
                if (sb.isEmpty()) {
                    number.start = i;
                }
                sb.append(c);
                i++;
            } else {
                var symbol = new Symbol(i, c);
                symbols.add(symbol);
                if (!sb.isEmpty()) {
                    number.end = i - 1;
                    number.value = Integer.parseInt(sb.toString());
                    number.isPart = true; // We know the number is a part, because it's adjacent to the symbol
                    numbers.add(number);
                    sb = new StringBuilder();
                    number = new Number();
                }
                i++;
            }
        }
    }

    static class Number {
        int start;
        int end;
        int value;
        boolean isPart = false;

        @Override
        public String toString() {
            return value + " (" + start + "," + end + ") isPart: " + isPart;
        }
    }

    static class Symbol {
        int column;
        char value;

        public Symbol(int column, char value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public String toString() {
            return value + " (" + column + ")";
        }
    }
}
