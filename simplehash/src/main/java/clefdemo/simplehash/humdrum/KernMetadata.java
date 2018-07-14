package clefdemo.simplehash.humdrum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class stores metadata information parsed from a Humdrum file.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class KernMetadata {

	private String catalog;
	private String catalogNumber;
	private String collectionName;
	private String composer;
	private String composerBorn;
	private String composerDied;
	private String title;
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getCatalog() {
		return catalog;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getCatalogNumber() {
		return catalogNumber;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getCollectionName() {
		return collectionName;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getComposer() {
		return composer;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getComposerBorn() {
		return composerBorn;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getComposerDied() {
		return composerDied;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param c
	 */
	public void setCatalog( String c ) {
		this.catalog = c;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param both
	 */
	public void setCatalogNameAndNumber( String both ) {
		String[] parts = both.split( " " );
		this.setCatalog( parts[0] );
		this.setCatalogNumber( parts[1] );
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param cn
	 */
	public void setCatalogNumber( String cn ) {
		this.catalogNumber = cn;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param colname
	 */
	public void setCollectionName( String colname ) {
		this.collectionName = colname;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param com
	 */
	public void setComposer( String com ) {
		this.composer = com;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param range
	 */
	public void setComposerDates( String range ) {
		Pattern pattern = Pattern.compile( "(\\d{4})" );
		Matcher matcher = pattern.matcher( range );
		
		if ( matcher.matches() && matcher.groupCount() > 1 ) {
			if ( matcher.group(1) != null ) {
				this.composerBorn = matcher.group( 1 );
			}
			
			if ( matcher.group(2) != null ) {
				this.composerDied = matcher.group( 2 );
			}
		}
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param key
	 * @param val
	 */
	public void setMeta( String key, String val ) {
		switch ( key ) {
		case "SCT":
			this.setCatalogNameAndNumber( val );
			break;
		case "XEN":
			this.setCollectionName( val );
			break;
		case "COM":
			this.setComposer( val );
			break;
		case "CDT":
			this.setComposerDates( val );
			break;
		case "OTL":
			this.setTitle( val );
			break;
		}
	}
	
	/**
	 * 
	 * @since 1.0.0
	 * @param t
	 */
	public void setTitle( String t ) {
		this.title = t;
	}
	
}
