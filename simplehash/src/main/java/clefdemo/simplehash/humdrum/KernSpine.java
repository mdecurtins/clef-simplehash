package clefdemo.simplehash.humdrum;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedList;

/**
 * This class provides functionality for storing and retrieving data related to 
 * encodings of data in Humdrum files, which are called spines.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class KernSpine {
	
	public static final String TERMINATOR = "*-";

	private LinkedList<String> data;
	private int index;
	private String name;
	
	public KernSpine() {
		this.data = new LinkedList<String>();
	}
	
	/**
	 * Adds a token to this instance.
	 * 
	 * @since 1.0.0
	 * @param token
	 */
	public void addToken( String token ) {
		this.data.add( token );
	}

	
	/**
	 * Applies filters to the raw token data of this spine.
	 * 
	 * This method first removes measure delimiters and then strips out all characters except those 
	 * representing pitch and rhythm information. A more flexible future implementation could take varargs 
	 * representing callable filter functions and apply them in the order passed.
	 * 
	 * @since 1.0.0
	 * @return a list of filtered Kern string tokens
	 */
	public List<String> applyFilters() {
	
		// Remove meaure delimiters from this spine's list of tokens.
		List<String> noMeasures = this.data.stream().filter( token -> !Filters.isMeasureDelimiter(token) ).collect( Collectors.toList() );
		
		// Strip out everything but pitch and rhythm information. Will delete beaming, articulations, annotations, etc.
		List<String> justTheNotes = noMeasures.stream().map( token -> Filters.stripDisallowedChars(token) ).collect( Collectors.toList() );
		
		justTheNotes = justTheNotes.stream().filter( token -> !Filters.isNullToken(token) ).collect( Collectors.toList() );
		
		return justTheNotes;
	}
	
	
	/**
	 * Gets the raw Kern tokens of this spine, unfiltered.
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public LinkedList<String> getData() {
		return this.data;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public int indexInFile() {
		return this.index;
	}
	
	
	/**
	 * Returns the number of tokens belonging to this spine.
	 * 
	 * N.B. This number represents the raw data.
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public int numTokens() {
		return this.data.size();
	}
	
	
	/**
	 * Allows the raw data for this spine to be set all at once, if desired.
	 * 
	 * @since 1.0.0
	 * @param data
	 */
	public void setData( LinkedList<String> data ) {
		this.data = data;
	}
	
	
	/**
	 * Sets this spine's index in the Humdrum file within which it appears.
	 * 
	 * @since 1.0.0
	 * @param i the index in the collection 
	 */
	public void setIndex( int i ) {
		this.index = i;
	}
	
	
	/**
	 * Set the name of this KernSpine.
	 * 
	 * Assumes that the string passed is taken from an instrument class signifier, e.g. *Iviola
	 * 
	 * @since 1.0.0
	 * @param n the string to set as the name.
	 */
	public void setName( String n ) {
		this.name = Filters.filterInstrumentClass( n );
	}
	
	
}
