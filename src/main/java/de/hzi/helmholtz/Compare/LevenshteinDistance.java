package de.hzi.helmholtz.Compare;

import java.util.List;

public class LevenshteinDistance {

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /*Treat ? as matching wild character*/
    private static boolean myStringEquality(String s1, String s2) {
        if (s1.equals("?") || s2.equals("?") || s1.equals(s2)) {
            return true;
        }
        return false;
    }

    public static int computeLevenshteinDistance(List<String> str1, List<String> str2) {
        int[][] distance = new int[str1.size() + 1][str2.size() + 1];

        for (int i = 0; i <= str1.size(); i++) {
            distance[i][0] = i;
        }
        for (int j = 1; j <= str2.size(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.size(); i++) {
            for (int j = 1; j <= str2.size(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        //distance[i - 1][j - 1]+ ((str1.get(i - 1).equals(str2.get(j - 1))) ? 0 : 1));
                        distance[i - 1][j - 1] + ((LevenshteinDistance.myStringEquality(str1.get(i - 1), str2.get(j - 1))) ? 0 : 1));
            }
        }

        return distance[str1.size()][str2.size()];
    }
}