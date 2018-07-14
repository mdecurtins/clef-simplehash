package clefdemo.simplehash.humdrum;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;
import java.util.HashMap;

/**
 * Class representing a source file of symbolic music data in Humdrum format.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class KernFile {

	private Map<Integer, KernSpine> spines;
	private KernMetadata meta;
	
	
	/**
	 * @since 1.0.0
	 */
	public KernFile() {
		this.spines = new HashMap<Integer, KernSpine>();
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public KernMetadata getMetadata() {
		return this.meta;
	}
	
	
	/**
	 * Gets the spines in this KernFile.
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public Map<Integer, KernSpine> getSpines() {
		return this.spines;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public int numSpines() {
		return this.spines.size();
	}
	
	
	/**
	 * Parses source file at {@code path}.
	 * 
	 * @since 1.0.0
	 * @param path the file to parse
	 * @return a new instance of KernFile
	 */
	public static KernFile parse( Path path ) {
		
		KernFile kf = new KernFile();
		
		KernMetadata km = new KernMetadata();
		
		Map<Integer, KernSpine> spineMap = new HashMap<Integer, KernSpine>();
		
		// Until this is set to true, lines from the Humdrum file are assumed not to represent music data.
		boolean isEventData = false;
		
		try {
			
			String[] spinesArr = null;
			
			for ( String line : Files.readAllLines( path, StandardCharsets.ISO_8859_1 ) ) {
				
				// If we've reached the end of the music data.
				if ( line.contains( KernSpine.TERMINATOR ) ) {
					break;
				}
				
				// If line starts with !!! turn it into metadata
				if ( line.startsWith( "!!!" ) ) {
					// Do metadata
					String[] meta = line.split( ":" );
					String metakey = meta[0].replaceAll( "!", "" );
					String metaval = meta[1].trim();
					km.setMeta( metakey, metaval );
					continue;
				}
						
				// Determine total number of spines and index of **kern spines
				if ( line.contains( "**kern" ) ) {
					spinesArr = line.split( "\\s+" );
					
					if ( spinesArr != null && spinesArr.length > 0 ) {
						for ( int i = 0; i < spinesArr.length; i++ ) {
							
							// Create a new Kern spine.
							if ( spinesArr[i].equals( "**kern" )  ) {
								
								KernSpine spine = new KernSpine();
								
								// Set this spine's index in the Kern file. This is purely to ensure that new tokens are assigned properly.
								spine.setIndex( i );
								
								// Add this spine to the working set of spines.
								spineMap.put( i, spine );
							}
							
						}
					}
					
					continue;
				}
				
				// Assign instrument classes to the spines.
				if ( line.contains( "*I" ) ) {
					String[] tokens = line.split( "\\s+" );
					if ( tokens.length > 0 ) {
						for ( int i = 0; i < tokens.length; i++ ) {
							if ( Filters.isInstrumentClass( tokens[i] ) ) {
								// Update the KernSpine and replace it in the spine map.
								KernSpine ks = spineMap.get( i );
								if ( ks != null ) {
									ks.setName( tokens[i] );
									spineMap.put( i, ks );
								}
							}
						}
					}				
					continue;
				}
				
				// Begin collecting music event data.
				if ( isEventData == false && ! Filters.isInterpretation( line, spinesArr.length ) ) {
					isEventData = true;
				}
							
				// Add musical events to each spine, according to the index of the current event.
				if ( isEventData ) {
					String[] tokens = line.split( "\\s+" );
					if ( tokens.length > 0 ) {
						for ( int i = 0; i < tokens.length; i++ ) {
							KernSpine ks = spineMap.get( i );
							// Add the token to the KernSpine and update the spine map.
							if ( ks != null ) {
								ks.addToken( tokens[i] );
								spineMap.put( i, ks );
							}
						}
					}				
				}			
			}
			
			// Assign the fully-initialized spines to this Kern file.
			kf.setSpines( spineMap );
			
			// Assign the metadata instance to this Kern file.
			kf.setMetadata( km );
			
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
		
		return kf;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param km a KernMetadata instance
	 */
	public void setMetadata( KernMetadata km ) {
		this.meta = km;
	}
	
	
	/**
	 * Sets KernSpine instances for this KernFile.
	 * 
	 * @since 1.0.0
	 * @param spines
	 */
	public void setSpines( Map<Integer, KernSpine> spines ) {
		this.spines = spines;
	}
	
	
	
}
