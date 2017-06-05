import java.util.ArrayList;

/**
 * Created by reshmi on 5/18/17.
 * Modified by Arvind
 */
public class EpigraphBaseNode extends NodeAligned
{
    private double F;
    private ArrayList<Double> F_values;
    EpigraphBaseNode(double a, String b, int c, int p)
    {
        super(a, b, c, p);
        F = a;
        F_values = new ArrayList<>();
    }

    double getF()
    {
        return F;
    }

    void setF(double val)
    {
        F = val;
    }


    public ArrayList<Double> getF_values() {
        return F_values;
    }

    public void setF_values(ArrayList<Double> f_values)
    {
        F_values = f_values;
    }

    public void setSpecificF_value(int i, double val)
    {
        F_values.add(i, val);
    }

    public Double getSpecificF_value(int i)
    {
        return F_values.get(i);
    }
}
