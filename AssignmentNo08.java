import java.util.*;

/**
 * DAA Assignment 08 - Travelling Salesman Problem using Least Cost Branch and Bound
 * Scenario : FreshRoute Dairy - milk delivery truck with fuel cost and time limit
 * Author   : Sahil Dhumal (PRN 125B2F006)
 */
public class AssignmentNo08 {

    static final int INF = 1_000_000;

    static class Node implements Comparable<Node> {
        int[][] mat;
        int lb;
        int last;
        int timeUsed;
        ArrayList<Integer> path;

        Node(int[][] mat, int lb, int last, int timeUsed, ArrayList<Integer> path) {
            this.mat = mat;
            this.lb = lb;
            this.last = last;
            this.timeUsed = timeUsed;
            this.path = path;
        }

        // least lower bound first; deeper node first when bounds tie
        public int compareTo(Node o) {
            if (lb != o.lb) {
                return Integer.compare(lb, o.lb);
            }
            return Integer.compare(o.path.size(), path.size());
        }
    }

    static int n, start;
    static String[] names;
    static int[][] fuel, minutes;
    static long generated, expanded, boundPruned, timePruned;

    // ---------- matrix reduction : returns the total amount subtracted ----------
    static int reduce(int[][] m) {
        int total = 0;
        for (int i = 0; i < n; i++) {
            int mn = INF;
            for (int j = 0; j < n; j++) {
                mn = Math.min(mn, m[i][j]);
            }
            if (mn != INF && mn > 0) {
                for (int j = 0; j < n; j++) {
                    if (m[i][j] != INF) {
                        m[i][j] -= mn;
                    }
                }
                total += mn;
            }
        }
        for (int j = 0; j < n; j++) {
            int mn = INF;
            for (int i = 0; i < n; i++) {
                mn = Math.min(mn, m[i][j]);
            }
            if (mn != INF && mn > 0) {
                for (int i = 0; i < n; i++) {
                    if (m[i][j] != INF) {
                        m[i][j] -= mn;
                    }
                }
                total += mn;
            }
        }
        return total;
    }

    static int[][] copyOf(int[][] m) {
        int[][] c = new int[n][];
        for (int i = 0; i < n; i++) {
            c[i] = m[i].clone();
        }
        return c;
    }

