import java.io.*;
import java.util.*;

public class QuickSortCSV {

    static class Movie {
        String title;
        int popularity;

        Movie(String title, int popularity) {
            this.title = title;
            this.popularity = popularity;
        }
    }

    // Quick Sort
    public static void quickSort(Movie[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    // Partition Function (Lomuto scheme, last element as pivot)
    public static int partition(Movie[] arr, int low, int high) {
        int pivot = arr[high].popularity;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j].popularity <= pivot) {
                i++;
                Movie temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        Movie temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("movies_real_titles_clean.csv"));
        String header = br.readLine(); // Read header: title,popularity

        ArrayList<Movie> list = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isBlank()) continue;
            int lastComma = line.lastIndexOf(',');
            String title = line.substring(0, lastComma);
            int popularity = Integer.parseInt(line.substring(lastComma + 1).trim());
            list.add(new Movie(title, popularity));
        }
        br.close();

        Movie[] movies = list.toArray(new Movie[0]);

        long start = System.nanoTime();
        // Apply Quick Sort
        quickSort(movies, 0, movies.length - 1);
        long end = System.nanoTime();

        // Display Sorted Data (first 15 rows)
        System.out.println("\n====================== SORTED MOVIES (QUICK SORT) ======================\n");
        System.out.println("+--------------------------------------------+------------+");
        System.out.printf("| %-44s | %-10s |%n", "Title", "Popularity");
        System.out.println("+--------------------------------------------+------------+");

        int limit = Math.min(15, movies.length);
        for (int k = 0; k < limit; k++) {
            System.out.printf("| %-44s | %-10d |%n", movies[k].title, movies[k].popularity);
        }
        System.out.println("+--------------------------------------------+------------+");

        System.out.println("\nTotal Records Sorted : " + movies.length);
        System.out.println("Sorting Algorithm    : Quick Sort");
        System.out.println("Sorted By            : Popularity (Ascending)");
        System.out.println("Time Taken (ns)      : " + (end - start));
    }
}
