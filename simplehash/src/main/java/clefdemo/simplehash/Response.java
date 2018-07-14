package clefdemo.simplehash;

import java.util.List;
import java.util.ArrayList;

public class Response {

	private List<String> errors;
	private int itemsSearched;
	private List<Result> results;
	private String status;
	
	public Response() {
		this.errors = new ArrayList<String>();
		this.results = new ArrayList<Result>();
		this.itemsSearched = 0;
		this.status = "success";
	}
	
	public void addError( String err ) {
		this.errors.add( err );
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public int getItemsSearched() {
		return itemsSearched;
	}
	
	public List<Result> getResults() {
		return results;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setErrors( List<String> errors ) {
		this.errors = errors;
	}
	
	public void setItemsSearched( int items ) {
		this.itemsSearched = items;
	}
	
	public void setResults( List<Result> r ) {
		this.results = r;
	}
	
	public void setStatus( String s ) {
		this.status = s;
	}
}
