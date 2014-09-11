package com.elibom.jogger;

import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;

/**
 * Class used for testing by {@link JoggerServletTest}.
 *
 * @author German Escobar
 */
@MockAnnotation
public class AnnotatedMockController {

	public void action(Request request, Response response) {}

}
