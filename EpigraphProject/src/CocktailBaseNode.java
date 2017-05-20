/**
 * Created by reshmi on 5/20/17.
 */
public class CocktailBaseNode extends Node
{
    private int f_star;
    CocktailBaseNode(int a, String b, int c)
    {
        super(a, b, c);
        f_star = a;
    }

    int getF_star()
    {
        return f_star;
    }

    void setF_star(int val)
    {
        f_star = val;
    }
}
