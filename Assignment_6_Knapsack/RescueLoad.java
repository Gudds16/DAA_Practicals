import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class RescueLoad {

    static class Supply {
        final String label;
        final int mass;
        final int benefit;

        Supply(String label, int mass, int benefit) {
            this.label = label;
            this.mass = mass;
            this.benefit = benefit;
        }

        double ratio() {
            return (double) benefit / mass;
        }
    }

    // Bottom-up 0/1 knapsack. Also records, for every cell, whether the item was taken.
    static List<Supply> solveDP(Supply[] s, int cap, int[] bestOut) {
        int n = s.length;
        int[][] best = new int[n + 1][cap + 1];
        boolean[][] taken = new boolean[n + 1][cap + 1];

        for (int i = 1; i <= n; i++) {
            Supply cur = s[i - 1];
            for (int c = 0; c <= cap; c++) {
                best[i][c] = best[i - 1][c];
                if (cur.mass <= c) {
                    int withItem = cur.benefit + best[i - 1][c - cur.mass];
                    if (withItem > best[i][c]) {
                        best[i][c] = withItem;
                        taken[i][c] = true;
                    }
                }
            }
        }

        List<Supply> chosen = new ArrayList<>();
        int c = cap;
        for (int i = n; i >= 1; i--) {
            if (taken[i][c]) {
                chosen.add(0, s[i - 1]);
                c -= s[i - 1].mass;
            }
        }
        bestOut[0] = best[n][cap];
        return chosen;
    }

    // Greedy by benefit per kg, shown only for comparison with the DP answer.
    static List<Supply> solveGreedy(Supply[] s, int cap) {
        Supply[] sorted = s.clone();
        Arrays.sort(sorted, Comparator.comparingDouble(Supply::ratio).reversed());
        List<Supply> chosen = new ArrayList<>();
        int used = 0;
        for (Supply x : sorted) {
            if (used + x.mass <= cap) {
                chosen.add(x);
                used += x.mass;
            }
        }
        return chosen;
    }

    static void report(String title, List<Supply> list) {
        int w = 0, b = 0;
        for (Supply x : list) {
            w += x.mass;
            b += x.benefit;
        }
        System.out.println(title);
        System.out.println("  Total mass    : " + w + " kg");
        System.out.println("  Total benefit : " + b);
        for (Supply x : list) {
            System.out.println("  * " + x.label + " (" + x.mass + " kg, benefit " + x.benefit + ")");
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Number of supply types: ");
        int n = in.nextInt();
        System.out.print("Helicopter payload limit (kg): ");
        int cap = in.nextInt();

        Supply[] supplies = new Supply[n];
        System.out.println("Enter label, mass (kg) and benefit for each supply:");
        for (int i = 0; i < n; i++) {
            supplies[i] = new Supply(in.next(), in.nextInt(), in.nextInt());
        }

        int[] best = new int[1];
        List<Supply> dpChoice = solveDP(supplies, cap, best);
        List<Supply> greedyChoice = solveGreedy(supplies, cap);

        System.out.println();
        System.out.println("Optimal benefit (DP): " + best[0]);
        report("--- Dynamic Programming selection ---", dpChoice);
        report("--- Greedy (benefit per kg) selection ---", greedyChoice);

        in.close();
    }
}
