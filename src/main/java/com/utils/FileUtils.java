package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class FileUtils {

	public static HashMap<String, String> insert(String path, String username) {

		String fileName = "users.txt";
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = readFile(path, fileName, username);
			System.out.println("map:" + map.size());
			if (map.size() == 0) {
				String fullFilePath = path + fileName;
				long lineCount = countLine(fullFilePath);
				String key = username;
				String no = "" + (lineCount + 1);
				String valueToInsert = no + "|" + username;
				writeFile(fullFilePath, valueToInsert);
				map.put(username, no);
				return map;

			}

		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}

		return map;
	}

	public static void writeFile(String fullFilePath, String value) throws IOException {
		FileWriter fw = new FileWriter(fullFilePath, true);
		fw.write(value + System.getProperty("line.separator"));
		fw.close();
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
			System.out.println("Reading file using Buffered Reader" + path + fileName);
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

	public static void deleteFilesByExtension(String directory) {
		File folder = new File(directory);
		File fList[] = folder.listFiles();
		for (int i = 0; i < fList.length; i++) {
			File f = fList[i];
			if (!f.getName().endsWith(".pgm")) {
				f.delete();
			}
		}
	}

	public static int countTotalItemsInfolder(String directory) {
		File folder = new File(directory);
		int fileCount = folder.list().length;
		return fileCount;
	}

	public static void readCSV(String csvFilePath2, ArrayList<Mat> images, ArrayList<Integer> labels) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(csvFilePath2));

			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\\;");
				Mat readImage = Imgcodecs.imread(tokens[0], 0);
				images.add(readImage);
				labels.add(Integer.parseInt(tokens[1]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getUsernameByUserNo(String path, String fileName, int userNo) {
		try {
			File f = new File(path + fileName);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			System.out.println("Reading file using Buffered Reader" + path + fileName);
			while ((readLine = b.readLine()) != null) {
				String[] info = readLine.split("\\|");
				String no = info[0];
				String username = info[1];
				if (no.equals(String.valueOf(userNo))) {
					return username;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
