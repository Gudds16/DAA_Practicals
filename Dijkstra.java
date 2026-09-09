import java.util.*;

public class Dijkstra {

    static void dijkstra(int source, ArrayList<ArrayList<int[]>> graph, int[] dist) {

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<int[]> pq =
                new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        pq.add(new int[]{0, source});

        while (!pq.isEmpty()) {

            int[] current = pq.poll();

            int d = current[0];
            int u = current[1];

            if (d > dist[u])
                continue;

            for (int[] edge : graph.get(u)) {

                int v = edge[0];
                int w = edge[1];

                if (dist[v] > dist[u] + w) {

                    dist[v] = dist[u] + w;

                    pq.add(new int[]{dist[v], v});
                }
            }
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of intersections: ");
        int V = sc.nextInt();

        System.out.print("Enter number of roads: ");
        int E = sc.nextInt();

        ArrayList<ArrayList<int[]>> graph = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }

        System.out.println("Enter roads (u v time):");

        for (int i = 0; i < E; i++) {

            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();

            graph.get(u).add(new int[]{v, w});
            graph.get(v).add(new int[]{u, w});
        }

        System.out.print("Enter ambulance start location: ");
        int source = sc.nextInt();

        System.out.print("Enter number of hospitals: ");
        int H = sc.nextInt();

        int[] hospitals = new int[H];

        System.out.print("Enter hospital locations: ");

        for (int i = 0; i < H; i++) {
            hospitals[i] = sc.nextInt();
        }

        int[] dist = new int[V];

        dijkstra(source, graph, dist);

        int nearestHospital = -1;
        int minTime = Integer.MAX_VALUE;

        for (int h : hospitals) {

            if (dist[h] < minTime) {
                minTime = dist[h];
                nearestHospital = h;
            }
        }

        if (nearestHospital == -1 || minTime == Integer.MAX_VALUE) {
            System.out.println("No hospital reachable.");
        } else {
            System.out.println("Nearest hospital: " + nearestHospital);
            System.out.println("Travel time: " + minTime + " minutes");
        }

        sc.close();
    }
}