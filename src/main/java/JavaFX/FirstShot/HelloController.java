package JavaFX.FirstShot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.stage.Stage;
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
	private MenuItem syncWith2Menu;

	@FXML
	private MenuItem syncWith1Menu;

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

	@FXML
	private Button okChangeNameButton;

	private TreeItem<String> selectedItem;
	private File folder_tab1;
	private File folder_tab2;
	private String selectedFilePath;

	public void okChangeNameButton() {
		log.info("okChangeNameButton pressed");
	}

	public void changeName() throws IOException {
		Stage dialog = new Stage();
		String fxmlFile = "/fxml/changeNamePopup.fxml";
		log.debug("Loading FXML for modal view from: {}", fxmlFile);
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
		loader.setController(new NewNameController());
		Parent rootNode = (Parent) loader.load();
		// Parent rootNode = (Parent)
		// loader.load(getClass().getResourceAsStream(fxmlFile));

		log.debug("Showing JFX scene");
		Scene scene = new Scene(rootNode);
		// scene.getStylesheets().add("/styles/styles.css");

		dialog.setTitle("Hello JavaFX and Maven");
		dialog.setScene(scene);
		dialog.show();
		//
		// log.info("Changing name of file: " + selectedItem.getValue());
		// log.info("Selected file path: " + selectedFilePath);
		// File f = new File(selectedFilePath);
		// File f2 = new File(selectedFilePath.substring(0,
		// selectedFilePath.length() - selectedItem.getValue().length())
		// + "dupa.txt");
		// Path source = f.toPath();
		// Path destination = f2.toPath();
		// Files.move(source, destination);

	}

	public void changePath(ActionEvent event) {
		DirectoryChooser fc = new DirectoryChooser();
		MenuItem mi = (MenuItem) event.getTarget();
		ContextMenu cm = mi.getParentPopup();
		Scene scene = cm.getScene();
		Window window = scene.getWindow();
		TreeItem<String> root = new TreeItem<String>();
		File folder = fc.showDialog(window);
		if (folder == null) {
			return;
		}
		if (tab1.isSelected()) {
			folder_tab1 = folder;
			chooseFolder.setVisible(false);
			root = createTree(event, folder_tab1);
			folderTreeView.setRoot(root);
		} else if (tab2.isSelected()) {
			folder_tab2 = folder;
			root = createTree(event, folder_tab2);
			chooseFolder1.setVisible(false);
			folderTreeView1.setRoot(root);
		}

	}

	private void sync(int currentFolder, int folderWithNewFiles) {
		log.info("Syncing folder " + currentFolder + " with folder " + folderWithNewFiles);
		File folderFrom;
		File folderTo;
		switch (currentFolder) {
		case 1:
			folderTo = folder_tab2;
			break;
		case 2:
			folderTo = folder_tab1;
			break;
		default:
			return;
		}

		switch (folderWithNewFiles) {
		case 1:
			folderFrom = folder_tab2;
			break;
		case 2:
			folderFrom = folder_tab1;
			break;
		default:
			return;
		}
		log.debug("FolderFrom: " + Paths.get(folderFrom.toURI()));
		log.debug("Folder to: " + Paths.get(folderTo.toURI()));
		CopyOption[] options = new CopyOption[] {};
		copy(folderFrom, folderTo, options);
		TreeItem<String> root = createTree(new ActionEvent(), folderTo);
		switch (currentFolder) {
		case 1:
			folderTreeView1.setRoot(root);
			break;
		case 2:
			folderTreeView.setRoot(root);
			break;
		default:
			return;
		}

	}

	private void copy(File folderFrom, File folderTo, CopyOption[] options) {
		Path pathFrom;
		Path pathTo;
		for (File file : folderFrom.listFiles()) {

			try {
				pathFrom = Paths.get(file.getAbsolutePath());
				pathTo = Paths.get(folderTo.getAbsolutePath(), file.getName());
				Files.copy(pathFrom, pathTo, options);
			} catch (IOException e) {
				log.error(e.toString());
			}
			if (file.isDirectory()) {
				File tempFolderTo = new File(folderTo.getAbsolutePath() + "/" + file.getName());
				copy(file, tempFolderTo, options);
			}
		}
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
			addBranchesToRoot(file, root);
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

			selectedFilePath = path.substring(6);
			// show Img
			Image img = new Image(path);
			imgView.fitWidthProperty().bind(stackPane.widthProperty());
			imgView.fitHeightProperty().bind(stackPane.heightProperty());
			imgView.setPreserveRatio(true);
			imgView.setImage(img);

		}
		selectedItem = tempItem;

	}

	private void addBranchesToRoot(File folder, TreeItem<String> root) {
		Image img = new Image(folder.toURI().toString(), 50, 50, true, true, true);
		Node imgView = new ImageView(img);
		TreeItem<String> treeitem = new TreeItem<>(folder.getName(), imgView);
		treeitem.setExpanded(true);
		root.getChildren().add(treeitem);
		if (!folder.isHidden() && folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				log.debug(file.getName());
				addBranchesToRoot(file, treeitem);

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

	public void close(ActionEvent event) throws InvalidFileFormatException, IOException {
		log.info("Closing...");
		saveInitValues();
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			loadInitValues();
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		syncWith2Menu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				sync(2, 1);

			}
		});
		syncWith1Menu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				sync(1, 2);
			}
		});

	}

	private void loadInitValues() throws InvalidFileFormatException, IOException {
		log.info("Loading init files...");
		Ini ini = new Ini(new File(
				"C:\\Users\\MIMLAK\\git\\learning-jfx\\Project\\FirstShot\\src\\main\\resources\\init\\init.ini"));
		if (!ini.get("initial_folders", "tab1").isEmpty() && (new File(ini.get("initial_folders", "tab1")).exists())) {
			folder_tab1 = new File(ini.get("initial_folders", "tab1"));
			TreeItem<String> root = createTree(new ActionEvent(), folder_tab1);
			folderTreeView.setRoot(root);
			chooseFolder.setVisible(false);
		}
		if (!ini.get("initial_folders", "tab2").isEmpty() && (new File(ini.get("initial_folders", "tab2")).exists())) {
			folder_tab2 = new File(ini.get("initial_folders", "tab2"));
			TreeItem<String> root = createTree(new ActionEvent(), folder_tab2);
			folderTreeView1.setRoot(root);
			chooseFolder1.setVisible(false);
		}
	}

	private void saveInitValues() throws InvalidFileFormatException, IOException {
		Ini ini = new Ini(new File(
				"C:\\Users\\MIMLAK\\git\\learning-jfx\\Project\\FirstShot\\src\\main\\resources\\init\\init.ini"));
		ini.clear();
		ini.put("initial_folders", "tab1", folder_tab1.toString());
		ini.put("initial_folders", "tab2", folder_tab2.toString());
		ini.store();
	}

}
