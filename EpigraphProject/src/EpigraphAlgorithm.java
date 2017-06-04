import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by reshmi on 5/18/17.
 */
public class EpigraphAlgorithm
{
	ArrayList<EpigraphBaseNode> path;
	
	public String optimalPath(EpigraphBaseGraph graph, EpigraphBaseNode begin, EpigraphBaseNode end)
    {
        System.out.println("path1---"+graph.num_edges);
        System.out.println("path2---"+begin);
        System.out.println("path3---"+end);

        String output = new String();
        begin.setF(0);
        //System.out.println(graph.getNum_vertices());
        for(int i=1;i<graph.getNum_vertices();i++)
        {
            EpigraphBaseNode n = graph.getNode(i);
            //System.out.println(n.getEpitope());
            ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(n);
            if (!predecessors.isEmpty())
            {
            	double max_freq = getMaxFrequency(predecessors);
            	n.setF(n.getFreq() + max_freq);
            }
            else
            {
            	n.setF(n.getFreq());
            }
        }


        path = new ArrayList<>();
        path.add(end);
        //System.out.println("Start: " + path);
        output = addToOutput(end.getEpitope(), output);
//        output = end.getEpitope() + output;
        EpigraphBaseNode current_node = end;
        while(true)
        {
            ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(current_node);
            if (!predecessors.isEmpty())
            {
            	EpigraphBaseNode max_freq_node = getNodeMaxFrequency(predecessors, begin);
            	path.add(max_freq_node);
                //System.out.println("max_freq_node = " + max_freq_node.getEpitope());
                //System.out.println("output = " + output);
                output = addToOutput(max_freq_node.getEpitope(), output);
            	current_node = max_freq_node;
            }
            else
            {
            	break;
            }

            if(current_node == begin)
            {
                break;
            }
        }
        return output;
    }

    private String addToOutput(String epitope, String output)
    {
        if(epitope.isEmpty())
            return output;
        else if(output.isEmpty())
            return epitope;
        int j=0, i = epitope.length()-1;
        for(i=epitope.length()-1;i>=0;i--)
        {
            if(epitope.charAt(i) == output.charAt(j))
            {
                break;
            }
        }
        if(epitope.length() - i >= 0 && output.length() > 1)
            return epitope + output.substring((epitope.length() - i));
        else
            return epitope + output;
    }

    public ArrayList<String> optimalPathParallel(EpigraphBaseGraph graph, ArrayList<EpigraphBaseNode> begin, EpigraphBaseNode end)
    {
        int parallel_runs = begin.size();

        for(int i=0;i<parallel_runs;i++)
        {
            begin.get(i).setFreq(0);
        }

        ArrayList<String> output = new ArrayList<>();

        for(int i=0;i<parallel_runs;i++)
        {
            output.add(end.getEpitope());
        }
        ArrayList<EpigraphBaseNode> current_node = new ArrayList<>();
        for(int i=0;i<parallel_runs;i++)
        {
            current_node.add(end);
        }

        preProcessing(parallel_runs, begin, graph);

        ArrayList<Integer> not_valid = new ArrayList<>();
        while(true)
        {
            for(int i=0;i<parallel_runs;i++)
            {
                if(!not_valid.contains(i))
                {
                    ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(current_node.get(i));
                    if (!predecessors.isEmpty())
                    {
                        EpigraphBaseNode max_freq_node = getNodeMaxF_value(predecessors, i, begin.get(i));

                        output.set(i, addToOutput(max_freq_node.getEpitope(), output.get(i)));
                        current_node.set(i, max_freq_node);

                        //using one path through the graph to bias another’s
                        max_freq_node.setFreq(0);
                        preProcessing(parallel_runs, begin, graph);
                    }

                    if (current_node.get(i).getEpitope().equals(begin.get(i).getEpitope()))
                    {
                        not_valid.add(i);
                    }
                }
            }
            if(not_valid.size() == parallel_runs)
                break;
        }

        return output;
    }

    public void preProcessing(int parallel_runs, ArrayList<EpigraphBaseNode> begin, EpigraphBaseGraph graph)
    {
        for(int j=0;j<parallel_runs;j++)
        {
            begin.get(j).setSpecificF_value(j, 0);
            for (int i = 1; i < graph.getNum_vertices(); i++)
            {
                EpigraphBaseNode n = graph.getNode(i);
                ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(n);

                if (!predecessors.isEmpty())
                {
                    double max_freq = getMaxFrequency(predecessors);
                    n.setSpecificF_value(j, n.getFreq() + max_freq);
                }
                else
                {
                    n.setSpecificF_value(j, n.getFreq());
                }
            }
        }
    }

