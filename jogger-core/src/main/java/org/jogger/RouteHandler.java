package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * 
 * @author German Escobar
 */
public interface RouteHandler {

	void handle(Request request, Response response);
	
}
