package org.jogger.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a file from an HTTP multipart/form-data request.
 * 
 * @author German Escobar
 */
public class FileItem {
	
	/**
	 * The name of the field to which this file was associated in the HTTP request
	 */
	private String name;
	
	/**
	 * The name of the file taken from the HTTP part (in the filename attribute of the Content-Disposition header)
	 */
	private String fileName;
	
	/**
	 * The content type of the file taken from the Content-Type header of the part, null if not specified
	 */
	private String contentType;
	
	/**
	 * The content length of the file taken from the Content-Length header of the part, -1 if not specified
	 */
	private long contentLength;
	
	/**
	 * The file.
	 */
	private File file;
	
	/**
	 * The headers of the file part
	 */
	private Map<String,String> headers;
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param fileName
	 * @param contentType
	 * @param contentLength
	 * @param inputStream
	 * @param headers
	 */
	public FileItem(String name, String fileName, String contentType, long contentLength, File file, Map<String,String> headers) {
		
		assertNotNull(name, "no fieldName specified.");
		assertNotNull(fileName, "no fileName specified.");
		assertNotNull(file, "no inputStream specified");
		
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.file = file;
		this.headers = headers;
		if (headers == null) {
			this.headers = new HashMap<String,String>();
		}
		
	}
	
	private void assertNotNull(Object o, String message) {
		if (o == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public File getFile() {
		return file;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
