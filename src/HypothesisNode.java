import java.util.List;

/**
 * Created by oguz on 24/05/16.
 */
public class HypothesisNode {

    /**
     * For every elemtnt of array suppose the length is 4, for every bit we have 3 conditions;
     * -1 Don't care, (1)
     * 0 Not
     * 1 Normal
     */
    byte[] hypothesis;

    HypothesisNode father;
    List<HypothesisNode> children;

    public int getChildrenNum() {
        return children.size();
    }

    public HypothesisNode(int n, int index, byte value) {
        hypothesis = new byte[n];
        hypothesis = father.hypothesis;
        hypothesis[index-1] = value;

    }

}
