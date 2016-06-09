/**
 * Created by oguz on 07/06/16.
 */
public class MultiRunner {

    public static void main(String[] args) throws Exception{
        StringBuilder mainStringBuilder = new StringBuilder();
        for(int i = 0; i < 10; i++){
            /**
             *
             */
            KMeans kMeans = new KMeans("./data/myfile_"+i+".txt", "euclidean");
            StringBuilder stringBuilder = new StringBuilder();
            kMeans.stringBuilder = stringBuilder;
            kMeans.run();
            KMeans customkMeans = new KMeans("./data/myfile_"+i+".txt", "custom");
            StringBuilder stringBuilder1 = new StringBuilder();
            customkMeans.stringBuilder = stringBuilder1;
            customkMeans.run();
            mainStringBuilder.append("--------------\nResults for "+(i+1)+"th trial\n\n");

            mainStringBuilder.append(stringBuilder);
            mainStringBuilder.append(String.format("Purity: %.2f\n\n", kMeans.computePurity()) );
            mainStringBuilder.append(stringBuilder1);

            mainStringBuilder.append(String.format("Purity: %.2f\n", customkMeans.computePurity()) );
            mainStringBuilder.append("\n\n");
        }
        System.out.println("\n\n---------------\n\n\n\n\n");
        System.out.println(mainStringBuilder);
    }

}
