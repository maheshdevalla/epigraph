import java.util.*;

/**
 * Created by reshmi on 5/14/17.
 */
public abstract class Graph
{

    private int num_vertices;
    private int num_edges;
    // graph, implemented using an array of HashSets
    private HashMap<Node, ArrayList<Node>> adjacency_list;

    //Initializes an empty graph with V vertices and E edges
    public Graph(int V, int E, ArrayList<Node> vert, Map<Node, Node> edge)
    {
        num_vertices = V;
        num_edges = E;
        adjacency_list = new HashMap<>();
        for (int v = 0; v < V; v++)
        {
            adjacency_list.put(vert.get(v), new ArrayList<>());
        }
        for (Map.Entry<Node, Node> entry : edge.entrySet())
        {
            adjacency_list.get(entry.getKey()).add(entry.getValue());
        }
    }

    public void addEdge(Node a, Node b)
    {
        adjacency_list.get(a).add(b);
    }

    public void removeEdge(Node a, Node b)
    {
        adjacency_list.get(a).remove(b);
    }

    public boolean hasEdge(Node a, Node b)
    {
        return adjacency_list.get(a).contains(b);
    }

    //Returns the vertices adjacent to vertex {@code v}
    public ArrayList<Node> adjacentVertices(Node v)
    {
        return adjacency_list.get(v);
    }

    //Returns the out degree of vertex
    public int outDegree(Node v)
    {
        return adjacency_list.get(v).size();
    }

    public int inDegree(Node v)
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
}
