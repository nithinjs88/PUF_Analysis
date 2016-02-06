package njs.utils;

public class ValidationUtils {
	
	public static boolean nullSafeEquals(Object data1,Object data2) {
		if(data1 == null || data2 == null) {
			return data1 == data2;
		} else {
			return data1 == data2 || data1.equals(data2);
		}
	}
}
