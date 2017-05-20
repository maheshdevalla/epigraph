import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reshmi on 5/20/17.
 */
public class CocktailBaseGraph extends Graph
{
    CocktailBaseGraph(int a, int b, ArrayList<Node> c, HashMap<Node, ArrayList<Node>> d)
    {
        super(a, b, c, d);
    }

    public CocktailBaseNode getNode(int i)
    {
        return (CocktailBaseNode) Graph.vertices.get(i);
    }

    public ArrayList<CocktailBaseNode> getPredecessors(CocktailBaseNode v)
    {
        ArrayList<CocktailBaseNode> pred = new ArrayList<>();
        for (int i=0;i<Graph.num_vertices;i++)
        {
            if (adjacency_list.get(getNode(i)).contains(v))
                pred.add(getNode(i));

        }
        return pred;
    }

}
