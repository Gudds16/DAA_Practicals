import java.util.*;

public class fractionalKnapsack {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of items: ");
        int n = sc.nextInt();

        double[] weight = new double[n];
        double[] value = new double[n];

        // Input
        for (int i = 0; i < n; i++) {
            System.out.print("Enter weight of item " + (i + 1) + ": ");
            weight[i] = sc.nextDouble();

            System.out.print("Enter value of item " + (i + 1) + ": ");
            value[i] = sc.nextDouble();
        }

        System.out.print("Enter knapsack capacity: ");
        double capacity = sc.nextDouble();

        // Sort according to value/weight ratio
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {

                double ratio1 = value[i] / weight[i];
                double ratio2 = value[j] / weight[j];

                if (ratio1 < ratio2) {

                    double temp = weight[i];
                    weight[i] = weight[j];
                    weight[j] = temp;

                    temp = value[i];
                    value[i] = value[j];
                    value[j] = temp;
                }
            }
        }

        double totalValue = 0;

        // Fill knapsack
        for (int i = 0; i < n; i++) {

            if (weight[i] <= capacity) {

                // Take complete item
                capacity = capacity - weight[i];
                totalValue = totalValue + value[i];

                System.out.println("Item " + (i + 1) + " taken completely");

            } else {

                // Take fraction
                double fraction = capacity / weight[i];

                totalValue = totalValue + value[i] * fraction;

                System.out.println(
                    "Item " + (i + 1) + " taken " +
                    (fraction * 100) + "%"
                );

                break;
            }
        }

        System.out.println("\nMaximum value = " + totalValue);

        sc.close();
    }
}
