package clefdemo.simplehash;


import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import clefdemo.simplehash.humdrum.KernSpine;

/**
 * The main class of the Simplehash MIR algorithm server application.
 *
 * @author Max DeCurtins
 * @since 1.0.0
 */
@SpringBootApplication
@RestController
public class Application {
	
	public static void main(String[] args) {
		SpringApplication.run( Application.class, args );
	}
	
	
    /**
     * The primary REST endpoint for the Simplehash algorithm environment.
     *  
     * @since 1.0.0 
     * @param params the parameters present in the request URL.
     * @return an object serializing to the JSON format specified for the Clef system.
     */
    @RequestMapping( value = "/simplehash", method = RequestMethod.GET, consumes = MediaType.APPLICATION_XML_VALUE )
    public Response simplehash( @RequestParam Map<String, String> params, @RequestBody String musicxml ) {
    	
    	// Create a new response.
    	Response response = new Response();
    
    	int staffIdx = 1;
  
    	// Get the staff index parameter from the URL.
    	if ( params.get( "staffIdx" ) == null ) {
    		response.setStatus( "error" );
    		response.addError( "Error: required parameter staffIdx is missing." );
    		return response;
    	} else {
    		staffIdx = Integer.parseInt( params.get( "staffIdx" ) );
    	}
    	
    	// The main algorithm class.
    	Simplehash sh = new Simplehash();
    	
    	// Write the MusicXML to a temp file so that it can be converted to Humdrum.
    	sh.writeQueryToTempFile( musicxml );
    	
    	// Convert the MusicXML file to Humdrum using the specified staff index.
    	KernSpine ks = sh.xml2hum( staffIdx );
    	
    	// Perform the hash-based lookup.
    	List<Result> results = sh.lookup( ks );
    	
    	// Add the errors, if any, to the response; set the reponse status to error.
    	if ( sh.hasErrors() ) {
    		response.setErrors( sh.getErrors() );
    		response.setStatus( "error" );
    	} else {
    		// Add the results, if any, to the response.
    		response.setResults( results );
    	}
    	
    	// Include the number of files searched in the response (default is 0).
    	response.setItemsSearched( sh.numItems() );
    	
    	return response;
    }
    
    
   
    
}
