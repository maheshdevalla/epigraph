import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Arvind Suresh
 */

public class SequenceProcessor {
	public ArrayList<String> sequences = new ArrayList<String>();
	//public int seqlen = 0;
	public ArrayList<Integer> seqlen = new ArrayList<Integer>();
	public ArrayList<String> all_epitopes = new ArrayList<String>();
	public int seqlenmax = 0;
	
	//method to read in input files and HIV sequences
	public void process(File file) throws FileNotFoundException, IOException
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    String next = null;
		    while ((line = br.readLine()) != null) {
		    	if (line.contains(">")) 
		    	{
		    		if (next != null) sequences.add(next);
		    		next = "";
		    		//next += line;
		    	}
		    	else {
		    		next += line;
		    	}
		    }
		    if (sequences.size() == 0)
		    	sequences.add(next);
		}
		
		int counter = 0;
		for (String seq : sequences) {
			//System.out.println(seq);
			//System.out.println(seq.length());
			seqlen.add(seq.length());
			if (seq.length() > seqlenmax)
				seqlenmax = counter;
			counter++;
		}
		
		
	}
	
	// primary method of the class to create a full Epigraph from a list of HIV aligned sequences as input
	public EpigraphBaseGraph createGraph(ArrayList<String> seq, ArrayList<Integer> seqlen, int k, int slnmax)
	{
		ArrayList<NodeAligned> nodes = new ArrayList<NodeAligned>();
		HashMap<NodeAligned, ArrayList<NodeAligned>> edges = new HashMap<NodeAligned, ArrayList<NodeAligned>>();
		ArrayList<ArrayList<String>> epitopeList = new ArrayList<ArrayList<String>>();
		String newEpitope;
		ArrayList<HashMap<String, Integer>> freqMapList = new ArrayList<HashMap<String, Integer>>();
		int numVertices;
		int numEdges;
		
		// for loop to read in sequences a generate a list of eptiopes
		for (int i=0; i < seq.size(); i++)
		{
			String s = seq.get(i);
			epitopeList.add(new ArrayList<String>());
			ArrayList<Integer> gapPos = new ArrayList<Integer>();
			for (int index = s.indexOf("-"); index >= 0; index = s.indexOf("-", index+1))
				gapPos.add(index);
			
			for (int j =0; j<seqlen.get(i)-(2*k); j++)
			{
				boolean gap = false;
				int kcurr = 0;
				newEpitope = "";
				int currPos = j;
				if (s.charAt(currPos) == '-') {
					newEpitope += "-";
					currPos++;
					kcurr++;
				}
					
				
				while (kcurr < k && currPos < seqlen.get(i)-k) 
				{
					if (s.charAt(currPos) == '-')
						currPos++;
					else 
					{
						newEpitope += s.charAt(currPos);
						currPos++;
						kcurr++;
					}
				}
				epitopeList.get(i).add(newEpitope);
				all_epitopes.add(newEpitope);
			}
		}

		// calculates the frequency of each eptiope and creates a list of maps from each epitope to its frequency
		for (int i=0; i < epitopeList.get(slnmax).size(); i++)
		{
			freqMapList.add(new HashMap<String, Integer>());
			for (int j=0; j < epitopeList.size(); j++)
			{
				try {
					String epitope = epitopeList.get(j).get(i);
					Set<String> currkeyset = freqMapList.get(i).keySet();
					if (!currkeyset.contains(epitope))
					{
						if (epitope.contains("-")) {
							freqMapList.get(i).put(epitope, 0);
						}
						else {
							freqMapList.get(i).put(epitope, 1);
						}
					}
					else
					{
						freqMapList.get(i).put(epitope, freqMapList.get(i).get(epitope)+1);
					}
				} catch (Exception e) {
					
				}
			}
		}
		
		// generates nodes from the prior data
		int id = 0;
		numVertices = 0;
		ArrayList<HashMap<String, NodeAligned>> epitopeToNode = new ArrayList<HashMap<String, NodeAligned>>();
		for (int i=0; i < freqMapList.size(); i++)
		{
			epitopeToNode.add(new HashMap<String, NodeAligned>());
			for (String key : freqMapList.get(i).keySet()) {
				EpigraphBaseNode n = new EpigraphBaseNode(freqMapList.get(i).get(key), key, id, i);
				nodes.add(n);
				epitopeToNode.get(i).put(key, n);
				id++;
				numVertices++;
			}
		}
		
		// generates edges from the prior data
		numEdges = 0;
		for (int i=0; i < freqMapList.size()-1; i++)
		{
			for (String key : freqMapList.get(i).keySet()) {
				String eight = key.substring(1, key.length());
				for (String keytwo : freqMapList.get(i+1).keySet()) {
					if (keytwo.charAt(0) == '-') {
						if (eight.equals(keytwo.substring(1, keytwo.length()))) 
							numEdges++;
							if (!edges.containsKey(epitopeToNode.get(i).get(key))) {
								ArrayList<NodeAligned> firstedge = new ArrayList<NodeAligned>();
								firstedge.add(epitopeToNode.get(i+1).get(keytwo));
								edges.put(epitopeToNode.get(i).get(key), firstedge);
							} else {
								edges.get(epitopeToNode.get(i).get(key)).add(epitopeToNode.get(i+1).get(keytwo));
							}
					} else {
						if (eight.equals(keytwo.substring(0, keytwo.length()-1))) {
							numEdges++;
							if (!edges.containsKey(epitopeToNode.get(i).get(key))) {
								ArrayList<NodeAligned> firstedge = new ArrayList<NodeAligned>();
								firstedge.add(epitopeToNode.get(i+1).get(keytwo));
								edges.put(epitopeToNode.get(i).get(key), firstedge);
							} else {
								edges.get(epitopeToNode.get(i).get(key)).add(epitopeToNode.get(i+1).get(keytwo));
							}
						}
					}
				}
			}
		}
		
		// creates the final Epigraph and returns it
		EpigraphBaseGraph epigraph = new EpigraphBaseGraph(numVertices, numEdges, nodes, edges);
		System.out.println("grph"+epigraph.getNum_vertices());
		return epigraph;
		
	}
	
	// method to generates input file for MHC Class I prediction
	public void createImmunoFile(ArrayList<String> seqList) throws IOException {
		List<String> newSeqList = new ArrayList<String>();
		int id = 1;
		for (String seq: seqList) {
			String newSeq = "";
			for(int i=0; i<seq.length(); i++) {
				if (seq.charAt(i) != '-') {
					newSeq += seq.charAt(i) + "";
				}
			}
			newSeqList.add(">" + id);
			newSeqList.add(newSeq);
			id++;
		}
		File file = new File("/Users/Arvind/Desktop/testi.fasta");
		Path path = Paths.get("testi.fasta");
		Files.write(path, newSeqList, Charset.forName("UTF-8"));
	}
	
	// method to generate intput file for pMHC immunogenicity prediction
	public void pMHCImmunoFile(ArrayList<String> epitopes) throws IOException {
		File file = new File("/Users/Arvind/Desktop/testp.fasta");
		Path path = Paths.get("testp.fasta");
		ArrayList<String> etr = new ArrayList<String>();
		for (String e : epitopes) {
			if (e.contains("-") || e.contains("X") || e.contains("*")) {
				etr.add(e);
			}
		}
		epitopes.removeAll(etr);
		Files.write(path, epitopes, Charset.forName("UTF-8"));
	}
	
	// rescores epitope frequencies based on immunogenicity prediction data
	public HashMap<NodeAligned, Double> rescore(File file, EpigraphBaseGraph epigraph) throws FileNotFoundException, IOException {
		//EpigraphBaseGraph newepi = new EpigraphBaseGraph(epigraph);
		HashMap<String, Double> immunoscore = new HashMap<String, Double>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] val = line.split(",");
		    	immunoscore.put(val[0], Double.parseDouble(val[1]));
		    }
		}
		
		HashMap<NodeAligned, Double> old_f = new HashMap<NodeAligned, Double>();
		for (NodeAligned n : epigraph.getVertices()) {
			String epitope = n.getEpitope();
			old_f.put(n, n.getFreq());
			if (immunoscore.containsKey(epitope)) {
				n.setFreq((double)(n.getFreq()) * immunoscore.get(epitope));
			}
		}
		
		//return newepi;
		return old_f;
		
		
	}
	
	// calculates coverage scores for the various heuristics
	public static ArrayList<Double> calc_coverage(ArrayList<ArrayList<Double>> paths) {
		ArrayList<Double> coverages = new ArrayList<Double>();
		for (ArrayList<Double> path : paths) {
			double coverage = 0.0;
			for (Double d : path) {
				coverage += d/2.0;
			}
			coverage = (coverage/(double)(path.size()));
			coverages.add(coverage);
		}
		return coverages;
	}
	
	
	
	// primary main method for the whole program - START HERE
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//input file
		File file = new File("/Users/Arvind/Desktop/testo.fasta");
		
		//Creates the Epigraph - use for all algo and heuristics EXCEPT clustering heuristic
		SequenceProcessor sp = new SequenceProcessor();
		sp.process(file);
		EpigraphBaseGraph epigraph = sp.createGraph(sp.sequences, sp.seqlen, 9, sp.seqlenmax);
		
		//Immunogenicity Heuristic
		//System.out.println(epigraph);
		//sp.createImmunoFile(sp.sequences);
		//sp.pMHCImmunoFile(sp.all_epitopes);
		
		//File ifile = new File("/Users/Arvind/Desktop/testoimmuno.csv");
		
		//System.out.println(epigraph);
		
		//Clustering Heuristic
