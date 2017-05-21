import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by reshmi on 5/18/17.
 */
public class EpigraphAlgorithm
{
    public String optimalPath(EpigraphBaseGraph graph, EpigraphBaseNode begin, EpigraphBaseNode end)
    {
        String output = new String();
        begin.setF(0);
        for(int i=1;i<graph.getNum_vertices();i++)
        {
            EpigraphBaseNode n = graph.getNode(i);
            ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(n);
            System.out.println(predecessors);
            if (!predecessors.isEmpty()) 
            {
            	int max_freq = getMaxFrequency(predecessors);
            	n.setF(n.getFreq() + max_freq);
            }
            else 
            {
            	n.setF(n.getFreq());
            }
        }


        ArrayList<EpigraphBaseNode> path = new ArrayList<>();
        path.add(end);
        output = end.getEpitope()+output;
        EpigraphBaseNode current_node = end;
        while(true)
        {
            ArrayList<EpigraphBaseNode> predecessors = graph.getPredecessors(current_node);
            if (!predecessors.isEmpty()) {
            	EpigraphBaseNode max_freq_node = getNodeMaxFrequency(predecessors);
            	path.add(max_freq_node);
            	output = max_freq_node.getEpitope()+output;
            	current_node = max_freq_node;
            } else {
            	break;
            }
            if(current_node == begin)
            {
                break;
            }
        }

//        for(int i=0;i<path.size();i++)
//        {
//            output = path.get(i).getEpitope()+output;
//        }
        System.out.println("output = " + output);

        return output;
    }

    public EpigraphBaseNode getNodeMaxFrequency(ArrayList<EpigraphBaseNode> ar)
    {
        int val = ar.get(0).getF();
        EpigraphBaseNode node_val = ar.get(0);
        for(int i=1;i<ar.size();i++)
        {
            if(ar.get(i).getF() > val)
            {
                val = ar.get(i).getF();
                node_val = ar.get(i);
            }
        }
        return node_val;
    }

    public int getMaxFrequency(ArrayList<EpigraphBaseNode> ar)
    {
        int val = ar.get(0).getFreq();
        for(int i=1;i<ar.size();i++)
        {
            if(ar.get(i).getFreq() > val)
                val = ar.get(i).getFreq();
        }
        return val;
    }

    public static void main(String[] args)
    {
        int v = 5, e = 5;
        ArrayList<Node> ar = new ArrayList<>();
        HashMap<Node, ArrayList<Node>> m = new HashMap<>();
        for(int i=0;i<v;i++)
        {
            EpigraphBaseNode temp = new EpigraphBaseNode(i, (String) (((char) ('a'+i)) + ""), i);
            ar.add(temp);
        }

        for (int i = 0; i < v; i++)
        {
            m.put(ar.get(i), new ArrayList<>());
        }

        m.get(ar.get(0)).add(ar.get(1));
        m.get(ar.get(0)).add(ar.get(2));
        m.get(ar.get(1)).add(ar.get(3));
        m.get(ar.get(2)).add(ar.get(3));
        m.get(ar.get(3)).add(ar.get(4));

        EpigraphBaseGraph in = new EpigraphBaseGraph(v, e, ar, m);

        EpigraphAlgorithm ea = new EpigraphAlgorithm();
        String op = ea.optimalPath(in, in.getNode(0), in.getNode(4));

    }


}
