import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/*
 * CS89/189 Final Project
 * Created and Implemented by Arvind Suresh, Reshmi Suresh
 * Debugging and Testing by Arvind Suresh, Mahesh Devalla
 */
public class TTVAlgorithm {
	//method to compute frequency of an epitope in a sequence set and assign these frequencies
	//to nodes in the epigraph
	public ArrayList<Double> coverageset = new ArrayList<Double>();
	
	public void computeFreq(ArrayList<String> seq, EpigraphBaseGraph graph)
	{
		double total = seq.size();
		HashMap<String,Double> freqmap = new HashMap<String,Double>();
		
		for (String s : seq)
		{
			for (int i=0; i<s.length(); i++)
			{
				String epitope = s.charAt(i)+"";
				if (!(freqmap.containsKey(s.charAt(i)+""))) {
					freqmap.put(epitope, 1/total);
				}
				
				else {
					freqmap.put(epitope, freqmap.get(epitope) + 1/total);
				}
			}
		}

		for (String key : freqmap.keySet())
		{
			EpigraphBaseNode n = graph.getNodeFromEpitope(key);
			n.setFreq((int)(freqmap.get(key)*100)); //converts double to integer by multiplying 100
		}

	}
	
	//TTV Algorithm - Algorithm 4 from Epigraph Paper
	public ArrayList<String> ttvalgo(EpigraphBaseGraph graph, int m, int k, ArrayList<String> seq)
    {
		int[][] coverage = new int[seq.size()][m]; //stores the coverage calculation of each sequence-sequence pair
//		ArrayList<String> cocktail = new ArrayList<String>();
		EpigraphAlgorithm epi = new EpigraphAlgorithm();
		
		//creates the first antigen sequence (q0) that will be in every vaccine
		String q0 = epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getNum_vertices()-1));
		ArrayList<EpigraphBaseNode> q0path = epi.getPath();
		double q0score = 0.0;
		for (NodeAligned n : q0path) {
			q0score += (n.getFreq()/(double)(seq.size()));
		}
		q0score = q0score/(double)(q0path.size());
		coverageset.add(q0score);
		ArrayList<String> q_list = new ArrayList<>(); //stores temporary chosen q's
		ArrayList<String> ttv_list = new ArrayList<String>(); // keeps track of the final antigens
		ttv_list.add(q0);
		//select random sequences (m-1)
		for (int i=0; i<m; i++)
		{
			Random rand = new Random();
			int q_val = rand.nextInt(seq.size());
			q_list.add(seq.get(q_val));
		}
		//String q1 = "abcde"; // m=2;
		//q_list.add(q1);
		ArrayList<String> old_ttv_list = new ArrayList<String>(); //stores the old TTV for optimization purposes
		
		//outside loop is for iterative refinement and optimization
		int counter = 1;
		while (!(ttv_list.equals(old_ttv_list)) && counter <= 5)
		{
			counter++;
			old_ttv_list = new ArrayList<String>(ttv_list);
			//Collections.sort(old_ttv_list);
			ttv_list = new ArrayList<String>();
			ttv_list.add(q0);
			
			//this list of lists stores all the individual clusters based on epitope similarity
			ArrayList<ArrayList<String>> S = new ArrayList<>();
	
			//initializes S (list of lists) above
			for(int i=1;i<m;i++)
			{
				ArrayList<String> temp = new ArrayList<>();
				S.add(temp);
			}
	
			//initializes the coverage matrix
			for(int i=0;i<seq.size();i++)
			{
				for(int j=0;j<m;j++)
					coverage[i][j] = 0;
			}
	
			//calculates the coverage for each sequence compared to q0 + q(n) and adds this to matrix
			int n;
			for(int i=0;i<seq.size();i++)
			{
				for(n=1;n<m;n++)
				{
					coverage[i][n] = function_u(q0, q_list, seq.get(i), n);
				}
				//maximum value (best epitope similarity sequence) is added to corresponding cluster for q(n)
				n = getMaxValue(coverage, i, m);
				S.get(n-1).add(seq.get(i));
			}
			
			//recomputes frequencies for each cluster based on frequency of each epitope in the cluster
			for (int i=1; i<m; i++)
			{
				ArrayList<NodeAligned> v = graph.getVertices();
				ArrayList<String> e = new ArrayList<String>();
				for (int j=0; j<v.size(); j++)
				{
					e.add(j, v.get(j).getEpitope());
				}
				for (int j=0; j<e.size(); j++)
				{
					double freq = computeOneFreq(e.get(j), S.get(i-1), seq);
					graph.getNodeFromEpitope(e.get(j)).setFreq(freq);
				}
				ArrayList<NodeAligned> vertices = graph.getVertices();
				//sets the epitopes from q0 to a frequency of zero again
				for (EpigraphBaseNode node : q0path) {
					node.setFreq(0);
				}
//				for (int j = 0; j<q0.length(); j++)
//				{
//					NodeAligned node = graph.getNodeFromEpitope(mySubString(q0, j, 1));
//					if (vertices.contains(node)) 
//						graph.getNodeFromEpitope(mySubString(q0, j, 1)).setFreq(0);
//				}
				//generates a new antigen sequence for each cluster and adds it to the final TTV
				Random random = new Random();
				int start = random.nextInt(graph.getNum_vertices() - 0 + 1) + 0;
				//int end = random.nextInt(graph.getNum_vertices() - 0 + 1) + 0;
				ttv_list.add(epi.optimalPath(graph, graph.getNode(start), graph.getNode(graph.getVertices().size()-1)));
				//ttv_list.add(epi.optimalPath(graph, graph.getNode(start), graph.getNode(end)));
				ArrayList<EpigraphBaseNode> path = epi.getPath();
				//System.out.println("PATH = " + path);
				double cscore = 0.0;
				for (NodeAligned na : path) {
					System.out.println("FREQ " + na.getFreq());
					System.out.println("SIZE " + S.get(i-1).size());
					if (S.get(i-1).size() != 0) {
						cscore += (na.getFreq()/((double)S.get(i-1).size()));
					} else {
						cscore += 0;
					}
				}
				cscore = (cscore/(double)(path.size()));
	    		coverageset.add(cscore);
			}
//			System.out.println(coverage);
			//for(int[] temp:coverage){
				//for(int temp1: temp){
					//System.out.println(temp1);
				//}
			//}
			System.out.println(q0);
			for (ArrayList<String> cluster: S) {
				System.out.println(cluster);
			}
			//System.out.println(S);
			//Collections.sort(ttv_list);
		}
		/*for (int i=1; i<m; i++)
		{
			EpigraphBaseGraph clustergraph = makeClusterGraph(S.get(i-1), graph);
			ArrayList<Node> clusterv = clustergraph.getVertices();
			ArrayList<String> clustere = new ArrayList<String>();
			for (int j=0; j<clusterv.size(); j++)
			{
				clustere.add(j, clusterv.get(j).getEpitope());
			}
			for (int j=0; j<clustere.size(); j++)
			{
				int freq = computeOneFreq(clustere.get(j), S.get(i-1));
				clustergraph.getNodeFromEpitope(clustere.get(j)).setFreq(freq);
			}
			ArrayList<Node> vertices = clustergraph.getVertices();
			for (int j = 0; j<q0.length(); j++)
			{
				Node node = graph.getNodeFromEpitope(q0.charAt(j)+"");
				if (vertices.contains(node)) 
					graph.getNodeFromEpitope(q0.charAt(j)+"").setFreq(0);
			}
			ttv_list.add(i, epi.optimalPath(clustergraph, clustergraph.getNode(0), clustergraph.getNode(clustergraph.getVertices().size()-1)));
		}*/
		
		
		return ttv_list;
    }
	
	//function computes the frequency of an epitope in a particular cluster
	private double computeOneFreq(String epitope, ArrayList<String> cluster, ArrayList<String> seq)
	{
		double val = 0;
		for (String s : cluster)
		{
			if (s.contains(epitope)) val+=1;
		}
		//return (int)((val*seq.size())*100);
		//return (val*seq.size());
		return val;
	}

	String mySubString(String myString, int start, int length)
	{
		return myString.substring(start, Math.min(start + length, myString.length()));
	}

	//function to take in text file input of sequences, cluster according to sub-clade similarity into
	// different lists of sequences, output clusters
	public static HashMap<String, ArrayList<String>> makeClusterMap(File file) throws FileNotFoundException, IOException
	{
		HashMap<String, ArrayList<String>> clusters = new HashMap<String, ArrayList<String>>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			String currSeq = null;
			String currSubclade = "";
			
			while ((line = br.readLine()) != null) {
				if (line.contains(">")) {
									
					if (currSeq != null) { //if a sequence has been gathered for a subclade
						if (clusters.containsKey(currSubclade)) { //if a cluster for the subclade exists
							clusters.get(currSubclade).add(currSeq); //add the gathered sequence to that cluster
						}
						else { //for the new subclade, add a new cluster with the gathered sequence
							ArrayList<String> newCluster = new ArrayList<String>();
							newCluster.add(currSeq);
							clusters.put(currSubclade, newCluster);
						}
						
						currSeq = ""; //wipe the gathered sequence, time to start a new one			
						String[] parts = line.split("\\.");
						currSubclade = parts[1]; //wipe the gathered sequence, time to get the next one
					}
					
					else { //if first time gathering a sequence
						currSeq = ""; //start a new sequence
						String[] parts = line.split("\\.");
						currSubclade = parts[1]; //get the subclade
					}	
				}
				else {
					currSeq = currSeq + line;
				}
			}
			
			//Take care of the last sequence
			if (clusters.containsKey(currSubclade)) { //if a cluster for the subclade exists
				clusters.get(currSubclade).add(currSeq); //add the gathered sequence to that cluster
			}
			else { //for the new subclade, add a new cluster with the gathered sequence
				ArrayList<String> newCluster = new ArrayList<String>();
				newCluster.add(currSeq);
				clusters.put(currSubclade, newCluster);
			}
		}
	return clusters;
	}
	
	//function for later heuristic to cluster the graph based on eptiope similarity to create sub-clade epigraphs
	private EpigraphBaseGraph makeClusterGraph(ArrayList<String> cluster, EpigraphBaseGraph graph)
	{
		EpigraphBaseGraph clustergraph = new EpigraphBaseGraph(graph);
		ArrayList<NodeAligned> badvertices = new ArrayList<NodeAligned>();
		for (String c: cluster){
			for (int i=0; i<c.length(); i++){
				NodeAligned cnode = graph.getNodeFromEpitope(c.charAt(i)+"");
				if (!(graph.hasNode(cnode))) 
					badvertices.add(cnode);
			}
		}
		clustergraph.removeFromGraph(badvertices);
		return clustergraph;
	}
	
	//the argmax function that returns the index of maximum value for a row in the coverage matrix
	private int getMaxValue(int[][] coverage, int i, int m)
	{
		int val = 0, return_val = 0;
		for(int j=0;j<m;j++)
		{
			if(coverage[i][j] > val)
			{
				val = coverage[i][j];
				return_val = j;
			}
		}
		return return_val;
	}

	//function u calculates frequency of an epitope based on whether it appears in q0 or q(n) at least once
	private int function_u(String q0, ArrayList<String> q_list, String s, int n)
	{
		int val = 0;

		for (int i=0; i<s.length(); i++)
		{
			if (q0.contains(s.charAt(i)+"") || q_list.get(n-1).contains(s.charAt(i)+""))
			{
				val += 1;
			}
		}

		return val;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
//		ArrayList<String> seq = new ArrayList<String>();
//		//Test Case #1
////		seq.add("abcde");
////		seq.add("afgie");
////		seq.add("fgbde");
////		seq.add("fghej");
//	
//		//Test Case #0
////		seq.add("bde");
////		seq.add("cde");
////		seq.add("cde");
////		seq.add("de");
////		seq.add("e");
//		
//		//Test Case #2
//		seq.add("abcdef");
//		seq.add("bijkl");
//		seq.add("cjel");
//		seq.add("ghcjkl");
//		seq.add("bcjkl");
//		seq.add("ghijkf");
//		seq.add("gbi");
//		seq.add("bijk");
//		seq.add("gbidkf");
//		seq.add("ghijkl");
//		
//		//Test Case #0
////		int v = 5, e = 5;
//		//Test Case #2
//		int v = 12, e = 20;
//		//Test Case #1
//		//int v = 10, e = 12;
//		ArrayList<NodeAligned> ar = new ArrayList<>();
//		HashMap<NodeAligned, ArrayList<NodeAligned>> m = new HashMap<>();
//	
//		for(int i=0;i<v;i++)
//		{
//			//creates new Epigraph nodes with alphabet characters representing the eptiopes for testing
//			EpigraphBaseNode temp = new EpigraphBaseNode(i, (String) (((char) ('a'+i)) + ""), i, 0);
//			ar.add(temp);
//		}
//
//		for (int i = 0; i < v; i++)
//		{
//			m.put(ar.get(i), new ArrayList<>());
//		}
//
//		//Test Case #1
////		m.get(ar.get(0)).add(ar.get(1));
////		m.get(ar.get(0)).add(ar.get(5));
////		m.get(ar.get(1)).add(ar.get(2));
////		m.get(ar.get(1)).add(ar.get(3));
////		m.get(ar.get(2)).add(ar.get(3));
////		m.get(ar.get(3)).add(ar.get(4));
////		m.get(ar.get(4)).add(ar.get(9));
////		m.get(ar.get(5)).add(ar.get(6));
////		m.get(ar.get(6)).add(ar.get(1));
////		m.get(ar.get(6)).add(ar.get(7));
////		m.get(ar.get(6)).add(ar.get(8));
////		m.get(ar.get(7)).add(ar.get(4));
////		m.get(ar.get(8)).add(ar.get(4));
//		
//		//Test Case #0
////		m.get(ar.get(0)).add(ar.get(1));
////		m.get(ar.get(0)).add(ar.get(2));
////		m.get(ar.get(1)).add(ar.get(3));
////		m.get(ar.get(2)).add(ar.get(3));
////		m.get(ar.get(3)).add(ar.get(4));
//		
//		//Test Case #2
//		m.get(ar.get(0)).add(ar.get(1));
//		m.get(ar.get(0)).add(ar.get(7));
//		m.get(ar.get(1)).add(ar.get(2));
//		m.get(ar.get(1)).add(ar.get(8));
//		m.get(ar.get(2)).add(ar.get(3));
//		m.get(ar.get(2)).add(ar.get(9));
//		m.get(ar.get(3)).add(ar.get(4));
//		m.get(ar.get(3)).add(ar.get(10));
//		m.get(ar.get(4)).add(ar.get(5));
//		m.get(ar.get(4)).add(ar.get(11));
//		m.get(ar.get(6)).add(ar.get(7));
//		m.get(ar.get(6)).add(ar.get(1));
//		m.get(ar.get(7)).add(ar.get(2));
//		m.get(ar.get(7)).add(ar.get(8));
//		m.get(ar.get(8)).add(ar.get(3));
//		m.get(ar.get(8)).add(ar.get(9));
//		m.get(ar.get(9)).add(ar.get(4));
//		m.get(ar.get(9)).add(ar.get(10));
//		m.get(ar.get(10)).add(ar.get(5));
//		m.get(ar.get(10)).add(ar.get(11));
//
//		EpigraphBaseGraph in = new EpigraphBaseGraph(v, e, ar, m);
//
//		//CocktailAlgorithm ca = new CocktailAlgorithm();
//		TTVAlgorithm ta = new TTVAlgorithm();
//		ta.computeFreq(seq, in);
//		ArrayList<String> ttv = ta.ttvalgo(in, 3, 9, seq);
//		System.out.println(ttv);
////		ArrayList<EpigraphBaseNode> path = ea.optimalPath(in, in.getNode(0), in.getNode(4));
////		for(int i=0;i<path.size();i++)
////		{
////			System.out.println(path.get(i).getVertexID() + " ");
////		}
		
		File file = new File("/Users/Arvind/Desktop/testmed.fasta");
		//sp.process(file);
		//EpigraphBaseGraph epigraph = sp.createGraph(sp.sequences, sp.seqlen, 9);
		
		TTVAlgorithm tta = new TTVAlgorithm();
		HashMap<String, ArrayList<String>> clusters = TTVAlgorithm.makeClusterMap(file);
		HashMap<String, EpigraphBaseGraph> clustergraphs = new HashMap<String, EpigraphBaseGraph>();
		//System.out.println(clustergraphs);
		ArrayList<Integer> cseqlen = new ArrayList<Integer>();
		
		for (String key : clusters.keySet()) {
			ArrayList<String> seqs = clusters.get(key);
			//System.out.println(key + " = " + seqs);
			for (String s : seqs) {
				cseqlen.add(s.length());
			}
			//System.out.println(cseqlen);
			SequenceProcessor sp = new SequenceProcessor();
			clustergraphs.put(key, sp.createGraph(seqs, cseqlen, 9, sp.seqlenmax));
		}
		
		
		for (String key : clustergraphs.keySet()) {
			EpigraphAlgorithm ea = new EpigraphAlgorithm();
			//System.out.println(clustergraphs.get(key));
			String op = ea.optimalPath(clustergraphs.get(key), clustergraphs.get(key).getNode(0), clustergraphs.get(key).getNode(clustergraphs.get(key).getNum_vertices()-1));
			System.out.println(op);
		}
	}
}
