package njs.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {
	
	@SuppressWarnings("unchecked")
	public static <K> void addToCollection(Object collection,K ... elements) throws RuntimeException {
		try {
			Class<?> componentType = null;
			boolean array = elements.getClass().isArray();
			if(array) {
				componentType = elements.getClass().getComponentType();
			}
			
			Method method = null;
			Class<?> collectionClass = collection.getClass();
			try {
				method = collectionClass.getMethod("add",componentType);
			} catch (Exception e) {
				Method[] methods = collectionClass.getMethods();
				for (Method methodItr : methods) {
					if(methodItr.getName().equals("add")) {
						int modifier = methodItr.getModifiers();
						if(Modifier.isPublic(modifier) && !Modifier.isStatic(modifier)) {
							Class<?>[] parameterTypes = methodItr.getParameterTypes();
							if(parameterTypes.length == 1) {
								//method takes only argument
								Class<?> methodItrParam = parameterTypes[0];
								if(methodItrParam.isAssignableFrom(componentType)) {
									//parameter class is super class
									if(method == null) {
										method = methodItr;
									} else {
										//Choose most specific method
										Class<?> methodParam = method.getParameterTypes()[0];
										if(methodParam.isAssignableFrom(methodItrParam)) {
											//methodItr is more specific
											method = methodItr;
										}
									}
								}
							}
						}
					}
				}
			}
			
			if(method == null) {
				String errMsg = "No add method for "+collectionClass;
				throw new RuntimeException(errMsg, new NoSuchMethodException(errMsg));
			}
			
			for (K ele : elements) {
				method.invoke(collection, ele);
			}
		
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public static <K> void addNumbers(Object collection) {
		addNumbers(collection, 5);
	}
	
	public static <K> void addNumbers(Object collection,Order order) {
		addNumbers(collection, 5,order);
	}
	
	public static <K> void addNumbers(Object collection, int noOfEle) {
		addNumbers(collection, noOfEle,Order.RANDOM);
	}
	
	public static void addNumbers(Object collection, int noOfEle, Order order) {
		addNumbers(collection, 0, 10, noOfEle,order);
	}

	public static <K> void addNumbers(Object collection,int start,int maxNo,int noOfEle,Order order) {
		if((maxNo - start) < noOfEle) {
			//not possible
			return;
		}
		List<Integer> list = new ArrayList<Integer>();
		switch (order) {
		case RANDOM:
		case INCREASING:
		case DECREASING:
			while(list.size() < noOfEle) {
				int random = (int) (Math.random()*maxNo);
				if(!list.contains(random)) {
					list.add(random);
				}
			}
			break;
		default:
			break;
		}
		switch (order) {
		case INCREASING:
			Collections.sort(list);
			break;
		case DECREASING:
			Collections.sort(list);
			Collections.reverse(list);
			break;
		default:
			break;
		}
		System.out.println("Inserting "+list);
		addToCollection(collection, list.toArray(new Integer[]{}));
	}
	
	public static enum Order {
		RANDOM,
		INCREASING,
		DECREASING;
	}
	
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		addNumbers(list,Order.DECREASING);
		System.out.println(list);
	}

}