    public EpigraphBaseNode getNodeMaxF_value(ArrayList<EpigraphBaseNode> ar, int p, EpigraphBaseNode epigraphBaseNode)
    {
        double val = ar.get(0).getSpecificF_value(p);
        EpigraphBaseNode node_val = ar.get(0);
        for(int i=1;i<ar.size();i++)
        {
            if(ar.get(i).getSpecificF_value(p) > val)
            {
                val = ar.get(i).getSpecificF_value(p);
                node_val = ar.get(i);
            }
        }
        if(ar.contains(epigraphBaseNode))
            return epigraphBaseNode;
        return node_val;
    }

    public EpigraphBaseNode getNodeMaxFrequency(ArrayList<EpigraphBaseNode> ar, EpigraphBaseNode begin)
    {
        double val = ar.get(0).getF();
        EpigraphBaseNode node_val = ar.get(0);
        for(int i=1;i<ar.size();i++)
        {
            if(ar.get(i).getF() > val)
            {
                val = ar.get(i).getF();
                node_val = ar.get(i);
            }
        }
        if(ar.contains(begin))
            return begin;
        return node_val;
    }

    public double getMaxFrequency(ArrayList<EpigraphBaseNode> ar)
    {
        double val = ar.get(0).getFreq();
        for(int i=1;i<ar.size();i++)
        {
            if(ar.get(i).getFreq() > val)
                val = ar.get(i).getFreq();
        }
        return val;
    }

    public static void main(String[] args)
    {
        ArrayList<String> seq = new ArrayList<String>();

        //Test Case #0
//        int v = 5, e = 5;

        //Test Case #2
        int v = 12, e = 20;

        //Test Case #2
        seq.add("abcdef");
        seq.add("bijkl");
        seq.add("cjel");
        seq.add("ghcjkl");
        seq.add("bcjkl");
        seq.add("ghijkf");
        seq.add("gbi");
        seq.add("bijk");
        seq.add("gbidkf");
        seq.add("ghijkl");

        ArrayList<NodeAligned> ar = new ArrayList<>();
        HashMap<NodeAligned, ArrayList<NodeAligned>> m = new HashMap<>();
        for(int i=0;i<v;i++)
        {
            EpigraphBaseNode temp = new EpigraphBaseNode(i, (String) (((char) ('a'+i)) + ""), i, 0);
            ar.add(temp);
        }

        for (int i = 0; i < v; i++)
        {
            m.put(ar.get(i), new ArrayList<>());
        }

        //Test Case #0
//        m.get(ar.get(0)).add(ar.get(1));
//        m.get(ar.get(0)).add(ar.get(2));
//        m.get(ar.get(1)).add(ar.get(3));
//        m.get(ar.get(2)).add(ar.get(3));
//        m.get(ar.get(3)).add(ar.get(4));

        //Test Case #2
        m.get(ar.get(0)).add(ar.get(1));
        m.get(ar.get(0)).add(ar.get(7));
        m.get(ar.get(1)).add(ar.get(2));
        m.get(ar.get(1)).add(ar.get(8));
        m.get(ar.get(2)).add(ar.get(3));
        m.get(ar.get(2)).add(ar.get(9));
        m.get(ar.get(3)).add(ar.get(4));
        m.get(ar.get(3)).add(ar.get(10));
        m.get(ar.get(4)).add(ar.get(5));
        m.get(ar.get(4)).add(ar.get(11));
        m.get(ar.get(6)).add(ar.get(7));
        m.get(ar.get(6)).add(ar.get(1));
        m.get(ar.get(7)).add(ar.get(2));
        m.get(ar.get(7)).add(ar.get(8));
        m.get(ar.get(8)).add(ar.get(3));
        m.get(ar.get(8)).add(ar.get(9));
        m.get(ar.get(9)).add(ar.get(4));
        m.get(ar.get(9)).add(ar.get(10));
        m.get(ar.get(10)).add(ar.get(5));
        m.get(ar.get(10)).add(ar.get(11));
        
        EpigraphBaseGraph in = new EpigraphBaseGraph(v, e, ar, m);

        EpigraphAlgorithm ea = new EpigraphAlgorithm();
        CocktailAlgorithm ca = new CocktailAlgorithm();
        ca.computeFreq(seq, in);

//        String op = ea.optimalPath(in, in.getNode(0), in.getNode(4));
//        System.out.println("in.getNode(7).getFreq() = " + in.getNode(7).getFreq());

        ArrayList<EpigraphBaseNode> begin_nodes = new ArrayList<>();
        begin_nodes.add(in.getNode(0));
        begin_nodes.add(in.getNode(9));
        begin_nodes.add(in.getNode(1));
        begin_nodes.add(in.getNode(2));

//        System.out.println("here");
        ArrayList<String> op1 = ea.optimalPathParallel(in, begin_nodes, in.getNode(5));
        System.out.println(op1);

    }
    
    public ArrayList<EpigraphBaseNode> getPath() {
    	return path;
    }


}
