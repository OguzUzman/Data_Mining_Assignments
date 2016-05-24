import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oguz on 24/05/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {


        /**
         * First [] is row; the other is column.
         */
        boolean[][] data;
        //Sizeis number of rows(instances)
        boolean[] label;
        String fileName = "SPECT.train.txt";

        List<HypothesisNode> bestHypoteses = new ArrayList<>();
        double minQuality = 0;
        int m, n, k, F;

        //m instances
        //n attributes
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            line = br.readLine();
            String[] nums = line.split(",");
            m = Integer.parseInt(nums[0]);
            n = Integer.parseInt(nums[1]);
            k = Integer.parseInt(nums[2]);
            F = Integer.parseInt(nums[3]);
            label = new boolean[m];
            data = new boolean[m][n];

            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] bits = line.split(",");
                for(int i = 0; i < n; i++){
                    data[row][i] = bits[i].equals("1");
                }
                label[row] = bits[n].equals("1");
                row++;
            }
        }

        HypothesisNode startingNode = new HypothesisNode(n, -1, -1, null);

        traverseNodes(1, startingNode, data);

    }


    public static void traverseNodes(int F, HypothesisNode hypothesisNode, boolean[][] data, List<HypothesisNode> bestHypoteses, int maxBest){
        double quality = calculateQuality(F, hypothesisNode, data);
        if(bestHypoteses.size() < maxBest | )
    }



    /**
     *
     * @param F which quality function
     * @param hypothesisNode hypothesis to calculate the quality
     * @param data dataset
     * @return
     */
    public static double calculateQuality(int F, HypothesisNode hypothesisNode, boolean[][] data){
        return 0;
    }
}
