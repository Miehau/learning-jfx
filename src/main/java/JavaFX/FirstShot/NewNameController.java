package JavaFX.FirstShot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class NewNameController extends Application {

	Logger log = LoggerFactory.getLogger(NewNameController.class);

	@FXML
	private PasswordField newFileName;
	@FXML
	private Button okChangeNameButton;

	public void okChangeNameButton() {
		log.info("okChangeNameButton pressed");
		log.info("New file name: " + newFileName.getText());
	}

	public void close(ActionEvent e) {
		log.info("Closing...");
		stop();
	}

	@Override
	public void stop() {

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

	}
}
