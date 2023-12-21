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
        var n = lines.size();
        // Do first line...
        var line1 = lines.get(0);
        var total = getLineValue(line1);
        total += getPartsLineValue(line1, lines.get(1));
        // ... do last line...
        var lineN = lines.get(n - 1);
        var value = getLineValue(lineN);
        if (value == 0) {
            // check diagonals
            value = getPartsLineValue(lineN, lines.get(n - 2));
        }
        total += value;
        // ... do lines "in between"...
        for (var i = 1; i < n - 1; i++) {
            var line = lines.get(i);
            value = getLineValue(line);
            if (value == 0) {
                // Check diagonals
                if (i < n - 2) {
                    total += getPartsLineValue(line, lines.get(i - 1));
                    total += getPartsLineValue(line, lines.get(i + 1));
                } else {
                    total += getPartsLineValue(line, lines.get(i - 1));
                }
            } else {
                total += value;
            }
        }
        System.out.println("Total of Part Numbers: " + total);
    }

    // [447 (4,6), -1 (18,18), 342 (30,32), -1 (37,37), -1 (43,43), -1 (52,52), 938 (55,57), 238 (64,66), 327 (72,74), -1 (89,89), 152 (90,92), -1 (99,99), -1 (103,103), -1 (123,123), 472 (126,128), 153 (130,132)]
    // [152 (13,15), -1 (16,16), -1 (29,29), 792 (36,38), 334 (42,44), 741 (51,53), 570 (78,80), -1 (81,81), 335 (86,88), 137 (103,105), 338 (116,118), -1 (129,129), -1 (136,136)]
    private static int getPartsLineValue(String line1, String line2) {
        // First we process the line itself to check for adjacent symbols...
        var value = getLineValue(line1);
        // ... then we process the line against the 2nd one to look for diagonal symbols
        var list1 = parseLine(line1);
        list1.removeIf(d -> d.value == -1); // Removes symbols, leaving only potential parts
        var list2 = parseLine(line2);
        for (var data : list1) {
            if (isPart(data, list2)) {
                value += data.value;
            }
        }
        return value;
    }

    // .....*....*........./227..-113........@...825/.....348...881......603...........%....793...=............235*..............472.........82.941
    private static int getLineValue(String line) {
        var total = 0;
        var i = 0;
        var sb = new StringBuilder();
        var seenSymbol = false;
        while (i < line.length()) {
            var c = line.charAt(i);
            if (c != '.') {
                if (NUMBERS.contains(c)) {
                    sb.append(c);
                } else {
                    if (!sb.isEmpty()) {
                        // had a number followed by a symbol, found a part
                        total += Integer.parseInt(sb.toString());
                        seenSymbol = false;
                        sb = new StringBuilder();
                    } else {
                        // Flag that symbol has been seen to check if we have enough info to consider another part
                        seenSymbol = true;
                    }
                }
                i++; // Move on to next char
            } else {
                if (seenSymbol && !sb.isEmpty()) {
                    // symbol followed by a number, found a part
                    total += Integer.parseInt(sb.toString());
                }
                sb = new StringBuilder();
                seenSymbol = false; // Ignore symbol not adjacent to number
                i++; // Move on to next char
            }
        }
        return total;
    }

    private static boolean isPart(Data data, List<Data> dataList) {
        var isPart = false;
        dataList.removeIf(d -> d.value != -1); // Remove parts, leaving only symbols
        // We do not need to check out of bounds here...
        var i1 = data.idxStart - 1;
        var i2 = data.idxEnd + 1;
        for (var i = 0; !isPart && i < dataList.size(); i++) {
            var symbol = dataList.get(i);
            isPart = symbol.idxStart >= i1 && symbol.idxStart <= i2;
        }
        return isPart;
    }

    // ......726...811...........................+..91..980..*........................$..........*.......639..................193.%............403.
    // 952.........................................................793......583..........623............11........730............50.116.........446
    // .....*....*........./227..-113........@...825/.....348...881......603...........%....793...=............235*..............472.........82.941
    private static List<Data> parseLine(String line) {
        var dataList = new ArrayList<Data>();
        var i = 0;
        while (i < line.length()) {
            var c = line.charAt(i);
            if (c != '.') {
                var data = parseItem(line, i);
                dataList.add(data);
                i = data.idxEnd + 1;
            } else {
                i++;
            }
        }
        return dataList;
    }

    private static Data parseItem(String line, int i) {
        var data = new Data();
        data.idxStart = i;
        var sb = new StringBuilder();
        while (i < line.length()) {
            var c = line.charAt(i);
            if (c == '.') {
                break; // Finished parsing "interesting" item, stop
            } else {
                if (NUMBERS.contains(c)) {
                    sb.append(c);
                    i++; // Keep going..
                } else {
                    // Hit a symbol, if we moved only one char, return, else stop
                    if (i == data.idxStart) {
                        data.value = -1;
                        data.idxEnd = i;
                        return data;
                    } else {
                        break; // Ugly (two breaks in the while loop), but working
                    }
                }
            }
        }
        data.idxEnd = i - 1;
        var s = sb.toString(); // s is a number represented as a String
        data.value = Integer.parseInt(s);
        return data;
    }

    static class Data {
        int idxStart;
        int idxEnd;
        int value; // -1 for symbols, the number for numbers

        @Override
        public String toString() {
            return value + " (" + idxStart + "," + idxEnd + ")";
        }
    }
}
