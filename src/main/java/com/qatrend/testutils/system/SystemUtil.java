package com.qatrend.testutils.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.qatrend.testutils.logging.PLogger;

public class SystemUtil {
	private static Logger logger = PLogger.getLogger();
	private static final int BUFFER = 2048;

	public static String getProperty(String propName) {
		String propVal = "";
		propVal = System.getenv(propName);
		if (propVal == null) {
			propVal = System.getProperty(propName);
		}
		return propVal;
	}

	public static String getPropertyFromFile(String propsFilePath,
			String propName) {
		String propValue = null;
		try {
			Properties props = new Properties();
			FileInputStream in = new FileInputStream(propsFilePath);
			props.load(in);
			propValue = props.getProperty(propName);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		return propValue;
	}

	public static void runLocalCommand(String command) {
		Runtime runTime = Runtime.getRuntime();
		Process localProc = null;

		try {
			localProc = runTime.exec(command);
			localProc.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					localProc.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				logger.info( line);
				line = reader.readLine();
			}
		} catch (Exception ex) {
			logger.error( "Exception: " + ex.getMessage());
		}
	}

	public static void runLocalCommand(String command, int timeoutInSeconds) {
		Runtime runTime = Runtime.getRuntime();
		Process localProc = null;

		try {

			localProc = runTime.exec(command);
			localProc.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					localProc.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				logger.info( line);
				line = reader.readLine();
			}
		} catch (Exception ex) {
			logger.error( "Exception: " + ex.getMessage());
		}
	}

	public static void unZip(String filePath) {
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(filePath);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isWindows() {

		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return (os.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public static boolean isSolaris() {

		String os = System.getProperty("os.name").toLowerCase();
		// Solaris
		return (os.indexOf("sunos") >= 0);

	}

}
