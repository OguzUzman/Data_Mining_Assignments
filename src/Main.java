import javax.xml.soap.Node;
import java.io.*;
import java.util.*;

/**
 * Created by oguz on 24/05/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        /**
         * First [] is row; the other is column.
         */
        boolean[][] data;
        //Sizeis number of rows(instances)
        boolean[] labels;
        String fileName = "data0_simple.txt";

        List<HypothesisNode> bestHypoteses = new ArrayList<>();
        double minQuality = 0;
        int m, n, k, F;

        StringBuilder sb = new StringBuilder();
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
        List<HypothesisNode> startingHypothesis = new ArrayList<>();
        startingHypothesis.add(startingNode);
        List<HypothesisNode> hypothesisNodes = new ArrayList<>();
        bfs3(F, startingHypothesis, data, bestHypoteses, k, p0, labels, m, 0, n, 0, new ArrayList<HypothesisNode>());


        sb.append("F = ");
        sb.append(F);
        sb.append("  ");
        sb.append("K = ");
        sb.append(k);
        sb.append("  ");
        sb.append("m = ");
        sb.append(m);
        sb.append("  ");
        sb.append("n = ");
        sb.append(n);
        sb.append("\n");

        for(HypothesisNode hypothesisNode : bestHypoteses){
            sb.append(hypothesisNode);
            sb.append("  q = ");
            sb.append(calculateQuality(F, hypothesisNode, data, p0, labels, m, n));
            sb.append("  Z-Score = ");
            double z = zScore(hypothesisNode, data, labels, m, n);
            sb.append(z);
            if (Math.abs(z) > 2.58) {
                sb.append("  rejected");
            }else{
                sb.append("  not rejected");
            }
            sb.append("\r\n");
        }
        System.out.println(sb);

        PrintWriter writer = new PrintWriter("./output.txt");
        writer.print(sb);
        writer.close();
        System.out.println("Total "+ (System.currentTimeMillis()-start) + " milliseconds");
    }

