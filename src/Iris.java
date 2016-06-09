/**
 * Created by oguz on 07/06/16.
 */
public class Iris {
    public static void main(String[] args) throws Exception{
        KMeans kMeans = new KMeans("iris.data.txt", "euclidean");
        kMeans.stringBuilder = new StringBuilder();
        kMeans.run();
        KMeans customKMeans = new KMeans("iris.data.txt", "custom");
        customKMeans.stringBuilder = new StringBuilder();
        customKMeans.run();

        System.out.println("\n\n\n\n\n---------Iris Data Results---------\n\n");
        System.out.println(kMeans.stringBuilder);

        double m = kMeans.computePurity();

        System.out.println(String.format("Purity is: %.3f", m));
        System.out.println();
        System.out.println(customKMeans.stringBuilder);
        m = customKMeans.computePurity();
        System.out.println(String.format("Purity is: %.3f", m));

    }
}
