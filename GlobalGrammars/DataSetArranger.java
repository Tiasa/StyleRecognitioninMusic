import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.stream.Stream;
import java.util.List;
//import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
//import static java.util.stream.Collectors.*;
public class DataSetArranger {
	List<Song> dataset = null;
	//Map<String, Integer> artistList = null;
	
	// Call this one time 
	public DataSetArranger(String objectsFolderLoc, String referenceFileLoc) {
		// Args will have folder location of the pitch files
		// and the text file location of the reference file
		List<String> convertedSongFileNames = new ArrayList<String>();
		this.dataset = new ArrayList<Song>();
		//this.artistList = new HashMap<String, Integer>();
		File folder = new File(objectsFolderLoc);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(".pitch")) {
				// First add the file name as in the naming convention
				convertedSongFileNames.add(file.getName().substring(0, file.getName().lastIndexOf("_")));
				// Then add the name of the analyst
				convertedSongFileNames.add(file.getName().substring(file.getName().lastIndexOf("_")+1));
			}
		}
		try {
			List<String> allLines = Files.readAllLines(Paths.get(referenceFileLoc));
			for (String line : allLines) {
				String[] songDetails = line.trim().split("\\s+");
				int index = convertedSongFileNames.indexOf(songDetails[0]);
				if (index !=  -1) {
					int songNameLength = songDetails[0].split("_").length;
					int offset = 0;
					if (songDetails[songDetails.length-1].compareToIgnoreCase("*")==0) {
						offset = 1;
					}
					String title = String.join(" ", Arrays.copyOfRange(songDetails, 2, 2+songNameLength));
					String fileName = convertedSongFileNames.get(index)+"_"+convertedSongFileNames.get(index+1);
					String artist = String.join(" ", Arrays.copyOfRange(songDetails, 2+songNameLength, songDetails.length-offset-1));
					int year = Integer.parseInt(songDetails[songDetails.length-offset-1]);
					int rank = Integer.parseInt(songDetails[1]);
					this.dataset.add(new Song(title,fileName,artist,year,rank));
//					if (this.artistList.containsKey(artist)) {
//						this.artistList.put(artist, new Integer(this.artistList.get(artist).intValue()+1));
//					} else {
//						this.artistList.put(artist, new Integer(1));
//					}
				}
			}
//			this.artistList = this.artistList.entrySet()
//								.stream()
//								.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//								.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
//						                LinkedHashMap::new));
//					
//			System.out.println(this.artistList);
		} catch(IOException e) {
			e.printStackTrace();
		}

	}
	public List<Song> getSongsByArtist(List<String> artistList) {
		List<Song> result = new ArrayList<Song>();
		for (String artist: artistList) {
			//System.out.println(artist);
			Stream<Song> songsOfArtist = dataset.stream().filter(s -> s.getArtist().compareToIgnoreCase(artist)==0);
			songsOfArtist.forEach(s -> {
				//System.out.println(s);
				result.add(s);
				});
		}
		return result;
	}
	
	public List<Song> getSongsByDecade(List<Integer> decadeList) {
		List<Song> result = new ArrayList<Song>();
		for(Integer decade: decadeList) {
			Stream<Song> songsOfDecade = dataset.stream().filter(s -> (s.getYear()/10)== (decade.intValue()/10));
			songsOfDecade.forEach(s -> {
				result.add(s);
				});
		}
		return result;
	}
	public List<Song> getSongsForRankAnalysis() {
		List<Song> result = new ArrayList<Song>();
		Stream<Song> songsOfDecade = dataset.stream().filter(s -> s.getRank()<=100 );
		songsOfDecade.forEach(s -> {
			System.out.println(s);
			result.add(s);
			});
		return result;
	}
}
