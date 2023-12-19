package org.duckdns.daufsys;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

/**
 * Determine which games would have been possible if the bag had been loaded with only 12 red cubes,
 * 13 green cubes, and 14 blue cubes. What is the sum of the IDs of those games?
 *
 * For each game, find the minimum set of cubes that must have been present.
 * What is the sum of the power of these sets?
 * (The power of a set of cubes is equal to the numbers of red, green, and blue cubes multiplied together).
 */
public class Day02 {

    static final int MAX_R = 12;
    static final int MAX_G = 13;
    static final int MAX_B = 14;

    public static void main(String... args) throws Exception {
        var path = Paths.get("02.txt");
        Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
        int total = lines.reduce(0, (subtotal, line) -> subtotal + getPossibleGamesLineValue(line), Integer::sum);
        lines.close();
        System.out.println("Possible Games Total: " + total);
        Stream<String> lines2 = Files.lines(path, StandardCharsets.UTF_8);
        int total2 = lines2.reduce(0, (subtotal, line) -> subtotal + getCubesPowerLineValue(line), Integer::sum);
        lines2.close();
        System.out.println("Cubes Power Total   : " + total2);
    }

    // Game 1: 10 red, 7 green, 3 blue; 5 blue, 3 red, 10 green; 4 blue, 14 green, 7 red; 1 red, 11 green; 6 blue, 17 green, 15 red; 18 green, 7 red, 5 blue
    // Game 2: 13 green, 10 red; 11 green, 1 blue, 7 red; 5 red, 12 green, 1 blue; 12 green, 6 red; 8 green, 5 red; 12 green, 1 red
    private static int getPossibleGamesLineValue(String line) {
        var games = line.replaceAll("Game \\d+: ", "");
        var gameNumber = Integer.parseInt(line.substring(0, line.length() - games.length()).replace("Game ", "").replace(": ", ""));
        return isPossibleGame(games) ? gameNumber : 0;
    }

    private static boolean isPossibleGame(String games) {
        var isPossibleGame = true;
        var st = new StringTokenizer(games, ";");
        while (isPossibleGame && st.hasMoreTokens()) {
            isPossibleGame = readGameEntry(st.nextToken().trim()).isPossibleGame();
        }
        return isPossibleGame;
    }

    private static int getCubesPowerLineValue(String line) {
        var games = line.replaceAll("Game \\d+: ", "");
        var minimumRgbCubes = new RgbCubes();
        minimumRgbCubes.r = 1;
        minimumRgbCubes.g = 1;
        minimumRgbCubes.b = 1;
        var st = new StringTokenizer(games, ";");
        while (st.hasMoreTokens()) {
            var rgbCubes = readGameEntry(st.nextToken().trim());
            if (rgbCubes.r > minimumRgbCubes.r) {
                minimumRgbCubes.r = rgbCubes.r;
            }
            if (rgbCubes.g > minimumRgbCubes.g) {
                minimumRgbCubes.g = rgbCubes.g;
            }
            if (rgbCubes.b > minimumRgbCubes.b) {
                minimumRgbCubes.b = rgbCubes.b;
            }
        }
        return minimumRgbCubes.getCubesPower();
    }

    // 10 red, 7 green, 3 blue
    // 11 green, 1 blue, 7 red
    // 12 green, 6 red
    private static RgbCubes readGameEntry(String entry) {
        var rgbCubes = new RgbCubes();
        var st = new StringTokenizer(entry, ",");
        while (st.hasMoreTokens()) {
            var token = st.nextToken().trim();
            if (token.matches("\\d+ red")) {
                rgbCubes.r = Integer.parseInt(token.replace(" red", ""));
            } else if (token.matches("\\d+ green")) {
                rgbCubes.g = Integer.parseInt(token.replace(" green", ""));
            } else if (token.matches("\\d+ blue")) {
                rgbCubes.b = Integer.parseInt(token.replace(" blue", ""));
            }
        }
        return rgbCubes;
    }

    static class RgbCubes {
        int r = 0;
        int g = 0;
        int b = 0;

        boolean isPossibleGame() {
            return r <= MAX_R && g <= MAX_G && b <= MAX_B;
        }

        // When calculating this, we know that none of the r, g, b properties are 0
        public int getCubesPower() {
            return r * g * b;
        }
    }
}
