package clefdemo.simplehash.db;

/**
 * This class is a simple DTO for keeping n-gram information together.
 * 
 * @author Max DeCurtins
 * @since 1.0.0
 */
public class HashRecord {

	private String datasetName;
	private String filename;
	private String partname;
	private int gramSize;
	private String gramRaw;
	private int gramHashed;
	
	/**
	 * Constructor.
	 * 
	 * @since 1.0.0
	 * @param filename a Path instance for the current Humdrum file
	 * @param partname the name of the part within the current Humdrum file
	 * @param gramSize the current value of n for the n-gram being stored
	 * @param gramRaw the raw string value of the n-gram
     * @param gramHashed the hashed value of the n-gram
	 */
	public HashRecord( String datasetName, String filename, String partname, int gramSize, String gramRaw, int gramHashed ) {
		this.datasetName = datasetName;
		this.filename = filename;
		this.partname = partname;
		this.gramSize = gramSize;
		this.gramRaw = gramRaw;
		this.gramHashed = gramHashed;
	}
	
	
	/**
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public String getDatasetName() {
		return datasetName;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getFilename() {
		return filename;
	}
	
	
	/**
	 * @since 1.0.0
	 * @return
	 */
	public String getPartname() {
		return partname;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public int getGramSize() {
		return gramSize;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public String getGramRaw() {
		return gramRaw;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public int getGramHashed() {
		return gramHashed;
	}
	
	
	/**
	 * 
	 * @param dset
	 * @since 1.0.0
	 */
	public void setDatasetName( String dset ) {
		this.datasetName = dset;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param filename
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param partname
	 */
	public void setPartname( String partname ) {
		this.partname = partname;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param gramSize
	 */
	public void setGramSize( int gramSize ) {
		this.gramSize = gramSize;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param gramRaw
	 */
	public void setGramRaw( String gramRaw ) {
		this.gramRaw = gramRaw;
	}
	
	
	/**
	 * 
	 * @since 1.0.0
	 * @param gramHashed
	 */
	public void setGramHashed( int gramHashed ) {
		this.gramHashed = gramHashed;
	}
	
	
}
