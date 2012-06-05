package org.jogger.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jogger.http.Request;
import org.jogger.http.Value;

/**
 * Support class that acts as a base class for {@link org.jogger.http.Request} and {@link org.jogger.test.MockRequest} 
 * providing common functionality.
 * 
 * @author German Escobar
 */
public abstract class AbstractRequest implements Request {
	
	/**
	 * The regular expression to find holders in a path (e.g. {userId}).
	 */
	private final String PATH_VAR_REGEXP = "\\{([^{}]+)\\}";

	/**
	 * Holds the path variables of the request.
	 */
	protected Map<String,Value> pathVariables = new HashMap<String,Value>();
	
	@Override
	public Map<String, Value> getPathVariables() {
		return pathVariables;
	}
	
	@Override
	public Value getPathVariable(String name) {
		return pathVariables.get(name);
	}
	
	/**
	 * Helper method. Initializes the pathVariables property of this class.
	 * 
	 * @param routePath the route path as defined in the routes.config file.
	 */
	protected void initPathVariables(String routePath) {
		
		pathVariables.clear();
		
		List<String> variables = getVariables( routePath );
		String regexPath = routePath.replaceAll( PATH_VAR_REGEXP, "([^#?]+)" );
		
		Matcher matcher = Pattern.compile(regexPath).matcher( getPath() );
		matcher.matches();
		
		// start index at 1 as group(0) always stands for the entire expression
		for (int i=1; i <= variables.size(); i++) {
			String value = matcher.group(i);
			pathVariables.put(variables.get(i-1), new Value(value));
		}
		
	}
	
	/**
	 * Helper method. Retrieves all the variables defined in the path. 
	 * 
	 * @param routePath the route path as defined in the routes.config file.
	 * 
	 * @return a List object with the names of the variables. 
	 */
	private List<String> getVariables(String routePath) {
		
		List<String> variables = new ArrayList<String>();
		
		Matcher matcher = Pattern.compile(PATH_VAR_REGEXP).matcher( routePath );
		while (matcher.find()) {
			// group(0) always stands for the entire expression and we only want what is inside the {}
			variables.add( matcher.group(1) );
		}
		
		return variables;
		
	}
	
}
