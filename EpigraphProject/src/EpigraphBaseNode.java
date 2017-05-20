/**
 * Created by reshmi on 5/18/17.
 */
public class EpigraphBaseNode extends Node
{
    private int F;
    EpigraphBaseNode(int a, String b, int c)
    {
        super(a, b, c);
        F = a;
    }

    int getF()
    {
        return F;
    }

    void setF(int val)
    {
        F = val;
    }
}