/**
    public static void bfs(int F, HypothesisNode hypothesisNode,
                           final boolean[][] data, List<HypothesisNode> bestHypoteses,
                           int sizeOfBestHypothesis, final double p0, final boolean[] labels, final int m){
        double minQualityHypothesis = 0;
        Queue queue = new LinkedList();
        queue.add(hypothesisNode);
        hypothesisNode.visited = true;
        while (!queue.isEmpty()){
            hypothesisNode = (HypothesisNode) queue.remove();
            if(bestHypoteses.size()<sizeOfBestHypothesis){
                bestHypoteses.add(hypothesisNode);
            } else if(calculateQuality(F, hypothesisNode, data, p0, labels, m) > minQualityHypothesis){
                bestHypoteses.set(sizeOfBestHypothesis, hypothesisNode);
                final HypothesisNode tempHypothesisNode = hypothesisNode;
                final int tempF = F;
                Collections.sort(bestHypoteses, new Comparator<HypothesisNode>() {
                    @Override
                    public int compare(HypothesisNode o1, HypothesisNode o2) {
                        if(calculateQuality(tempF, tempHypothesisNode, data, p0, labels, m) > calculateQuality(tempF, tempHypothesisNode, data, p0, labels, m))
                            return 1;
                        if (calculateQuality(tempF, tempHypothesisNode, data, p0, labels, m) < calculateQuality(tempF, tempHypothesisNode, data, p0, labels, m))
                            return -1;
                        return 0;
                    }
                });
                minQualityHypothesis = bestHypoteses.get(sizeOfBestHypothesis).getQuality();
            }

            if(calculateOptimisticQuality(F, hypothesisNode, data, labels, p0, m) < minQualityHypothesis){
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
    */

    public static void bfs3(int F, List<HypothesisNode> hypothesisNodes,
                            final boolean[][] data, List<HypothesisNode> bestHypoteses,
                            int sizeOfBestHypothesis, final double p0, final boolean[] labels, final int m, double worstQualityHypothesis, int numberOfAttributes, int level, List<HypothesisNode> all ){
        if(hypothesisNodes.size() == 0)
            return;
        Iterator<HypothesisNode> iterator = hypothesisNodes.iterator();
        List<HypothesisNode> childrenHypotheses = new ArrayList<>();
        while (iterator.hasNext()){
            int a = -1;
            HypothesisNode hypothesisNode = iterator.next();
            if(bestHypoteses.size() < sizeOfBestHypothesis){
                bestHypoteses.add(hypothesisNode);
                sortHypothesesArray(bestHypoteses, F, data, p0, labels, m, numberOfAttributes);
                worstQualityHypothesis = calculateQuality(F, bestHypoteses.get(bestHypoteses.size()-1), data, p0,
                        labels, m, numberOfAttributes);
                a = 1;
            } else if(calculateQuality(F, hypothesisNode, data, p0, labels, m, numberOfAttributes) > worstQualityHypothesis){
                sortHypothesesArray(bestHypoteses, F, data, p0, labels, m, numberOfAttributes);
                bestHypoteses.set(sizeOfBestHypothesis-1, hypothesisNode);
                sortHypothesesArray(bestHypoteses, F, data, p0, labels, m, numberOfAttributes);
                worstQualityHypothesis = calculateQuality(F, bestHypoteses.get(sizeOfBestHypothesis-1), data, p0,
                        labels, m, numberOfAttributes);
                a= 2;
            }

            double optimisticQuality = calculateOptimisticQuality(F, hypothesisNode, data, labels, p0, m, numberOfAttributes);

            double g = ((double)extSize(hypothesisNode, data, labels, m, numberOfAttributes))/((double) labels.length);

            if((optimisticQuality < worstQualityHypothesis) | g < 0.2){
                //Prune // Not really necessary
        //        all.addAll(hypothesisNode.getChildren());
                iterator.remove();
            } else {
                childrenHypotheses.addAll(hypothesisNode.getChildren());
            }
        }
        bfs3(F, childrenHypotheses, data, bestHypoteses, sizeOfBestHypothesis, p0, labels, m, worstQualityHypothesis, numberOfAttributes, ++level, all);
    }

    public static void sortHypothesesArray(List<HypothesisNode> hypothesisNodes, final int F, final boolean[][] data,
                                           final double p0, final boolean[] labels, final int m, final int numberOfAtributes){
        Collections.sort(hypothesisNodes, new Comparator<HypothesisNode>() {
            @Override
            public int compare(HypothesisNode o1, HypothesisNode o2) {
                if(calculateQuality(F, o1, data, p0, labels, m, numberOfAtributes) > calculateQuality(F, o2, data, p0, labels, m, numberOfAtributes))
                    return -1;
                if (calculateQuality(F, o1, data, p0, labels, m, numberOfAtributes) < calculateQuality(F, o2, data, p0, labels, m, numberOfAtributes))
                    return 1;
                return 0;
            }
        });
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
    public static double calculateQuality(int F, HypothesisNode hypothesisNode, boolean[][] data, double p0, boolean[] labels, int m, int numberOfAttributes){
       // if(hypothesisNode.isQualityIsSet())
            //return hypothesisNode.getQuality();
        double p = 0;
        if(hypothesisNode.isPSet()){
            p = hypothesisNode.getP();
        }else {
            p = calculateP(hypothesisNode, data, labels, m, numberOfAttributes);
        }
        double quality = 0;
        double g = ((double)extSize(hypothesisNode, data, labels, m, numberOfAttributes))/((double) labels.length);
        switch (F){
            case 1:
                quality = Math.sqrt(g) * Math.abs(p-p0);
                break;
            case 2:
                quality = ((g)/(1.0-g))*Math.pow(p-p0, 2);
                if (g == 1.0) {
                    quality = 0.0;
                }
                break;
            case 3:
                quality = g*(2*p-1.0) + 1 - p0;
                break;
        }
        hypothesisNode.setQuality(quality);
        return quality;
    }
    public static double calculateOptimisticQuality(int F, HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, double p0, int m, int numberOfAttributes){
        if(hypothesisNode.isOptimalQualitySet()){
            return hypothesisNode.getOptimalQuality();
        }
        double p = 0;
        if(hypothesisNode.isPSet()){
            p = hypothesisNode.getP();
        }else {
            p = calculateP(hypothesisNode, data, labels, m, numberOfAttributes);
        }
        double g = ((double)extSize(hypothesisNode, data, labels, m, numberOfAttributes))/((double) labels.length);
        double optimalQuality = 0;
        switch (F){
            case 1:
                optimalQuality = Math.sqrt(g)*Math.max(p0, 1-p0);
                break;
            case 2:
            case 3:
                optimalQuality = calculateQuality(F, hypothesisNode, data, p0, labels, m, numberOfAttributes);
                break;
        }
        hypothesisNode.setOptimalQuality(optimalQuality);
        return optimalQuality;
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
    public static int extSize(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m, int numberOfAttributes){
        if(hypothesisNode.extSize != -1)
            return hypothesisNode.extSize;
        int total = 0;
        int[] hypothesis = hypothesisNode.hypothesis;
        for(int i = 0; i < labels.length; i++){
            boolean matches = true;
            for(int j = 0; j < numberOfAttributes; j++){
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
        hypothesisNode.extSize = total;
        return total;
    }

    /**
     * @param hypothesisNode
     * @param data
     * @param labels
     * @param m
     * @return
     */
    public static int extTIntersectionSize(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m, int numberOfAttributes){
        int total = 0;
        int[] hypothesis = hypothesisNode.hypothesis;
        for(int i = 0; i < labels.length; i++){
            boolean matches = true;
            if(labels[i]) {
                for (int j = 0; j < numberOfAttributes; j++) {
                    /**
                     * Since "0 and 1" or "1 and 1" will give 0; and others 1; We can check it by summation. 0+ 1 = 1+0 = 1
                     * While -1 +0 = -1; -1 + 1 = 0
                     */
                    try {

                        if ((hypothesis[j] + (data[i][j] ? 1 : 0)) == 1) {
                            matches = false;
                            break;
                        }
                    } catch (Throwable t){
                        t.printStackTrace();
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
    public static double calculateP(HypothesisNode hypothesisNode, boolean[][] data, boolean[] labels, int m, int numberOfAttributes){
        if(hypothesisNode.isPSet())
            return hypothesisNode.getP();
        double intersectionCount = extTIntersectionSize(hypothesisNode, data, labels, m, numberOfAttributes);
        double extSize = extSize(hypothesisNode, data, labels, m, numberOfAttributes);

        double p = intersectionCount/extSize;
        if(extSize == 0){
            p = 1;
        }
        hypothesisNode.setP(p);
        return p;
    }

    public static double zScore(HypothesisNode h, boolean[][] data, boolean[] labels, int m, int numberOfAttributes) {
        double result = 0;
        double p = 0;
        double pprime = 0;
        p = percentageOfPositivesInDataSet(labels);
        pprime = extSize(h, data, labels, m, numberOfAttributes) / (double)labels.length;

        result = (double)(pprime - p) / Math.sqrt(p*(1-p)/labels.length);
        return result;
    }

}

