import java.util.Set;

/**
 * Created by reshmi on 5/14/17.
 */
public abstract class NodeAligned
{
    private int freq;
    private String epitope;
    private int id;
    private int position;

    NodeAligned(int f, String e, int v, int p)
    {
        freq = f;
        epitope = e;
        id = v;
        position = p;
    }

    int getFreq()
    {
        return freq;
    }

    void setFreq(int f)
    {
        freq = f;
    }
    
    int getPos()
    {
    	return position;
    }
    
    void setPos(int p)
    {
    	position = p;
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
