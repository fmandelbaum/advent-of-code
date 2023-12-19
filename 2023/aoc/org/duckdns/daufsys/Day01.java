package org.duckdns.daufsys;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day01 {

    static final List<String> TOKENS_1 = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9");
    static final List<String> TOKENS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
    static final Map<String, Integer> VALUES = new HashMap<>();
    static {
        VALUES.put("1", 1);
        VALUES.put("one", 1);
        VALUES.put("2", 2);
        VALUES.put("two", 2);
        VALUES.put("3", 3);
        VALUES.put("three", 3);
        VALUES.put("4", 4);
        VALUES.put("four", 4);
        VALUES.put("5", 5);
        VALUES.put("five", 5);
        VALUES.put("6", 6);
        VALUES.put("six", 6);
        VALUES.put("7", 7);
        VALUES.put("seven", 7);
        VALUES.put("8", 8);
        VALUES.put("eight", 8);
        VALUES.put("9", 9);
        VALUES.put("nine", 9);
    }

    public static void main(String... args) throws Exception {
        var path = Paths.get("sample01.txt");
        Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
        int total = lines.reduce(0, (subtotal, element) -> subtotal + getLineValue(element, TOKENS_1), Integer::sum);
        lines.close();
        System.out.println("Total: " + total);
        Stream<String> lines2 = Files.lines(path, StandardCharsets.UTF_8);
        int total2 = lines2.reduce(0, (subtotal, element) -> subtotal + getLineValue(element), Integer::sum);
        lines2.close();
        System.out.println("Total2: " + total2);
    }

    private static int getLineValue(String line) {
        return getLineValue(line, TOKENS);
    }

    private static int getLineValue(String line, List<String> tokens) {
        var lowest = -1;
        var token1 = "";
        var highest = 0;
        var token2 = "";
        for (var token : tokens) {
            var l = line.indexOf(token);
            if (l != -1 && (lowest == -1 || l < lowest)) {
                lowest = l;
                token1 = token;
            }
            var h = line.lastIndexOf(token);
            if (h != -1 && h > highest) {
                highest = h;
                token2 = token;
            }
        }
        token2 = token2.isEmpty() ? token1 : token2;
        return 10 * VALUES.get(token1) + VALUES.get(token2);
    }
}