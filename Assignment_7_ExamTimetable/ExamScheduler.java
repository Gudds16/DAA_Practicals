import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class ExamScheduler {

    static class Hall {
        final String name;
        final int seats;

        Hall(String name, int seats) {
            this.name = name;
            this.seats = seats;
        }
    }

    // ---------- helpers ----------
    static int smallestFreeColour(List<TreeSet<Integer>> g, int[] colour, int u) {
        boolean[] blocked = new boolean[g.size() + 1];
        for (int v : g.get(u)) {
            if (colour[v] != -1) {
                blocked[colour[v]] = true;
            }
        }
        int c = 0;
        while (blocked[c]) {
            c++;
        }
        return c;
    }

    static int slotsUsed(int[] colour) {
        int max = -1;
        for (int c : colour) {
            max = Math.max(max, c);
        }
        return max + 1;
    }

    // ---------- Welsh-Powell: highest degree first, then greedy ----------
    static int[] welshPowell(List<TreeSet<Integer>> g) {
        int n = g.size();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> g.get(b).size() - g.get(a).size());

        int[] colour = new int[n];
        Arrays.fill(colour, -1);
        for (int u : order) {
            colour[u] = smallestFreeColour(g, colour, u);
        }
        return colour;
    }

    // ---------- DSATUR: pick the vertex with most distinct neighbour colours ----------
    static int[] dsatur(List<TreeSet<Integer>> g) {
        int n = g.size();
        int[] colour = new int[n];
        Arrays.fill(colour, -1);

        for (int step = 0; step < n; step++) {
            int pick = -1, pickSat = -1, pickDeg = -1;
            for (int u = 0; u < n; u++) {
                if (colour[u] != -1) {
                    continue;
                }
                TreeSet<Integer> seen = new TreeSet<>();
                for (int v : g.get(u)) {
                    if (colour[v] != -1) {
                        seen.add(colour[v]);
                    }
                }
                int sat = seen.size();
                int deg = g.get(u).size();
                if (sat > pickSat || (sat == pickSat && deg > pickDeg)) {
                    pick = u;
                    pickSat = sat;
                    pickDeg = deg;
                }
            }
            colour[pick] = smallestFreeColour(g, colour, pick);
        }
        return colour;
    }

    static boolean isConflictFree(List<TreeSet<Integer>> g, int[] colour) {
        for (int u = 0; u < g.size(); u++) {
            for (int v : g.get(u)) {
                if (colour[u] == colour[v]) {
                    return false;
                }
            }
        }
        return true;
    }

    // ---------- Room allocation: largest exam first, smallest hall that fits ----------
    static String[] allocateHalls(int[] colour, int[] headcount, Hall[] halls) {
        int n = colour.length;
        String[] result = new String[n];
        Arrays.fill(result, "Unassigned");

        for (int slot = 0; slot < slotsUsed(colour); slot++) {
            List<Integer> exams = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (colour[i] == slot) {
                    exams.add(i);
                }
            }
            exams.sort((a, b) -> headcount[b] - headcount[a]);

            boolean[] busy = new boolean[halls.length];
            for (int e : exams) {
                int chosen = -1;
                for (int h = 0; h < halls.length; h++) {
                    if (busy[h] || halls[h].seats < headcount[e]) {
                        continue;
                    }
                    if (chosen == -1 || halls[h].seats < halls[chosen].seats) {
                        chosen = h;
                    }
                }
                if (chosen != -1) {
                    busy[chosen] = true;
                    result[e] = halls[chosen].name;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.print("Number of subjects: ");
        int n = in.nextInt();
        String[] subject = new String[n];
        Map<String, Integer> idOf = new HashMap<>();
        System.out.println("Subject codes:");
        for (int i = 0; i < n; i++) {
            subject[i] = in.next();
            idOf.put(subject[i], i);
        }

        List<TreeSet<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new TreeSet<>());
        }
        int[] headcount = new int[n];

        System.out.print("Number of students: ");
        int m = in.nextInt();
        System.out.println("Enrolments (roll count code1 code2 ...):");
        for (int s = 0; s < m; s++) {
            in.next();
            int k = in.nextInt();
            List<Integer> taken = new ArrayList<>();
            for (int j = 0; j < k; j++) {
                Integer id = idOf.get(in.next());
                if (id != null) {
                    taken.add(id);
                    headcount[id]++;
                }
            }
            for (int a = 0; a < taken.size(); a++) {
                for (int b = a + 1; b < taken.size(); b++) {
                    graph.get(taken.get(a)).add(taken.get(b));
                    graph.get(taken.get(b)).add(taken.get(a));
                }
            }
        }

        System.out.print("Number of halls: ");
        int r = in.nextInt();
        Hall[] halls = new Hall[r];
        System.out.println("Hall name and seats:");
        for (int i = 0; i < r; i++) {
            halls[i] = new Hall(in.next(), in.nextInt());
        }

        int edges = 0;
        for (TreeSet<Integer> adj : graph) {
            edges += adj.size();
        }
        edges /= 2;

        int[] wp = welshPowell(graph);
        int[] ds = dsatur(graph);

        System.out.println();
        System.out.println("Conflict graph: " + n + " subjects, " + edges + " conflicts");
        System.out.println("Welsh-Powell slots: " + slotsUsed(wp));
        System.out.println("DSATUR slots      : " + slotsUsed(ds));

        int[] best = slotsUsed(ds) <= slotsUsed(wp) ? ds : wp;
        String[] hall = allocateHalls(best, headcount, halls);

        System.out.println("Clash check       : " + (isConflictFree(graph, best) ? "PASSED" : "FAILED"));
        System.out.println();
        System.out.println("Final timetable (" + slotsUsed(best) + " slots):");
        for (int slot = 0; slot < slotsUsed(best); slot++) {
            System.out.println("Slot " + (slot + 1) + ":");
            for (int i = 0; i < n; i++) {
                if (best[i] == slot) {
                    System.out.println("   " + subject[i] + "  ->  " + hall[i]
                            + "  (" + headcount[i] + " students, " + graph.get(i).size() + " conflicts)");
                }
            }
        }

        in.close();
    }
}
