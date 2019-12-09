
public class Song implements Comparable<Song>{
	private String Title;
	private String FileName;
	private String Artist;
	private int Year;
	private int Rank;
	public Song(String t, String f, String a, int y, int r) {
		this.Title = t;
		this.FileName = f;
		this.Artist = a;
		this.Year = y;
		this.Rank = r;
	}
	public String getArtist() {
		return this.Artist;
	}
	public String getFileName() {
		return this.FileName;
	}
	public int getYear() {
		return this.Year;
	}
	public int getRank() {
		return this.Rank;
	}
	public String getTitle() {
		return this.Title;
	}
	@Override
	public String toString() {
		return this.Title + "\t\t" + Integer.toString(this.Rank) +"\t\t"+this.FileName+"\t\t" + this.Artist + "\t\t" + Integer.toString(this.Year);
	}
	@Override
	public int compareTo(Song other) {
		return this.Title.compareToIgnoreCase(other.getTitle());
	}
	
}
