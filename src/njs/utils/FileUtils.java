package njs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
	
	public static String getFileContents(File file) throws Exception {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try {
			br= new BufferedReader(new FileReader(file));
			String line;
		    while ((line = br.readLine()) != null) {
				builder.append(line);
				builder.append(MyConstants.NEW_LINE);
		    }
		} finally {
			if(br != null)
				br.close( );
		}
		return builder.toString();
	}
	
	public static List<String> getFileLines(File file) throws Exception {
		String fileContents = getFileContents(file);
		String[] split = PatternUtils.NEWLINE_PATTERN.split(fileContents);
		return Arrays.asList(split);
		
	}
	
	public static void writeToFile(File file, String contents) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter( file));
			writer.write(contents);
		} finally {
			if(writer != null)
				writer.close( );
		}
	}
	
	public static File getFile(String testFile,Class<?> clazz) {
		File file = new File(testFile);
		if(!file.exists()) {
			String path = "";
			try {
				path = clazz.getProtectionDomain().getCodeSource()
									.getLocation().toURI().getPath();
				String regex =  "bin" ;
				String replacement = "src" ;
				path = path.replaceAll(regex , 
						replacement);
				Package package1 = clazz.getPackage();
				String folderName = path + package1.getName().replace('.', File.separatorChar);
				String filePath = folderName + File.separator + testFile;
				file = new File(filePath);
				if(!file.exists()) {
					return null;
				}
				//path = path + RELATIVE_LOCATION_IMG_FOLDER;
			} catch (URISyntaxException e) {
				//e.printStackTrace();
			}
		}
		return file;
	}
}
