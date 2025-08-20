package org.example;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App {

    static class Point {
        BigInteger x;
        BigInteger y;
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    // Read points from JSON file
    static List<Point> readPointsFromJson(String filePath, int[] kOut) {
        List<Point> points = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(filePath));

            JSONObject keys = (JSONObject) obj.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());
            kOut[0] = k;

            for (Object key : obj.keySet()) {
                if (key.equals("keys")) continue;
                String xStr = key.toString();
                BigInteger x = new BigInteger(xStr);

                JSONObject valueObj = (JSONObject) obj.get(key);
                int base = Integer.parseInt(valueObj.get("base").toString());
                String value = valueObj.get("value").toString();

                // decode y value using base
                BigInteger y = new BigInteger(value, base);
                points.add(new Point(x, y));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }

    // Lagrange interpolation at x=0
    static BigInteger lagrangeInterpolationAtZero(List<Point> points, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(xj.negate()); // (0 - xj)
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        // Change file path to your JSON file location
        String filePath = "src/main/resources/input2.json";

        int[] kOut = new int[1];
        List<Point> points = readPointsFromJson(filePath, kOut);

        int k = kOut[0];

        // Sort points by x for consistency
        points.sort(Comparator.comparing(p -> p.x));

        // Print decoded points
        System.out.println("Decoded (x, y) points:");
        for (Point p : points) {
            System.out.println("x = " + p.x + ", y = " + p.y);
        }

        // Pick first k points
        List<Point> selected = points.subList(0, k);

        BigInteger secret = lagrangeInterpolationAtZero(selected, k);
        System.out.println("\nSecret (c) = " + secret);
    }
}
