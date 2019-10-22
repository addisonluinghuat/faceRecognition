package com.demo.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class JpgToPgm {

	public static void main(String[] args) throws IOException {
		test();
	}

	public static void test() throws IOException {
		URL pngFile = JpgToPgm.class.getResource("/faceDetection.png");
		BufferedImage img = ImageIO.read(pngFile);

		for (String type : ImageIO.getWriterFormatNames()) {
			if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg")) {
				// Avoid issue #6 on OpenJDK8/Debian
				continue;
			}

			File f = File.createTempFile("imageio-test", "." + type);
			ImageIO.write(img, type, f);
			System.out.println(f);
			ImageIO.read(f);
		}
	}

}