    static String pathText(List<Integer> p) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.size(); i++) {
            sb.append(i == 0 ? "" : " -> ").append(p.get(i));
        }
        return sb.toString();
    }

    // ---------- Least Cost Branch and Bound ----------
    static int bestCost;
    static ArrayList<Integer> bestPath;

    static void solveLCBB(int limit, int traceLines) {
        generated = expanded = boundPruned = timePruned = 0;
        bestCost = INF;
        bestPath = null;

        int[][] root = copyOf(fuel);
        for (int i = 0; i < n; i++) {
            root[i][i] = INF;
        }
        int lb0 = reduce(root);
        System.out.println("  Initial reduced-matrix lower bound = " + lb0);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        ArrayList<Integer> p0 = new ArrayList<>();
        p0.add(start);
        pq.add(new Node(root, lb0, start, 0, p0));
        generated++;
        int shown = 0;

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            if (cur.lb >= bestCost) {
                boundPruned++;
                continue;
            }
            expanded++;
            if (shown < traceLines) {
                shown++;
                System.out.printf("  #%-2d expand path [%s]  lb = %d%n", shown, pathText(cur.path), cur.lb);
            }

            if (cur.path.size() == n) {
                // all cities visited: close the loop with the real fuel cost
                int back = cur.last;
                int totalTime = cur.timeUsed + minutes[back][start];
                if (totalTime > limit) {
                    timePruned++;
                    continue;
                }
                int total = 0;
                for (int i = 0; i + 1 < cur.path.size(); i++) {
                    total += fuel[cur.path.get(i)][cur.path.get(i + 1)];
                }
                total += fuel[back][start];
                if (total < bestCost) {
                    bestCost = total;
                    bestPath = new ArrayList<>(cur.path);
                    bestPath.add(start);
                }
                continue;
            }

            int u = cur.last;
            for (int v = 0; v < n; v++) {
                if (cur.path.contains(v)) {
                    continue;
                }
                int t = cur.timeUsed + minutes[u][v];
                if (t + minutes[v][start] > limit) {   // cannot get back in time
                    timePruned++;
                    continue;
                }
                int[][] cm = copyOf(cur.mat);
                for (int k = 0; k < n; k++) {
                    cm[u][k] = INF;
                    cm[k][v] = INF;
                }
                cm[v][start] = INF;
                int lb = cur.lb + cur.mat[u][v] + reduce(cm);
                generated++;
                if (lb < bestCost) {
                    ArrayList<Integer> np = new ArrayList<>(cur.path);
                    np.add(v);
                    pq.add(new Node(cm, lb, v, t, np));
                } else {
                    boundPruned++;
                }
            }
        }
    }

    // ---------- brute force used only to verify the answer ----------
    static int bfBest, bfCount;

    static void brute(int u, boolean[] seen, int cnt, int c, int t, int limit) {
        if (cnt == n) {
            if (t + minutes[u][start] <= limit) {
                bfCount++;
                bfBest = Math.min(bfBest, c + fuel[u][start]);
            }
            return;
        }
        for (int v = 0; v < n; v++) {
            if (!seen[v] && t + minutes[u][v] <= limit) {
                seen[v] = true;
                brute(v, seen, cnt + 1, c + fuel[u][v], t + minutes[u][v], limit);
                seen[v] = false;
            }
        }
    }

    static void report(int limit, boolean trace) {
        System.out.println("\n--- Time limit = " + (limit >= INF ? "none" : limit + " min") + " ---");
        solveLCBB(limit, trace ? 8 : 0);

        if (bestPath == null) {
            System.out.println("  Result : no tour can finish within the time limit.");
        } else {
            System.out.println("  Best tour : " + pathText(bestPath));
            int tm = 0;
            for (int i = 0; i + 1 < bestPath.size(); i++) {
                int a = bestPath.get(i), b = bestPath.get(i + 1);
                tm += minutes[a][b];
                System.out.printf("     %-11s -> %-11s fuel %3d   time %3d min%n", names[a], names[b], fuel[a][b], minutes[a][b]);
            }
            System.out.println("  Total fuel cost = " + bestCost + ", total time = " + tm + " min");
        }
        System.out.printf("  Nodes: generated %d, expanded %d, pruned by bound %d, pruned by time %d%n",
                generated, expanded, boundPruned, timePruned);

        bfBest = INF;
        bfCount = 0;
        boolean[] seen = new boolean[n];
        seen[start] = true;
        brute(start, seen, 1, 0, 0, limit);
        long fact = 1;
        for (int i = 2; i < n; i++) {
            fact *= i;
        }
        System.out.println("  Check by brute force: " + bfCount + " of " + fact + " tours feasible, best = "
                + (bfCount == 0 ? "none" : String.valueOf(bfBest))
                + ((bfCount == 0 && bestPath == null) || (bestPath != null && bfBest == bestCost) ? "  [MATCH]" : "  [MISMATCH]"));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== FreshRoute Dairy : TSP by Least Cost Branch and Bound ===");
        System.out.print("Number of stops (including depot): ");
        n = sc.nextInt();
        if (n < 3 || n > 12) {
            System.out.println("Error: stops must be between 3 and 12.");
            return;
        }
        names = new String[n];
        System.out.print("Stop names (" + n + " words): ");
        for (int i = 0; i < n; i++) {
            names[i] = sc.next();
        }
        fuel = new int[n][n];
        minutes = new int[n][n];
        System.out.println("Fuel cost matrix (" + n + " x " + n + "):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                fuel[i][j] = sc.nextInt();
                if (fuel[i][j] < 0) {
                    System.out.println("Error: negative cost.");
                    return;
                }
            }
        }
        System.out.println("Travel time matrix in minutes (" + n + " x " + n + "):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                minutes[i][j] = sc.nextInt();
            }
        }
        System.out.print("Depot (start) index: ");
        start = sc.nextInt();
        if (start < 0 || start >= n) {
            System.out.println("Error: invalid depot index.");
            return;
        }

        boolean first = true;
        while (true) {
            System.out.print("\nEnter time limit in minutes (0 = no limit, -1 = quit): ");
            int lim = sc.nextInt();
            if (lim == -1) {
                break;
            }
            report(lim == 0 ? INF : lim, first);
            first = false;
        }
        System.out.println("\nRoute planner closed.");
        sc.close();
    }
}
