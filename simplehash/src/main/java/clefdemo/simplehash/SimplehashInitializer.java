package clefdemo.simplehash;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import clefdemo.simplehash.db.Database;
import clefdemo.simplehash.db.HashRecord;
import clefdemo.simplehash.humdrum.KernFile;
import clefdemo.simplehash.humdrum.KernHasher;
import clefdemo.simplehash.humdrum.KernSpine;

/**
 * This class initializes the database to be used by the Simplehash algorithm. 
 * 
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
@Component
public class SimplehashInitializer implements CommandLineRunner {

	// Keep a copy of the data as a .csv file just in case it's needed for any use.
	private Path csvdata = Paths.get( "/usr/local/data/ngrams.csv" );
	private List<HashRecord> ngramHashRecords = new LinkedList<HashRecord>();
	
	
	/**
	 * Gets the name of the dataset to which a given file belongs.
	 * 
	 * This method uses the clefdataset.json file present in a directory of symbolic music data. This directory 
	 * is expected to be the same as that of the {@code file} passed; that is to say, this file and the 
	 * clefdataset.json file should be siblings in the file tree.
	 * 
	 * @param file a file of symbolic music data
	 * @return
	 * @since 1.0.0
	 */
	private String getDatasetName( Path file ) {
		String dsetName = "";
		Path parent = file.getParent();
		Path clefdataset = Paths.get( parent.toString(), "clefdataset.json" );
		if ( Files.exists( clefdataset, LinkOption.NOFOLLOW_LINKS ) ) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<?, ?> m = mapper.readValue( Files.newInputStream( clefdataset, StandardOpenOption.READ ), Map.class );
				if ( m.containsKey( "datasetAttributes" ) ) {
					Object datasetAttributes = m.get( "datasetAttributes" );
					Map<?, ?> dAtts = (Map<?, ?>) datasetAttributes;
					if ( dAtts.containsKey( "name" ) ) {
						dsetName = (String) dAtts.get( "name" );
					}
				}
			} catch ( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
		
		return dsetName;
	}
	
	
	/**
	 * Initialize the data layer for the Simplehash MIR program.
	 * 
	 * @since 1.0.0
	 */
    private void initialize() {
    	
    	List<Path> filesToProcess = new ArrayList<Path>();
    	
    	Path p = FileSystems.getDefault().getPath( "/usr/local/data" );
 
    	// Collect file paths of symbolic music data files.
    	try {
			Files.walkFileTree( p, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
					String absoluteFilename = file.toAbsolutePath().toString();
					if ( absoluteFilename.endsWith( ".krn" ) ) {
						filesToProcess.add( file );						
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
    	
    	// Process the source files of symbolic music data.
    	try {
    		this.processFiles( filesToProcess );
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
 
    	System.out.println( "Simplehash has been initialized." );
    }
    
    
    /**
     * Creates a new HashRecord object to be used in writing a hashed n-gram to the database.
     * 
     * Once the HashRecord is created, this method adds it to a collection of HashRecord objects 
     * maintained by the current instance of this class.
     * 
     * @since 1.0.0
	 * @param file a Path instance for the current Humdrum file
	 * @param partname the name of the part within the current Humdrum file
	 * @param gramSize the current value of n for the n-gram being stored
	 * @param gramRaw the raw string value of the n-gram
     * @param hash the hashed value of the n-gram
     */
    private void newHashRecord( Path file, String partname, int gramSize, String gramRaw, int hash ) {
    	if ( this.ngramHashRecords == null ) {
    		this.ngramHashRecords = new LinkedList<HashRecord>();
    	}
    	// Use file path to get related clefdataset.json and extract dataset name
    	String datasetName = this.getDatasetName( file );
    	HashRecord record = new HashRecord( datasetName, file.getFileName().toString(), partname, gramSize, gramRaw, hash );
    	this.ngramHashRecords.add( record );
    }
    
    
    /**
     * Populate the Simplehash database with processed symbolic music source data.
     * 
     * @since 1.0.0
     */
    private void populateDatabase() {
    	System.out.println( "Populating database..." );
    	Database db = null;
    	try {
    		db = new Database();
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    	
    	if ( db instanceof Database ) {
    		int outcome = 0;
    		if ( this.ngramHashRecords.size() > 0 ) {
    			outcome = db.bulkInsertHashRecords( this.ngramHashRecords );
    		}
    		
    		System.out.println( "Simplehash database initialized with " + outcome + " records." );
    	}
    }
    
    
    /**
     * Processes a Kern file and adds its ngrams to the database.
     * 
     * For every ngram size between {@code min} and {@code max} in {@code file}, insert hashed ngrams into the database.
     * 
     * @since 1.0.0
     * @param file a Path instance for the current Humdrum file
     * @param min the minimum value of n to be used in constructing n-grams
     * @param max the maximum value of n to be used in constructing n-grams
     * @throws IOException thrown if any n-gram record could not be written
     */
    public void processFile( Path file, int min, int max ) throws IOException {

    	KernFile kf = KernFile.parse( file.toAbsolutePath() );
    	KernHasher kh = new KernHasher();
    	
    	// Starting at min, generate ngrams for every spine in this Kern file.
    	int gramSize = min;
    	while ( gramSize <= max ) {
    		// Iterate through the KernSpines in this KernFile.
    		for ( Map.Entry<Integer, KernSpine> entry : kf.getSpines().entrySet() ) {
    			
    			// Get the current KernSpine.
    			KernSpine ks = entry.getValue();
    			
    			// Generate, and loop over, ngrams of size gramSize. Use the KernSpine's filtered data.
    			for ( List<String> ngram : kh.ngrams( ks.applyFilters(), gramSize ) ) {
    				// Hash the ngram
    				int hash = kh.hash( ngram );
    				// Create a new HashRecord object
    				this.newHashRecord( file, ks.getName(), gramSize, kh.ngramToString(ngram), hash );
    				// Write a line of CSV data 
    				this.writeCSV( file, ks.getName(), gramSize, kh.ngramToString(ngram), hash );
    			}
    		}
    		gramSize++;
    	}
    }
    
    
    /**
     * Processes sources of symbolic music data.
     * 
     * @since 1.0.0
     * @param filesToProcess a list of Path instances to symbolic music source files
     * @throws Exception if required environment variables QUERY_SIZE_MIN or QUERY_SIZE_MAX are not present
     */
    public void processFiles( List<Path> filesToProcess ) throws Exception {
    	String qsMin = System.getenv( "QUERY_SIZE_MIN" );
    	String qsMax = System.getenv( "QUERY_SIZE_MAX" );
    	
    	if ( qsMin == null || qsMax == null ) {
    		throw new Exception( "Error: QUERY_SIZE_MIN or QUERY_SIZE_MAX is not defined in the environment." );
    	}
    	
    	// Convert the query size min and max values into ints. These will be used to make ngrams in processFile().
    	int min = Integer.parseInt( qsMin );
    	int max = Integer.parseInt( qsMax );
    	
    	if ( ! filesToProcess.isEmpty() ) {
    		for ( Path file : filesToProcess ) {
    			this.processFile( file, min, max );
    		}
    	}
    }
	
    
    /**
     * Implementation of Spring Boot CommandLineRunner.run(String... args) interface method.
     * 
     * Will be run on startup after the Spring application context is created.
     */
	@Override
	public void run(String... args) throws Exception {
		this.initialize();
		this.populateDatabase();
	}
	
	
	/**
	 * A dumb, Simplehash-specific method for writing CSV data. Does not quote or otherwise escape values.
	 * 
	 * @since 1.0.0
	 * @param file a Path instance for the current Humdrum file
	 * @param partname the name of the part within the current Humdrum file
	 * @param gramSize the current value of n for the n-gram being stored
	 * @param gramRaw the raw string value of the n-gram
	 * @param hash the hashed value of the n-gram
	 * @throws IOException thrown if data could not be written to disk
	 */
	private void writeCSV( Path file, String partname, int gramSize, String gramRaw, int hash ) throws IOException {
    	
    	String line = String.format( "%s,%s,%d,%s,%d\n", file.getFileName().toString(), partname, gramSize, gramRaw, hash );
    	
    	Files.write( this.csvdata, line.getBytes(), StandardOpenOption.APPEND );
    }

}
