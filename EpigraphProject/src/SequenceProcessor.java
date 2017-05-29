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

public class SequenceProcessor {
	public ArrayList<String> sequences = new ArrayList<String>();
	//public int seqlen = 0;
	public ArrayList<Integer> seqlen = new ArrayList<Integer>();
	public int seqlenmax = 0;
	
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
	
	public EpigraphBaseGraph createGraph(ArrayList<String> seq, ArrayList<Integer> seqlen, int k)
	{
		ArrayList<NodeAligned> nodes = new ArrayList<NodeAligned>();
		HashMap<NodeAligned, ArrayList<NodeAligned>> edges = new HashMap<NodeAligned, ArrayList<NodeAligned>>();
		ArrayList<ArrayList<String>> epitopeList = new ArrayList<ArrayList<String>>();
		String newEpitope;
		ArrayList<HashMap<String, Integer>> freqMapList = new ArrayList<HashMap<String, Integer>>();
		int numVertices;
		int numEdges;
		//System.out.println(seq.size());
		for (int i=0; i < seq.size(); i++)
		{
			String s = seq.get(i);
			epitopeList.add(new ArrayList<String>());
			ArrayList<Integer> gapPos = new ArrayList<Integer>();
			for (int index = s.indexOf("-"); index >= 0; index = s.indexOf("-", index+1))
				gapPos.add(index);
			
			for (int j =0; j<seqlen.get(i)-(2*k); j++)
			{
				//System.out.println(k);
				//System.out.println(seqlen);
				//System.out.println(j);
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
					//System.out.println(currPos);
				}
				epitopeList.get(i).add(newEpitope);
			}
		}
		System.out.println(epitopeList);
//			for (int j=0; j<seqlen-k; j++)
//			{
//				String original = s.substring(j, j+k);
//				if (gapPos.contains(j))
//				{
//					int newStart = j+1;
//					while (s.charAt(newStart) == '-')
//						newStart++;
//					epitopeList.get(i).add("-" + epitopeList.get(i).add(s.substring(newStart, newStart+k-1)));
//				} else if (original.substring(1, original.length()).contains("-"))
//				{
//					ArrayList<Integer> gaps = new ArrayList<Integer>();
//					int numGaps = 0;
//					for (int start = j+1; start <= j+k-1; start++)
//					{
//						if (s.charAt(start) == '-')
//						{
//							numGaps++;
//							gaps.add(start);
//						}
//					}
//					String newEptiope = "";
//					int start = j;
//					for (int gap : gaps)
//					{
//						newEptiope += s.substring(start, gap);
//						start = gap+1;
//						while (gaps.contains(start))
//							start++;
//					}
//				}
//				
//			}
		
		//int j = 0;
		for (int i=0; i < epitopeList.get(seqlenmax).size(); i++)
		{
			freqMapList.add(new HashMap<String, Integer>());
			for (int j=0; j < epitopeList.size(); j++)
			{
				//System.out.println(i + " " + j);
				//System.out.println(epitopeList.size());
				//System.out.println(epitopeList.get(seqlenmax).size());
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
					//e.printStackTrace();
					//System.exit(0);
				}
			}
		}
		
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
		
		//for (int i=0; i<5; i++) {
			//NodeAligned no = nodes.get(i);
			//System.out.println(no.getEpitope());
		//}
		EpigraphBaseGraph epigraph = new EpigraphBaseGraph(numVertices, numEdges, nodes, edges);
		return epigraph;
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		SequenceProcessor sp = new SequenceProcessor();
		File file = new File("test5.fasta");
		sp.process(file);
		EpigraphBaseGraph epigraph = sp.createGraph(sp.sequences, sp.seqlen, 9);
		//System.out.println(epigraph);
//		TTVAlgorithm ta = new TTVAlgorithm();
//		ArrayList<String> ttv = ta.ttvalgo(epigraph, 3, 9, sp.sequences);
//		System.out.println();
//		for (String s : ttv) {
//			System.out.println(s);
//		}
//		//System.out.println(epigraph);
		
		
		
//		CocktailAlgorithm ca = new CocktailAlgorithm();
//		ArrayList<String> cockt = ca.cocktail(epigraph, 2, 9);
//		//System.out.println(cockt); //final cocktail output
//		System.out.println();
//		for (String s : cockt) {
//			System.out.println(s);
//		}
		//System.out.println(epigraph);
		
		EpigraphAlgorithm ea = new EpigraphAlgorithm();
		Random random = new Random();
		ArrayList<EpigraphBaseNode> begin_nodes = new ArrayList<EpigraphBaseNode>();
		int numOutput = 3;
		for (int i=0; i<numOutput; i++) {
			int start = random.nextInt(epigraph.getNum_vertices() - 0 + 1) + 0;
			begin_nodes.add(epigraph.getNode(start));
		}
		ArrayList<String> op1 = ea.optimalPathParallel(epigraph, begin_nodes, epigraph.getNode(epigraph.getNum_vertices()-1));
        for (String s : op1) {
        	System.out.println(s);
        }
	
	}
}
