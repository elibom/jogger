package org.jogger.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Wraps a String object that can be converted into other objects (e.g. Long, Boolean, Date, List, etc.). 
 * 
 * @author German Escobar
 */
public class Value {
	
	private String value;

	public Value(String value) {
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
	
	public List<Value> asList() {
		return asList( "," );
	}
	
	public List<Value> asList(String separator) {
		
		List<Value> params = new ArrayList<Value>();
		
		String[] tokens = value.split( separator );
		for (String token : tokens) {
			params.add( new Value(token) );
		}
		
		return params;
		
	}
	
	public <T> T asObject(ObjectConverter<T> converter) {
		return converter.convert(value);
	}
	
	interface ObjectConverter<T> {
		
		T convert(String value);
		
	}
}
