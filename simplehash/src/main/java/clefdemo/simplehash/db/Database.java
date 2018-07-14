package clefdemo.simplehash.db;

import java.util.List;
import java.util.LinkedList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import clefdemo.simplehash.Result;

/**
 * This class handles database functionality for Simplehash.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class Database {
	
	private String connectionUrl = "jdbc:sqlite:";
	private String dbPath;

	
	/**
	 * To initialize this class successfully, DB_PATH must be a fully-qualified path to a 
	 * database file.
	 * 
	 * @since 1.0.0
	 * @throws Exception thrown if the environment variable DB_PATH is not set
	 */
	public Database() throws Exception {
		this.dbPath = System.getenv( "DB_PATH" );
		if ( this.dbPath == null || this.dbPath == "" ) {
			throw new Exception( "Environment variable DB_PATH not found." );
		} else {
			this.connectionUrl = this.connectionUrl + this.dbPath;
		}
	}
	
	
	/**
	 * Begin a transaction on the given connection.
	 * 
	 * @since 1.0.0
	 * @param conn the current database connection
	 * @return the connection, with auto-commits turned off
	 */
	private Connection beginTransaction( Connection conn ) {
		try {
			conn.setAutoCommit( false );
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		}
		return conn;
	}
	
	
	/**
	 * Inserts records for hashed n-grams of symbolic music data.
	 * 
	 * @since 1.0.0
	 * @param records a list of records of hashed n-grams of symbolic music data
	 * @return the number of rows inserted as part of this transaction
	 */
	public int bulkInsertHashRecords( List<HashRecord> records ) {
		Connection conn = this.connect();
		
		int inserted = 0;
		if ( conn != null ) {
			
			// BEGIN TRANSACTION.
			conn = this.beginTransaction( conn );
			
			String sql = "INSERT INTO simplehash ( filename, partname, gram_size, gram_raw, gram_hashed ) VALUES ( ?, ?, ?, ?, ? );";
			PreparedStatement ps = null;
			
			try {
				ps = conn.prepareStatement( sql );
			} catch ( SQLException sqle ) {
				sqle.printStackTrace();
			}
			
			// If the statement was successfully prepared.
			if ( ps != null ) {
				
				for ( HashRecord record : records ) {
					try {
						// Just enumerate the parameters.
						ps.setString( 1, record.getFilename() );
						ps.setString( 2, record.getPartname() );
						ps.setInt( 3, record.getGramSize() );
						ps.setString( 4, record.getGramRaw() );
						ps.setInt( 5, record.getGramHashed() );
						
						// Increment the count of inserted rows.
						inserted += ps.executeUpdate();
						ps.clearParameters();
					} catch ( SQLException sqle ) {
						sqle.printStackTrace();
					}
				}
				
			}
			
			// COMMIT TRANSACTION
			this.commitTransaction( conn );
		}
		return inserted;
	}
	
	
	/**
	 * Commits any operations performed against the connection passed, and then closes the connection.
	 * 
	 * @since 1.0.0
	 * @param conn the current database connection
	 */
	private void commitTransaction( Connection conn ) {
		try {
			conn.commit();
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		} finally {
			this.disconnect( conn );
		}
	}
	
	
	/**
	 * Connects to the SQLite database, and creates the simplehash table if not exists.
	 * 
	 * @since 1.0.0
	 * @return a connection to the database, or null if a connection could not be opened
	 */
	private Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection( this.connectionUrl );
			System.out.println( "SQLite: Connection established." );
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		}
		
		if ( conn != null ) {
			this.createTable( conn );
		}
		
		return conn;
	}
	
	
	/**
	 * Creates a table to hold simplehash records, if it does not already exist.
	 * 
	 * The table schema is as follows:
	 * gram_id INTEGER PRIMARY KEY AUTOINCREMENT (N.B. SQLite implements auto-increment even without this keyword)
	 * filename TEXT
	 * partname TEXT
	 * gram_size INTEGER
	 * gram_raw TEXT
	 * gram_hashed INTEGER
	 * 
	 * @since 1.0.0
	 * @param conn the current database connection
	 */
	private void createTable( Connection conn ) {
		String sql = "CREATE TABLE IF NOT EXISTS simplehash ( gram_id INTEGER PRIMARY KEY, filename TEXT, partname TEXT, gram_size INTEGER, gram_raw TEXT, gram_hashed INTEGER );";
	
		try {
			Statement stmt = conn.createStatement();
			stmt.execute( sql );
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		}		
	}
	
	
	/**
	 * Closes the current database connection.
	 * 
	 * @since 1.0.0
	 * @param conn the current database connection
	 */
	private void disconnect( Connection conn ) {
		try {
			if ( conn != null ) {
				conn.close();
				System.out.println( "Connection closed." );
			}
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		}
	}
	
	
	/**
	 * Gets the number of distinct filenames stored in the simplehash database.
	 * 
	 * @since 1.0.0
	 * @return the number of distinct filenames in the simplehash database
	 */
	public int numDistinctFiles() {
		Connection conn = this.connect();
		String sql = "SELECT COUNT(DISTINCT filename) AS numFiles FROM simplehash;";
		int num = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while ( rs.next() ) {
				num = rs.getInt( "numFiles" );
			}
			rs.close();
		} catch ( SQLException sqle ) {
			sqle.printStackTrace();
		} finally {
			this.disconnect( conn );
		}
		
		return num;
	}
	

	/**
	 * Selects records matching a given hash.
	 * 
	 * Selects records from the simplehash database that match a given hash, grouped by the filename from 
	 * which the hash was generated.
	 * 
	 * @since 1.0.0
	 * @param hash a hashed n-gram to select
	 * @return a list of {@code Result} objects, empty if no rows were selected
	 */
	public List<Result> selectAllWithHash( int hash ) {
		Connection conn = this.connect();
		String sql = "SELECT filename, COUNT(gram_id) AS numMatches FROM simplehash WHERE gram_hashed = ? GROUP BY filename ORDER BY numMatches DESC;";
		
		List<Result> results = new LinkedList<Result>();
		
		if ( conn != null ) {
			ResultSet rs = null;
			try {
				PreparedStatement pstmt = conn.prepareStatement( sql );
				
				// Bind the parameter
				pstmt.setInt( 1, hash );
				
				rs = pstmt.executeQuery();
				
				// Add a new Result for every row returned from the database.
				int resultid = 1;
				while ( rs.next() ) {
					Result r = new Result( resultid, rs.getString( "filename" ) );
					r.setProperty( "matches", rs.getInt( "numMatches" ) );
					results.add( r );
					resultid++;
				}
				
				// Close the ResultSet
				rs.close();
			} catch ( SQLException sqle ) {
				sqle.printStackTrace();
			} finally {		
				this.disconnect( conn );
			}
		}
		
		return results;
	}
	

}
