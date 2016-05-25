import javax.xml.soap.Node;
import java.io.*;
import java.util.*;

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
        boolean[] labels;
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
            labels = new boolean[m];
            data = new boolean[m][n];

            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] bits = line.split(",");
                for(int i = 0; i < n; i++){
                    data[row][i] = bits[i].equals("1");
                }
                labels[row] = bits[n].equals("1");
                row++;
            }
        }

        HypothesisNode startingNode = new HypothesisNode(n, -1, -1, null);
        int numOfPositives = 0;
        for(boolean label : labels){
            if(label)
                numOfPositives++;
        }

        double p0 = percentageOfPositivesInDataSet(labels);

        bfs(F, startingNode, data, bestHypoteses, k, p0);

    }


    public static void bfs(int F, HypothesisNode hypothesisNode,
                           boolean[][] data, List<HypothesisNode> bestHypoteses,
                           int sizeOfBestHypothesis, double p0){
        double minQualityHypothesis = 0;
        Queue queue = new LinkedList();
        queue.add(hypothesisNode);
        hypothesisNode.visited = true;
        while (!queue.isEmpty()){
            hypothesisNode = (HypothesisNode) queue.remove();
            if(bestHypoteses.size()<sizeOfBestHypothesis){
                bestHypoteses.add(hypothesisNode);
            } else if(calculateQuality(F, hypothesisNode, data, p0) > minQualityHypothesis){
                bestHypoteses.set(sizeOfBestHypothesis, hypothesisNode);
                Collections.sort(bestHypoteses, new Comparator<HypothesisNode>() {
                    @Override
                    public int compare(HypothesisNode o1, HypothesisNode o2) {
                        if(o1.quality > o2.quality)
                            return 1;
                        if (o1.quality < o2.quality)
                            return -1;
                        return 0;
                    }
                });
                minQualityHypothesis = bestHypoteses.get(sizeOfBestHypothesis).quality;
            }

            if(calculateOptimisticQuality(F, hypothesisNode, data, p0) < minQualityHypothesis){
                hypothesisNode.visited = true;
                hypothesisNode.prune();
            } else{
                List<HypothesisNode> children = hypothesisNode.children;
                for (HypothesisNode child : children) {
                    if (!child.visited) {
                        queue.add(child);
                        child.visited = true;
                    }
                }
            }
        }
    }


    /**
    public static void traverseNodes2(int F, HypothesisNode hypothesisNode,
                                     boolean[][] data, List<HypothesisNode> bestHypoteses,
                                     int minQualityHypothesis, int sizeOfBestHypothesis){
        double quality = calculateQuality(F, hypothesisNode, data);
        if(bestHypoteses.size() < sizeOfBestHypothesis){
            bestHypoteses.add(hypothesisNode);
        }else if(minQualityHypothesis< quality){
            bestHypoteses.set(sizeOfBestHypothesis-1, hypothesisNode);
            Collections.sort(bestHypoteses, new Comparator<HypothesisNode>() {
                @Override
                public int compare(HypothesisNode o1, HypothesisNode o2) {
                    if (o1.quality < o2.quality) return -1;
                    if (o1.quality > o2.quality) return 1;
                    return 0;
                }
            });

        }
        double optimalQuality = calculateOptimisticQuality(F, hypothesisNode, data);
        if(optimalQuality < minQualityHypothesis){
            hypothesisNode.prune();
        }

    }*/



    /**
     *
     * @param F which quality function
     * @param hypothesisNode hypothesis to calculate the quality
     * @param data dataset
     * @return
     */
    public static double calculateQuality(int F, HypothesisNode hypothesisNode, boolean[][] data, double p0){

        return 0;
    }
    public static double calculateOptimisticQuality(int F, HypothesisNode hypothesisNode, boolean[][] data, double p0){

    }
    public static List<HypothesisNode> getUnvisitedChild(HypothesisNode hypothesisNode){
return null;
    }

    /**
     * Gives p0
     * @param labels
     * @return
     */
    public static double percentageOfPositivesInDataSet(boolean[] labels){
        double count = 0;
        for (boolean label: labels){
            if (label)
                count++;
        }
        return count/((double)labels.length);
    }

    /**
     * @param hypothesisNode
     * @param data
     * @param labels
     * @param m
     * @return
     */
    public static int extSize(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m){
        int total = 0;
        int[] hypothesis = hypothesisNode.hypothesis;
        for(int i = 0; i < labels.length; i++){
            boolean matches = true;
            for(int j = 0; j < m; j++){
                /**
                 * Since "0 and 1" or "1 and 1" will give 0; and others 1; We can check it by summation. 0+ 1 = 1+0 = 1
                 * While -1 +0 = -1; -1 + 1 = 0
                 */
                if((hypothesis[j] + (data[i][j] ? 1:0)) == 1){
                    matches = false;
                    break;
                }
            }
            if(matches){
                total++;
            }
        }
        return total;
    }

    /**
     * Gives p
     * @param hypothesisNode
     * @param data
     * @param labels
     * @param m
     * @return
     */
    public static int extTIntersectionSize(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m){
        int total = 0;
        int[] hypothesis = hypothesisNode.hypothesis;
        for(int i = 0; i < labels.length; i++){
            boolean matches = true;
            if(labels[i]) {
                for (int j = 0; j < m; j++) {
                    /**
                     * Since "0 and 1" or "1 and 1" will give 0; and others 1; We can check it by summation. 0+ 1 = 1+0 = 1
                     * While -1 +0 = -1; -1 + 1 = 0
                     */
                    if ((hypothesis[j] + (data[i][j] ? 1 : 0)) == 1) {
                        matches = false;
                        break;
                    }
                }
            } else {
                matches = false;
            }
            if(matches){
                total++;
            }
        }
        return total;
    }

    /**
     * Calculates p with hypothesis, data and labels
     * @param hypothesisNode
     * @param data
     * @return
     */
    public static double calculateP(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m){
        double intersectionCount = extTIntersectionSize(hypothesisNode, data, labels, m);
        double extSize = extSize(hypothesisNode, data, labels, m);
        return intersectionCount/extSize;
    }
}