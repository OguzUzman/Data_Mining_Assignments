import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by oguz on 07/06/16.
 */
public class KMeans {

    public KMeans(String fileName, String method){
        this.fileName = fileName;
        this.method = method;
    }

    String fileName;
    String method = true ? "euclidean" : "custom";
    String[] labels;
    int[] assignedCenters = null;
    int numOfCenters = 3;
    int numOfSamples ;
    int numOfattributes ;

    public  StringBuilder stringBuilder = new StringBuilder();
    public  void run() throws Exception{

        File file = new File(fileName);
        double standardDeviation ;
        double centermu[] = new double[3];
        boolean centerMuSet = false;
        double[/** Rows**/][/**Columns**/] data;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            int row = 0;
            String line;
            line = br.readLine();
            String[] v = line.split(",");
            numOfSamples = Integer.valueOf(v[0]);
            numOfattributes = Integer.valueOf(v[1]);
            data = new double[numOfSamples][numOfattributes];
            labels = new String[numOfSamples];
            while ((line = br.readLine()) != null) {
                if(row == numOfSamples){
                    break;
                }
                String[] vals = line.split(",");
                for(int i = 0; i < numOfattributes; i++){
                    data[row][i] = Double.parseDouble(vals[i]);
                }
                labels[row] = vals[numOfattributes];
                row++;
            }

            try {

                standardDeviation = Double.parseDouble(line);
                line = br.readLine();
                String[] arr = line.split(",");
                centermu[0] = Double.parseDouble(arr[0]);
                centermu[1] = Double.parseDouble(arr[1]);
                centermu[2] = Double.parseDouble(arr[2]);
                centerMuSet = true;

            } catch (Exception e){
                System.out.println("Error var amk");
            }
        }

        double[][] centers = new double[numOfCenters][numOfattributes];

        Integer[] arr = new Integer[numOfSamples];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        Collections.shuffle(Arrays.asList(arr));
        for (int i = 0; i < numOfCenters; i++) {
            for (int j = 0; j < numOfattributes; j++){
                centers[i][j] = data[i][j];
            }
        }

        int step = 0;

        while (true){
            int[] newAssignedCenters = new int[numOfSamples];


            /**
             * E-Step
             */
            for(int i = 0; i < numOfSamples; i++){
                double minCenterDistance = 0;
                int minCenterDistanceIndex = -1;
                for(int j = 0; j < numOfCenters; j++){
                    double dist = distance(data[i], centers[j]);
                    if( minCenterDistanceIndex == -1 || minCenterDistance > dist ){
                        minCenterDistance = dist;
                        minCenterDistanceIndex = j;
                    }
                }
                newAssignedCenters[i] = minCenterDistanceIndex;
            }


            if(assignedCenters != null){//Means not in the first loop
                boolean allSame = true;
                for(int i = 0; i < numOfSamples; i++){
                    if(newAssignedCenters[i] != assignedCenters[i]) {
                        allSame = false;
                        break;
                    }
                }
                if (allSame){
                    break;
                }
            }

            /**
             * M-Step
             */
            double[][] newCenters = new double[numOfCenters][numOfattributes];
            int[] centerAssigneeCount = new int[numOfCenters];
            for(int i= 0; i < numOfSamples; i++){
                for(int j = 0; j < numOfCenters; j++){
                    if(newAssignedCenters[i] == j){
                        sum(newCenters[j], data[i]);
                        centerAssigneeCount[j]++;
                    }
                }
            }


            for (int j = 0; j < numOfCenters; j++) {
                divide(newCenters[j], centerAssigneeCount[j]);
            }

            print(data, newCenters, newAssignedCenters,step++);
            centers = newCenters;
            assignedCenters = newAssignedCenters;

        }

        stringBuilder.append("Distance Method: "+ method+"\n");
        if(centerMuSet) {
            System.out.println("Original centers: ");
            stringBuilder.append("Original centers: \n");
            for (double str : centermu
                    ) {
                System.out.print(str);
                System.out.print("\n");
                stringBuilder.append(str + "\n");
            }
        }
        System.out.println();
        System.out.println("K-Means centers: ");
        stringBuilder.append("\nK-Means centers: \n");
        for (int i = 0; i < centers.length; i++) {
            for (int j = 0; j < centers[i].length; j++) {
                String str = String.format("%.3f",centers[i][j]);
                System.out.print(str);
                stringBuilder.append(str);
                if(j == centers[i].length-1){

                } else {
                    System.out.print(", ");
                    stringBuilder.append(", ");
                }
            }
            System.out.println();
            stringBuilder.append("\n");
        }


    }

    public double computePurity(){
        HashMap<String, HashMap<Integer, Integer>> labelTypes = new HashMap();
        for (int i = 0; i < numOfSamples; i++) {
            String la = labels[i];
            if(!labelTypes.containsKey(la)){
                HashMap<Integer, Integer> matches = new HashMap<>();
                for (int j = 0; j < numOfCenters; j++) {
                    matches.put(j,0);
                }
                labelTypes.put(la, matches);
            }
        }

        for (int i = 0; i < numOfSamples; i++) {
            HashMap<Integer, Integer> matches = labelTypes.get(labels[i]);
            int cluster = assignedCenters[i];
            matches.put(cluster, matches.get(cluster)+1);
        }

        double sum  = 0;
        Set<String> keyset = labelTypes.keySet();

        for (String key:keyset
             ) {
            int maxCount = 0;
            for (int i = 0; i < numOfCenters; i++) {
                if(labelTypes.get(key).get(i) > maxCount){
                    maxCount = labelTypes.get(key).get(i);
                }
            }
            sum += maxCount;
        }

        return sum/((double) numOfSamples);

    }


    public void sum(double[] data, double[] addition){
        for (int i = 0; i < data.length; i++) {
            data[i] += addition[i];
        }
    }
    public void divide(double[] data, double divisor){
        if(divisor == 0)
            return;
        for (int i = 0; i < data.length; i++) {
            data[i] /= divisor;
        }
    }

    public static void print(double[][] data, double[][] centers, int[] assignedCenter, int step){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < assignedCenter.length; i++){

            List<String> list = new ArrayList<>();
            for (int j = 0; j < data[i].length; j++) {
                stringBuilder.append(String.format("%.3f",data[i][j]));
                if(j == data[i].length-1){

                }else {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(",");
            stringBuilder.append(assignedCenter[i]);
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");

        System.out.print(stringBuilder.toString());


    }

    public  double distance(double[] first, double[] second){
        double sum = 0;
        switch (method){
            case "euclidean":
                for(int i = 0; i< first.length; i++){
                    sum += Math.pow(first[i]-second[i],2);
                }
                return Math.sqrt(sum);
            case "custom":

                for(int i = 0; i< first.length; i++){
                    sum += Math.abs(first[i]-second[i]);
                }
                return sum;
        }
        return 0;
    }
}
