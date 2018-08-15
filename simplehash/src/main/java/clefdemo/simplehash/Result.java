package clefdemo.simplehash;

import java.util.Map;
import java.util.HashMap;

public class Result {

	private int id;
	private String datasetName;
	private String filename;
	private Map<String, Object> properties;
	
	public Result() {
		this.id = 0;
		this.filename = "";
	}
	
	public Result( int id, String datasetName, String filename ) {
		this.id = id;
		this.datasetName = datasetName;
		this.filename = filename;
		this.properties = new HashMap<String, Object>();
	}
	
	public String getDatasetName() {
		return datasetName;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int getId() {
		return id;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	public void setDatasetName( String dset ) {
		this.datasetName = dset;
	}
	
	public void setFilename( String f ) {
		this.filename = f;
	}
	
	public void setId( int i ) {
		this.id = i;
	}
	
	public void setProperty( String key, Object val ) {
		this.properties.put( key, val );
	}
	
	public void setAllProperties( Map<String, Object> props ) {
		this.properties = props;
	}
}
