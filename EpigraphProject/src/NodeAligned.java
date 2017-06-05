import java.util.Set;

/**
 * Created by Arvind Suresh
 */
public abstract class NodeAligned
{
    private double freq;
    private String epitope;
    private int id;
    private int position;

    NodeAligned(double f, String e, int v, int p)
    {
        freq = f;
        epitope = e;
        id = v;
        position = p;
    }

    double getFreq()
    {
        return freq;
    }

    void setFreq(double f)
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
