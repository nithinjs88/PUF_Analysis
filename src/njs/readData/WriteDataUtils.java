package njs.readData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Set;

import njs.model.DiffBtwnCorrelationData;
import njs.model.PUFData;
import njs.utils.FileUtils;
import njs.utils.MyConstants;
import njs.utils.MyUtilities;

public class WriteDataUtils {
	public static <K> String getCSVSingleDim(K[] array) {
		StringBuilder builder = new StringBuilder();
		Class<?> componentType = array.getClass().getComponentType();
		boolean isDouble = false;
		if(componentType == Double.class || componentType == double.class) {
			isDouble = true;
		}
		for (K k : array) {
			if(isDouble) {
				String format = String.format( "%.2f", k );
				builder.append(format);
			} else {
				builder.append(k.toString());
			}
			builder.append(",");
		}
		if(builder.length() > 0)
			MyUtilities.removeCommaAtEndOfBuilder(builder);
		return builder.toString();
	}
	
	public static <K> String getCSVDoubleDim(K[][] array) {
		StringBuilder builder = new StringBuilder();
		for (K[] k : array) {
			builder.append(getCSVSingleDim(k));
			builder.append(MyConstants.NEW_LINE);
		}
		if(builder.length() > 0)
			MyUtilities.removeCommaAtEndOfBuilder(builder);
		return builder.toString();
	}

	public static String getSDDestFileName(PUFData pufData) {
		return MessageFormat.format(EntryPoint.MSG_FORMAT_INTERHD_LAST_BITS, "SD",pufData.noOfLastBits);
	}

	public static String getMeanDestFileName(PUFData pufData) {
		return MessageFormat.format(EntryPoint.MSG_FORMAT_INTERHD_LAST_BITS, "Mean",pufData.noOfLastBits);
	}

	public static String getPearsonCoeffDestFileName(String dev1, String dev2, PUFData pufData) {
		return MessageFormat.format(EntryPoint.MSG_FORMAT_PEARSON_DEV_LAST_BITS, dev1,dev2,pufData.noOfLastBits);
	}
	
	public static void writeToFile(DiffBtwnCorrelationData data) {
		Set<Integer> lastBits = data.lastBitsVsdevIdVsDiffInfo.keySet();
		for (Integer integer : lastBits) {
			String fileName = QueryConstants.DIFF_BTWN_CORR + "_"+integer + "bits.txt";
			writeToFile(fileName,data.getFullDetailedInfo(integer));
			System.out.println(data.getMaxInfoForDevices(integer));
		}
	}
	
	public static void writeToFile(String name,String contents) {
		File file = new File(EntryPoint.FOLDER_FILE, name);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.writeToFile(file, contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeDataToDATFile(Object obj, File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(obj);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void writeDiffBtwnCornToDATFile(
			DiffBtwnCorrelationData diffBtwnCorn) {
		File file = new File(EntryPoint.FOLDER_FILE, "DiffBtwnCorr.dat");
		writeDataToDATFile(diffBtwnCorn, file);
	}
	
	public static void writePUFDataToDATFile(
			PUFData	pufData) {
		File file = new File(EntryPoint.FOLDER_FILE, "PUFData.dat");
		writeDataToDATFile(pufData, file);
	}
	
	
}
