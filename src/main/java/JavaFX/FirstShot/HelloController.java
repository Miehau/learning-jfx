package JavaFX.FirstShot;

import java.awt.Desktop;
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
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

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private AnchorPane viewAnchorPane;

	@FXML
	private SplitPane splitPane;

	private TreeItem<String> selectedItem;
	private File folder_tab1;
	private File folder_tab2;
	private String selectedFilePath;
	private boolean fitToView = false;
	private ObservableValue<? extends Number> splitPaneRightWidth = new SimpleDoubleProperty(0);

	public void okChangeNameButton() {
		log.info("okChangeNameButton pressed");
	}

	public void changeName() throws IOException {
		Stage dialog = new Stage();
		String fxmlFile = "/fxml/changeNamePopup.fxml";
		log.debug("Loading FXML for modal view from: {}", fxmlFile);
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
		loader.setController(new NewNameController(selectedFilePath, selectedItem.getValue()));
		Parent rootNode = (Parent) loader.load();

		log.debug("Showing JFX scene");
		Scene scene = new Scene(rootNode);
		dialog.setTitle("Enter new file name:");
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setResizable(false);
		dialog.initOwner(chooseFolder.getScene().getWindow());
		dialog.setScene(scene);
		dialog.showAndWait();
		if (tab1.isSelected()) {
			folderTreeView.setRoot(createTree(new ActionEvent(), folder_tab1));
		} else if (tab2.isSelected()) {
			folderTreeView1.setRoot(createTree(new ActionEvent(), folder_tab2));
		}

	}

	public void scrollImageView() {
		log.info("Scrolling through imageView");
	}

	public void fitToView() {
		imgView.fitHeightProperty().bind(scrollPane.heightProperty());
		imgView.fitWidthProperty().bind(scrollPane.widthProperty());
		fitToView = true;
		log.info(imgView.getFitHeight() + "  " + imgView.getFitWidth());
		double imgHeight = imgView.getImage().getHeight();
		double imgWidth = imgView.getImage().getWidth();
		double imgViewHeight = scrollPane.getHeight();
		double ratio = imgHeight / imgWidth;

		switch (imgHeight < imgWidth ? 1 : 2) {
		case 1:
			imgView.setTranslateY(imgViewHeight / 2 - scrollPane.getWidth() * ratio / 2);
			break;
		case 2:
			break;
		default:
			break;

		}

	}

	public void showOriginalSize() {
		imgView.fitHeightProperty().bind(imgView.getImage().heightProperty());
		imgView.fitWidthProperty().bind(imgView.getImage().widthProperty());
		fitToView = false;
		imgView.setTranslateY(0);
	}

	private void openFile(File file) throws IOException {
		Desktop dt = Desktop.getDesktop();
		dt.open(file);
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
		boolean skipFirst = true;
		tempItem = selectedItem;
		if (!(selectedItem == null)) {
			StringBuilder sb = new StringBuilder();
			StringBuilder filePathBuilder = new StringBuilder();
			while (!(selectedItem.getParent() == null)) {
				sb.insert(0, "\\" + selectedItem.getValue());
				if (!skipFirst) {
					filePathBuilder.insert(0, "\\" + selectedItem.getValue());
				}
				skipFirst = false;
				selectedItem = selectedItem.getParent();
			}
			sb.delete(0, 1);
			if (tab1.isSelected()) {
				folder = folder_tab1;
			} else if (tab2.isSelected()) {
				folder = folder_tab2;
			}
			sb.insert(0, folder.toURI());
			filePathBuilder.insert(0, folder.toPath());
			String path = sb.toString();
			// path.replace(' ', '%');
			log.info("File path: " + path);
			selectedFilePath = filePathBuilder.toString();
			// show Img
			Image img = new Image(path);
			if (fitToView) {
				imgView.fitHeightProperty().bind(scrollPane.heightProperty());
				imgView.fitWidthProperty().bind(scrollPane.widthProperty());
			} else {
				imgView.fitHeightProperty().bind(img.heightProperty());
				imgView.fitWidthProperty().bind(img.widthProperty());
			}
			imgView.setPreserveRatio(true);
			imgView.setCache(true);
			imgView.setImage(img);
			imgView.setViewport(new Rectangle2D(0, 0, img.getWidth(), img.getHeight()));
			scrollPane.setContent(imgView);
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
		folderTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				chooseRow();
				if (event.getClickCount() == 2 && tab1.isSelected()) {
					try {
						log.debug("Handling event from doubleclick tab 1");
						log.debug("File path: " + selectedFilePath + "\\" + selectedItem.getValue());
						openFile(new File(selectedFilePath + "\\" + selectedItem.getValue()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		folderTreeView1.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				chooseRow();
				if (event.getClickCount() == 2 && tab2.isSelected()) {
					try {
						log.debug("Handling event from doubleclick tab 2");
						log.debug("File path: " + selectedFilePath + "\\" + selectedItem.getValue());
						openFile(new File(selectedFilePath + "\\" + selectedItem.getValue()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
		imgView.setOnScroll(e -> {
			double delta = e.getDeltaY();
			double scale = delta > 0 ? 1 / delta * 100 : -1 * delta / 100;
			Rectangle2D viewport = imgView.getViewport();
			double height = viewport.getHeight();
			double width = viewport.getWidth();
			double newX;
			double newY;
			Point2D mousePoint = new Point2D(e.getX(), e.getY());
			newX = mousePoint.getX() / imgView.getFitWidth() * width * scale + width * scale / 2;
			newY = mousePoint.getY() / imgView.getFitHeight() * height * scale;
			log.debug("mouse coords: " + mousePoint);
			System.out.println("x " + viewport.getMinX() + " y:" + viewport.getMinY() + " height: "
					+ viewport.getHeight() + " width: " + viewport.getWidth() + " delta: " + scale);
			log.info("new X: " + newX);
			log.info("new Y: " + newY);
			imgView.setCache(false);
			if (delta < 0)
				imgView.setViewport(new Rectangle2D(newX, newY, width * scale, height * scale));
			else {
				imgView.setViewport(
						new Rectangle2D(viewport.getMinX(), viewport.getMinY(), width * scale, height * scale));
			}

		});
		imgView.setOnMouseMoved(e -> {
			log.info("Mouse coords: [" + e.getX() + "," + e.getY() + "]");
			log.info("[Width, Height]: [" + imgView.getImage().getWidth() + "," + imgView.getImage().getHeight() + "]");
			log.info(" ");
		});
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (fitToView) {
				double imgHeight = imgView.getImage().getHeight();
				double imgWidth = imgView.getImage().getWidth();
				double imgViewWidth = (double) newVal;
				double imgViewHeight = scrollPane.getHeight();
				double ratio = imgHeight / imgWidth;

				imgView.setTranslateY(imgViewHeight / 2 - imgViewWidth * ratio / 2);
			}
			else{
				imgView.setTranslateY(0);
			}

		});

	}

	public void dupa() {
		System.out.println("dupa");
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
