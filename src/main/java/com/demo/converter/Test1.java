package com.demo.converter;

import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Test1 {

	public static void main(String[] args) throws IOException {
		Loader.load(opencv_java.class);

		Mat source = Imgcodecs.imread(
				"C:\\Users\\addison.lu\\Desktop\\test\\opencv-facerec-java-maven-master\\faceDetection.jpg",
				Imgcodecs.IMREAD_GRAYSCALE);
		Mat destination = new Mat(source.rows(), source.cols(), source.type());
		Imgproc.equalizeHist(source, destination);
		Imgcodecs.imwrite("C:\\Users\\addison.lu\\Desktop\\test\\opencv-facerec-java-maven-master\\enhancedParrot.pgm",
				destination);
		System.out.println("test");
	}
}
