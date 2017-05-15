public class Main extends Node
{

    private int d;
    Main(int f, String e, int v, int tmpd)
    {
        super(f, e, v);
        this.d = tmpd;
    }

    public static void main(String[] args)
    {
        Main m = new Main(1,"as",2,3);
        System.out.print(m.d);
        System.out.println("Hello World!");
    }
}
