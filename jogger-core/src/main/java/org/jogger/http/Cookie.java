package org.jogger.http;

/**
 * Represents an HTTP Cookie.
 * 
 * @author German Escobar
 */
public class Cookie {

	private String name;
	
	private String domain;
	
	private String path;
	
	private boolean secure = false;
	
	private String value;
	
	private int maxAge = -1;
	
	private boolean httpOnly = false;
	
	public Cookie(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public Cookie(String name, String value, int maxAge) {
		this.name = name;
		this.value = value;
		this.maxAge = maxAge;
	}
	
	public Cookie(String name, String value, int maxAge, boolean httpOnly) {
		this.name = name;
		this.value = value;
		this.maxAge = maxAge;
		this.httpOnly = httpOnly;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}
	
}
