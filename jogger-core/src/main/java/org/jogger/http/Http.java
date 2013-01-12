package org.jogger.http;

/**
 * Defines HTTP related constants.
 * 
 * @author German Escobar
 */
public class Http {

	public static class Headers {
		public static final String ACCEPT = "Accept";
		public static final String ACCEPT_CHARSET = "Accept-Charset";
		public static final String ACCEPT_ENCODING = "Accept-Encoding";
		public static final String ACCEPT_LANGUAGE = "Accept-Language";
		public static final String ACCEPT_DATETIME = "Accept-Datetime";
		public static final String AUTHORIZATION = "Authorization";
		public static final String CACHE_CONTROL = "Cache-Control";
		public static final String CONNECTION = "Connection";
		public static final String CONTENT_TYPE = "Conten-Type";
		public static final String CONTENT_LENGTH = "Content-Length";
		public static final String CONTENT_MD5 = "Content-MD5";
		public static final String DATE = "Date";
		public static final String IF_MATCH = "If-Match";
		public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
		public static final String IF_NONE_MATCH = "If-None-Match";
		public static final String USER_AGENT = "User-Agent";
	}
	
	public static class ContentType {
		public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_XML = "application/xml";
        public static final String TEXT_HTML = "text/xml";
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	}
}
