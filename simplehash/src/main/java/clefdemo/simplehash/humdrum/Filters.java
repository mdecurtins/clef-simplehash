package clefdemo.simplehash.humdrum;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class contains methods for filtering and manipulating Humdrum kern tokens.
 * 
 * @author Max DeCurtins
 * 
 * @since 1.0.0
 * @see https://csml.som.ohio-state.edu/Humdrum/representations/kern.html
 */
public class Filters {

	/**
	 * Regex pattern representing the characters that will be filtered from final output.
	 * 
	 * By default, this pattern specifies the inverse of the set of allowed characters.
	 */
	private static String disallowed = "[^A-Ga-g0-9\\-#rn\\.]+";
	
	
	/**
	 * Filters a spine instrument name.
	 * 
	 * Strips out the uppercase "I" that denotes an instrument class.
	 * 
	 * @since 1.0.0
	 * @param token
	 * @return
	 */
	public static String filterInstrumentClass( String token ) {
		return token.replaceAll( "[^A-HJ-Za-z0-9]+", "" );
	}
	
	
	/**
	 * Gets the expression representing disallowed characters.
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public static String getFilterExpression() {
		return disallowed;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param token
	 * @return
	 */
	public static boolean isMeasureDelimiter( String token ) {
		return matchesPattern( "=+([0-9a-z]*(\\|)?:?(\\|){0,2})([;:!'`\\-])?", token );
	}
	
	
	/**
	 * Determines whether or not the token passed represents a Kern interpretation.
	 * 
	 * Most commonly, the token will be an entire line from a Kern file. It is, however, possible to pass 
	 * a token from a single spine; simply call this method as {@code isInterpretation( token, 1 )}.
	 * 
	 * Note that in order to match exactly an entire line, you must pass the number of spines in the file, as 
	 * this number is merged into the regular expression before compiling the pattern and executing it 
	 * against the given line.
	 * 
	 * @since 1.0.0
	 * @param token a line from a Kern file, containing one or more tab-delimited spines.
	 * @param numSpines the number of spines in this Kern file.
	 * @return TRUE if the entire token represents an interpretation, FALSE otherwise.
	 */
	public static boolean isInterpretation( String token, int numSpines ) {
		String pattern = String.format( "(\\*+[a-zA-Z0-9:\\[\\]/#-]*\\s*){%d}", numSpines );
		return matchesPattern( pattern, token );
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param token
	 * @return
	 */
	public static boolean isInstrumentClass( String token ) {
		return matchesPattern( "\\*I([^\\s]+)", token );
	}
	
	
	/**
	 * In Kern, a null token is represented with a single period "."
	 * 
	 * @since 1.0.0
	 * @param token the Kern token to check
	 * @return true if token matches null token pattern, false otherwise
	 */
	public static boolean isNullToken( String token ) {
		return matchesPattern( "^\\.$", token );
	}
	
	
	/**
	 * Identifies a rest token in Kern.
	 * 
	 * @since 1.0.0
	 * @param token the Kern token to check
	 * @return true if token matches rest token pattern, false otherwise
	 */
	public static boolean isRestToken( String token ) {
		return matchesPattern( "\\d+\\.*r+", token );
	}
	
	
	/**
	 * Identifies a tandem interpretation token in Kern.
	 * 
	 * N.B. Currently depends on negative lookahead for **kern and **silbe representations only.
	 * 
	 * @since 1.0.0
	 * @see https://csml.som.ohio-state.edu/Humdrum/representations/kern.html#Tandem%20Interpretations
	 * @param token the Kern token to check
	 * @return
	 */
	public static boolean isTandemInterpretation( String token ) {
		return matchesPattern( "\\*{1}(?!I)(?!\\*)(?!kern|silbe)(clef[a-zA-Z]+[0-9]|k\\[[a-zA-Z#\\-]*\\]|M\\d{1,}\\/\\d{1,}|[a-gA-G]:|met\\([a-z]\\)|M{2}[0-9]*)?\\s*", token );
	}
	
	
	/**
	 * Utility method to check a regular expression against the given token.
	 *
	 * @since 1.0.0
	 * @param regex the regular expression against which the token should be matched
	 * @param token the token to match
	 * @return true if {@code token} matches {@code regex}, false otherwise
	 */
	private static boolean matchesPattern( String regex, String token ) {
		Pattern pattern = Pattern.compile( regex );
		Matcher matcher = pattern.matcher( token );
		
		return matcher.matches();
	}
	
	
	
	/**
	 * Partitions a List collection in a Collection of sub-lists of a given size.
	 * 
	 * N.B. Some sub-lists may be smaller than {@code size} due to remainders.
	 * 
	 * @since 1.0.0
	 * @param list a list of objects of type T to partition
	 * @param size the desired size of each sub-list
	 * @return a collection of lists of type T
	 */
	public static <T> Collection<List<T>> partition( List<T> list, int size ) {
		 
		final AtomicInteger counter = new AtomicInteger(0);
		
		return list.stream().collect( Collectors.groupingBy( it -> counter.getAndIncrement() / size ) ).values();
	}
	
	
	/**
	 * Filters the given list of string tokens to remove Kern null tokens.
	 * 
	 * @since 1.0.0
	 * @param tokens a list of Kern tokens to filter
	 * @return a list of tokens without Kern null tokens
	 */
	public static List<String> removeNullTokens( List<String> tokens ) {
		return tokens.stream().filter( token -> !isNullToken(token) ).collect( Collectors.toList() );
	}
	
	
	/**
	 * Sets the filter expression for this class to use.
	 * 
	 * Replaces the default set of disallowed characters with the set in {@code regex}.
	 * 
	 * @since 1.0.0
	 * @param regex a regular expression representing the characters to remove when filtering tokens.
	 */
	public static void setFilterExpression( String regex ) {
		disallowed = regex;
	}
	
	
	/**
	 * Replaces all instances of disallowed characters from {@code input}.
	 * 
	 * @since 1.0.0
	 * @param input the input from which to strip disallowed characters
	 * @return a string without the disallowed characters
	 */
	public static String stripDisallowedChars( String input ) {
		return input.replaceAll( disallowed, "" );
	}
	
	
	/**
	 * Trims any trailing rest tokens from a given list of Humdrum tokens.
	 * 
	 * N.B. This method is intended mostly for cleaning up input to the simplehash program, rather than for cleaning sources of 
	 * symbolic music data in Humdrum format.
	 * 
	 * @since 1.0.0
	 * @param tokens a list of Humdrum tokens
	 * @return a list of Humdrum tokens with any trailing rest tokens removed
	 */
	public static List<String> trim( List<String> tokens ) {
		
		// Start with the last token of the list.
		int lastNote = tokens.size() - 1;
		
		// Work backwards through the list until a non-rest token is encountered.
		while ( Filters.isRestToken( tokens.get( lastNote ) ) ) {
			lastNote--;
		}
		
		// Prevent the last non-rest token from being chopped off.
		if ( lastNote < ( tokens.size() - 1 ) ) {
			lastNote++;
		}
		
		// Use the maybe-modified index to return a sublist of the original. 
		// (N.B. If the last token is indeed a note, then tokens is returned unmodified.)
		return tokens.subList( 0, lastNote );
	}
	
}
