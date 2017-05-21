import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CocktailAlgorithm
{
	public void computeFreq(ArrayList<String> seq, EpigraphBaseGraph graph)
	{
		double total = seq.size();
		HashMap<String,Integer> freqmap = new HashMap<String,Integer>();
		
		for (String s : seq)
		{
			for (int i=0; i<s.length(); i++)
			{
				String epitope = s.charAt(i)+"";
				if (!(freqmap.containsKey(s.charAt(i)+""))) {
					freqmap.put(epitope, 1);
				}
				else {
					freqmap.put(epitope, freqmap.get(epitope) + 1);
				}
			}
		}

		System.out.println(freqmap);
		for (String key : freqmap.keySet())
		{
			graph.getNodeFromEpitope(key).setFreq(freqmap.get(key));
		}

	}
	
	public ArrayList<String> cocktail(EpigraphBaseGraph graph, int m, int k)
    {
    	//HashSet<String> cocktail = new HashSet<String>();
		ArrayList<String> cocktail = new ArrayList<String>();
		EpigraphAlgorithm epi = new EpigraphAlgorithm();
    	Set<String> all_eptiopes = new HashSet<String>();
    	ArrayList<String> qepitopes = new ArrayList<String>();
    	Map<Node,Integer> old_f = new HashMap<Node,Integer>();

		for (int i=0; i<m; i++)
    	{
    		//compute next antigen sequence
    		String q = epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getNum_vertices()-1));
    		System.out.println(q);
    		cocktail.add(q);
    		for(int j=0;j<q.length();j++)
			{
				if (!(old_f.containsKey(graph.getNodeFromEpitope(q.charAt(j)+""))))
					old_f.put(graph.getNodeFromEpitope(q.charAt(j)+""), graph.getNodeFromEpitope(q.charAt(j)+"").getFreq());
				graph.getNodeFromEpitope(q.charAt(j)+"").setFreq(0);
			}
    	}
			
		//HashSet<String> old_cocktail = new HashSet<String>();
		ArrayList<String> old_cocktail = new ArrayList<String>();
		while (!(cocktail.equals(old_cocktail)))
		{
			old_cocktail = new ArrayList<String>(cocktail);
			for (int i = 0; i<m; i++)
			{
				String q = cocktail.remove(i);
				System.out.println(q);
				for (int j=0; j<q.length(); j++)
				{
					
					
					Node n = graph.getNodeFromEpitope(q.charAt(j)+"");
					
					
					//System.out.println("NAME" + n.getEpitope());
					//System.out.println(n.getFreq());
					//System.out.println("OLD" + old_f.get(n));
					n.setFreq(old_f.get(n));
				
				}
				String repq = epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getNum_vertices()-1));
				cocktail.add(i,repq);
				for(int j=0;j<q.length();j++)
				{
					old_f.put(graph.getNodeFromEpitope(q.charAt(j)+""), graph.getNodeFromEpitope(q.charAt(j)+"").getFreq());
					graph.getNodeFromEpitope(q.charAt(j)+"").setFreq(0);
				}
			}
		}


////    		int qlen = q.length();
////    		qepitopes = new ArrayList<String>();
//    		for (int j=0; j<Math.max(qlen-k, 0)+1; j++)
//    		{
//    			String qeptiope = q.substring(j,j+k);
//    			qeptiopes.add(qeptiope);
////    			find vertex with this epitope and set f = 0
//    		}
//    	}
    	//iterative refinement here

//    	for (int i=0; i<m; i++)
//    	{
//    		cocktail.remove(q);
//    		new_eptiopes = all_eptiopes.copy();
//    		new_eptiopes.remove(qeptiopes);
//    		for (String e: new_epitopes)
//    		{
//    			//give credit back for e with f = f(e)
//    		}
//    		//compute replacement antigen sequence
//    		String qnew = "newantigen";
//    		cocktail.add(qnew);
//    		int qnlen = qnew.length();
//    		ArrayList<String> qnepitopes = new ArrayList<String>();
//    		for (int j=0; j<Math.max(qnlen-k, 0)+1; j++)
//    		{
//    			String qneptiope = qnew.substring(j,j+k);
//    			qneptiopes.add(qneptiope);
//    			//find vertex with this epitope and set f = 0
//    		}
//    	}

    	//repeat until no change
    	return cocktail;

    }

	public static void main(String[] args)
	{
		ArrayList<String> seq = new ArrayList<String>();
//		seq.add("abcde");
//		seq.add("afgie");
//		seq.add("fgbde");
//		seq.add("fghej");
//		seq.add("bde");
//		seq.add("cde");
//		seq.add("cde");
//		seq.add("de");
//		seq.add("e");
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
		int v = 12, e = 20;
		//int v = 5, e = 5;
		//int v = 10, e = 12;
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

//		m.get(ar.get(0)).add(ar.get(1));
//		m.get(ar.get(0)).add(ar.get(2));
//		m.get(ar.get(1)).add(ar.get(3));
//		m.get(ar.get(2)).add(ar.get(3));
//		m.get(ar.get(3)).add(ar.get(4));

//		m.get(ar.get(0)).add(ar.get(1));
//		m.get(ar.get(0)).add(ar.get(5));
//		m.get(ar.get(1)).add(ar.get(2));
//		m.get(ar.get(1)).add(ar.get(3));
//		m.get(ar.get(2)).add(ar.get(3));
//		m.get(ar.get(3)).add(ar.get(4));
//		m.get(ar.get(4)).add(ar.get(9));
//		m.get(ar.get(5)).add(ar.get(6));
//		m.get(ar.get(6)).add(ar.get(1));
//		m.get(ar.get(6)).add(ar.get(7));
//		m.get(ar.get(6)).add(ar.get(8));
//		m.get(ar.get(7)).add(ar.get(4));
//		m.get(ar.get(8)).add(ar.get(4));
		
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

		CocktailAlgorithm ca = new CocktailAlgorithm();
		ca.computeFreq(seq, in);
		ArrayList<String> cockt = ca.cocktail(in, 5, 9);
		System.out.println(cockt);
//		ArrayList<EpigraphBaseNode> path = ea.optimalPath(in, in.getNode(0), in.getNode(4));
//		for(int i=0;i<path.size();i++)
//		{
//			System.out.println(path.get(i).getVertexID() + " ");
//		}
	}
}