//		TTVAlgorithm tta = new TTVAlgorithm();
//		HashMap<String, ArrayList<String>> clusters = TTVAlgorithm.makeClusterMap(file);
//		HashMap<String, EpigraphBaseGraph> clustergraphs = new HashMap<String, EpigraphBaseGraph>();
//		//System.out.println(clustergraphs);
//		ArrayList<Integer> cseqlen = new ArrayList<Integer>();
//		SequenceProcessor sp = new SequenceProcessor();
//
//		int seqlenmax = 0;
//		for (String key : clusters.keySet()) {
//			ArrayList<String> seqs = clusters.get(key);
//			int counter = 0;
//			for (String seq : seqs) {
//				//System.out.println(seq);
//				//System.out.println(seq.length());
//				cseqlen.add(seq.length());
//				if (seq.length() > seqlenmax)
//					seqlenmax = counter;
//				counter++;
//			}
//			//System.out.println(key + " = " + seqs);
////			System.out.println(cseqlen);
//			clustergraphs.put(key, sp.createGraph(seqs, cseqlen, 9, seqlenmax));
//			//System.out.println("grph-------"+clustergraphs.get(key).getNum_edges());
//		}
//		
//		
//		for (String key : clustergraphs.keySet()) {
//			//System.out.println(clustergraphs.get(key));
//			EpigraphAlgorithm ea = new EpigraphAlgorithm();
//			//System.out.println("grph2220-------"+clustergraphs.get(key).getNum_edges());
//
//			//System.out.println("initnode---"+clustergraphs.get(key).getNum_vertices());
//			String op = ea.optimalPath(clustergraphs.get(key), clustergraphs.get(key).getNode(0), clustergraphs.get(key).getNode(clustergraphs.get(key).getNum_vertices()-1));
//			System.out.println(op);
//		}
		
		
		
		//TTV Algo Original
		//System.out.println(epigraph);
