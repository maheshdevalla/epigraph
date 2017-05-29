public class Main extends Node
{

    private int d;
    Main(int f, String e, int v, int tmpd)
    {
        super(f, e, v);
        this.d = tmpd;
    }

    private String addToOutput(String epitope, String output)
    {
        int j=0, i = epitope.length()-1;
        for(i=epitope.length()-1;;i--)
        {
            if(epitope.charAt(i) == output.charAt(j))
            {
                break;
            }
        }
        return epitope + output.substring((epitope.length() - i));
    }

    String mySubString(String myString, int start, int length)
    {
        return myString.substring(start, Math.min(start + length, myString.length()));
    }

    public static void main(String[] args)
    {
        Main m = new Main(1,"as",2,3);
        System.out.print(m.d);
        System.out.println("Hello World!");
        String a = "RESHMI";
        String trial = m.mySubString(a, 0, 1);
        System.out.println(trial);
        String test = m.addToOutput("abijkf", "ijkfcd");
//        System.out.println("test = " + test);
    }
}
