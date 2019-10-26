package com.utils;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageConverter {

	public static void convertPNGToPGM(String resourcePath, String photoFolderName, String fileName, String userNo)
			throws IOException {
		System.out.println("Path:" + resourcePath + photoFolderName);
		Mat source = Imgcodecs.imread(resourcePath + photoFolderName + fileName, Imgcodecs.IMREAD_GRAYSCALE);
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Imgproc.equalizeHist(source, destination);
		FileUtils.deleteFilesByExtension(resourcePath + photoFolderName);
		int fileCount = FileUtils.countTotalItemsInfolder(resourcePath + photoFolderName);
		Imgcodecs.imwrite(resourcePath + photoFolderName + (fileCount + 1) + ".pgm", destination);
		FileUtils.writeFile(resourcePath + "TrainingData.txt",
				resourcePath + photoFolderName + (fileCount + 1) + ".pgm" + ";" + userNo);
	}

}
