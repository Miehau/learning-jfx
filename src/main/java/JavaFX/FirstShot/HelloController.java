package JavaFX.FirstShot;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

public class HelloController implements Initializable {
	private static final Logger log = LoggerFactory.getLogger(HelloController.class);
	// @FXML private TextField firstNameField;
	// @FXML private TextField lastNameField;
	// @FXML private Label messageLabel;
	@FXML
	private TreeView<String> folderTreeView;

	@FXML
	private TreeView<String> folderTreeView1;

	@FXML
	private Button chooseFolder;

	@FXML
	private Button chooseFolder1;

	@FXML
	private MenuItem choosePath;

	@FXML
	private MenuItem changePath;

	@FXML
	private ImageView imgView;

	@FXML
	private AnchorPane anchorPaneView;

	@FXML
	private AnchorPane tabAnchorPane;

	@FXML
	private StackPane stackPane;

	@FXML
	private Tab tab1;

	@FXML
	private Tab tab2;

	@FXML
	private TabPane tabPane;

	private TreeItem<String> selectedItem;
	private File folder_tab1;
	private File folder_tab2;

	public void changePath(ActionEvent event) {
		DirectoryChooser fc = new DirectoryChooser();
		MenuItem mi = (MenuItem) event.getTarget();
		ContextMenu cm = mi.getParentPopup();
		Scene scene = cm.getScene();
		Window window = scene.getWindow();
		TreeItem<String> root = new TreeItem<String>();
		if (chooseFolder.isVisible()) {
			chooseFolder.setVisible(false);
		}

		if (tab1.isSelected()) {
			folder_tab1 = fc.showDialog(window);
			root = createTree(event, folder_tab1);
			folderTreeView.setRoot(root);
		} else if (tab2.isSelected()) {
			folder_tab2 = fc.showDialog(window);
			root = createTree(event, folder_tab2);
			folderTreeView1.setRoot(root);
		}

	}

	public void choosePath(ActionEvent event) {
	}

	public void chooseFolder(ActionEvent event) {
		final DirectoryChooser dirChooser = new DirectoryChooser();
		File folder = null;
		log.debug("Choosing folder...");

		folder = dirChooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (folder == null) {
			return;
		}
		if (tab1.isSelected()) {
			folder_tab1 = folder;
			chooseFolder.setVisible(false);
		} else if (tab2.isSelected()) {
			folder_tab2 = folder;
			chooseFolder1.setVisible(false);
		}

		TreeItem<String> root = createTree(event, folder);
		if (tab1.isSelected()) {
			folderTreeView.setEditable(true);
			folderTreeView.setRoot(root);
		} else if (tab2.isSelected()) {
			folderTreeView1.setEditable(true);
			folderTreeView1.setRoot(root);
		}
	}

	private TreeItem<String> createTree(ActionEvent event, File folder) {

		log.debug("File path: " + folder.getAbsolutePath());
		Image img = new Image(folder.toURI().toString());
		Node imgView = new ImageView(img);
		TreeItem<String> root = new TreeItem<>(folder.getAbsolutePath().toString(), imgView);
		root.setExpanded(true);
		for (File file : folder.listFiles()) {
			performBranchCreation(file, root);
		}
		return root;
	}

	public void chooseRow() {
		File folder = null;
		TreeItem<String> tempItem = new TreeItem<String>("");
		tempItem = selectedItem;
		if (!(selectedItem == null)) {
			StringBuilder sb = new StringBuilder();
			while (!(selectedItem.getParent() == null)) {
				sb.insert(0, "/" + selectedItem.getValue());
				selectedItem = selectedItem.getParent();
			}
			sb.delete(0, 1);
			if (tab1.isSelected()) {
				folder = folder_tab1;
			} else if (tab2.isSelected()) {
				folder = folder_tab2;
			}
			sb.insert(0, folder.toURI());
			String path = sb.toString();
			// path.replace(' ', '%');
			log.info("File path: " + path);

			// show Img
			Image img = new Image(path);
			imgView.fitWidthProperty().bind(stackPane.widthProperty());
			imgView.fitHeightProperty().bind(stackPane.heightProperty());
			imgView.setPreserveRatio(true);
			imgView.setImage(img);

		}
		selectedItem = tempItem;

	}

	private void performBranchCreation(File folder, TreeItem<String> root) {
		Image img = new Image(folder.toURI().toString(), 50, 50, true, true, true);
		Node imgView = new ImageView(img);
		TreeItem<String> treeitem = new TreeItem<>(folder.getName(), imgView);
		treeitem.setExpanded(true);
		root.getChildren().add(treeitem);
		if (!folder.isHidden() && folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				log.debug(file.getName());
				performBranchCreation(file, treeitem);

			}
		}

	}

	public void sayHello() {

		// String firstName = firstNameField.getText();
		// String lastName = lastNameField.getText();
		//
		// StringBuilder builder = new StringBuilder();
		//
		// if (!StringUtils.isEmpty(firstName)) {
		// builder.append(firstName);
		// }
		//
		// if (!StringUtils.isEmpty(lastName)) {
		// if (builder.length() > 0) {
		// builder.append(" ");
		// }
		// builder.append(lastName);
		// }
		//
		// if (builder.length() > 0) {
		// String name = builder.toString();
		// log.debug("Saying hello to " + name);
		// messageLabel.setText("Hello " + name);
		// } else {
		// log.debug("Neither first name nor last name was set, saying hello to
		// anonymous person");
		// messageLabel.setText("Hello mysterious person");
		// }
	}

	public void close(ActionEvent event) {
		log.info("Closing...");
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		folderTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
					TreeItem<String> newValue) {
				selectedItem = newValue;
			}

		});
		folderTreeView1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
					TreeItem<String> newValue) {
				selectedItem = newValue;
			}

		});

	}

}
