import java.util.*;

/**
 * DAA Assignment 05 - Multistage Graph (Forward Dynamic Programming)
 * Scenario : MediLink Pharma - cold-chain medicine delivery network
 * Author   : Sahil Dhumal (PRN 125B2F006)
 */
public class AssignmentNo05 {

    // One directed road between two consecutive stages
    static class Road {
        final int from, to;
        final double baseCost;
        double liveCost;
        boolean closed;

        Road(int from, int to, double cost) {
            this.from = from;
            this.to = to;
            this.baseCost = cost;
            this.liveCost = cost;
            this.closed = false;
        }

        double effectiveCost() {
            return closed ? Double.POSITIVE_INFINITY : liveCost;
        }
    }

    static final double INF = Double.POSITIVE_INFINITY;
    static final String[] STAGE_NAMES = {
        "Plant", "Regional DC", "City Hub", "Local Depot", "Hospital"
    };

    static int stageCnt, nodeCnt;
    static int[] firstId, sizeOf, stageOf;
    static List<Road> roads = new ArrayList<>();
    static List<List<Road>> outgoing = new ArrayList<>();
    static double[] dist;
    static int[] parent;

    // ---------- Forward DP : cheapest cost from src to every later node ----------
    static void forwardDP(int src) {
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[src] = 0.0;

        for (int st = stageOf[src]; st < stageCnt - 1; st++) {
            for (int k = 0; k < sizeOf[st]; k++) {
                int u = firstId[st] + k;
                if (dist[u] == INF) {
                    continue;
                }
                for (Road r : outgoing.get(u)) {
                    double w = r.effectiveCost();
                    if (w == INF) {
                        continue;
                    }
                    if (dist[u] + w < dist[r.to]) {
                        dist[r.to] = dist[u] + w;
                        parent[r.to] = u;
                    }
                }
            }
        }
    }

    static Road findRoad(int u, int v) {
        for (Road r : outgoing.get(u)) {
            if (r.to == v) {
                return r;
            }
        }
        return null;
    }

    static String label(int node) {
        return node + "(" + STAGE_NAMES[Math.min(stageOf[node], STAGE_NAMES.length - 1)] + ")";
    }

    // ---------- Delivery request : source in stage 0, destination in last stage ----------
    static void deliver(int src, int dst) {
        if (src < 0 || src >= nodeCnt || dst < 0 || dst >= nodeCnt) {
            System.out.println("  Invalid node id.");
            return;
        }
        if (stageOf[src] != 0) {
            System.out.println("  Node " + src + " is not a Stage-0 (plant) node.");
            return;
        }
        if (stageOf[dst] != stageCnt - 1) {
            System.out.println("  Node " + dst + " is not in the last stage (hospital).");
            return;
        }

        forwardDP(src);
        if (dist[dst] == INF) {
            System.out.println("  No open route from " + src + " to " + dst + ".");
            return;
        }

        LinkedList<Integer> path = new LinkedList<>();
        for (int c = dst; c != -1; c = parent[c]) {
            path.addFirst(c);
        }

        System.out.println("  Route  : " + path.toString().replace("[", "").replace("]", "").replace(", ", " -> "));
        System.out.println("  Legs   :");
        for (int i = 0; i + 1 < path.size(); i++) {
            Road r = findRoad(path.get(i), path.get(i + 1));
            System.out.printf("     %s -> %s  cost %.2f%n",
                    label(path.get(i)), label(path.get(i + 1)), r.effectiveCost());
        }
        System.out.printf("  Total delivery cost = %.2f%n", dist[dst]);
    }

    static double costOf(int src, int dst) {
        forwardDP(src);
        return dist[dst];
    }

    static String fmt(double c) {
        return c == INF ? "BLOCKED" : String.format("%.2f", c);
    }

    // ---------- Cost matrix : every plant x every hospital ----------
    static void printMatrix() {
        System.out.println("  Cost matrix (rows = plants, columns = hospitals)");
        System.out.print("  " + String.format("%-8s", ""));
        for (int d = 0; d < sizeOf[stageCnt - 1]; d++) {
            System.out.print(String.format("%-10s", "H" + (firstId[stageCnt - 1] + d)));
        }
        System.out.println();
        for (int s = 0; s < sizeOf[0]; s++) {
            int src = firstId[0] + s;
            forwardDP(src);
            System.out.print("  " + String.format("%-8s", "P" + src));
            for (int d = 0; d < sizeOf[stageCnt - 1]; d++) {
                System.out.print(String.format("%-10s", fmt(dist[firstId[stageCnt - 1] + d])));
            }
            System.out.println();
        }
    }

    static void listRoads() {
        System.out.println("  From -> To   Base    Live    Status");
        for (Road r : roads) {
            System.out.printf("  %4d -> %-4d %6.2f  %6.2f  %s%n",
                    r.from, r.to, r.baseCost, r.liveCost, r.closed ? "CLOSED" : "open");
        }
    }

    // ---------- main : build network then run menu ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== MediLink Cold-Chain Route Planner (Forward DP) ===");

