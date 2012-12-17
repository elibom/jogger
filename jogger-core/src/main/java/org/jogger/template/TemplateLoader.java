package org.jogger.template;

import java.io.IOException;
import java.io.Reader;

public interface TemplateLoader {
	
	public long getLastModified(String name) throws IOException;
	
	public Reader getReader(String name) throws IOException;

}
