
import java.util.*;

public class ExamScheduler {

    static int n;
    static String[] subject;
    static int[][] graph;
    static int[] students;
    static String[] hallName;
    static int[] hallSeats;

    static String[] colourNames = {
        "Red", "Green", "Blue", "Yellow", "Orange",
        "Purple", "Pink", "Brown", "Cyan", "Magenta"
    };

    static int getDegree(int v) {
        int degree = 0;
        for (int i = 0; i < n; i++) {
            degree += graph[v][i];
        }
        return degree;
    }

    static int[] welshPowell() {
        Integer[] order = new Integer[n];

        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, (a, b) ->
            Integer.compare(getDegree(b), getDegree(a))
        );

        int[] color = new int[n];
        Arrays.fill(color, -1);

        int currentColor = 0;

        for (int v : order) {
            if (color[v] != -1) {
                continue;
            }

            color[v] = currentColor;

            for (int u : order) {
                if (color[u] == -1) {
                    boolean canColor = true;

                    for (int i = 0; i < n; i++) {
                        if (color[i] == currentColor && graph[u][i] == 1) {
                            canColor = false;
                            break;
                        }
                    }

                    if (canColor) {
                        color[u] = currentColor;
                    }
                }
            }

            currentColor++;
        }

        return color;
    }

    static int saturation(int v, int[] color) {
        Set<Integer> usedColors = new HashSet<>();

        for (int i = 0; i < n; i++) {
            if (graph[v][i] == 1 && color[i] != -1) {
                usedColors.add(color[i]);
            }
        }

        return usedColors.size();
    }

    static int[] dsatur() {
        int[] color = new int[n];
        Arrays.fill(color, -1);

        for (int count = 0; count < n; count++) {
            int selected = -1;
            int maxSaturation = -1;
            int maxDegree = -1;

            for (int v = 0; v < n; v++) {
                if (color[v] == -1) {
                    int sat = saturation(v, color);
                    int degree = getDegree(v);

                    if (sat > maxSaturation ||
                        (sat == maxSaturation && degree > maxDegree)) {
                        selected = v;
                        maxSaturation = sat;
                        maxDegree = degree;
                    }
                }
            }

            boolean[] unavailable = new boolean[n];

            for (int i = 0; i < n; i++) {
                if (graph[selected][i] == 1 && color[i] != -1) {
                    unavailable[color[i]] = true;
                }
            }

            int c = 0;
            while (c < n && unavailable[c]) {
                c++;
            }

            color[selected] = c;
        }

        return color;
    }

    static int countColors(int[] color) {
        int max = -1;

        for (int c : color) {
            max = Math.max(max, c);
        }

        return max + 1;
    }

    static String getColourName(int color) {
        if (color < colourNames.length) {
            return colourNames[color];
        }

        return "Colour " + (color + 1);
    }

    static boolean checkClashes(int[] color) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph[i][j] == 1 && color[i] == color[j]) {
                    return false;
                }
            }
        }

        return true;
    }

    static int findHall(int requiredSeats, boolean[] usedHalls) {
        int selectedHall = -1;
        int smallestSeats = Integer.MAX_VALUE;

        for (int i = 0; i < hallName.length; i++) {
            if (!usedHalls[i] &&
                hallSeats[i] >= requiredSeats &&
                hallSeats[i] < smallestSeats) {

                selectedHall = i;
                smallestSeats = hallSeats[i];
            }
        }

        return selectedHall;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("========== EXAM SCHEDULER ==========");

        System.out.print("\nNumber of subjects: ");
        n = sc.nextInt();

        subject = new String[n];
        graph = new int[n][n];
        students = new int[n];

        System.out.println("Subject codes:");
        for (int i = 0; i < n; i++) {
            subject[i] = sc.next();
        }

        System.out.print("Number of students: ");
        int studentCount = sc.nextInt();

        System.out.println("Enrolments (roll count code1 code2 ...):");

        for (int i = 0; i < studentCount; i++) {
            String roll = sc.next();
            int count = sc.nextInt();

            int[] enrolled = new int[count];

            for (int j = 0; j < count; j++) {
                String code = sc.next();
                enrolled[j] = -1;

                for (int k = 0; k < n; k++) {
                    if (subject[k].equals(code)) {
                        enrolled[j] = k;
                        students[k]++;
                        break;
                    }
                }
            }

            for (int j = 0; j < count; j++) {
                for (int k = j + 1; k < count; k++) {
                    if (enrolled[j] != -1 && enrolled[k] != -1) {
                        graph[enrolled[j]][enrolled[k]] = 1;
                        graph[enrolled[k]][enrolled[j]] = 1;
                    }
                }
            }
        }

        System.out.print("Number of halls: ");
        int hallCount = sc.nextInt();

        hallName = new String[hallCount];
        hallSeats = new int[hallCount];

        System.out.println("Hall name and seats:");

        for (int i = 0; i < hallCount; i++) {
            hallName[i] = sc.next();
            hallSeats[i] = sc.nextInt();
        }

        int conflicts = 0;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph[i][j] == 1) {
                    conflicts++;
                }
            }
        }

        int[] wpColor = welshPowell();
        int[] dsColor = dsatur();

        int[] best = countColors(wpColor) <= countColors(dsColor)
                ? wpColor : dsColor;

        int totalSlots = countColors(best);

        System.out.println("\n========== COLOURING RESULTS ==========");
        System.out.println("Conflict graph: " + n
                + " subjects, " + conflicts + " conflicts");
        System.out.println("Welsh-Powell slots: " + countColors(wpColor));
        System.out.println("DSATUR slots      : " + countColors(dsColor));
        System.out.println("Clash check       : "
                + (checkClashes(best) ? "PASSED" : "FAILED"));

        System.out.println("\n========== COLOUR ASSIGNMENT ==========");

        for (int i = 0; i < n; i++) {
            System.out.println(subject[i] + " -> "
                    + getColourName(best[i])
                    + " (Slot " + (best[i] + 1) + ")");
        }

        System.out.println("\n========== FINAL TIMETABLE ==========");
        System.out.println("Total slots: " + totalSlots);

        for (int slot = 0; slot < totalSlots; slot++) {
            System.out.println("\nSlot " + (slot + 1)
                    + " (Colour " + getColourName(slot) + "):");

            boolean[] usedHalls = new boolean[hallCount];

            List<Integer> slotSubjects = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if (best[i] == slot) {
                    slotSubjects.add(i);
                }
            }

            slotSubjects.sort((a, b) ->
                Integer.compare(students[b], students[a])
            );

            for (int i : slotSubjects) {
                int hall = findHall(students[i], usedHalls);

                System.out.println("  Subject   : " + subject[i]);

                if (hall != -1) {
                    System.out.println("  Hall      : " + hallName[hall]);
                    usedHalls[hall] = true;
                } else {
                    System.out.println("  Hall      : No suitable hall available");
                }

                System.out.println("  Colour    : " + getColourName(best[i]));
                System.out.println("  Students  : " + students[i]);
                System.out.println("  Conflicts : " + getDegree(i));
                System.out.println();
            }
        }

        System.out.println("======================================");

        sc.close();
    }
}
