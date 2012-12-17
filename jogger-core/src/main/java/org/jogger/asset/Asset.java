package org.jogger.asset;

import java.io.InputStream;

public class Asset {
	
	private final InputStream inputStream;
	
	private final String name;
	
	private final long length;
	
	private final String contentType;

	public Asset(InputStream inputStream, String name, String contentType, long length) {
		this.inputStream = inputStream;
		this.name = name;
		this.contentType = contentType;
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public long getLength() {
		return length;
	}

}
