package njs.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import njs.model.CorrDiffInfo;
import njs.model.DiffBtwnCorrelationData;
import njs.model.ShiftAndCorrInfoWithDevAndTryInfo;
import njs.model.TwoDeviceIdWrapper;

public class JAXBUtils {
	private static JAXBContext jaxbContext = null;
	private static Marshaller marshaller = null;
	private static Unmarshaller unMarshaller = null;
	
	public static JAXBContext getJAXBContext() {
		if(jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(DiffBtwnCorrelationData.class,
						CorrDiffInfo.class,
						ShiftAndCorrInfoWithDevAndTryInfo.class,
						TwoDeviceIdWrapper.class,
						
						HashMap.class, LinkedHashMap.class,TreeMap.class,
						ArrayList.class, LinkedList.class
						);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jaxbContext;
	}
	
	public static Marshaller getMarshaller() {
		if(marshaller == null) {
			try {
				marshaller = getJAXBContext().createMarshaller();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return marshaller;
	}
	
	public static Unmarshaller getUnMarshaller() {
		if(unMarshaller == null) {
			try {
				unMarshaller = getJAXBContext().createUnmarshaller();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return unMarshaller;
	}
	
	public static void saveDataToFile(Object data,File file) {
		try {
			if(!file.exists())
				file.createNewFile();
			getMarshaller().marshal(data, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <K> K readDataFromFile(File file, Class<K> clazz ) {
		try {
			if(!file.exists())
				file.createNewFile();
			K data = (K) (getUnMarshaller().unmarshal(file));
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
