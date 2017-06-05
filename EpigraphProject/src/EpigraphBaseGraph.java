import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by reshmi on 5/18/17.
 * Modified by Arvind
 */
public class EpigraphBaseGraph extends Graph
{
    EpigraphBaseGraph(int a, int b, ArrayList<NodeAligned> c, HashMap<NodeAligned, ArrayList<NodeAligned>> d)
    {
        super(a, b, c, d);
    }
    
    EpigraphBaseGraph(EpigraphBaseGraph g)
    {
    	super(g);
    }

    public EpigraphBaseNode getNode(int i)
    {
        return (EpigraphBaseNode) Graph.vertices.get(i);
    }

    public EpigraphBaseNode getNodeFromEpitope(String a)
    {
        for(int i=0;i<Graph.vertices.size();i++)
        {
            if(Graph.vertices.get(i).getEpitope().equals(a))
                return (EpigraphBaseNode) Graph.vertices.get(i);
        }
        return null;
    }

    public ArrayList<EpigraphBaseNode> getPredecessors(EpigraphBaseNode v)
    {
        ArrayList<EpigraphBaseNode> pred = new ArrayList<>();
        for (int i=0;i<Graph.num_vertices;i++)
        {
            try {
            	//System.out.print("a");
            	//System.out.println(adjacency_list.get(getNodeFromEpitope("IVQGIFRAIL")));
            	//System.out.println(adjacency_list.get(getNode(i)));
            } catch (Exception e) {
            	e.printStackTrace();
            }
        	if (adjacency_list.get(getNode(i)) != null) {
        		if (adjacency_list.get(getNode(i)).contains(v))
        			pred.add(getNode(i));
        	}

        }
        return pred;
    }

}