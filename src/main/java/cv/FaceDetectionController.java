package cv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import com.utils.FileUtils;
import com.utils.Utils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FaceDetectionController {
	// FXML buttons
	@FXML
	private Button cameraButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	// checkboxes for enabling/disabling a classifier

	@FXML
	private TextField username;
	@FXML
	private Button takePhoto;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// private ScheduledExecutorService speakTimer;
	Timer speakTimer = new Timer();
	// the OpenCV object that performs the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;

	private boolean canTakePhoto = false;

	// face cascade classifier
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;

	private static String basePath = System.getProperty("user.dir");
	private String resourcePath = basePath + "\\src\\main\\resources\\";
	private String haarcascadesPath = basePath
			+ "\\src\\main\\resources\\haarcascades\\haarcascade_frontalface_alt.xml";
	private static String csvFilePath = basePath + "\\src\\main\\resources\\TrainingData.txt";
	private String fileName = "tempPhoto.jpg";

	private List<String> speakList = new ArrayList<String>();

	/**
	 * Init the controller, at start time
	 */
	protected void init() {
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier();
		this.faceCascade.load(haarcascadesPath);
		this.absoluteFaceSize = 0;

		// set a fixed width for the frame
		originalFrame.setFitWidth(600);
		// preserve image ratio
		originalFrame.setPreserveRatio(true);
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startCamera() {
		if (!this.cameraActive) {

			// start the video capture
			this.capture.open(0);

			// is the video stream available?
			if (this.capture.isOpened()) {
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(originalFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();

				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				/*
				 * TimerTask speakTask = new TimerTask() { public void run() {
				 * System.out.print("speack task"); SpeechUtils.speak(speakList); } };
				 */

				// speakTimer.schedule(new SpeechUtils(), 0, 5000);
				// this.speakTimer = Executors.newSingleThreadScheduledExecutor();
				// this.speakTimer.scheduleAtFixedRate(speakTask, 0, 33, TimeUnit.MILLISECONDS);
				// update the button content
				this.cameraButton.setText("Stop Camera");
			} else {
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		} else {
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");
			// now the video capture can start

			// stop the timer
			this.stopAcquisition();
		}
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Mat grabFrame() {
		Mat frame = new Mat();

		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty()) {
					// face detection
					this.detectAndDisplay(frame);
				}

			} catch (Exception e) {
				// log the (full) error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return frame;
	}

	/**
	 * Method for face detection and tracking
	 * 
	 * @param frame
	 *            it looks for faces in this frame
	 * @throws IOException
	 */
	private void detectAndDisplay(Mat frame) throws IOException {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();

		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);

		// compute minimum face size (20% of the frame height, in our case)
		if (this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0) {
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}

		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

		// each rectangle in faces is a face: draw them!
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++) {

			if (canTakePhoto == false) {
				Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
				// Starting training
				int userNo = Utils.training(csvFilePath, facesArray[i].tl(), facesArray[i].br(), frame);
				String username = "";
				if (userNo == 0) {
					username = "UnKnown";
				} else {
					username = FileUtils.getUsernameByUserNo(resourcePath, "users.txt", userNo);
				}

				speakList.add(username);
				Imgproc.putText(frame, username, facesArray[i].tl(), Imgproc.FONT_HERSHEY_PLAIN, 2.0,
						new Scalar(255, 0, 0));
			} else {
				System.out.println("starting take photo");
				// 1.check username exists in users.txt
				HashMap<String, String> map = FileUtils.insert(resourcePath, username.getText());

				// 2. create new folder in photo folder
				if (map.size() != 0)
					FileUtils.createFolder(resourcePath + "photo\\" + map.get(username.getText()));

				// 3. create new photo
				Utils.createPhoto(facesArray[i].tl(), facesArray[i].br(), frame, resourcePath,
						"photo\\" + map.get(username.getText()) + "\\", fileName, map.get(username.getText()));

				canTakePhoto = false;
				takePhoto.setDisable(canTakePhoto);
			}
		}
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);

			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		speakTimer.cancel();
		/*
		 * if (this.speakTimer != null && !this.speakTimer.isShutdown()) { try { // stop
		 * the timer this.speakTimer.shutdown(); this.speakTimer.awaitTermination(33,
		 * TimeUnit.MILLISECONDS);
		 * 
		 * } catch (InterruptedException e) { // log any exception
		 * System.err.println("Exception trying to release the sound now... " + e); } }
		 */

		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
		}
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}

	@FXML
	protected void startTakePhoto() {
		canTakePhoto = true;
		takePhoto.setDisable(canTakePhoto);
	}

}
