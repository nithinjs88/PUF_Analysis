package njs.utils;

import java.util.Arrays;
import java.util.List;

public class ArrayUtilities {
	public static <K> void fillMultiDimensionalArray(K[][] array, K value) {
		for (int i = 0; i < array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
	
	public static <K> void copyArray(K[][] array,K[][] copy) {
		for (int i = 0; i < array.length; i++) {
			System.arraycopy(array[i], 0, copy[i], 0, array[i].length);
		}
	}

	public static <K> void swapInArray(K [] array,int idx1,int idx2) {
		K temp = array[idx1];
		array[idx1] = array[idx2];
		array[idx2] = temp;
	}

	public static <K extends Comparable<? super K>> K getMaxElementinArray(K[] elements) {
		K maxEle = null;
		int itr = 0;
		for (K eleItr : elements) {
			if(itr == 0) {
				maxEle = eleItr;
			} else {
				int compareTo = maxEle.compareTo(eleItr);
				if(compareTo < 0) {
					maxEle = eleItr;
				}
			}
			itr++;
		}
		return maxEle;
	}

	public static <K extends Comparable<? super K>> K getMinElementInArray(K[] elements) {
		K minEle = null;
		int itr = 0;
		for (K eleItr : elements) {
			if(itr == 0) {
				minEle = eleItr;
			} else {
				int compareTo = minEle.compareTo(eleItr);
				if(compareTo > 0) {
					minEle = eleItr;
				}
			}
			itr++;
		}
		return minEle;
	}

	public static void populateIntegerArrayWithRandomNos(int [][] array) {
		ArrayUtilities.populateIntegerArrayWithRandomNos(array,10);
	}

	public static void populateIntegerArrayWithRandomNos(int [][] array,int maxElement) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				int no = 0;
				while(no==0) {
					no = (int) (Math.random() * maxElement);
				}
				array[i][j] = no;
			}
		}
	}

	public static void populateIntegerArrayWithRandomNos(Integer [][] array, int maxEle) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				int no = 0;
				while(no==0) {
					no = (int) (Math.random() * maxEle);
				}
				array[i][j] = no;
			}
				
		}
	}

	public static void populateIntegerArrayWithRandomNos(Integer [][] array) {
		populateIntegerArrayWithRandomNos(array,10);
	}

	public static void populateIntegerArrayWithRandomNos(Integer [] array) {
		ArrayUtilities.populateIntegerArrayWithRandomNos(array, 10);
	}

	public static void populateIntegerArrayWithRandomNos(Integer [] array,int maxEle) {
		for (int i = 0; i < array.length; i++) {
			int no = 0;
			while(no==0) {
				no = (int) (Math.random() * maxEle);
			}
			array[i] = no;
		}
	}

	public static void populateIntegerArrayWithRandomNos(int [] array) {
		ArrayUtilities.populateIntegerArrayWithRandomNos(array, 10);
	}

	public static void populateIntegerArrayWithRandomNos(int [] array,int maxEle) {
		for (int i = 0; i < array.length; i++) {
			int no = 0;
			while(no==0) {
				no = (int) (Math.random() * maxEle);
			}
			array[i] = no;
		}
	}

	/**
	 * [0,0] appears on top
	 * @param <K>
	 * @param array
	 * @param cellWidth
	 */
	public static <K> void printDoubleDimensionalArray(K[][] array,int cellWidth) {
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				String string = String.valueOf(array[i][j]);
				System.out.print(string);
				MyUtilities.printTrailingSpaces(cellWidth, string);
			}
			System.out.println();
		}
	}

	/**
	 * [0,0] appears on bottom
	 */
	public static <K> void printDoubleDimensionalArrayNeatly(K[][] array,int cellWidth) {
		for (int i = array.length-1; i >= 0; i--) {
			for (int j = 0; j < array[0].length; j++) {
				String string = String.valueOf(array[i][j]);
				System.out.print(string);
				MyUtilities.printTrailingSpaces(cellWidth, string);
			}
			System.out.println();
		}
	}

	public static <K> void printSingleDimensionArray(K[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(String.valueOf(array[i]));
			System.out.print("\t");
		}
		System.out.println();
	}

	/**
	 * 
	 * @param <K>
	 * @param array
	 * @param cellWidth
	 */
	public static <K> void printSingleDimensionArrayNeatly(K[] array,int cellWidth) {
		for (int i = 0; i < array.length; i++) {
			String string = String.valueOf(array[i]);
			System.out.print(string);
			MyUtilities.printTrailingSpaces(cellWidth, string);
		}
		System.out.println();
	}

	public static <K> String getStringRepOfArray(K[] array) {
		List<K> asList = Arrays.asList(array);
		return asList.toString();
	}

	public static void swapCharArray(char[] array, int idx1, int idx2) {
		// TODO Auto-generated method stub
		char temp = array[idx1];
		array[idx1] = array[idx2];
		array[idx2] = temp;
	}

	public static void printIntArrayTill(int[] array, int n) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < n && i<array.length; i++) {
			builder.append(array[i]);
			builder.append(",");
		}
		System.out.println(builder);
	}

	public static void printCharArrayTill(char[] array, int n) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < n && i<array.length; i++) {
			builder.append(array[i]);
			builder.append(",");
		}
		System.out.println(builder);
	}
}
