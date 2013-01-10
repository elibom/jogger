package org.jogger.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Utility class used to convert String objects into other objects (e.g. Long, Boolean, Date, Array, etc.). 
 * 
 * @author German Escobar
 */
public final class Value {
	
	private Value() {}
	
	/**
	 * @see {@link Long#parseLong(String)}.
	 */
	public static Long asLong(final String value) throws NumberFormatException {
		return Long.parseLong(value);
	}
	
	/**
	 * @see {@link Integer#parseInt(String)}.
	 */
	public static Integer asInt(final String value) throws NumberFormatException {
		return Integer.parseInt(value);
	}
	
	/**
	 * @see {@link Boolean#parseBoolean(String)}.
	 */
	public static Boolean asBoolean(final String value) {
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Parses the <code>value</code> with the specified <code>pattern</code>.
	 * 
	 * @param value
	 * @param pattern
	 * 
	 * @return a Date object.
	 * @throws IllegalArgumentException if <code>value</code> or <code>pattern</code> is null or empty. 
	 * @throws ParseException
	 * 
	 * @see {@link SimpleDateFormat#parse(String}.
	 */
	public static Date asDate(final String value, final String pattern) throws IllegalArgumentException, ParseException {
		
		Preconditions.notEmpty(value, "no value provided.");
		Preconditions.notEmpty(pattern, "no pattern provided.");
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(value);
	}
	
	/**
	 * Creates a String array by splitting the <code>value</code> using comma(,) as separator.
	 * 
	 * @param value
	 * 
	 * @return a non-empty String array or an empty String array if the <code>value</code> is null or empty.
	 * @throws IllegalArgumentException if the separator is null.
	 */
	public static String[] asArray(final String value) throws IllegalArgumentException {
		return asArray(value, ",");
	}
	
	/**
	 * Creates a String array by splitting the <code>value</code> using the <code>separator</code>.
	 * 
	 * @param value
	 * @param separator
	 * 
	 * @return a non-empty String array or an empty String array if the <code>value</code> is null or empty.
	 * @throws IllegalArgumentException if the separator is null
	 */
	public static String[] asArray(final String value, final String separator) throws IllegalArgumentException {
		Preconditions.notEmpty(separator, "no separator provided.");
		
		if (value == null || "".equals(value)) {
			return new String[0];
		}
		
		String[] tokens = value.split( Pattern.quote(separator) );
		String[] values = new String[tokens.length];
		for (int i=0; i < tokens.length; i++) {
			values[i] = tokens[i];
		}
		
		return values;
		
	}
	
	/**
	 * Converts the <code>value</code> to the specified object type.
	 * 
	 * @param value
	 * @param converter a non null {@link ObjectConverter} implementation.
	 * 
	 * @return an object or null.
	 */
	public static <T> T asObject(final String value, final ObjectConverter<T> converter) {
		Preconditions.notNull(converter, "no converter specified.");
		return converter.convert(value);
	}
	
	/**
	 * Provides a mechanism to convert strings to objects using the {@link Value#asObject(String, ObjectConverter)} 
	 * method.
	 * 
	 * @author German Escobar
	 *
	 * @param <T> the type of the object to which we will convert the String
	 */
	interface ObjectConverter<T> {
		
		/**
		 * Converts the specified <code>value</code> to the type defined by this converter.
		 * 
		 * @param value the String object that we are going to convert
		 * 
		 * @return an instance of the type defined by this converter.
		 */
		T convert(String value);
		
	}
}
