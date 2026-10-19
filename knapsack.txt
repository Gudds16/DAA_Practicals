import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Knapsack {
    static class Item {
        String name;
        int weight;
        int value;

        Item(String name, int weight, int value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of relief items: ");
        int n = sc.nextInt();

        System.out.print("Enter truck capacity (kg): ");
        int W = sc.nextInt();

        Item[] items = new Item[n];

        System.out.println("Enter each item's name, weight, and utility value:");
        for (int i = 0; i < n; i++) {
            String name = sc.next();
            int weight = sc.nextInt();
            int value = sc.nextInt();
            items[i] = new Item(name, weight, value);
        }

        int[][] dp = new int[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            int wt = items[i - 1].weight;
            int val = items[i - 1].value;

            for (int w = 1; w <= W; w++) {
                if (wt <= w) {
                    dp[i][w] = Math.max(val + dp[i - 1][w - wt], dp[i - 1][w]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        int maxUtility = dp[n][W];
        System.out.println("\nMaximum Relief Utility: " + maxUtility);

        ArrayList<String> selectedItems = new ArrayList<>();
        int totalWeight = 0;
        int currW = W;

        for (int i = n; i >= 1; i--) {
            if (dp[i][currW] != dp[i - 1][currW]) {
                selectedItems.add(items[i - 1].name);
                totalWeight += items[i - 1].weight;
                currW -= items[i - 1].weight;
            }
        }

        Collections.reverse(selectedItems);

        System.out.println("Total Weight Carried: " + totalWeight + " kg");
        System.out.println("Selected Relief Supplies:");
        for (String itemName : selectedItems) {
            System.out.println("- " + itemName);
        }

        sc.close();
    }
}
