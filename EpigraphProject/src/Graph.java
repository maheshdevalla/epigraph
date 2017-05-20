import java.util.*;

/**
 * Created by reshmi on 5/14/17.
 */
public abstract class Graph
{

    protected static int num_vertices;
    protected int num_edges;
    // graph, implemented using an array of HashSets
    protected static HashMap<Node, ArrayList<Node>> adjacency_list;
    protected static ArrayList<Node> vertices;

    //Initializes an empty graph with V vertices and E edges
    public Graph(int V, int E, ArrayList<Node> vert, HashMap<Node, ArrayList<Node>> edge)
    {
        num_vertices = V;
        num_edges = E;
        adjacency_list = edge;
        vertices = vert;
    }

    public HashMap<Node, ArrayList<Node>> getAdjacency_list()
    {
        return adjacency_list;
    }

    public ArrayList<Node> getVertices()
    {
        return vertices;
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
