import java.util.Set;

/**
 * Created by reshmi on 5/14/17.
 * Modified by Arvind
 */
public abstract class Node
{
    private int freq;
    private String epitope;
    private int id;

    Node(int f, String e, int v)
    {
        freq = f;
        epitope = e;
        id = v;
    }

    int getFreq()
    {
        return freq;
    }

    void setFreq(int f)
    {
        freq = f;
    }

    String getEpitope()
    {
        return epitope;
    }

    public int getVertexID()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
    	return epitope;
    }
}
