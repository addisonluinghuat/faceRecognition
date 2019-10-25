package com.demo.utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageConverter {

	public static void convertPNGToPGM(String photoPath, String fileName) {
		System.out.println("Path:" + photoPath);
		Mat source = Imgcodecs.imread(photoPath + fileName, Imgcodecs.IMREAD_GRAYSCALE);
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Imgproc.equalizeHist(source, destination);
		Imgcodecs.imwrite(photoPath + "temphoto.pgm", destination);
	}

}
