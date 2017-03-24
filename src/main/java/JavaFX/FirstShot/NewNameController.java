package JavaFX.FirstShot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewNameController extends Application {

	Logger log = LoggerFactory.getLogger(NewNameController.class);

	@FXML
	private TextField newFileName;
	@FXML
	private Button okChangeNameButton;
	private String selectedFilePath;
	private String selectedFileName;

	public NewNameController(String selectedFilePath, String selectedFileName) {
		log.debug("Granted string: " + selectedFilePath + "\\" + selectedFileName);
		this.selectedFilePath = selectedFilePath;
		this.selectedFileName = selectedFileName;
	}

	public void okChangeNameButton(ActionEvent e) {
		log.info("New file name: " + newFileName.getText());
		try {
			changeName(newFileName.getText());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!newFileName.getText().isEmpty()) {
			((Stage) okChangeNameButton.getScene().getWindow()).close();
		}
	}

	public void close(ActionEvent e) {
		log.info("Closing modal window...");
		((Stage) okChangeNameButton.getScene().getWindow()).close();
	}

	@Override
	public void stop() {

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}

	private void changeName(String fileName) throws IOException {
		log.info("Selected file path: " + selectedFilePath);
		File f = new File(selectedFilePath + "\\" + selectedFileName);
		File f2 = new File(selectedFilePath + "\\" + fileName + "."
				+ (selectedFileName.split("\\."))[(selectedFileName.split("\\.")).length - 1]);
		Files.move(f.toPath(), f2.toPath());

	}
}
