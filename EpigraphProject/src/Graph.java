import java.util.*;

/**
 * Created by reshmi on 5/14/17.
 */
public abstract class Graph
{

    protected static int num_vertices;
    protected int num_edges;
    // graph, implemented using an array of HashSets
    protected static HashMap<NodeAligned, ArrayList<NodeAligned>> adjacency_list;
    protected static ArrayList<NodeAligned> vertices;

    //Initializes an empty graph with V vertices and E edges
    public Graph(int V, int E, ArrayList<NodeAligned> vert, HashMap<NodeAligned, ArrayList<NodeAligned>> edge)
    {
        num_vertices = V;
        num_edges = E;
        adjacency_list = edge;
        vertices = vert;
    }
    
    public Graph(Graph g)
    {
    	this.num_vertices = g.num_vertices;
    	this.num_edges = g.num_edges;
    	this.adjacency_list = g.adjacency_list;
    	this.vertices = g.vertices;
    }

    public HashMap<NodeAligned, ArrayList<NodeAligned>> getAdjacency_list()
    {
        return adjacency_list;
    }

    public ArrayList<NodeAligned> getVertices()
    {
        return vertices;
    }

    public void addEdge(NodeAligned a, NodeAligned b)
    {
        adjacency_list.get(a).add(b);
    }

    public void removeEdge(NodeAligned a, NodeAligned b)
    {
        adjacency_list.get(a).remove(b);
    }

    public boolean hasNode(NodeAligned a)
    {
    	return vertices.contains(a);
    }
    
    public boolean hasEdge(NodeAligned a, NodeAligned b)
    {
        return adjacency_list.get(a).contains(b);
    }
    
    public void removeFromGraph(ArrayList<NodeAligned> v)
    {
    	for (NodeAligned n : v)
    	{
    		vertices.remove(n);
    		num_vertices -= 1;
    		ArrayList<NodeAligned> al = adjacency_list.get(n);
    		num_edges -= al.size();
    		adjacency_list.remove(n);
    	}
    }

    //Returns the vertices adjacent to vertex {@code v}
    public ArrayList<NodeAligned> adjacentVertices(NodeAligned v)
    {
        return adjacency_list.get(v);
    }

    //Returns the out degree of vertex
    public int outDegree(NodeAligned v)
    {
        return adjacency_list.get(v).size();
    }

    public int inDegree(NodeAligned v)
    {
        int degree = 0;
        for (int i=0;i<num_vertices;i++)
        {
            if (adjacency_list.get(i).contains(v))
                degree++;
        }
        return degree;
    }

    public int getNum_vertices()
    {
        return num_vertices;
    }

    public int getNum_edges()
    {
        return num_edges;
    }
    
    @Override
    public String toString()
    {
    	ArrayList<String> output = new ArrayList<String>();
    	for (NodeAligned n : vertices){
    		String s = n.getEpitope();
    		s += "--> " + adjacency_list.get(n);
    		output.add(s);
    	}
    	return output.toString();
    }
    
}
