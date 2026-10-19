import java.io.*;
import java.util.*;

public class MergeSortCSV {

    static class Order {
        int orderID;
        String[] data;

        Order(int orderID, String[] data) {
            this.orderID = orderID;
            this.data = data;
        }
    }

    // Merge Sort
    public static void mergeSort(Order[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
        }
    }

    // Merge Function

    public static void merge(Order[] arr, int left, int mid, int right) {

        int n1 = mid - left + 1;
        int n2 = right - mid;

        Order[] L = new Order[n1];
        Order[] R = new Order[n2];

        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];

        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (L[i].orderID <= R[j].orderID) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1)
            arr[k++] = L[i++];

        while (j < n2)
            arr[k++] = R[j++];
    }

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("orders.csv"));

        String header = br.readLine(); // Read header

        ArrayList<Order> list = new ArrayList<>();

        String line;

        while ((line = br.readLine()) != null) {

            String[] values = line.split(",");

            int orderID = Integer.parseInt(values[1]); // OrderID column

            list.add(new Order(orderID, values));
        }

        br.close();

        Order[] orders = list.toArray(new Order[0]);

        // Apply Merge Sort
        mergeSort(orders, 0, orders.length - 1);

        // Display Sorted Data
        System.out.println("\n====================== SORTED ORDERS (MERGE SORT) ======================\n");

        System.out.println("+------------+----------+----------------------+------------+---------------+------------+");
        System.out.printf("| %-10s | %-8s | %-20s | %-10s | %-13s | %-10s |%n",
                "CustomerID", "OrderID", "Product", "Amount", "Date", "Location");
        System.out.println("+------------+----------+----------------------+------------+---------------+------------+");

        for (Order order : orders) {
            System.out.printf("| %-10s | %-8s | %-20s | %-10s | %-13s | %-10s |%n",
                    order.data[0], // CustomerID
                    order.data[1], // OrderID
                    order.data[2], // Product
                    order.data[3], // Transaction Amount
                    order.data[4], // Purchase Date
                    order.data[5]  // Location
            );
        }

        System.out.println("+------------+----------+----------------------+------------+---------------+------------+");

        System.out.println("\nTotal Records Sorted : " + orders.length);
        System.out.println("Sorting Algorithm    : Merge Sort");
        System.out.println("Sorted By            : OrderID (Ascending)");
    }
}
