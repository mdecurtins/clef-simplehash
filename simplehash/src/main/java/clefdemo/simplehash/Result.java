package clefdemo.simplehash;

import java.util.Map;
import java.util.HashMap;

public class Result {

	private int id;
	private String filename;
	private Map<String, Object> properties;
	
	public Result() {
		this.id = 0;
		this.filename = "";
	}
	
	public Result( int id, String filename ) {
		this.id = id;
		this.filename = filename;
		this.properties = new HashMap<String, Object>();
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
