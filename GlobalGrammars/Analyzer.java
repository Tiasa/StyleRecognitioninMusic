import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mdsj.MDSJ;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Analyzer {
	public static Grammar getSmallestCFG (String x) {
		Grammar smallest = null;
		Grammar greedy = CFGGlobalAlgorithm.compress(x,Constants.Greedy);
		Grammar mostFrequent = CFGGlobalAlgorithm.compress(x, Constants.MostFrequent);
		Grammar longest = CFGGlobalAlgorithm.compress(x, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = greedy;
			}
		} else {
			if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = mostFrequent;
			}
		}
		// For testing the longest pattern scheme
		// Delete later
		//return CFGGlobalAlgorithm.compress(x, Constants.Longest);
		return smallest;
	}
	public static Grammar getSmallestCFG(String x, Grammar g) {
		Grammar smallest = null;
		Grammar greedy = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.Greedy);
		Grammar mostFrequent = CFGGlobalAlgorithm.compressWithExistingGrammar(x, g, Constants.MostFrequent);
		Grammar longest = CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		if(greedy.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
			if (longest.KolmogorovComplexity() < greedy.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = greedy;
			}
		} else {
			if (longest.KolmogorovComplexity() < mostFrequent.KolmogorovComplexity()) {
				smallest = longest;
			} else {
				smallest = mostFrequent;
			}
		}
		//return CFGGlobalAlgorithm.compressWithExistingGrammar (x, g, Constants.Longest);
		return smallest;
	}
	private static double NCDWithDefinition(int num, String file1Name, String file2Name, String separator) {
		
		switch (num) {
		case 1:
			return NCD.MingVityaniNCD(file1Name, file2Name);
		case 2:
			return NCD.MingVityaniSumOfConditionalsNCD(file1Name, file2Name, separator);
		case 3:
			return NCD.MySumOfConditionalsNCD(file1Name, file2Name);
		case 4:
			return NCD.MingVityaniNCDApproximation(file1Name, file2Name); // For general purpose
		default:
			System.out.println("Wrong Defition");
    		System.exit(1);
		}
		return 0;
	}
	private static String read(Reader reader) throws IOException
	  {
	      boolean over = false;
	      String content = "";
	      int bufferSize = 1024;
	      char[] buffer = new char[bufferSize];
	      while(!over)
	      {
	        int r = reader.read(buffer);
	        if (r!=-1)
	        {
	          String tmp = String.valueOf(buffer,0,r);
	          content = content + tmp;
	        }
	        if (r<bufferSize) over = true;
	      }
	      return content;
	  }
	private static void generalAnalyzer(String[] args) {
		if(args.length != 1)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		final String objectsFolder = args[0];
		
		DataSetArranger data = new DataSetArranger(objectsFolder, objectsFolder+"/"+objectsFolder.substring(objectsFolder.lastIndexOf("/")+1,objectsFolder.lastIndexOf("_"))+".txt");
		List<String> Artists = Arrays.asList("Chopin");
		List<Song> objects = data.getSongsByArtist(Artists);
//		List<Integer> Decades = Arrays.asList(1960);
//		List<Song> objects = data.getSongsByDecade(Decades);
		//List<Song> objects = data.getSongsForRankAnalysis();
		try {
//			File folder = new File(objectsFolder);  
//			File[] listOfFiles = folder.listFiles();
//			for (File file : listOfFiles) {
//				if (file.isFile() && file.getName().contains(".pitch")) {
//					objects.add(file.getName());
//				}
//			}
			int numObjects = objects.size();
			    
			//for (int def = 2; def <= Constants.NumOfNCDDefinitions ; def++) {
			int def = 2;
				double[][] distanceMatrix = new double[numObjects][numObjects];
				for (int i=0; i<numObjects; i++) {
					for (int j=i+1;j<numObjects;j++) {
						String object1Loc = objectsFolder+"/"+objects.get(i).getFileName();
						String object2Loc = objectsFolder+"/"+objects.get(j).getFileName();
						// Taking the average of two to maintain symmetry
						// could take maximum too
						double xy = NCDWithDefinition(def,object1Loc, object2Loc, " 160 ");
//						double yx = NCDWithDefinition(def,object2Loc, object1Loc, " 100000 ");
						distanceMatrix[i][j] = distanceMatrix[j][i] = xy;
					}
				}
					// we want the matrix to be symmetric
				
				String phylipFileName = objectsFolder+"/phylipDistanceMatrix"+new Integer(def).toString()+".txt";
				File phylipFile = new File(phylipFileName);
				if (phylipFile.exists()) {
					phylipFile.delete();
				}
				phylipFile.createNewFile();
				FileWriter phylipfw = new FileWriter(phylipFile);
				phylipfw.write (new Integer(numObjects).toString());
				phylipfw.write("\n");
				for (int i = 0; i<numObjects; i++) {
					phylipfw.write(objects.get(i).getFileName().substring(0, objects.get(i).getFileName().lastIndexOf("_"))+"_"+String.join("_", objects.get(i).getArtist().split("\\s+")));
					//phylipfw.write(objects.get(i).split("\\.")[0]);
					for (int j=0; j<numObjects; j++) {
						phylipfw.write(" "+String.format("%.5f", distanceMatrix[i][j]));
					}
					phylipfw.write("\n");
				}
				
				phylipfw.close();
				
			
				double[][] coordinates = MDSJ.classicalScaling(distanceMatrix,2);
				String MDSFileName = objectsFolder + "/"+new Integer(2).toString()+"DScalingWithDef"+new Integer(def).toString()+".txt";
				File MDSFile = new File(MDSFileName);
				if(MDSFile.exists()) {
					MDSFile.delete();
				}
				MDSFile.createNewFile();
				FileWriter MDSfw = new FileWriter(MDSFile);
				
				String SVCFileName = objectsFolder + "/SVCWithDef"+new Integer(def).toString()+".txt";
				File SVCFile = new File(SVCFileName);
				if(SVCFile.exists()) {
					SVCFile.delete();
				}
				SVCFile.createNewFile();
				FileWriter SVCfw = new FileWriter(SVCFile);
				
				// Special for DBSCAN Clustering
//				MDSfw.write("[");
//				for (int i=0; i < numObjects; i++) {
//					MDSfw.write("["+Double.toString(coordinates[0][i])+","+Double.toString(coordinates[1][i])+"],");
//				}
//				MDSfw.write("]\n\n[");
//				for(int i=0; i<numObjects; i++) {
//					MDSfw.write("'"+Integer.toString(Artists.indexOf(objects.get(i).getArtist())) +"',");
//				}
//				MDSfw.write("]");
				// By Artist
//					for (String artist: Artists) {
//						MDSfw.write(String.join("_", artist.split("\\s+")));
//						for (int i = 0; i<numObjects; i++) {
//							if (objects.get(i).getArtist().compareTo(artist)==0) {
//								for (int dim=0;dim<2;dim++) {
//									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//								}
//							}
//						}
//						MDSfw.write("\n");
//					}
//				// Format for SVC
//				for (String artist: Artists) {
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getArtist().compareTo(artist)==0) {
//							SVCfw.write("["+new Double(coordinates[0][i]).toString()+","+new Double(coordinates[1][i]).toString()+"],");
//						}
//						
//					}
//					SVCfw.write("\n");
//				}
//				SVCfw.write("\n\n\n\n\n\n[");
//				for (int i=0;i<Artists.size();i++) {
//					for (int j = 0; j<numObjects; j++) {
//						if (objects.get(j).getArtist().compareTo(Artists.get(i))==0) {
//							SVCfw.write(Integer.toString(i)+",");
//						}
//					}
//				}
//				SVCfw.write("]");
				// Individual Artist
					List<String> classification =  Arrays.asList("Prel","Etu");
					for (String group: classification) {
						String groupName = "";
						switch (group) {
						case "WTK":
							groupName = "Wohltemperierte_Klavier";
							break;
						case "Inven":
							groupName = "Invention";
							break;
						case "Son":
							groupName = "Violin_Sonata";
							break;
						case "Etu":
							groupName = "Etudes";
							break;
						case "Prel":
							groupName = "Preludes";
							break;
						case "Maz":
							groupName = "Mazurkas";
							break;
						case "Noc":
							groupName = "Nocturnes";
							break;
						}
						MDSfw.write(groupName);
						for (int i = 0; i<numObjects; i++) {
							if (objects.get(i).getTitle().contains(group)) {
								for (int dim=0;dim<2;dim++) {
									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
								}
							}
						}
						MDSfw.write("\n");
					}
				
				// Format for SVC
				for (String style: classification) {
					for (int i = 0; i<numObjects; i++) {
						if (objects.get(i).getTitle().contains(style)) {
							SVCfw.write("["+new Double(coordinates[0][i]).toString()+","+new Double(coordinates[1][i]).toString()+"],");
						}
						
					}
					SVCfw.write("\n");
				}
				SVCfw.write("\n\n\n\n\n\n[");
				for (int i=0;i<classification.size();i++) {
					for (int j = 0; j<numObjects; j++) {
						if (objects.get(j).getTitle().contains(classification.get(i))) {
							SVCfw.write(Integer.toString(i)+",");
						}
					}
				}
				SVCfw.write("]");
				
				// By individual song
//					for (int i = 0; i<numObjects; i++) {
//						MDSfw.write(objects.get(i).getFileName());
//						for (int dim=0;dim<2;dim++) {
//							MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//						}
//						MDSfw.write("\n");
//					}
//				

					
				// By Year
//					for (Integer decade: Decades) {
//						MDSfw.write(decade.toString()+"s");
//						for (int i = 0; i<numObjects; i++) {
//							if (objects.get(i).getYear()/10==decade.intValue()/10) {
//								for (int dim=0;dim<Dimension;dim++) {
//									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//								}
//							}
//						}
//						MDSfw.write("\n");
//					}
				
				   
				
				// By Rank
//					
//					MDSfw.write("Rank_Top_100_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()<=100) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
//					MDSfw.write("Rank_100_-_400_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()>100 && objects.get(i).getRank()<=400) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
//					MDSfw.write("Rank_From_400_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()>400) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
				
				
				SVCfw.close();
				MDSfw.close();
				
				
				
			//}
			
			
		} catch (Exception e) {
			System.out.println("File or folder not found"+e.getMessage());
		}
	}
	private static void realWorldCompressorAnalyzer(String[] args) {
		if(args.length != 1)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		final String objectsFolder = args[0];
		
		DataSetArranger data = new DataSetArranger(objectsFolder, objectsFolder+"/"+objectsFolder.substring(objectsFolder.lastIndexOf("/")+1,objectsFolder.lastIndexOf("_"))+".txt");
		List<String> Artists = Arrays.asList("Bach","Chopin","Haydn");
		List<Song> objects = data.getSongsByArtist(Artists);
		try {
			int numObjects = objects.size();
			    
			int def = 4;
				double[][] distanceMatrix = new double[numObjects][numObjects];
				for (int i=0; i<numObjects; i++) {
					for (int j=i+1;j<numObjects;j++) {
						String object1Loc = objectsFolder+"/"+objects.get(i).getFileName();
						String object2Loc = objectsFolder+"/"+objects.get(j).getFileName();
						
						double xy = NCDWithDefinition(def,object1Loc, object2Loc, " 160 ");
						distanceMatrix[i][j] = distanceMatrix[j][i] = xy;
					}
				}
					// we want the matrix to be symmetric
				
				String phylipFileName = objectsFolder+"/phylipDistanceMatrix"+new Integer(def).toString()+".txt";
				File phylipFile = new File(phylipFileName);
				if (phylipFile.exists()) {
					phylipFile.delete();
				}
				phylipFile.createNewFile();
				FileWriter phylipfw = new FileWriter(phylipFile);
				phylipfw.write (new Integer(numObjects).toString());
				phylipfw.write("\n");
				for (int i = 0; i<numObjects; i++) {
					phylipfw.write(objects.get(i).getFileName().substring(0, objects.get(i).getFileName().lastIndexOf("_"))+"_"+String.join("_", objects.get(i).getArtist().split("\\s+")));
					//phylipfw.write(objects.get(i).split("\\.")[0]);
					for (int j=0; j<numObjects; j++) {
						phylipfw.write(" "+String.format("%.5f", distanceMatrix[i][j]));
					}
					phylipfw.write("\n");
				}
				
				phylipfw.close();
				
			
				double[][] coordinates = MDSJ.classicalScaling(distanceMatrix,2);
				String MDSFileName = objectsFolder + "/"+new Integer(2).toString()+"DScalingWithDef"+new Integer(def).toString()+".txt";
				File MDSFile = new File(MDSFileName);
				if(MDSFile.exists()) {
					MDSFile.delete();
				}
				MDSFile.createNewFile();
				FileWriter MDSfw = new FileWriter(MDSFile);
				
				String SVCFileName = objectsFolder + "/SVCWithDef"+new Integer(def).toString()+".txt";
				File SVCFile = new File(SVCFileName);
				if(SVCFile.exists()) {
					SVCFile.delete();
				}
				SVCFile.createNewFile();
				FileWriter SVCfw = new FileWriter(SVCFile);
				
				// Special for DBSCAN Clustering
//				MDSfw.write("[");
//				for (int i=0; i < numObjects; i++) {
//					MDSfw.write("["+Double.toString(coordinates[0][i])+","+Double.toString(coordinates[1][i])+"],");
//				}
//				MDSfw.write("]\n\n[");
//				for(int i=0; i<numObjects; i++) {
//					MDSfw.write("'"+Integer.toString(Artists.indexOf(objects.get(i).getArtist())) +"',");
//				}
//				MDSfw.write("]");
				// By Artist
//					for (String artist: Artists) {
//						MDSfw.write(String.join("_", artist.split("\\s+")));
//						for (int i = 0; i<numObjects; i++) {
//							if (objects.get(i).getArtist().compareTo(artist)==0) {
//								for (int dim=0;dim<2;dim++) {
//									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//								}
//							}
//						}
//						MDSfw.write("\n");
//					}
//				// Format for SVC
//				for (String artist: Artists) {
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getArtist().compareTo(artist)==0) {
//							SVCfw.write("["+new Double(coordinates[0][i]).toString()+","+new Double(coordinates[1][i]).toString()+"],");
//						}
//						
//					}
//					SVCfw.write("\n");
//				}
//				SVCfw.write("\n\n\n\n\n\n[");
//				for (int i=0;i<Artists.size();i++) {
//					for (int j = 0; j<numObjects; j++) {
//						if (objects.get(j).getArtist().compareTo(Artists.get(i))==0) {
//							SVCfw.write(Integer.toString(i)+",");
//						}
//					}
//				}
//				SVCfw.write("]");
				// Individual Artist
					List<String> classification =  Arrays.asList("Noc","Prel");
					for (String group: classification) {
						String groupName = "";
						switch (group) {
						case "WTK":
							groupName = "Wohltemperierte_Klavier";
							break;
						case "Inven":
							groupName = "Invention";
							break;
						case "Son":
							groupName = "Violin_Sonata";
							break;
						case "Etu":
							groupName = "Etudes";
							break;
						case "Prel":
							groupName = "Preludes";
							break;
						case "Maz":
							groupName = "Mazurkas";
							break;
						case "Noc":
							groupName = "Nocturnes";
							break;
						}
						MDSfw.write(groupName);
						for (int i = 0; i<numObjects; i++) {
							if (objects.get(i).getTitle().contains(group)) {
								for (int dim=0;dim<2;dim++) {
									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
								}
							}
						}
						MDSfw.write("\n");
					}
				
				// Format for SVC
				for (String style: classification) {
					for (int i = 0; i<numObjects; i++) {
						if (objects.get(i).getTitle().contains(style)) {
							SVCfw.write("["+new Double(coordinates[0][i]).toString()+","+new Double(coordinates[1][i]).toString()+"],");
						}
						
					}
					SVCfw.write("\n");
				}
				SVCfw.write("\n\n\n\n\n\n[");
				for (int i=0;i<classification.size();i++) {
					for (int j = 0; j<numObjects; j++) {
						if (objects.get(j).getTitle().contains(classification.get(i))) {
							SVCfw.write(Integer.toString(i)+",");
						}
					}
				}
				SVCfw.write("]");
				
				// By individual song
//					for (int i = 0; i<numObjects; i++) {
//						MDSfw.write(objects.get(i).getFileName());
//						for (int dim=0;dim<2;dim++) {
//							MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//						}
//						MDSfw.write("\n");
//					}
//				

					
				// By Year
//					for (Integer decade: Decades) {
//						MDSfw.write(decade.toString()+"s");
//						for (int i = 0; i<numObjects; i++) {
//							if (objects.get(i).getYear()/10==decade.intValue()/10) {
//								for (int dim=0;dim<Dimension;dim++) {
//									MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//								}
//							}
//						}
//						MDSfw.write("\n");
//					}
				
				   
				
				// By Rank
//					
//					MDSfw.write("Rank_Top_100_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()<=100) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
//					MDSfw.write("Rank_100_-_400_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()>100 && objects.get(i).getRank()<=400) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
//					MDSfw.write("Rank_From_400_60s_Songs");
//					for (int i = 0; i<numObjects; i++) {
//						if (objects.get(i).getRank()>400) {
//							for (int dim=0;dim<Dimension;dim++) {
//								MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
//							}
//						}
//					}
//					MDSfw.write("\n");
				
				
				SVCfw.close();
				MDSfw.close();
		} catch (Exception e) {
			System.out.println("File or folder not found"+e.getMessage());
		}
	}
	private static void rockAnalyzer(String[] args, List<String> Artists) {
		if(args.length != 3)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		final String outputFolder = args[0];
		final String vocalPitchFolder = args[1];
		final String melodyPitchFolder = args[2];
		
		
		DataSetArranger vocalData = new DataSetArranger(vocalPitchFolder, vocalPitchFolder+"/"+vocalPitchFolder.substring(vocalPitchFolder.lastIndexOf("/")+1,vocalPitchFolder.lastIndexOf("_"))+".txt");
		DataSetArranger melodyData = new DataSetArranger(melodyPitchFolder, melodyPitchFolder+"/"+melodyPitchFolder.substring(melodyPitchFolder.lastIndexOf("/")+1,melodyPitchFolder.lastIndexOf("_"))+".txt");
		
		List<Song> vocalObjects = vocalData.getSongsByArtist(Artists);
		List<Song> melodyObjects = melodyData.getSongsByArtist(Artists);
		
		if (vocalObjects.size() != melodyObjects.size()) {
			System.out.println("Oops! Sizes don't match.");
			System.exit(1);
		}
		// To have an ordering because we need the 
		// distance matrices to be of the same order
		Collections.sort(vocalObjects);
		Collections.sort(melodyObjects);
		
		// じゃや、そろそろ始めましょうか？
		for (int def = 2; def <= Constants.NumOfNCDDefinitions ; def++) {
			double[][] vocalDistanceMatrix = new double[vocalObjects.size()][vocalObjects.size()];
			for (int i=0; i<vocalObjects.size(); i++) {
				for (int j=i+1;j<vocalObjects.size();j++) {
					String object1Loc = vocalPitchFolder+"/"+vocalObjects.get(i).getFileName();
					String object2Loc = vocalPitchFolder+"/"+vocalObjects.get(j).getFileName();
					// Taking the average of two to maintain symmetry
					// could take maximum too
					double xy = NCDWithDefinition(def,object1Loc, object2Loc, " 100000 ");
//					double yx = NCDWithDefinition(def,object2Loc, object1Loc, " 1000 ");
					vocalDistanceMatrix[i][j] = vocalDistanceMatrix[j][i] = xy;
				}
			}
			double[][] melodyDistanceMatrix = new double[melodyObjects.size()][melodyObjects.size()];
			for (int i=0; i<melodyObjects.size(); i++) {
				for (int j=i+1;j<melodyObjects.size();j++) {
					String object1Loc = melodyPitchFolder+"/"+melodyObjects.get(i).getFileName();
					String object2Loc = melodyPitchFolder+"/"+melodyObjects.get(j).getFileName();
					// Taking the average of two to maintain symmetry
					// could take maximum too
					double xy = NCDWithDefinition(def,object1Loc, object2Loc, " 100000 ");
//					double yx = NCDWithDefinition(def,object2Loc, object1Loc, " 1000 ");
					melodyDistanceMatrix[i][j] = melodyDistanceMatrix[j][i] = xy;
				}
			}
			
			// First let's calculate the L2 norm distance that is d = √(a^2+b^2). Also called the
			// Eucledean distance
			double[][] euclideanDistanceMatrix = new double[vocalObjects.size()][melodyObjects.size()];
			for (int i=0; i<vocalObjects.size(); i++) {
				for (int j=0; j<melodyObjects.size(); j++) {
					euclideanDistanceMatrix[i][j] = Math.sqrt(Math.pow(vocalDistanceMatrix[i][j], 2)+Math.pow(melodyDistanceMatrix[i][j], 2));
				}
			}
			
			double[][] euclideanCoordinates = MDSJ.classicalScaling(euclideanDistanceMatrix,2);
			try {
				String MDSFileName = outputFolder + "/"+"2DEuclidean"+Integer.toString(Artists.size())+"ArtistsWithDef"+Integer.toString(def)+".txt";
				File MDSFile = new File(MDSFileName);
				if(MDSFile.exists()) {
					MDSFile.delete();
				}
				
				MDSFile.createNewFile();
				FileWriter MDSfw = new FileWriter(MDSFile);
				
				
				// By Artist
				for (String artist: Artists) {
					MDSfw.write(String.join("_", artist.split("\\s+")));
					for (int i = 0; i<vocalObjects.size(); i++) {
						if (vocalObjects.get(i).getArtist().compareTo(artist)==0) {
							for (int dim=0;dim<2;dim++) {
								MDSfw.write(" "+new Double(euclideanCoordinates[dim][i]).toString());
							}
						}
					}
					MDSfw.write("\n");
				}
				MDSfw.close();
			} catch (Exception e) {
				System.out.println("Oops something happened.");
				System.exit(1);
			}
			// Now let's see what happens in the Manhattan distance 
			double[][] manhattanDistanceMatrix = new double[vocalObjects.size()][melodyObjects.size()];
			for (int i=0; i<vocalObjects.size(); i++) {
				for (int j=0; j<melodyObjects.size(); j++) {
					manhattanDistanceMatrix[i][j] = vocalDistanceMatrix[i][j]+melodyDistanceMatrix[i][j];
				}
			}
			double[][] manhattanCoordinates = MDSJ.classicalScaling(manhattanDistanceMatrix,2);
			try {
				String MDSFileName = outputFolder + "/"+"2DManhattan"+Integer.toString(Artists.size())+"ArtistsWithDef"+Integer.toString(def)+".txt";
				File MDSFile = new File(MDSFileName);
				if(MDSFile.exists()) {
					MDSFile.delete();
				}
				
				MDSFile.createNewFile();
				FileWriter MDSfw = new FileWriter(MDSFile);
				
				
				// By Artist
				for (String artist: Artists) {
					MDSfw.write(String.join("_", artist.split("\\s+")));
					for (int i = 0; i<vocalObjects.size(); i++) {
						if (vocalObjects.get(i).getArtist().compareTo(artist)==0) {
							for (int dim=0;dim<2;dim++) {
								MDSfw.write(" "+new Double(manhattanCoordinates[dim][i]).toString());
							}
						}
					}
					MDSfw.write("\n");
				}
				MDSfw.close();
			} catch (Exception e) {
				System.out.println("Oops something happened.");
				System.exit(1);
			}
			
			// Now let's try with projecting each type of distance to one coordinate
			double[][] xCoordinates = MDSJ.classicalScaling(vocalDistanceMatrix,1);
			double[][] yCoordinates = MDSJ.classicalScaling(melodyDistanceMatrix,1);
			try {
				String MDSFileName = outputFolder + "/"+"2DProjections"+Integer.toString(Artists.size())+"ArtistsWithDef"+Integer.toString(def)+".txt";
				File MDSFile = new File(MDSFileName);
				if(MDSFile.exists()) {
					MDSFile.delete();
				}
				
				MDSFile.createNewFile();
				FileWriter MDSfw = new FileWriter(MDSFile);
				// By Artist
				for (String artist: Artists) {
					MDSfw.write(String.join("_", artist.split("\\s+")));
					for (int i = 0; i<vocalObjects.size(); i++) {
						if (vocalObjects.get(i).getArtist().compareTo(artist)==0) {
							MDSfw.write(" "+Double.toString(xCoordinates[0][i])+" "+Double.toString(yCoordinates[0][i]));
						}
					}
					MDSfw.write("\n");
				}
				MDSfw.close();
				
				
			} catch (Exception e) {
				System.out.println("Oops something happened.");
				System.exit(1);
			}
		}
		
	}
	private static void DNAAnalyzer(String[] args) {
//		if(args.length != 1)
//	    {
//	    	System.err.println("Incorrect Parameters Passed!\n");
//		    System.exit(1);
//	    }
//		final String objectsFolder = args[0];
//		
//		try {
//			List<String> objects = new ArrayList<String>();
//			File folder = new File(objectsFolder);  
//			File[] listOfFiles = folder.listFiles();
//			for (File file : listOfFiles) {
//				if (file.isFile()) {
//					objects.add(file.getName());
//				}
//			}
//			int numObjects = objects.size();
//			    
//			for (int def = 2; def <= Constants.NumOfNCDDefinitions ; def++) {
//			
//				double[][] distanceMatrix = new double[numObjects][numObjects];
//				for (int i=0; i<numObjects; i++) {
//					for (int j=i+1;j<numObjects;j++) {
//						String object1Loc = objectsFolder+"/"+objects.get(i);
//						String object2Loc = objectsFolder+"/"+objects.get(j);
//						// Taking the average of two to maintain symmetry
//						// could take maximum too
//						double xy = NCDWithDefinition(def,object1Loc, object2Loc,"o");
//						double yx = NCDWithDefinition(def,object2Loc, object1Loc,"o");
//						distanceMatrix[i][j] = distanceMatrix[j][i] = (xy+yx)/2;
//					}
//				}
//					// we want the matrix to be symmetric
//				
//				String phylipFileName = objectsFolder+"/phylipDistanceMatrix"+new Integer(def).toString()+".txt";
//				File phylipFile = new File(phylipFileName);
//				if (phylipFile.exists()) {
//					phylipFile.delete();
//				}
//				phylipFile.createNewFile();
//				FileWriter phylipfw = new FileWriter(phylipFile);
//				phylipfw.write (new Integer(numObjects).toString());
//				phylipfw.write								``("\n");
//				for (int i = 0; i<numObjects; i++) {
//					phylipfw.write(objects.get(i));
//					//phylipfw.write(objects.get(i).split("\\.")[0]);
//					for (int j=0; j<numObjects; j++) {
//						phylipfw.write(" "+String.format("%f", distanceMatrix[i][j]));
//					}
//					phylipfw.write("\n");
//				}
//				
//				phylipfw.close();
//				
//			}
//			
//			
//		} catch (Exception e) {
//			System.out.println("File or folder not found"+e.getMessage());
//		}
//		
	}
	public static void densityAnalyzer(String[] args) {
		if(args.length != 2)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		final String referenceTrackLoc = args[0];
		final String objectsFolder = args[1];
		try {
			List<String> objects = new ArrayList<String>();
			File folder = new File(objectsFolder);  
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().contains(".pitch")) {
					objects.add(file.getName());
				}
			}
			int def = 2;
			int numObjects = objects.size();
			double[][] distanceMatrix = new double[numObjects+1][numObjects+1];
			for (int i=0; i<numObjects+1; i++) {
				for (int j=i+1;j<numObjects+1;j++) {
					distanceMatrix[i][j] = distanceMatrix[j][i] = 0.2;
				}
			}
			for (int i=0; i<numObjects; i++) {
					String yLoc = objectsFolder+"/"+objects.get(i);
					// Taking the average of two to maintain symmetry
					// could take maximum too
					double xy = NCDWithDefinition(def,referenceTrackLoc, yLoc," 1000 ");
					double yx = NCDWithDefinition(def,yLoc, referenceTrackLoc," 1000 ");
					distanceMatrix[i+1][0] = distanceMatrix[0][i+1] = (xy+yx)/2;
			}
			String MDSFileName = objectsFolder + "/DensityWithDef"+new Integer(def).toString()+".txt";
			File MDSFile = new File(MDSFileName);
			if(MDSFile.exists()) {
				MDSFile.delete();
			}
			MDSFile.createNewFile();
			FileWriter MDSfw = new FileWriter(MDSFile);
			double[][] coordinates = MDSJ.classicalScaling(distanceMatrix,2);			
			
				MDSfw.write("Reference_Track");
				for (int i = 0; i<numObjects+1; i++) {
					if (Double.compare(distanceMatrix[0][i], 0)==0) {
						for (int dim=0;dim<2;dim++) {
							MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
						}
					}
				}
				
				MDSfw.write("\n");
				MDSfw.write("Inside_The_Ball");
				for (int i = 0; i<numObjects+1; i++) {
					if (Double.compare(distanceMatrix[0][i], 0.85) < 0) {
						for (int dim=0;dim<2;dim++) {
							MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
						}
					}
				}
				
				MDSfw.write("\n");

				MDSfw.write("Outside_The_Ball");
				for (int i = 0; i<numObjects+1; i++) {
					if (Double.compare(distanceMatrix[0][i], 0.85) > 0) {
						for (int dim=0;dim<2;dim++) {
							MDSfw.write(" "+new Double(coordinates[dim][i]).toString());
						}
					}
				}
				
				MDSfw.write("\n");

			
			MDSfw.close();
		}catch (Exception e) {
			System.out.println("File or folder not found"+e.getMessage());
		}
		
	}
	
	
	public static void composerClassification(String[] args) {
		// Let the args be of the form:
		// [Folder with files to be classified] [corpora 1] [corpora 2] ..... [corpora n]
		// So there is a folder of objects to be classified
		if(args.length != 1)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		final String objectsFolder = args[0];
		final String cetegoriesFileLoc = objectsFolder + "/categories.txt";
		//System.out.println(cetegoriesFileLoc);
		try {
			List<String> categories = Files.readAllLines(Paths.get(cetegoriesFileLoc));
			//String[] categories = (String[]) Files.readAllLines(Paths.get(cetegoriesFileLoc)).toArray();
			Grammar[] characteristicGrammars = new Grammar[categories.size()];
			for (int i = 0;i < categories.size(); i++) {
				String corpus = objectsFolder+ "/"+categories.get(i);
				characteristicGrammars[i] = ModelContemplation.ConstrainedMaximumLikelyModel(corpus, 20);
				System.out.println(characteristicGrammars[i].toString(true));
			}
			String uncategorizedCorpus = objectsFolder+ "/Unknown";
			File folder = new File(uncategorizedCorpus);
			File[] listOfFiles = folder.listFiles();
			
			for (File file: listOfFiles) {
				if (file.getAbsolutePath().contains(".pitch")) {
					String uncategorizedString = null;
					try {
						uncategorizedString = read(new FileReader(file.getAbsolutePath()));
			        }         
			        catch(FileNotFoundException e1)
			        {
			            System.out.println("file not found \""+file.getAbsolutePath()+"\" !");
			            System.exit(1);
			        } catch (IOException e2) {
						System.out.println(e2.getMessage());
						System.exit(1);
					}
					// We first take note of the individual complexity and regard it as
					// a canonical upper-bound of complexity.
					// If none of the model can significantly reduce this complexity
					// The category of the unknown string is not among our contemplated model
					// and the individual grammar is the best description of the string.
					double typicalityDeficiency = Double.POSITIVE_INFINITY;
					String categoryString = "Unknown";
					//double dataToModelLength = ModelContemplation.getSmallestCFG(uncategorizedString).KolmogorovComplexity();
					for (int i = 0;i < categories.size(); i++) {
						Grammar compressed = ModelContemplation.getSmallestCFG(uncategorizedString, characteristicGrammars[i]);
						System.out.println(compressed.toString(true));
						double temp = compressed.KolmogorovComplexity();
						if (temp < typicalityDeficiency) {
							typicalityDeficiency = temp;
							categoryString = categories.get(i);
						}
					}
					System.out.println(file.getAbsolutePath()+"-"+categoryString);
				}
			}
		}catch (Exception e) {
			System.out.println("File or folder not found"+e.getMessage());
		}
	}
