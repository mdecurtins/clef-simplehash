package clefdemo.simplehash.humdrum;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * This class contains methods for generating n-grams and hashing them.
 * 
 * A future implementation may use n-gram objects that hold the start and end index of the 
 * n-gram in the raw Humdrum source data, in addition to the n-gram tokens and their 
 * hashed value.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class KernHasher {


	/**
	 * Hashes the given list of {@code tokens}.
	 * 
	 * @since 1.0.0
	 * @param tokens
	 * @return the hashed value
	 */
	public int hash( List<String> tokens ) {
		return Objects.hash( tokens );
	}
	
	
	/**
	 * Produces n-grams of size {@code n} for the given list {@code tokens}.
	 * 
	 * @since 1.0.0
	 * @param tokens the list of tokens that comprises an n-gram
	 * @param n the size of the n-gram to generate
	 * @return a list of token lists, each sub-list comprises a single n-gram
	 */
	public List<List<String>> ngrams( List<String> tokens, int n ) {
		
		List<List<String>> ngrams = new ArrayList<List<String>>();
		
		// Window through the data in increments of 1 token.
		for ( int i = 0; i < ( tokens.size() - n + 1 ); i++ ) {
			List<String> window = new LinkedList<String>();
			for ( int j = i; j < ( i + n ); j++ ) {
				if ( tokens.get(j) != null ) {
					window.add( tokens.get(j) );
				}
			}
			ngrams.add( window );
		}
		
		return ngrams;
		
	}
	
	
	/**
	 * Converts the given tokens to a single string.
	 * 
	 * @since 1.0.0
	 * @param ngram the list of tokens that comprises an n-gram
	 * @return the tokens joined as a single string
	 */
	public String ngramToString( List<String> ngram ) {
		return String.join( "", ngram );
	}
	
	
	/**
	 * Creates a formatted string containing the n-gram and its hashed value.
	 * 
	 * @since 1.0.0
	 * @param ngram the list of tokens that comprises an n-gram
	 * @param hash the hashed value of the tokens in {@code ngram}
	 * @return a formatted string for debugging/logging
	 */
	public String printTokensAndHash( List<String> ngram, int hash ) {
		String joined = String.join( ", ", ngram );
		return String.format( "n-gram: [ %s ]\t\thash: %d\n", joined, hash );
	}
}
