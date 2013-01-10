package org.jogger.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jogger.util.Value.ObjectConverter;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValueTest {

	@Test
	public void shouldConvertToLong() throws Exception {
		long val = Value.asLong("2");
		Assert.assertEquals(val, 2L);
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shouldFailConvertToLongWithNotLong() throws Exception {
		Value.asLong("3.14");
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shoudlFailConvertToLongWithEmptyString() throws Exception {
		Value.asLong("");
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shouldFailConvertToLongWithNullString() throws Exception {
		Value.asLong(null);
	}
	
	@Test
	public void shouldConvertToInteger() throws Exception {
		int val = Value.asInt("2");
		Assert.assertEquals(val, 2);
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shouldFailConvertToIntegerWithNotInt() throws Exception {
		Value.asInt("3.14");
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shoudlFailConvertToIntegerWithEmptyString() throws Exception {
		Value.asInt("");
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shouldFailConvertToIntegerWithNullString() throws Exception {
		Value.asInt(null);
	}
	
	@Test
	public void shouldConvertToTrue() throws Exception {
		boolean val = Value.asBoolean("true");
		Assert.assertEquals(val, true);
	}
	
	@Test
	public void shouldConvertAnyStringToFalse() throws Exception {
		boolean val = Value.asBoolean("somethingelse");
		Assert.assertEquals(val, false);
	}
	
	@Test
	public void shouldConvertNullToFalse() throws Exception {
		boolean val = Value.asBoolean(null);
		Assert.assertEquals(val, false);
	}
	
	@Test
	public void shouldConvertToDate() throws Exception {
		String strDate = "12/01/2014 09:34";
		String pattern = "dd/MM/yyyy HH:mm";
		
		Date date = Value.asDate(strDate, pattern);
		Assert.assertNotNull(date);
		Assert.assertEquals(date.getTime(), new SimpleDateFormat(pattern).parse(strDate).getTime());
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailConvertToDateWithNullValue() throws Exception {
		Value.asDate(null, "dd/MM/yyyy HH:mm");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailConvertToDateWithNullPattern() throws Exception {
		Value.asDate("12/01/2014 09:34", null);
	}
	
	@Test
	public void shouldConvertToArray() throws Exception {
		String[] val = Value.asArray("1,2,3,4");
		Assert.assertNotNull(val);
		Assert.assertEquals(val.length, 4);
		Assert.assertEquals(val[0], "1");
		Assert.assertEquals(val[1], "2");
		Assert.assertEquals(val[2], "3");
		Assert.assertEquals(val[3], "4");
	}
	
	@Test
	public void shouldConvertToArrayWithSeparator() throws Exception {
		String[] val = Value.asArray("1|2|3|4", "|");
		Assert.assertNotNull(val);
		Assert.assertEquals(val.length, 4);
		Assert.assertEquals(val[0], "1");
		Assert.assertEquals(val[1], "2");
		Assert.assertEquals(val[2], "3");
		Assert.assertEquals(val[3], "4");
	}
	
	@Test
	public void shouldConvertToArrayEmptyValue() throws Exception {
		String[] val = Value.asArray("");
		Assert.assertNotNull(val);
		Assert.assertEquals(val.length, 0);
	}
	
	@Test
	public void shouldConvertToArrayNullValue() throws Exception {
		String[] val = Value.asArray(null);
		Assert.assertNotNull(val);
		Assert.assertEquals(val.length, 0);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailConvertToArrayWithNullSeparator() throws Exception {
		Value.asArray("", null);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldConvertToObject() throws Exception {
		ObjectConverter<String> converter = Mockito.mock(ObjectConverter.class);
		Value.asObject("test", converter);
		Mockito.verify(converter).convert("test");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailConvertToObjectIfNullConverter() throws Exception {
		Value.asObject("", null);
	}
}
