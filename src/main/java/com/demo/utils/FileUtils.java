package com.demo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileUtils {

	public static HashMap<String, String> insert(String path, String username) {

		String fileName = "users.txt";
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = readFile(path, fileName, username);
			System.out.println("map:" + map.size());
			if (map.size() == 0) {
				String fullFilePath = path + fileName;
				FileWriter fw = new FileWriter(fullFilePath, true); // the true will append the new data
				long lineCount = countLine(fullFilePath);
				String key = username;
				String no = "" + (lineCount + 1);
				String valueToInsert = no + "|" + username;
				fw.write(valueToInsert + System.getProperty("line.separator"));// appends the string
																				// // to // the file
				fw.close();
				map.put(username, no);
				return map;

			}

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

		return map;
	}

	public static long countLine(String fullPath) throws IOException {
		Path path = Paths.get(fullPath);
		return Files.lines(path).count();
	}

	public static HashMap<String, String> readFile(String path, String fileName, String inputUsername) {

		HashMap<String, String> map = new HashMap<String, String>();

		try {
			File f = new File(path + fileName);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			System.out.println("Reading file using Buffered Reader");
			while ((readLine = b.readLine()) != null) {
				String[] info = readLine.split("\\|");
				String no = info[0];
				String username = info[1];
				System.out.println("no:" + no);
				System.out.println("username:" + username);
				if (username.equals(inputUsername)) {
					map.put(username, no);
					System.out.println("readLine:" + readLine);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;

	}

	public static void createFolder(String directory) {
		File dir = new File(directory);
		if (!dir.exists())
			dir.mkdirs();
	}
}
