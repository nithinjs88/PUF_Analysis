package njs.utils;


public class MyUtilities {
	
	public static boolean nullSafeEquals(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	public static boolean isEven(int no) {
		return no % 2 == 0;
	}
	
	public static boolean isOdd(int no) {
		return no % 2 != 0;
	}
	
	public static boolean isPositive(int no) {
		return no > 0;
	}
	
	public static boolean isNegative(int no) {
		return no < 0;
	}
	
	/**
	 * 
	 * @param obj1 not null
	 * @param obj2 not null
	 * wont work as in java pointers are passed by value
	 */
	public static <K> void swap(K obj1, K obj2) {
		K temp = obj1;
		obj1 = obj2;
		obj2 =temp;
	}
	
	public static <K extends Comparable<? super K>> K getMaxElement(K... elements) {
		return ArrayUtilities.getMaxElementinArray(elements);
	}
	
	public static <K extends Comparable<? super K>> K getMinElement(K... elements) {
		return ArrayUtilities.getMinElementInArray(elements);
	}
	
	public static void removeCommaAtEndOfBuilder(StringBuilder builder) {
		if(builder != null) {
			int length = builder.length();
			if(length >0 && builder.charAt(length - 1) == ',') {
				builder.setLength(length - 1);
			}
		}
	}
	
	public static void removeCharacterAtEndOfBuilder(StringBuilder builder) {
		if(builder != null) {
			int length = builder.length();
			if(length >0) {
				builder.setLength(length - 1);
			}
		}
	}
	
	public static void printTrailingSpaces(int cellWidth, String printedString) {
		for(int k = cellWidth;k >= printedString.length();k--) {
			System.out.print(" ");
		}
	}
	
}
