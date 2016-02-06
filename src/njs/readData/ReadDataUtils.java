package njs.readData;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import njs.model.PUFData;
import njs.utils.FileUtils;
import njs.utils.PatternUtils;

public class ReadDataUtils {
	public static double[][] getDeviceOutputFromCSV(File file) {
		double[][] data = new double[EntryPoint.NO_OF_VALID_TRIES][EntryPoint.NO_OF_VALID_VALS];
		try {
			String fileContents = FileUtils.getFileContents(file);
			String[] split = PatternUtils.NEWLINE_PATTERN.split(fileContents);
			String string;
			String[] valuesForATry;
			for (int i = 0; i < EntryPoint.NO_OF_VALID_TRIES; i++) {
				string = split[i];
				valuesForATry = PatternUtils.COMMA_PATTERN.split(string);
				for (int j = 0; j < EntryPoint.NO_OF_VALID_VALS; j++) {
					data[i][j] = Integer.parseInt(valuesForATry[j]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return data;
	}
	
	public static double[] getDeviceInputFromFile(File inputFile) {
		double[] data = new double[EntryPoint.NO_OF_VALID_VALS];
		String fileContents;
		try {
			fileContents = FileUtils.getFileContents(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String[] split = PatternUtils.NEWLINE_PATTERN.split(fileContents);
		String string = split[0];
		String[] valuesForATry = PatternUtils.COMMA_PATTERN.split(string);;
		for (int j = 0; j < EntryPoint.NO_OF_VALID_VALS; j++) {
			data[j] = Integer.parseInt(valuesForATry[j]);
		}
		return data;
	}
	
	public static <K> K readDataFromFile(File file, Class<K> clazz ) {
		K data = null;
		try {
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(file);
				in = new ObjectInputStream(fis);
				data = (K)in.readObject();
				in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static void main(String[] args) {
		File file = new File(EntryPoint.FOLDER_FILE, "PUFData.dat");
		PUFData readDataFromFile = readDataFromFile(file, PUFData.class);
		WriteDataUtils.writePUFDataToDATFile(readDataFromFile);
		//System.out.println(readDataFromFile.getFullDetailedInfo(3));
	}
}
