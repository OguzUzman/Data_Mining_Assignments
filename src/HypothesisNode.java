import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by oguz on 24/05/16.
 */
public class HypothesisNode{

    /**
     * For every elemtnt of array suppose the length is 4, for every bit we have 3 conditions;
     * -1 Don't care, (1)
     * 0 Not
     * 1 Normal
     */
    int[] hypothesis;

    HypothesisNode father;
    double quality, optimalQuality;
    List<HypothesisNode> children = null;

    public int getChildrenNum() {
        return children.size();
    }

    int n, index;
    /**
     *
     * @param n
     * @param index -1 means starting node
     * @param value
     */

    public HypothesisNode(int n, int index, int value, HypothesisNode father) {
        this.n = n;
        this.index = index;
        try {
            hypothesis = new int[n];
            if (index == -1) { //Starting node
                this.father = null;
                for (int i = 0; i < n; i++) {
                    hypothesis[i] = -1;
                }
                index = 0;
            } else { //Any other
                this.father = father;
                for (int i = 0; i < n; i++) {
                    hypothesis[i] = father.hypothesis[i];
                }
                hypothesis[index] = value;
            }


        } catch (Throwable t){
            t.printStackTrace();
        }

    }

    public void generateChildren(int index, int n){
        for (int i = index+1; i < n; i++) {
            children.add(new HypothesisNode(n, i, 1, this));
            children.add(new HypothesisNode(n, i, 0, this));
        }
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < hypothesis.length; i++){
            switch (hypothesis[i]){
                case -1:
                    stringBuilder.append(" ");
                    break;
                case 0:
                    stringBuilder.append("0");
                    break;
                case 1:
                    stringBuilder.append("1");
                    break;
            }
        }
        return stringBuilder.toString();
    }

    public List<HypothesisNode> getChildren(){
        if(children == null){
            generateChildren();
        }
        return children;
    }

    public void prune(){
        father.children.remove(this);
    }

    public boolean visited = false;

    public List<HypothesisNode> generateChildren(){
        children = new ArrayList<>();
        for(int i = index + 1; i <n ; i++){
            children.add(new HypothesisNode(n, i, 1, this));
            children.add(new HypothesisNode(n, i, 0, this));
        }
        return children;
    }

}
