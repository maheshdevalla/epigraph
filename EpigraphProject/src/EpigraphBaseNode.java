import java.util.ArrayList;

/**
 * Created by reshmi on 5/18/17.
 */
public class EpigraphBaseNode extends NodeAligned
{
    private int F;
    private ArrayList<Integer> F_values;
    EpigraphBaseNode(int a, String b, int c, int p)
    {
        super(a, b, c, p);
        F = a;
        F_values = new ArrayList<>();
    }

    int getF()
    {
        return F;
    }

    void setF(int val)
    {
        F = val;
    }


    public ArrayList<Integer> getF_values() {
        return F_values;
    }

    public void setF_values(ArrayList<Integer> f_values)
    {
        F_values = f_values;
    }

    public void setSpecificF_value(int i, int val)
    {
        F_values.add(i, val);
    }

    public Integer getSpecificF_value(int i)
    {
        return F_values.get(i);
    }
}