//		TTVAlgorithm ta = new TTVAlgorithm();
//		ArrayList<String> ttv = ta.ttvalgo(epigraph, 5, 9, sp.sequences);
//		System.out.println();
//		for (int i=0; i<ttv.size(); i++) {
//			System.out.println(ttv.get(i));
//			System.out.println(ta.coverageset.get(i));
//		}
////		for (NodeAligned n : epigraph.getVertices()) {
////			System.out.println(n.getFreq());
////		}
//		for (String s : ttv) {
//			System.out.println(s);
//		}
		//System.out.println(epigraph);
		
		
		//Cocktail Algo Original
		CocktailAlgorithm ca = new CocktailAlgorithm();
		ArrayList<String> cockt = ca.cocktail(epigraph, 3, 9, (double)sp.sequences.size());
		//System.out.println(cockt); //final cocktail output
		System.out.println();
		for (int i=0; i<cockt.size(); i++) {
			System.out.println(cockt.get(i));
			System.out.println(ca.coverageset.get(i));
		}
		
		
		//Immunogenicity Heuristic
//		HashMap<NodeAligned, Double> old_f = sp.rescore(ifile, epigraph);
//		
//		CocktailAlgorithm cai = new CocktailAlgorithm();
//		ArrayList<String> cockti = cai.cocktailimmuno(old_f, epigraph, 3, 9, (double)sp.sequences.size());
//		//System.out.println(cockt); //final cocktail output
//		System.out.println();
//		for (int i=0; i<cockti.size(); i++) {
//			System.out.println(cockti.get(i));
//			System.out.println(cai.coverageset.get(i));
//		}
		
		
//		for (NodeAligned n : epigraph.getVertices()) {
//			System.out.println(n.getFreq());
//		}
		
//		for (String s : cockt) {
//			System.out.println(s);
//		}
//		System.out.println(epigraph);
		
		
		//Multi-dimensional DP Heuristic
//		EpigraphAlgorithm ea = new EpigraphAlgorithm();
//		Random random = new Random();
//		ArrayList<EpigraphBaseNode> begin_nodes = new ArrayList<EpigraphBaseNode>();
//		int numOutput = 3;
//		for (int i=0; i<numOutput; i++) {
//			int start = random.nextInt(epigraph.getNum_vertices() - 0 + 1) + 0;
//			System.out.println(start);
//			begin_nodes.add(epigraph.getNode(start));
//		}
//		ArrayList<String> op1 = ea.optimalPathParallel(epigraph, begin_nodes, epigraph.getNode(epigraph.getNum_vertices()-1));
//		ArrayList<Double> coverages = calc_coverage(ea.paths);
//		for (int i=0; i<op1.size(); i++) {
//        	System.out.println(op1.get(i));
//        	System.out.println(coverages.get(i));
//        }
		
		// Testing Code to check frequencies of each node in the epigraph
//		for(int i=0; i<epigraph.getNum_vertices(); i++)
//		{
//			System.out.println("epigraph.getNode(i) = " + epigraph.getNode(i));
//			if (epigraph.getNode(i).getFreq() > 20) 
//				System.out.println("epigraph.getNode(i).getFreq() = " + epigraph.getNode(i).getFreq());
//		}
	
	}
}
