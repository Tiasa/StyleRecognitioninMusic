
import java.util.*;


public class SuffixTree<Item> {
	// TO denote the flexible end of the string
	// while building the tree
	
	// Pre-process the tree to have leaf counts
	// Every occurrence of some pattern P is a prefix of some
	// suffix of T
	// All those occurrences will be in the same subtree
	
	
	public Item[] str; 
	private Node root, sentinel;
	public int implicitNodeIndex=0;
	
	public SuffixTree(Item[] input) {
		str = input;
		buildTree();
		root.setOccurences(0);
	}
//	public Pattern<Item> findPatternOccurences(Item[] p) {
//		if (p!=null && p.length!=0 && root.get(p[0])!=null) {
//			List<Integer> occ = root.get(p[0]).getOccurence(p);
//			if (occ == null) {
//				return null;
//			} else {
//				return new Pattern<Item>(p.length,occ);
//			}
//		} else {
//			return null;
//		}
//	}
//	public Pattern<Item> getMostFrequentPattern () {
//		
//		return root.getLongestMostFrequentPattern(0);
//	}
//	public Pattern<Item> getGreedyPattern() {
//		return root.getGreedyPattern(0);
//	}
//	public Pattern<Item> getLongestPattern() {
//		return root.getLongestAtleastTwiceOccuringPattern(0);
//	}
	public Node getRoot() {
		return root;
	}
	public void buildTree () {
		root = new Node();
		sentinel = new Node();
		root.suffixLink = sentinel;
		Node s = root;
		
		for (int i = 0; i < str.length ; ++i) {
			s = update(s, i );
			s = canonize (s, i);
		}
		
	}
	public Node update(Node s, int index) {
		Node oldr = root; 
		Node nodeToAttachTo = testAndSplit(s, implicitNodeIndex, index-1, str[index]);
		while (nodeToAttachTo != null) {
			Node newNode = new Node();
			Edge newEdge = new Edge(index,str.length-1,newNode);
			nodeToAttachTo.add(newEdge);
			if (oldr != root) {
				oldr.suffixLink = nodeToAttachTo;
			}
			oldr = nodeToAttachTo;
			s = canonize(s.suffixLink,index-1);
			nodeToAttachTo = testAndSplit(s,implicitNodeIndex,index-1,str[index]);
		}
		if (oldr != root) {
			oldr.suffixLink = s;
		}
		return s;
	}
	
	
	
	
	public Node testAndSplit(Node s,int k,int prev , Item item) {
		
		if (k > prev) {
			return (s==sentinel)? null: s.get(item) == null? s : null;
		}
		Edge e = s.get(str[k]);
		if (item.equals(str[e.getStartPoint()+prev-k+1])) {
			return null;
		}
		Node splitNode = new Node();
		Edge splitEdge = new Edge(e.getStartPoint()+prev-k+1,e.getEndPoint(), e.endNode);
		splitNode.add(splitEdge);
		Edge edgeWithOldNode = new Edge(e.getStartPoint(), e.getStartPoint()+prev-k , splitNode);
		s.add(edgeWithOldNode);
		return splitNode;
	}
	
	
	
	public Node canonize(Node s, int index) {
		if (index >= implicitNodeIndex) {
			if (s == sentinel) {
				s = root;
				implicitNodeIndex++;
				if (index < implicitNodeIndex) {
					return s;
				}
			}
			
			Edge edgeWithImplicitNode = s.get(str[implicitNodeIndex]);
		
			
			while (edgeWithImplicitNode.getEndPoint()-edgeWithImplicitNode.getStartPoint() <= index-implicitNodeIndex) {
				s = edgeWithImplicitNode.endNode;
				implicitNodeIndex += edgeWithImplicitNode.getEndPoint()-edgeWithImplicitNode.getStartPoint()+1;
				if (implicitNodeIndex <= index) {
					edgeWithImplicitNode = s.get(str[implicitNodeIndex]); 
				}
			}
		}
		return s;
	}
	

	public class Node implements Iterable<Edge>  {
		private Node suffixLink = null;
		protected int prefixStartIndex, prefixEndIndex;
		protected List<Integer> occurences;
		protected List<Integer> nonOverlapOccurences;
		
		public TreeMap<Item, Edge> edges = new TreeMap<Item, Edge>();
		@Override
		public Iterator<Edge> iterator() {
			return edges.values().iterator();
		}
		public void merge(List<Integer> l1, List<Integer> l2) {
		    for (int index1 = 0, index2 = 0; index2 < l2.size(); index1++) {
		        if (index1 == l1.size() || l1.get(index1) > l2.get(index2)) {
		            l1.add(index1, l2.get(index2++));
		        }
		    }
		} 
		//Both overlapping and nonoverlapping
		public void setOccurences(int prefixLength) {	 

			
			this.occurences = new ArrayList<Integer>();
			if (this.getNumChildren() == 0) {
				this.occurences.add(str.length - prefixLength);
			} else {
				for(Edge edge: this) {
					edge.getEndNode().setOccurences(edge.getEndPoint()-edge.getStartPoint()+1+prefixLength);
					this.occurences.addAll(edge.getEndNode().occurences);
				}
			}
			if (this != root) { // if this is root, it's wasteful
				this.removeOverlap(prefixLength);
			}
			
		}
		private void removeOverlap(int prefixLength) {
			Collections.sort(this.occurences); // probably expensive
			int prevAdded = -1;
			this.nonOverlapOccurences = new ArrayList<Integer>();
			for (Integer i: this.occurences) {
				if (prevAdded == -1 || prevAdded+prefixLength<=i.intValue()) {
					this.nonOverlapOccurences.add(i);
					prevAdded = i.intValue();
				}
			}
		}

