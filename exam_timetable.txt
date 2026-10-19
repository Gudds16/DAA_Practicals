import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExamTimetable {
    static class Room {
        String name;
        int capacity;

        Room(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of courses: ");
        int numCourses = sc.nextInt();

        String[] courses = new String[numCourses];
        Map<String, Integer> courseIndex = new HashMap<>();

        System.out.println("Enter course codes:");
        for (int i = 0; i < numCourses; i++) {
            courses[i] = sc.next();
            courseIndex.put(courses[i], i);
        }

        System.out.print("Enter number of students: ");
        int numStudents = sc.nextInt();

        int[] studentCount = new int[numCourses];
        int[][] adj = new int[numCourses][numCourses];

        System.out.println("Enter student enrollments (student_id count course1 course2 ...):");
        for (int i = 0; i < numStudents; i++) {
            String studentId = sc.next();
            int count = sc.nextInt();
            List<Integer> enrolled = new ArrayList<>();

            for (int j = 0; j < count; j++) {
                String courseCode = sc.next();
                if (courseIndex.containsKey(courseCode)) {
                    int courseIdx = courseIndex.get(courseCode);
                    studentCount[courseIdx]++;
                    enrolled.add(courseIdx);
                }
            }

            for (int a = 0; a < enrolled.size(); a++) {
                for (int b = a + 1; b < enrolled.size(); b++) {
                    int u = enrolled.get(a);
                    int v = enrolled.get(b);
                    adj[u][v] = 1;
                    adj[v][u] = 1;
                }
            }
        }

        System.out.print("Enter number of available classrooms: ");
        int numRooms = sc.nextInt();

        Room[] rooms = new Room[numRooms];
        System.out.println("Enter room name and capacity:");
        for (int i = 0; i < numRooms; i++) {
            String name = sc.next();
            int capacity = sc.nextInt();
            rooms[i] = new Room(name, capacity);
        }

        int[] degree = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            for (int j = 0; j < numCourses; j++) {
                if (adj[i][j] == 1) {
                    degree[i]++;
                }
            }
        }

        Integer[] order = new Integer[numCourses];
        for (int i = 0; i < numCourses; i++) {
            order[i] = i;
        }

        Arrays.sort(order, (a, b) -> Integer.compare(degree[b], degree[a]));

        int[] color = new int[numCourses];
        Arrays.fill(color, -1);

        for (int idx = 0; idx < numCourses; idx++) {
            int u = order[idx];
            boolean[] used = new boolean[numCourses + 1];

            for (int v = 0; v < numCourses; v++) {
                if (adj[u][v] == 1 && color[v] != -1) {
                    used[color[v]] = true;
                }
            }

            int assignedColor = 0;
            while (used[assignedColor]) {
                assignedColor++;
            }
            color[u] = assignedColor;
        }

        int maxColor = 0;
        for (int i = 0; i < numCourses; i++) {
            maxColor = Math.max(maxColor, color[i]);
        }

        System.out.println("\nExam Timetable Schedule:");
        System.out.println("Total Time Slots Required: " + (maxColor + 1));
        System.out.println();

        String[] assignedRoom = new String[numCourses];
        Arrays.fill(assignedRoom, "Unassigned");

        for (int slot = 0; slot <= maxColor; slot++) {
            List<Integer> slotCourses = new ArrayList<>();
            for (int i = 0; i < numCourses; i++) {
                if (color[i] == slot) {
                    slotCourses.add(i);
                }
            }

            slotCourses.sort((a, b) -> Integer.compare(studentCount[b], studentCount[a]));
            boolean[] roomOccupied = new boolean[numRooms];

            for (int cIdx : slotCourses) {
                int needed = studentCount[cIdx];
                int bestRoom = -1;
                int minCapacityFit = Integer.MAX_VALUE;

                for (int r = 0; r < numRooms; r++) {
                    if (!roomOccupied[r] && rooms[r].capacity >= needed) {
                        if (rooms[r].capacity < minCapacityFit) {
                            minCapacityFit = rooms[r].capacity;
                            bestRoom = r;
                        }
                    }
                }

                if (bestRoom != -1) {
                    assignedRoom[cIdx] = rooms[bestRoom].name;
                    roomOccupied[bestRoom] = true;
                }
            }
        }

        for (int i = 0; i < numCourses; i++) {
            System.out.println("Course: " + courses[i]
                    + " -> Time Slot: Slot " + (color[i] + 1)
                    + " -> Room: " + assignedRoom[i]
                    + " (Students: " + studentCount[i] + ")");
        }

        sc.close();
    }
}
