/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hzi.helmholtz.Util;

import java.util.List;

/**
 *
 * @author skondred
 */
public class ListUtils {

    // Better use a `List`. It is more generic and it also receives an `ArrayList`.
    public static double average(List<Integer> list) {
        // 'average' is undefined if there are no elements in the list.
        if (list == null || list.isEmpty()) {
            return 0.0;
        }
        // Calculate the summation of the elements in the list
        long sum = 0;
        int n = list.size();
        // Iterating manually is faster than using an enhanced for loop.
        for (int i = 0; i < n; i++) {
            sum += list.get(i);
        }
        // We don't want to perform an integer division, so the cast is mandatory.
        return ((double) sum) / n;
    }

    // Better use a `List`. It is more generic and it also receives an `ArrayList`.
    public static double max(List<Integer> list) {
        // 'average' is undefined if there are no elements in the list.
        if (list == null || list.isEmpty()) {
            return 0;
        }
        // Calculate the summation of the elements in the list
        double max = Double.MIN_VALUE;
        int n = list.size();
        // Iterating manually is faster than using an enhanced for loop.
        for (int i = 0; i < n; i++) {
            if (list.get(i) > max) {
                max = list.get(i);
            }
        }
        return max;
    }

    // Better use a `List`. It is more generic and it also receives an `ArrayList`.
    public static double min(List<Integer> list) {
        // 'average' is undefined if there are no elements in the list.
        if (list == null || list.isEmpty()) {
            return 0;
        }
        // Calculate the summation of the elements in the list
        double min = Double.MAX_VALUE;
        int n = list.size();
        // Iterating manually is faster than using an enhanced for loop.
        for (int i = 0; i < n; i++) {
            if (list.get(i) < min) {
                min = list.get(i);
            }
        }
        return min;
    }
}
