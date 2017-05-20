import java.util.ArrayList;
import java.util.HashMap;

public class TTVAlgorithm {
	public void computeFreq(ArrayList<String> seq, EpigraphBaseGraph graph)
	{
		double total = seq.size();
		HashMap<String,Double> freqmap = new HashMap<String,Double>();
		
		for (String s : seq)
		{
			for (int i=0; i<s.length(); i++)
			{
				String epitope = s.charAt(i)+"";
				if (!(freqmap.containsKey(s.charAt(i)))) {
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
			n.setFreq((int)(freqmap.get(key)*100));
		}

	}
	
	public ArrayList<String> ttvalgo(EpigraphBaseGraph graph, int m, int k, ArrayList<String> seq)
    {
		int[][] coverage = new int[seq.size()][m];
//		ArrayList<String> cocktail = new ArrayList<String>();
		EpigraphAlgorithm epi = new EpigraphAlgorithm();
		String q0 = epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getNum_vertices()-1));
		//select random sequences (m-1)
		ArrayList<String> q_list = new ArrayList<>();
		ArrayList<String> ttv_list = new ArrayList<String>();
		ttv_list.add(q0);
		String q1 = "abcde"; // m=2;
		q_list.add(q1);
		ArrayList<ArrayList<String>> S = new ArrayList<>();

		for(int i=1;i<m;i++)
		{
			ArrayList<String> temp = new ArrayList<>();
			S.add(temp);
		}

		for(int i=0;i<seq.size();i++)
		{
			for(int j=0;j<m;j++)
				coverage[i][j] = 0;
		}

		int n;
		for(int i=0;i<seq.size();i++)
		{
			for(n=1;n<m;n++)
			{
				coverage[i][n] = function_u(q0, q_list, seq.get(i), n);
			}
			n = getMaxValue(coverage, i, m);
			S.get(n-1).add(seq.get(i));
		}
		
		for (int i=1; i<m; i++)
		{
			ArrayList<Node> v = graph.getVertices();
			ArrayList<String> e = new ArrayList<String>();
			for (int j=0; j<v.size(); j++)
			{
				e.add(j, v.get(j).getEpitope());
			}
			for (int j=0; j<e.size(); j++)
			{
				int freq = computeOneFreq(e.get(j), S.get(i-1), seq);
				graph.getNodeFromEpitope(e.get(j)).setFreq(freq);
			}
			ArrayList<Node> vertices = graph.getVertices();
			for (int j = 0; j<q0.length(); j++)
			{
				Node node = graph.getNodeFromEpitope(q0.charAt(j)+"");
				if (vertices.contains(node)) 
					graph.getNodeFromEpitope(q0.charAt(j)+"").setFreq(0);
			}
			ttv_list.add(epi.optimalPath(graph, graph.getNode(0), graph.getNode(graph.getVertices().size()-1)));
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
	
	private int computeOneFreq(String epitope, ArrayList<String> cluster, ArrayList<String> seq)
	{
		double val = 0;
		for (String s : cluster)
		{
			if (s.contains(epitope)) val+=1;
		}
		return (int)((val*seq.size())*100);
	}

	private EpigraphBaseGraph makeClusterGraph(ArrayList<String> cluster, EpigraphBaseGraph graph)
	{
		EpigraphBaseGraph clustergraph = new EpigraphBaseGraph(graph);
		ArrayList<Node> badvertices = new ArrayList<Node>();
		for (String c: cluster){
			for (int i=0; i<c.length(); i++){
				Node cnode = graph.getNodeFromEpitope(c.charAt(i)+"");
				if (!(graph.hasNode(cnode))) 
					badvertices.add(cnode);
			}
		}
		clustergraph.removeFromGraph(badvertices);
		return clustergraph;
	}
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

	public static void main(String[] args)
	{
		ArrayList<String> seq = new ArrayList<String>();
		seq.add("abcde");
		seq.add("afgie");
		seq.add("fgbde");
		seq.add("fghej");
		int v = 10, e = 12;
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
		m.get(ar.get(0)).add(ar.get(5));
		m.get(ar.get(1)).add(ar.get(2));
		m.get(ar.get(1)).add(ar.get(3));
		m.get(ar.get(2)).add(ar.get(3));
		m.get(ar.get(3)).add(ar.get(4));
		m.get(ar.get(4)).add(ar.get(9));
		m.get(ar.get(5)).add(ar.get(6));
		m.get(ar.get(6)).add(ar.get(1));
		m.get(ar.get(6)).add(ar.get(7));
		m.get(ar.get(6)).add(ar.get(8));
		m.get(ar.get(7)).add(ar.get(4));
		m.get(ar.get(8)).add(ar.get(4));

		EpigraphBaseGraph in = new EpigraphBaseGraph(v, e, ar, m);

		//CocktailAlgorithm ca = new CocktailAlgorithm();
		TTVAlgorithm ta = new TTVAlgorithm();
		ta.computeFreq(seq, in);
		ArrayList<String> ttv = ta.ttvalgo(in, 2, 9, seq);
		System.out.println(ttv);
//		ArrayList<EpigraphBaseNode> path = ea.optimalPath(in, in.getNode(0), in.getNode(4));
//		for(int i=0;i<path.size();i++)
//		{
//			System.out.println(path.get(i).getVertexID() + " ");
//		}
	}
}
