package com.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utils {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	public static BufferedImage matToBufferedImage(Mat original) {
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	public static void createPhoto(Point tl, Point br, Mat frame, String resourcePath, String photoFolderName,
			String fileName, String userNo) throws IOException {
		Rect rectCrop = new Rect(tl, br);
		Mat cropFace = new Mat(frame, rectCrop);
		Imgproc.resize(cropFace, cropFace, new Size(92, 112));
		Imgcodecs.imwrite(resourcePath + photoFolderName + fileName, cropFace);
		ImageConverter.convertPNGToPGM(resourcePath, photoFolderName, fileName, userNo);

	}

	public static Image mat2Image(Mat frame) {
		try {
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		} catch (Exception e) {
			System.err.println("Cannot convert the Mat object: " + e);
			return null;
		}
	}

	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(() -> {
			property.set(value);
		});
	}

	public static int training(String csvFilePath, Point tl, Point br, Mat frame) {
		System.out.println("Starting training..." + csvFilePath);
		ArrayList<Mat> images = new ArrayList<>();
		ArrayList<Integer> labels = new ArrayList<>();
		FileUtils.readCSV(csvFilePath, images, labels);

		// Mat testSample = images.get(images.size() - 1);
		// Integer testLabel = labels.get(images.size() - 1);
		// images.remove(images.size() - 1);
		// labels.remove(labels.size() - 1);
		MatOfInt labelsMat = new MatOfInt();
		labelsMat.fromList(labels);
		EigenFaceRecognizer efr = EigenFaceRecognizer.create();

		efr.train(images, labelsMat);

		int[] outLabel = new int[1];
		double[] outConf = new double[1];
		System.out.println("Starting Prediction...");

		Rect rectCrop = new Rect(tl, br);
		Mat cropFace = new Mat(frame, rectCrop);
		Imgproc.resize(cropFace, cropFace, new Size(92, 112));

		Mat grayFrame = new Mat();
		// convert the frame in gray scale
		Imgproc.cvtColor(cropFace, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);

		efr.predict(grayFrame, outLabel, outConf);

		System.out.println("***Predicted label is " + outLabel[0] + ".***");

		// System.out.println("***Actual label is " + testLabel + ".***");
		System.out.println("***Confidence value is " + outConf[0] + ".***");

		double mark = 100 - (outConf[0] / 100);
		System.out.println("Mark:" + mark);
		if (mark < 50.00) {
			System.out.println("Mark:" + mark);
			return 0;
		}
		return outLabel[0];
	}
}
