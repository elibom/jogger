package org.jogger.http.servlet.multipart;

import org.jogger.http.FileItem;

/**
 * Implementations of this interface are passed to the 
 * {@link Multipart#parse(javax.servlet.http.HttpServletRequest, PartHandler)} method and handle the request parts.
 * 
 * @author German Escobar
 */
public interface PartHandler {

	/**
	 * Called when a form item part is found.
	 * 
	 * @param name the name of the field to which this part is associated. 
	 * @param value the value of the field.
	 */
	void handleFormItem(String name, String value);
	
	/**
	 * Called when a file item part (or subpart) is found.
	 * 
	 * @param name the name of the field to which this part is associated.
	 * @param fileItem the {@link FileItem} that holds the data and input stream of the file.
	 */
	void handleFileItem(String name, FileItem fileItem);
	
}