//	public static void listSongNames(String[] args) {
//		if(args.length != 1)
//	    {
//	    	System.err.println("Incorrect Parameters Passed!\n");
//		    System.exit(1);
//	    }
//		final String objectsFolder = args[0];
//		
//		DataSetArranger data = new DataSetArranger(objectsFolder, objectsFolder+"/"+objectsFolder.substring(objectsFolder.lastIndexOf("/")+1,objectsFolder.lastIndexOf("_"))+".txt");
//		List<String> Artists = Arrays.asList("Bach","Chopin","Beethoven","Haydn","Mozart");
//		List<Song> objects = data.getSongsByArtist(Artists);
//		for (String artist: Artists) {
//			
//		}
//	}
	public static void compressionRatio(String[] args) {
		if(args.length != 1)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		File folder = new File(args[0]);
		File[] listOfFolder = folder.listFiles();
		for (File corpus : listOfFolder) {
			if (corpus.isDirectory()) {
				File[] listOfObjects = corpus.listFiles();
				double average = 0.0;
				for (File object: listOfObjects) {
					if (object.isFile()) {
						try {
							String origString = read(new FileReader(object));
							Grammar smallest = getSmallestCFG(origString);
							double compressionRatio = (smallest.KolmogorovComplexity())/(Math.log(smallest.getSigmaSize())*smallest.getOriginalLength());
							average += compressionRatio;
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				average = average / listOfObjects.length;
				System.out.println(corpus.getName()+"\t\t:"+Double.toString(average));
			}
		}
	}
	public static void interIntraCorporaDistance(String[] args) {
		// Average Intra Corpora distance in Classical, vocal rock and jazz
		// Average inter corpora distance Classical - Rock, Rock Jazz, Classical Jazz
		if(args.length != 1)
	    {
	    	System.err.println("Incorrect Parameters Passed!\n");
		    System.exit(1);
	    }
		File folder = new File(args[0]);
		File[] listOfFolder = folder.listFiles();
		// First Intra corpora distance
//		for (File corpus: listOfFolder) {
//			if (corpus.isDirectory()) {
//				File[] listOfObjects = corpus.listFiles();
//				double average = 0.0;
//				for (int i=0; i<listOfObjects.length; i++) {
//						if (listOfObjects[i].isFile()) {
//							try {				
//								for (int j=i+1;j<listOfObjects.length;j++) {
//									if(listOfObjects[j].isFile() ) {								
//										average += NCDWithDefinition(2, listOfObjects[i].getAbsolutePath(), listOfObjects[j].getAbsolutePath(), " 100000 ");
//									}
//								}
//							} catch (Exception e) {
//								// TODO: handle exception
//							}
//					}
//				}
//				average = (average*2)/(listOfObjects.length*(listOfObjects.length-1));
//				System.out.println("Intra corpora distance of "+corpus.getName()+"\t\t:"+Double.toString(average));
//			}
//		}
//		
		// Then inter corpora distance
		for (int i =0; i<listOfFolder.length; i++) {
			if (listOfFolder[i].isDirectory()) {
				File[] listOfObjectsGenrei = listOfFolder[i].listFiles();
				for (int j=i+1;j<listOfFolder.length;j++) {
					if (listOfFolder[j].isDirectory()) {
						double average = 0.0;
						File[] listOfObjectsGenrej = listOfFolder[j].listFiles();
						for (int k=0; k<listOfObjectsGenrei.length;k++) {
							if (listOfObjectsGenrei[k].isFile()) {
								for (int l=0;l<listOfObjectsGenrej.length;l++) {
									if (listOfObjectsGenrej[l].isFile()) {
										try {
											average += NCDWithDefinition(2, listOfObjectsGenrei[k].getAbsolutePath(), listOfObjectsGenrej[l].getAbsolutePath(), " 100000 ");
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}
							}
						}
						average = average / (listOfObjectsGenrei.length*listOfObjectsGenrej.length);
						System.out.println(listOfFolder[i].getName()+"-"+listOfFolder[j].getName()+"\t\t:"+Double.toString(average));
					}
					
				}
				
			}
		}
		
	}
	
	public static void main(String[] args) {
		generalAnalyzer(args);
		//realWorldCompressorAnalyzer(args);
		//densityAnalyzer(args);
		//rockAnalyzer(args,Arrays.asList("The Beatles", "The Rolling Stones", "Elvis Presley", "Prince"));
		//rockAnalyzer(args,Arrays.asList("The Beatles", "The Rolling Stones"));
		//DNAAnalyzer(args);
		//composerClassification(args);
		//listSongNames(args);
		//compressionRatio(args);
		//interIntraCorporaDistance(args);
	}
}
