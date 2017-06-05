import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/*
 * Created by Arvind Suresh
 * Debugging and Testing by Arvind Suresh, Mahesh Devalla
 */

public class CocktailAlgorithm
{
	public ArrayList<Double> coverageset = new ArrayList<Double>();
	//method to compute frequency of an epitope in a sequence set and assign these frequencies
	//to nodes in the epigraph
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

	String mySubString(String myString, int start, int length)
	{
		return myString.substring(start, Math.min(start + length, myString.length()));
	}
	
	//main cocktail algorithm method - Algorithm 3 in Epigraph paper
	public ArrayList<String> cocktail(EpigraphBaseGraph graph, int m, int k, double numSeq)
    {
    	//HashSet<String> cocktail = new HashSet<String>();
		ArrayList<String> cocktail = new ArrayList<String>(); //stores current state of the vaccine cocktail
		Double coverage = 0.0;
		coverageset = new ArrayList<Double>();
		EpigraphAlgorithm epi = new EpigraphAlgorithm();
		// keeps track of old frequencies so they can be reinstated later during iterative refinement/opimization
		Map<NodeAligned,Double> old_f = new HashMap<NodeAligned,Double>(); 
		
		//main for loop to create new antigen sequences
		for (int i=0; i<m; i++)
    	{
    		//compute next antigen sequence
			//System.out.println(i);
			Random random = new Random();
			int start = random.nextInt(graph.getNum_vertices() - 0 + 1) + 0;
    		String q = epi.optimalPath(graph, graph.getNode(start), graph.getNode(graph.getNum_vertices()-1));
    		//System.out.println(q);
    		//adds new optimal path to the cocktail
    		cocktail.add(q);
    		
    		ArrayList<EpigraphBaseNode> visited = epi.getPath();
    		for (NodeAligned n : visited) {
    			
    			if (!(old_f.containsKey(n))) {
    				old_f.put(n, n.getFreq());
    			}
    			//System.out.println(old_f.get(n)/(double)numSeq);
    			coverage += (old_f.get(n)/(double)numSeq);
    			//coverage += old_f.get(n);
    			n.setFreq(0.0);
    		}
    		coverage = coverage/(double)(visited.size());
    		coverageset.add(coverage);

    	}
		System.out.println(coverageset);	
		//HashSet<String> old_cocktail = new HashSet<String>();
		ArrayList<String> old_cocktail = new ArrayList<String>(); //temporary storage variable for old cocktail
		
		//iterative refinement/optimization loop continue until convergence
//		while (!(cocktail.equals(old_cocktail)))
//		{
//			old_cocktail = new ArrayList<String>(cocktail); //copies the current cocktail
//			//removes each individual antigen sequence from the cocktail and refines it
//			for (int i = 0; i<m; i++)
//			{
//				String q = cocktail.remove(i);
//				System.out.println(q);
//				for (int j=0; j<q.length(); j++)
//				{
//
//					//resets the frequency of each eptiope back to its original value
//					Node n = graph.getNodeFromEpitope(q.charAt(j)+"");
//					n.setFreq(old_f.get(n));
//
//					//System.out.println("NAME" + n.getEpitope());
//					//System.out.println(n.getFreq());
//					//System.out.println("OLD" + old_f.get(n));
//
//
//				}
//				//recalculates a new antigen sequence for the cocktail
//				String repq = epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getNum_vertices()-1));
//				cocktail.add(i,repq);
//				for(int j=0;j<q.length();j++)
//				{
//					old_f.put(graph.getNodeFromEpitope(q.charAt(j)+""), graph.getNodeFromEpitope(q.charAt(j)+"").getFreq());
//					//sets the frequency of these new epitopes to zero
//					graph.getNodeFromEpitope(q.charAt(j)+"").setFreq(0);
//				}
//			}
//		}


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
	
	// modified Cocktail Algorithm method for Immunogenicity heuristic
	public ArrayList<String> cocktailimmuno(HashMap<NodeAligned, Double> old_freq, EpigraphBaseGraph graph, int m, int k, double numSeq)
    {
		ArrayList<String> cocktail = new ArrayList<String>(); //stores current state of the vaccine cocktail
		Double coverage = 0.0;
		coverageset = new ArrayList<Double>();
		EpigraphAlgorithm epi = new EpigraphAlgorithm();
		// keeps track of old frequencies so they can be reinstated later during iterative refinement/opimization
		Map<NodeAligned,Double> old_f = new HashMap<NodeAligned,Double>(); 
		
		//main for loop to create new antigen sequences
		for (int i=0; i<m; i++)
    	{
    		//compute next antigen sequence
			Random random = new Random();
			int start = random.nextInt(graph.getNum_vertices() - 0 + 1) + 0;
			int end = random.nextInt(graph.getNum_vertices() - 0 + 1) + 0;
    		String q = epi.optimalPath(graph, graph.getNode(start), graph.getNode(end));
    		//adds new optimal path to the cocktail
    		cocktail.add(q);
    		
    		ArrayList<EpigraphBaseNode> visited = epi.getPath();
    		for (NodeAligned n : visited) {
    			double oldfreq = old_freq.get(n);
    			if (!(old_f.containsKey(n))) {
    				old_f.put(n, oldfreq);
    			}
    			coverage += (old_f.get(n)/(double)numSeq);
    			n.setFreq(0.0);
    		}
    		coverage = coverage/(double)(visited.size());
    		coverageset.add(coverage);

    	}
		System.out.println(coverageset);	
    	return cocktail;

    }

	public static void main(String[] args)
	{
		ArrayList<String> seq = new ArrayList<String>();
		
		//Test Case #1
//		seq.add("abcde");
//		seq.add("afgie");
//		seq.add("fgbde");
//		seq.add("fghej");
		
		//Test Case #0
//		seq.add("bde");
//		seq.add("cde");
//		seq.add("cde");
//		seq.add("de");
//		seq.add("e");
		
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
		
		//Test Case #2
		int v = 12, e = 20;
		//Test Case #0
		//int v = 5, e = 5;
		//Test Case #1
		//int v = 10, e = 12;
		ArrayList<NodeAligned> ar = new ArrayList<>();
		HashMap<NodeAligned, ArrayList<NodeAligned>> m = new HashMap<>();
		for(int i=0;i<v;i++)
		{
			//creates specified number of nodes with alphabet characters as the epitopes for testing
			EpigraphBaseNode temp = new EpigraphBaseNode(i, (String) (((char) ('a'+i)) + ""), i, 0);
			ar.add(temp);
		}

		for (int i = 0; i < v; i++)
		{
			m.put(ar.get(i), new ArrayList<>());
		}

		//Test Case #0
//		m.get(ar.get(0)).add(ar.get(1));
//		m.get(ar.get(0)).add(ar.get(2));
//		m.get(ar.get(1)).add(ar.get(3));
//		m.get(ar.get(2)).add(ar.get(3));
//		m.get(ar.get(3)).add(ar.get(4));

		//Test Case #1
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

		CocktailAlgorithm ca = new CocktailAlgorithm();
		ca.computeFreq(seq, in);
		ArrayList<String> cockt = ca.cocktail(in, 5, 9, 10);
		System.out.println(cockt); //final cocktail output
//		ArrayList<EpigraphBaseNode> path = ea.optimalPath(in, in.getNode(0), in.getNode(4));
//		for(int i=0;i<path.size();i++)
//		{
//			System.out.println(path.get(i).getVertexID() + " ");
//		}
	}
}