		public void add(Edge e) {
			edges.put(e.getFirst(), e);
		}
		public int getNumChildren() {
			return edges.size();
		}
		public Edge get(Item suffixStart) {
			return edges.get(suffixStart);
		}
		public Pattern<Item> getLongestMostFrequentPattern(int prefixLength) {
			if(prefixLength<2) {
				Pattern<Item> bestPattern = null;
				for(Edge edge: this) {
					if (edge.getEndNode().getNumChildren()!=0) {
						Pattern<Item> temp = edge.getEndNode().getLongestMostFrequentPattern(edge.getEndPoint()-edge.getStartPoint()+1+prefixLength);
						if(bestPattern!=null) {
							if(temp!=null ) {
								
								if (bestPattern.frequency() == temp.frequency()) {
									if (bestPattern.length() < temp.length()) {
										bestPattern = temp;
									}
								} else {
									if(bestPattern.frequency() <  temp.frequency()) {
										bestPattern = temp;
									}								
								}
							}
						} else {
							if (temp!=null && temp.frequency()>1 && temp.length()>1) {
								bestPattern = temp;
							}
						}
					}
				}
				return bestPattern;
			} else {
				return new Pattern<Item>(prefixLength,this.nonOverlapOccurences);
			}
		}
		
		
		
		public Pattern<Item> getGreedyPattern(int prefixLength) {
			
			// Each Node will be responsible for supplying the
			// best greedy pattern in that subtree that
			
			Pattern<Item> bestPattern = null;
			for(Edge edge: this) {
				if (edge.getEndNode().getNumChildren()!=0) {
					Pattern<Item> temp = edge.getEndNode().getGreedyPattern(edge.getEndPoint()-edge.getStartPoint()+1+prefixLength);
 					
					if(bestPattern!=null) {
						if (temp!=null && bestPattern.compressionSize() < temp.compressionSize()) {
							bestPattern = temp;
					}
						
					} else {
						if (temp!=null && temp.frequency()>1 && temp.length()>1) {
							bestPattern = temp;
						}
					}
				}
			}
			// calculate current node's best pattern , and it also serves
			// as a base case
			Pattern<Item> curNodePattern = null;
			if (this!= root) {
				curNodePattern = new Pattern<Item>(prefixLength,this.nonOverlapOccurences);
			}
			if (bestPattern == null) {
				if (curNodePattern!= null && curNodePattern.length()>1 && curNodePattern.frequency()>1) {
					bestPattern = curNodePattern;
				}
			} else {
				if (curNodePattern != null && curNodePattern.length()>1 && curNodePattern.frequency()>1 && bestPattern.compressionSize() < curNodePattern.compressionSize()) {
					bestPattern = curNodePattern;
				}
			}
			return bestPattern;
			
		}
		
		public Pattern<Item> getLongestAtleastTwiceOccuringPattern(int prefixLength) {
			Pattern<Item> bestPattern = null;
			for(Edge edge: this) {
				if (edge.getEndNode().getNumChildren()!=0) {
					Pattern<Item> temp = edge.getEndNode().getLongestAtleastTwiceOccuringPattern(edge.getEndPoint()-edge.getStartPoint()+1+prefixLength);
					if(bestPattern!=null) {
						if(temp!=null ) {
							if (bestPattern.length() == temp.length()) {
								if (bestPattern.frequency() < temp.frequency()) {
									bestPattern = temp;
								}
							} else {
								if(bestPattern.length() <  temp.length()) {
									bestPattern = temp;
								}								
							}
						}
					} else {
						if (temp!=null && temp.frequency()>1 && temp.length()>1) {
							bestPattern = temp;
						}		
					}
				}
			}
			// calculate current node's best pattern , and it also serves
			// as a base case
			Pattern<Item> curNodePattern = null;
			if (this!= root) {
				curNodePattern = new Pattern<Item>(prefixLength,this.nonOverlapOccurences);
			}
			if (bestPattern == null) {
				if (curNodePattern!= null && curNodePattern.length()>1 && curNodePattern.frequency()>1) {
					bestPattern = curNodePattern;
				}
			} else { 
				if (curNodePattern != null && curNodePattern.length()>1 && curNodePattern.frequency()>1 && bestPattern.length() < curNodePattern.length()) {
					bestPattern = curNodePattern;
				}
			}
			return bestPattern;
		}
		
		
		
	}
	
	public class Edge {
		//private Node head = null;
		protected Node endNode = null;
		protected int startPoint;
		protected int endPoint;
		
		public Edge(int s, int e, Node n) {
			this.startPoint = s;
			this.endPoint = e; 
			this.endNode = n;
		}
		public Item getFirst() {
			return (Item)str[this.startPoint];
		}
		
		public int getStartPoint() {
			return this.startPoint;
		}
		public int getEndPoint() {
			return this.endPoint;
		} 
		
		public Node getEndNode() {
			return this.endNode; 
		}
		public List<Integer> getOccurence(Item[] p) {
			//List<Integer> result = null;
			for (int i = 0; i < p.length; i++) {
				if (this.startPoint + i > this.endPoint) {
					Edge nextEdge = this.endNode.get(p[i]);
					if (nextEdge != null) {
						return nextEdge.getOccurence(Arrays.copyOfRange(p,i,p.length));
					}
					else {
						return null;
					}
				} else {
					if (!str[this.startPoint + i].equals( p[i])) {
						return null;
					}
				}
			}
			return this.endNode.nonOverlapOccurences;
		}
	}
	
	
}
