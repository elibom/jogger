package org.jogger.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jogger.util.Preconditions;

/**
 * Wraps a String object that can be converted into other objects (e.g. Long, Boolean, Date, Array, etc.). 
 * 
 * @author German Escobar
 */
public class Value {
	
	private String value;

	public Value(String value) {
		Preconditions.notNull(value, "value not provided.");
		this.value = value;
	}
	
	public String asString() {
		return value;
	}
	
	public Long asLong() {
		return Long.parseLong(value);
	}
	
	public Integer asInteger() {
		return Integer.parseInt(value);
	}
	
	public Boolean asBoolean() {
		return Boolean.parseBoolean(value);
	}
	
	public Date asDate(String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(value);
	}
	
	public Value[] asArray() {
		return asArray( "," );
	}
	
	public Value[] asArray(String separator) {
		
		String[] tokens = value.split( separator );
		Value[] values = new Value[tokens.length];
		for (int i=0; i < tokens.length; i++) {
			values[i] = new Value(tokens[i]);
		}
		
		return values;
		
	}
	
	public <T> T asObject(ObjectConverter<T> converter) {
		return converter.convert(value);
	}
	
	interface ObjectConverter<T> {
		
		T convert(String value);
		
	}
}