        System.out.print("Number of stages: ");
        stageCnt = sc.nextInt();
        if (stageCnt < 2) {
            System.out.println("Error: at least 2 stages are required.");
            return;
        }

        sizeOf = new int[stageCnt];
        firstId = new int[stageCnt];
        System.out.print("Nodes in each stage (" + stageCnt + " values): ");
        for (int i = 0; i < stageCnt; i++) {
            sizeOf[i] = sc.nextInt();
            if (sizeOf[i] <= 0) {
                System.out.println("Error: every stage needs at least one node.");
                return;
            }
            firstId[i] = nodeCnt;
            nodeCnt += sizeOf[i];
        }

        stageOf = new int[nodeCnt];
        for (int st = 0; st < stageCnt; st++) {
            for (int k = 0; k < sizeOf[st]; k++) {
                stageOf[firstId[st] + k] = st;
            }
        }
        dist = new double[nodeCnt];
        parent = new int[nodeCnt];
        for (int i = 0; i < nodeCnt; i++) {
            outgoing.add(new ArrayList<>());
        }

        System.out.print("Number of roads: ");
        int m = sc.nextInt();
        System.out.println("Enter roads as: from to cost");
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt(), v = sc.nextInt();
            double c = sc.nextDouble();
            if (u < 0 || u >= nodeCnt || v < 0 || v >= nodeCnt) {
                System.out.println("Rejected road " + u + " -> " + v + " : node id out of range.");
                continue;
            }
            if (stageOf[v] != stageOf[u] + 1) {
                System.out.println("Rejected road " + u + " -> " + v + " : must join consecutive stages.");
                continue;
            }
            if (c < 0) {
                System.out.println("Rejected road " + u + " -> " + v + " : negative cost.");
                continue;
            }
            if (findRoad(u, v) != null) {
                System.out.println("Rejected road " + u + " -> " + v + " : duplicate road.");
                continue;
            }
            Road r = new Road(u, v, c);
            roads.add(r);
            outgoing.get(u).add(r);
        }
        System.out.println("Network loaded: " + nodeCnt + " nodes, " + roads.size() + " roads.");

        int lastSrc = -1, lastDst = -1;
        boolean running = true;
        while (running) {
            System.out.println("\n----------------- MENU -----------------");
            System.out.println(" 1 Delivery request (plant, hospital)");
            System.out.println(" 2 Traffic / fuel update (u v multiplier)");
            System.out.println(" 3 Close a road (u v)");
            System.out.println(" 4 Reopen a road (u v)");
            System.out.println(" 5 Show cost matrix");
            System.out.println(" 6 List all roads");
            System.out.println(" 0 Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt();

            switch (ch) {
                case 1: {
                    System.out.print("Enter plant and hospital node ids: ");
                    int s = sc.nextInt(), d = sc.nextInt();
                    deliver(s, d);
                    if (s >= 0 && s < nodeCnt && d >= 0 && d < nodeCnt
                            && stageOf[s] == 0 && stageOf[d] == stageCnt - 1) {
                        lastSrc = s;
                        lastDst = d;
                    }
                    break;
                }
                case 2:
                case 3:
                case 4: {
                    System.out.print(ch == 2 ? "Enter u v multiplier: " : "Enter u v: ");
                    int u = sc.nextInt(), v = sc.nextInt();
                    double mul = (ch == 2) ? sc.nextDouble() : 1.0;
                    if (u < 0 || u >= nodeCnt || v < 0 || v >= nodeCnt) {
                        System.out.println("  Invalid node id. Nothing changed.");
                        break;
                    }
                    Road r = findRoad(u, v);
                    if (r == null) {
                        System.out.println("  Road " + u + " -> " + v + " does not exist.");
                        break;
                    }
                    if (ch == 2 && mul < 0) {
                        System.out.println("  Multiplier cannot be negative. Nothing changed.");
                        break;
                    }

                    double before = (lastSrc >= 0) ? costOf(lastSrc, lastDst) : 0;
                    if (ch == 2) {
                        r.liveCost = r.baseCost * mul;
                        System.out.printf("  Road %d -> %d : base %.2f x %.2f = live %.2f%n",
                                u, v, r.baseCost, mul, r.liveCost);
                    } else if (ch == 3) {
                        r.closed = true;
                        System.out.println("  Road " + u + " -> " + v + " is now CLOSED.");
                    } else {
                        r.closed = false;
                        System.out.println("  Road " + u + " -> " + v + " reopened at live cost "
                                + String.format("%.2f", r.liveCost) + ".");
                    }

                    if (lastSrc >= 0) {
                        double after = costOf(lastSrc, lastDst);
                        System.out.println("  Last request " + lastSrc + " -> " + lastDst
                                + " : " + fmt(before) + " => " + fmt(after));
                        deliver(lastSrc, lastDst);
                    }
                    break;
                }
                case 5:
                    printMatrix();
                    break;
                case 6:
                    listRoads();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("  Unknown option.");
            }
        }
        System.out.println("\nPlanner closed. Safe deliveries!");
        sc.close();
    }
}
