package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTMLDocument.Iterator;

// import javax.swing.plaf.FileChooserUI;
import application.widgets.EditableLabel;
import application.widgets.PhoneDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
// import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logger.LoggerFactory;
import model.Address;
import model.Contact;
import model.ContactManager;
import model.CorporateContact;
import model.Note;
import model.JSONLoader;
import model.PersonalContact;
import model.PhoneNumber;
import utils.IconFactory;
import javafx.scene.control.ButtonBar;
import javafx.scene.Node;

/**
 * Controller associated with ContactsFrame.fxml
 * Contains:
 * <ul>
 * 	<li>FXML UI elements that need to be referenced in business logic</li>
 * 	<li>onXXXX() callback methods handling UI requests</li>
 * </ul>
 * @author davidroussel
 * @see Initializable so it can initialize FXML related attributes.
 */
public class Controller implements Initializable
{
	// -------------------------------------------------------------------------
	// internal attributes
	// -------------------------------------------------------------------------
	/**
	 * Logger to show debug message or only log them in a file
	 */
	private Logger logger = null;

	/**
	 * Reference to parent stage so it can be quickly closed on quit
	 * Initialized through {@link #setParentStage(Stage)} in
	 * {@link Main#start(Stage)}
	 */
	private Stage parentStage = null;

	/**
	 * The contact manager managinf contacts
	 */
	private ContactManager manager = null;

	/**
	 * Currently selected contact in {@link #contactListView} or null
	 * if there is no selected contact
	 */
	private Contact currentContact = null;

	/**
	 * Property indicating edition mode
	 * When Edition mode is off values can only be displayed in {@link Button}s
	 * When Edition mode is on values are still displayed in {@link Button} but
	 * these buttons can now be clicked to be replaced by {@link TextField}
	 * allowing value edition. When Edition is completed {@link TextField} are
	 * switched back to {@link Button}s
	 */
	private BooleanProperty edition;

	// -------------------------------------------------------------------------
	// FXML identified attributes (with fx:id)
	// WARNING Every attribute featuring an fx:id shall be referenced in
	// ConverterFrame.fxml
	// -------------------------------------------------------------------------

	/**
	 * List view containing {@link #contactsList}
	 */
	@FXML
	private ListView<Contact> contactListView;

	/**
	 * Combobox to filter Contacts in {@link #contactListView}
	 */
	@FXML
	private ComboBox<Contact.Type> typeComboBox;

	/**
	 * Search text field to filter Contacts in {@link #contactListView}
	 */
	@FXML
	private TextField searchField;

	/**
	 * Grid pane where contact details are shown
	 */
	@FXML
	private GridPane contactGridPane;

	/**
	 * Contact's 100x100 Icon
	 */
	@FXML
	private ImageView iconView;

	/**
	 * Title Label for company or staff
	 */
	@FXML
	private Label companyStaffTitle;

	/**
	 * VBox to show company or staff infos
	 */
	@FXML
	private VBox companyStaffVBox;

	/**
	 * Title Label for phones
	 */
	@FXML
	private Label phonesTitle;

	/**
	 * VBox to show phone numbers
	 */
	@FXML
	private VBox phonesVBox;

	/**
	 * Title Label for emails
	 */
	@FXML
	private Label emailsTitle;

	/**
	 * VBox to show e-mails
	 */
	@FXML
	private VBox emailsVBox;

	/**
	 * Title Label for addresses
	 */
	@FXML
	private Label addressesTitle;

	/**
	 * VBox to show addresses
	 */
	@FXML
	private VBox addressesVBox;

	/**
	 * Title Label for links
	 */
	@FXML
	private Label linksTitle;

	/**
	 * VBox to show links
	 */
	@FXML
	private VBox linksVBox;

	/**
	 * Title Label for notes
	 */
	@FXML
	private Label notesTitle;

	/**
	 * VBox to show notes
	 */
	@FXML
	private VBox notesVBox;

	/**
	 * Toolbar "Add" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddAction
	 */
	@FXML
	private Button addButton;

	/**
	 * Toolbar "Delete" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onDeleteAction
	 */
	@FXML
	private Button deleteButton;

	/**
	 * Toolbar "Edit" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @implNote In order to share its SelectedProperty with {@link #editMenuItem}
	 * and {@link #editContextMenuItem}
	 * @see onEditAction
	 */
	@FXML
	private ToggleButton editButton;

	/**
	 * Edit Menu "Edit" menu item
	 * @implNote In order to share its SelectedProperty with {@link #editButton}
	 * and {@link #editContextMenuItem}
	 */
	@FXML
	private CheckMenuItem editMenuItem;

	/**
	 * Listview's Context Menu "Edit" menu item
	 * @implNote In order to share its SelectedProperty with {@link #editButton}
	 * and {@link #editMenuItem}
	 */
	@FXML
	private CheckMenuItem editContextMenuItem;

	/**
	 * Toolbar "Quit" Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onQuitAction
	 */
	@FXML
	private Button quitButton;

	/**
	 * Contact Toolbar "Add Company" to Personal Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddCompanyAction
	 */
	@FXML
	private Button companyButton;

	/**
	 * Contact Toolbar "Add Staff" to Corporate Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddStaffAction
	 */
	@FXML
	private Button staffButton;

	/**
	 * Contact Toolbar "Add Image" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddImageAction
	 */
	@FXML
	private Button imageButton;

	/**
	 * Contact Toolbar "Add Phone Number" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddPhoneAction
	 */
	@FXML
	private Button phoneButton;

	/**
	 * Contact Toolbar "Add E-mail" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddEmailAction
	 */
	@FXML
	private Button emailButton;

	/**
	 * Contact Toolbar "Add Address" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddAddressAction
	 */
	@FXML
	private Button addressButton;

	/**
	 * Contact Toolbar "Add Link" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddLinkAction
	 */
	@FXML
	private Button linkButton;

	/**
	 * Contact Toolbar "Add Note" to Contact Button
	 * @implSpec Should be part of {@link #styleableButtons}
	 * @see onAddNoteAction
	 */
	@FXML
	private Button noteButton;

	/**
	 * Cancel Contact modifications Button
	 * @see onCancelAction
	 */
	@FXML
	private Button cancelButton;

	/**
	 * Sumit Contact modifications Button
	 * @see onSubmitAction
	 */
	@FXML
	private Button submitButton;

	/**
	 * Message Label at the bottom of UI
	 * (to be cleared at startup and used for info messages)
	 */
	@FXML
	private Label messageLabel;

	// -------------------------------------------------------------------------
	// Other FXML attributes
	// -------------------------------------------------------------------------
	/**
	 * First name field
	 */
	private EditableLabel firstName;

	/**
	 * Last name field
	 */
	private EditableLabel lastName;

	/**
	 * The list of contacts managed by this application
	 */
	private ObservableList<Contact> contactsList;

	/**
	 * List of buttons with display style that can change.
	 * These buttons are:
	 * <ul>
	 * 	<li>{@link #addButton}</li>
	 * 	<li>{@link #deleteButton}</li>
	 * 	<li>{@link #editButton}</li>
	 * 	<li>{@link #quitButton}</li>
	 * 	<li>{@link #quitButton}</li>
	 * 	<li>{@link #companyButton}</li>
	 * 	<li>{@link #staffButton}</li>
	 * 	<li>{@link #imageButton}</li>
	 * 	<li>{@link #phoneButton}</li>
	 * 	<li>{@link #emailButton}</li>
	 * 	<li>{@link #addressButton}</li>
	 * 	<li>{@link #linkButton}</li>
	 * 	<li>{@link #noteButton}</li>
	 * </ul>
	 */
	private List<Labeled> styleableButtons;

	private Map<String, EditableLabel> phonesEdit = new HashMap<>();
	private Map<String, EditableLabel> emailsEdit = new HashMap<>();
	private Map<String, EditableLabel> linksEdit = new HashMap<>();
	private Map<String, EditableLabel> notesEdit = new HashMap<>();
	private Map<String, VBox> addressesEdit = new HashMap<>();
	private Map<PersonalContact, HBox> companyStaffEdit = new HashMap<>();
	private EditableLabel companyEdit = null;

	/**
	 * Default constructor.
	 * @param manager the mananger containing contacts
	 * Initialize all non FXML attributes
	 */
	public Controller()
	{
		/*
		 * Can't get parent logger now, so standalone logger.
		 * Parent logger will be set in Main.
		 */
		logger = LoggerFactory.getParentLogger(getClass(), null, Level.INFO);

		/*
		 * complete Controller(): Load Data file instead
		 */

		
		manager = new ContactManager();

		//Tests dataset to be removed

		PhoneNumber phone1 = new PhoneNumber("0690762451");
		URI email1 = URI.create("pierre.durand@gmail.com");
		URI link1 = URI.create("http://www.pierre-durand.fr");
		Note note1 = new Note("note de test");
		@SuppressWarnings("deprecation")
		Locale locale1 = new Locale("fr", "FR");
		@SuppressWarnings("deprecation")
		Locale locale2 = new Locale("us", "US");
		Address address1 = new Address(1, "rue de la paix", "Paris", "75000", locale1);
		Address address2 = new Address("Hollywood Boulevard", "Los Angeles", "90001", locale2);

		PersonalContact p1 = new PersonalContact("Pierre", "Durand", null, phone1, email1, link1, note1, null);
		p1.addAddress("maison", address1);
		p1.addAddress("bureau", address2);
		manager.add(p1);
		PersonalContact p2 = new PersonalContact("Sophie", "Durand", null, null, null, null, null, null);
		manager.add(p2);
		PersonalContact p3 = new PersonalContact("Paul", "Dupont", null, null, null, null, null, null);
		manager.add(p3);
		CorporateContact ensiie = new CorporateContact("ENSIIE", null, null, null, null, null);
		ensiie.add(p1);
		ensiie.add(p2);
		manager.add(ensiie);
		contactsList = manager.getFilteredContacts();

		/*
		 * Setup edition property
		 */
		edition = new SimpleBooleanProperty();

		/*
		 * Setup Contact view in GridPane
		 */
		Font.getDefault();
		Font font = Font.font(24);
		firstName = new EditableLabel("[first]Name", font);
		lastName = new EditableLabel("lastName", font);
	}

	/**
	 * Controller initialization to initialize FXML related attributes.
	 * @param location The location used to resolve relative paths for the root
	 * object, or null if the location is not known.
	 * @param resources Resource Bundle containing translations resources for
	 * the UI (or null)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		styleableButtons = new ArrayList<Labeled>() ;
		styleableButtons.add(quitButton) ;
		styleableButtons.add(deleteButton) ;
		styleableButtons.add(editButton) ;
		styleableButtons.add(addressButton) ;
		styleableButtons.add(companyButton) ;
		styleableButtons.add(staffButton) ;
		styleableButtons.add(imageButton) ;
		styleableButtons.add(phoneButton) ;
		styleableButtons.add(addButton) ;
		styleableButtons.add(emailButton) ;
		styleableButtons.add(linkButton) ;
		styleableButtons.add(noteButton) ;
		onDisplayButtonsWithGraphicsOnlyAction(null) ;
		edition.bindBidirectional(editMenuItem.selectedProperty()) ;
		edition.bindBidirectional(editButton.selectedProperty()) ;
		edition.bindBidirectional(editContextMenuItem.selectedProperty()) ;
		cancelButton.visibleProperty().bind(edition) ;
		firstName.editableProperty().bind(edition) ;
		submitButton.visibleProperty().bind(edition) ;
		lastName.editableProperty().bind(edition) ;
		companyButton.disableProperty().bind(edition.not()) ;
		staffButton.disableProperty().bind(edition.not()) ;
		imageButton.disableProperty().bind(edition.not()) ;
		noteButton.disableProperty().bind(edition.not()) ;
		emailButton.disableProperty().bind(edition.not()) ;
		addressButton.disableProperty().bind(edition.not()) ;
		linkButton.disableProperty().bind(edition.not()) ;
		phoneButton.disableProperty().bind(edition.not()) ;
		contactListView.setItems(contactsList) ;
		contactListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE) ;
		GridPane.setConstraints(firstName, 1, 0) ;
		GridPane.setConstraints(lastName, 2, 0) ;
		contactGridPane.getChildren().addAll(firstName, lastName) ;
		ObservableList<Contact.Type> typeList = FXCollections.observableArrayList(Contact.Type.all()) ;
		typeComboBox.setItems(typeList) ;
		typeComboBox.getSelectionModel().select(Contact.Type.ALL) ;
		manager.typeFilteringProperty().bind(typeComboBox.valueProperty()) ;
		manager.searchedProperty().bind(searchField.textProperty()) ;
		contactListView.getSelectionModel().selectFirst() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		updateContactView(currentContact) ; }

	// ------------------------------------------------------------------------
	// Action Callbacks
	// ------------------------------------------------------------------------
public void onLoadAction(ActionEvent event) {
    try {
        JSONLoader loader = new JSONLoader(new File("chemin_vers_votre_fichier.json"));

        Set<Contact> contacts = loader.load();
        for (Contact contact : contacts) {
				System.out.println(contact);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Action triggered to save file
	 * @param event event associated with this action
	 */
	@FXML
	public void onSaveAction(ActionEvent event)
	{
		if (file != null) {
			try {
				// On Crée un FileChooser pour sélectionner l'emplacement de sauvegarde du fichier
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Contacts");
				
				// On Affiche la boîte de dialogue de sauvegarde et on obtient le fichier sélectionné
				File selectedFile = fileChooser.showSaveDialog(new Stage());
				
				if (selectedFile != null) {
					// On utiliser  Loader pour sauvegarder les contacts dans le fichier sélectionné
					Loader loader = createLoader(selectedFile);
					loader.save(contactSet);
					
					// On met à jour le fichier actuel ainsi que la date de dernière modification
					file = selectedFile;
					date = new Date();
					
					showMessage("Contacts saved successfully.");
				}
			} catch (IOException e) {
				// Gérer les erreurs d'entrée/sortie
				showMessage("Error: Unable to save contacts.");
				e.printStackTrace();
			}
		} else {
			// Si le fichier est nul, on affiche un message d'erreur
			showMessage("Error: No file selected.");
		}
		//complete onSaveAction
	}


	/**
	 * Action triggered to save file as ...
	 * @param event event associated with this action
	 */
	@FXML
	public void onSaveAsAction(ActionEvent event)
	{

		try {
			// De même on crée un FileChooser pour sélectionner l'emplacement de sauvegarde du fichier
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Contacts As");
			
			// Puis on affiche la boîte de dialogue de sauvegarde et obtenir le fichier sélectionné
			File selectedFile = fileChooser.showSaveDialog(new Stage());
			
			if (selectedFile != null) {
				// On utilise le Loader pour sauvegarder les contacts dans le fichier sélectionné
				Loader loader = createLoader(selectedFile);
				loader.save(contactSet);
				
				// Et enfin on met à jour le fichier actuel et la date de dernière modification
				file = selectedFile;
				date = new Date();
				
				showMessage("Contacts saved successfully.");
			}
		} catch (IOException e) {
			// Gérer les erreurs d'entrée/sortie
			showMessage("Error: Unable to save contacts.");
			e.printStackTrace();
		}
		//logger.info("Save as action triggered ...");
		//complete onSaveAsAction(ActionEvent event)...
		
	}

	/**
	 * Action to quit the application
	 * @param event event associated with this action
	 */
	@FXML
	public void onQuitAction(ActionEvent event)
	{
		quitActionImpl(event);
	}

	/**
	 * Action triggered to add a Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddAction(ActionEvent event) {
		logger.info("Action activée...");
		Dialog<Contact> dialog = new Dialog<>() ;
		dialog.setTitle("Ajouter contact") ;
		dialog.setHeaderText("Entrer les informations du contact") ;
		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE) ;
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ;
		GridPane contactForm = new GridPane() ;
		contactForm.setHgap(10) ;
		contactForm.setVgap(10) ;
		TextField firstNameField = new TextField() ;
		TextField lastNameField = new TextField() ;
		contactForm.add(new Label("Prénom :"), 0, 0) ;
		contactForm.add(firstNameField, 1, 0) ;
		contactForm.add(new Label("Nom de famille :"), 0, 1) ;
		contactForm.add(lastNameField, 1, 1) ;
		dialog.getDialogPane().setContent(contactForm) ;
		Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(true) ;
		firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty()) ; } ) ;
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				String firstName = firstNameField.getText() ;
				String lastName = lastNameField.getText() ;
				return new PersonalContact(firstName, lastName, null, null, null, null, null, null) ; }
			return null ; } ) ;
		Optional<Contact> result = dialog.showAndWait() ;
		result.ifPresent(contact -> {
			manager.add(contact) ;
			contactListView.getSelectionModel().select(contact) ;
			updateContactView(contact) ; } ) ; }

	/**
	 * Action triggered to Delete a Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onDeleteAction(ActionEvent event) {
		logger.info("Delete Action triggered ...") ; 
		Contact selectedContact = contactListView.getSelectionModel().getSelectedItem() ;
		if (selectedContact != null) {
			Alert confirmationDialog = new Alert(AlertType.CONFIRMATION) ;
			confirmationDialog.setTitle("Delete Contact") ;
			confirmationDialog.setHeaderText("Are you sure you want to delete this contact?") ;
			confirmationDialog.setContentText("Contact: " + selectedContact.getName()) ;
			Optional<ButtonType> result = confirmationDialog.showAndWait() ;
			if (result.isPresent() && result.get() == ButtonType.OK) {
				if (selectedContact instanceof CorporateContact) {
					CorporateContact corporateContact = (CorporateContact) selectedContact ;
					for (PersonalContact personalContact : corporateContact.getEmployees()) {
						personalContact.setCorporation(null) ; }
				} else if (selectedContact instanceof PersonalContact) {
					PersonalContact personalContact = (PersonalContact) selectedContact ;
					if (personalContact.getCorporation() != null) {
						personalContact.setCorporation(null) ; } }
				manager.remove(selectedContact) ;
				contactListView.getSelectionModel().clearSelection() ;
				updateContactView(null) ; } } }


	/**
	 * Action triggered to Toggle Contact edition
	 * @param event event associated with this action
	 */
	@FXML
	public void onEditAction(ActionEvent event) {
		boolean edited = false ;
		Object source = event.getSource() ;
		if (source instanceof ToggleButton) {
			ToggleButton button = (ToggleButton) source ;
			edited = button.isSelected() ; }
		else if (source instanceof CheckMenuItem) {
			CheckMenuItem item = (CheckMenuItem) source ;
			edited = item.isSelected() ; }
		else {
			logger.warning("unknown source for edit action: "
			    + source.getClass().getSimpleName()) ; }
		logger.info("Edit Action triggered ... (edition is "
		    + (edited ? "on" : "off") + ")") ; 
		edition.set(edited) ;
		if (edited) {
			addButton.setDisable(true) ;
			contactListView.setDisable(true) ;
			deleteButton.setDisable(true) ; } 
		else { 
			contactListView.setDisable(false) ;
			addButton.setDisable(false) ;
			deleteButton.setDisable(false) ; } }

	/**
	 * Action to show simple dialog presenting the application
	 * @param event event associated with this action
	 */
	@FXML
	public void onAboutAction(ActionEvent event)
	{
		logger.info("About action triggered");
		String fileName = new String("icons/speed-48.png");
		ImageView icon = null;
		try
		{
			icon = new ImageView(fileName);
		}
		catch (IllegalArgumentException iae)
		{
			logger.severe("couldn't load file " + fileName);
		}

		Alert alert = new Alert(AlertType.INFORMATION, "Contact Manager", ButtonType.OK);
		alert.setTitle("About ...");
		alert.setHeaderText("Contact Manager v1.0");
		alert.setContentText("A Simple application to manage contacts");
		if (icon != null)
		{
			alert.setGraphic(icon);
		}
		alert.showAndWait();
	}

	/**
	 * Action to add company info to {@link PersonalContact}
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddCompanyAction(ActionEvent event) {
		logger.info("Action ajout d'entreprise activé ...") ;
		Dialog<String> dialog = new Dialog<>() ;
		dialog.setTitle("Ajouter une entreprise") ;
		dialog.setHeaderText("Ajouter un nom d'entreprise déjà existant") ;
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE) ;
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ;
		GridPane companyForm = new GridPane() ;
		companyForm.setHgap(10) ;
		companyForm.setVgap(10) ;
		TextField companyNameField = new TextField() ;
		companyForm.add(new Label("Nom de l'entreprise :"), 0, 0) ;
		companyForm.add(companyNameField, 1, 0) ;
		dialog.getDialogPane().setContent(companyForm) ;
		Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(true) ;
		companyNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty()) ; } ) ;
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				return companyNameField.getText() ; }
			return null ; } ) ;
		Optional<String> result = dialog.showAndWait() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		result.ifPresent(companyName -> {
			boolean checkExist = false ;
			for (Contact contact : manager.getContacts()) {
				if (contact instanceof CorporateContact) {
					CorporateContact corporateContact = (CorporateContact) contact ;
					if (corporateContact.getName().equals(companyName)) {
						checkExist = true ;
						if (currentContact instanceof PersonalContact) {
							PersonalContact personalContact = (PersonalContact) currentContact ;
							if (personalContact.getCorporation() != null) {
								personalContact.setCorporation(null) ; ; }
							personalContact.setCorporation(corporateContact) ;
							corporateContact.add(personalContact) ; } break ; } } }
			if (!checkExist) {	
				Alert alert = new Alert(AlertType.WARNING) ;
				alert.setTitle("Entreprise non trouvée") ;
				alert.setHeaderText("L'entreprise n'existe pas") ;
				alert.setContentText("Veuillez vous assurer d'entrer un nom d'entreprise existant.") ;
				alert.showAndWait() ; } } ) ;
		deleteButton.setDisable(false) ;
		edition.set(false) ;
		addButton.setDisable(false) ;
		contactListView.setDisable(false) ;
		updateContactView(currentContact) ; }

	/**
	 * Action to add staff to {@link CorporateContact}
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddStaffAction(ActionEvent event) {
		logger.info("Action ajout de personnel activée ...") ; 
		Dialog<String[]> staffDialog = new Dialog<>() ;
		staffDialog.setTitle("Add Staff") ;
		staffDialog.setHeaderText("Entrer les informations du personnel") ;
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE) ;
		staffDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ;
		GridPane staffForm = new GridPane() ;
		staffForm.setVgap(10) ;
		staffForm.setHgap(10) ;
		TextField firstNameField = new TextField() ;
		TextField lastNameField = new TextField() ;
		staffForm.add(new Label("Nom de famille :"), 0, 1) ;
		staffForm.add(lastNameField, 1, 1) ;
		staffForm.add(new Label("Prénom :"), 0, 0) ;
		staffForm.add(firstNameField, 1, 0) ;
		staffDialog.getDialogPane().setContent(staffForm) ;
		Node saveButton = staffDialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(true) ;
		firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty() || lastNameField.getText().trim().isEmpty()) ; } ) ;
		lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(newValue.trim().isEmpty() || firstNameField.getText().trim().isEmpty()) ; } ) ;
		staffDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				return new String[]{firstNameField.getText(), lastNameField.getText()} ; }
			return null ; } ) ;
		Optional<String[]> result = staffDialog.showAndWait() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		result.ifPresent(staffDetails -> {
			String firstName = staffDetails[0] ;
			String lastName = staffDetails[1] ;
			boolean checkExist = false ;
			for (Contact contact : manager.getContacts()) {
				if (contact instanceof PersonalContact) {
					PersonalContact personalContact = (PersonalContact) contact ;
					if (personalContact.getFirstName().equals(firstName) && personalContact.getName().equals(lastName)) {
						checkExist = true ;
						if (currentContact instanceof CorporateContact) {
							CorporateContact corporateContact = (CorporateContact) currentContact ; 
							if (personalContact.getCorporation() != null) {
								personalContact.setCorporation(null) ; ; }
							personalContact.setCorporation(corporateContact) ;
							corporateContact.add(personalContact) ; } break ; } } }
			if (!checkExist) {	
				Alert alert = new Alert(AlertType.WARNING) ;
				alert.setTitle("Ce membre du personnel n'existe pas") ;
				alert.setHeaderText("Le membre du personnel n'existe pas") ;
				alert.setContentText("Veuillez vous assurer d'entrer un membre du personnel existant.") ;
				alert.showAndWait() ; } } ) ;
		deleteButton.setDisable(false) ;
		contactListView.setDisable(false) ;
		addButton.setDisable(false) ;
		edition.set(false) ;
		updateContactView(currentContact) ; }

	/**
	 * Action to add an image to selected contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddImageAction(ActionEvent event) {
		logger.info("Add Image action triggered ...") ;
		if (currentContact != null) {
			FileChooser fileChooser = new FileChooser() ;
			fileChooser.setTitle("Open Image File") ;
			fileChooser.getExtensionFilters()
			    .addAll(new ExtensionFilter("Image Files", "*.png","*.jpg","*.gif"),
			            new ExtensionFilter("All Files", "*.*")) ;
			File selectedFile = fileChooser.showOpenDialog(parentStage) ;
			if (selectedFile != null) {
				logger.info("Load " + selectedFile.getName() + " to current contact") ;
				URI imageURI = selectedFile.toURI() ;
				currentContact.setImage(imageURI) ;
				updateContactView(currentContact) ; } }
		edition.set(false) ;
		contactListView.setDisable(false) ;
		deleteButton.setDisable(false) ;
		addButton.setDisable(false) ;}

	/**
	 * Action to add PhoneNumber to Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddPhoneAction(ActionEvent event)
	{
		logger.info("Add Phone Number action triggered ...");
		PhoneDialog phoneDialog = new PhoneDialog();
		Optional<AbstractMap.SimpleImmutableEntry<String, PhoneNumber>> result = phoneDialog.showAndWait();
		if (result.isPresent())
		{
			AbstractMap.SimpleImmutableEntry<String, PhoneNumber> pair = result.get();
			String name = pair.getKey();
			PhoneNumber number = pair.getValue();
			logger.info("New Phone number: <" + name + ", " + number + ">");
			currentContact.addPhoneNumber(name, number) ;
			updateContactView(currentContact) ;
			edition.set(false) ;
			deleteButton.setDisable(false) ;
			addButton.setDisable(false) ;
			contactListView.setDisable(false) ; }
		else {
			logger.warning("Cancelled operation") ; } }

	/**
	 * Action to add E-mail to Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddEmailAction(ActionEvent event) {
		logger.info("Action ajout d'email activée ...") ; 
		Dialog<String[]> emailDialog = new Dialog<>() ;
		emailDialog.setTitle("Ajouter un e-mail") ;
		emailDialog.setHeaderText("Entrez les détails de l'e-mail") ;
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE) ;
		emailDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ; 
		GridPane emailForm = new GridPane() ;
		emailForm.setHgap(10) ;
		emailForm.setVgap(10) ;
		TextField nameField = new TextField() ;
		TextField addressBeforeField = new TextField() ;
		TextField addressAfterField = new TextField() ;
		emailForm.add(new Label("Name:"), 0, 0) ;
		emailForm.add(nameField, 1, 0) ;
		emailForm.add(new Label("Address before @:"), 0, 1) ;
		emailForm.add(addressBeforeField, 1, 1) ;
		emailForm.add(new Label("Address after @:"), 0, 2) ;
		emailForm.add(addressAfterField, 1, 2) ;
		emailDialog.getDialogPane().setContent(emailForm) ;
		Node saveButton = emailDialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(true) ;
		saveButton.disableProperty().bind(
			nameField.textProperty().isEmpty()
			.or(addressBeforeField.textProperty().isEmpty())
			.or(addressAfterField.textProperty().isEmpty())) ;
		nameField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("[a-z0-9.]*")) {
				nameField.setText(newValue.replaceAll("[^a-z0-9.]", "")) ; } } ) ;
		addressBeforeField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("[a-z0-9.]*")) {
				addressBeforeField.setText(newValue.replaceAll("[^a-z0-9.]", "")) ; } } ) ;
		addressAfterField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("[a-z0-9.]*")) {
				addressAfterField.setText(newValue.replaceAll("[^a-z0-9.]", "")) ; } } ) ;
		emailDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				String name = nameField.getText() ;
				String addressBefore = addressBeforeField.getText() ;
				String addressAfter = addressAfterField.getText() ;
				return new String[]{name, addressBefore, addressAfter} ; }
			return null ; } ) ;
		Optional<String[]> result = emailDialog.showAndWait() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		result.ifPresent(emailAddress -> {
			String name = emailAddress[0] ;
			String addressBefore = emailAddress[1] ;
			String addressAfter = emailAddress[2] ;
			URI email = URI.create(addressBefore + "@" + addressAfter) ;
			currentContact.addEmail(name, email) ; } ) ;
		updateContactView(currentContact) ;
		addButton.setDisable(false) ;
		contactListView.setDisable(false) ;
		deleteButton.setDisable(false) ;
		edition.set(false) ; }

	/**
	 * Action to add Address to Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddAddressAction(ActionEvent event) {
		logger.info("Action ajout d'adresse activée ...") ;
		Dialog<String[]> addressDialog = new Dialog<>() ;
		addressDialog.setTitle("Ajouter une adresse") ;
		addressDialog.setHeaderText("Entrez les détails de l'adresse") ;
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE) ;
		addressDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ;
		GridPane addressForm = new GridPane() ;
		addressForm.setHgap(10) ;
		addressForm.setVgap(10) ;
		TextField cityField = new TextField() ;
		TextField numberField = new TextField() ;
		TextField wayField = new TextField() ;
		TextField nameField = new TextField() ;
		TextField zipcodeField = new TextField() ;
		TextField countryField = new TextField() ;
		addressForm.add(new Label("Name:"), 0, 0) ;
		addressForm.add(nameField, 1, 0) ;
		addressForm.add(new Label("Number:"), 0, 1) ;
		addressForm.add(numberField, 1, 1) ;
		addressForm.add(new Label("Way:"), 0, 2) ;
		addressForm.add(wayField, 1, 2) ;
		addressForm.add(new Label("Zipcode:"), 0, 3) ;
		addressForm.add(zipcodeField, 1, 3) ;
		addressForm.add(new Label("City:"), 0, 4) ;
		addressForm.add(cityField, 1, 4) ;
		addressForm.add(new Label("Country:"), 0, 5) ;
		addressForm.add(countryField, 1, 5) ;
		addressDialog.getDialogPane().setContent(addressForm) ;
		Node saveButton = addressDialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(false) ;
		saveButton.disableProperty().bind(
			nameField.textProperty().isEmpty()
			.or(zipcodeField.textProperty().isEmpty())
			.or(cityField.textProperty().isEmpty()) ) ;
		addressDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				String number = numberField.getText().isEmpty() ? null : numberField.getText() ;
				String country = countryField.getText().isEmpty() ? "France" : countryField.getText() ;
				return new String[]{nameField.getText(), number, wayField.getText(), zipcodeField.getText(), cityField.getText(), country} ; }
			return null ; } ) ;
		Optional<String[]> result = addressDialog.showAndWait() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		result.ifPresent(addressDetails -> {
			String name = addressDetails[0] ;
			String number = addressDetails[1] ;
			String way = addressDetails[2] ;
			String zipcode = addressDetails[3] ;
			String city = addressDetails[4] ;
			String country = addressDetails[5] ;
			Locale locale = null ;
			for (Locale elt : Locale.getAvailableLocales()) {
				if (elt.getDisplayCountry().equals(country) || elt.getCountry().equals(country)) {
					locale = elt ;
					break ; } }
			if (locale == null) {
				locale = Locale.getDefault() ;
				for (Locale elt : Locale.getAvailableLocales()) {
					if (elt.toString().contains(country)) {
						locale = elt ;
						break ; } } }
			Address address = null ;
			if (number == null || number.isEmpty() || number=="") {
				address = new Address(way, city, zipcode, locale) ; }
			else {
				address = new Address(Integer.parseInt(number), way, city, zipcode, locale) ; }
			currentContact.addAddress(name, address) ; } ) ;
		updateContactView(currentContact) ;
		contactListView.setDisable(false) ;
		deleteButton.setDisable(false) ;
		edition.set(false) ;
		addButton.setDisable(false) ; }

	/**
	 * Action to add Link to Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddLinkAction(ActionEvent event)
	{
		logger.info("Action ajout de lien activée ...") ;
		Dialog<String[]> linkDialog = new Dialog<>() ;
		linkDialog.setTitle("Ajouter un lien") ;
		linkDialog.setHeaderText("Entrer les détails du lien") ;
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE) ;
		linkDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL) ;
		GridPane linkForm = new GridPane() ;
		linkForm.setHgap(10) ;
		linkForm.setVgap(10) ;
		TextField nameField = new TextField() ;
		TextField linkField = new TextField() ;
		linkForm.add(new Label("Nom :"), 0, 0) ;
		linkForm.add(nameField, 1, 0) ;
		linkForm.add(new Label("Lien :"), 0, 1) ;
		linkForm.add(linkField, 1, 1) ;
		linkDialog.getDialogPane().setContent(linkForm) ;
		Node saveButton = linkDialog.getDialogPane().lookupButton(saveButtonType) ;
		saveButton.setDisable(false) ;
		saveButton.disableProperty().bind(
			nameField.textProperty().isEmpty()
			.or(linkField.textProperty().isEmpty())) ;
		linkDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				return new String[]{nameField.getText(), linkField.getText()} ; }
			return null ; } ) ;
		Optional<String[]> result = linkDialog.showAndWait() ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		result.ifPresent(linkDetails -> {
			String name = linkDetails[0] ;
			String link = linkDetails[1] ;
			if (link.startsWith("http://") || link.startsWith("https://")) {
				URI url = URI.create(link) ;
				currentContact.addLink(name, url) ; } 
			else {
				Alert alert = new Alert(AlertType.ERROR, "Invalid URL", ButtonType.OK) ;
				alert.setTitle("Erreur URL invalide") ;
				alert.setHeaderText("URL invalide") ;
				alert.setContentText("L'URL doit commencer par http:// ou https://") ;
				alert.showAndWait() ; } } ) ;
		updateContactView(currentContact) ;
		edition.set(false) ;
		contactListView.setDisable(false) ;
		deleteButton.setDisable(false) ;
		addButton.setDisable(false) ; }

	/**
	 * Action to add Note to Contact
	 * @param event event associated with this action
	 */
	@FXML
	public void onAddNoteAction(ActionEvent event)
	{
		logger.info("Action ajout de note activée ...");


		Dialog<String[]> noteDialog = new Dialog<>();
		noteDialog.setTitle("Ajouter une note");
		noteDialog.setHeaderText("Entrez les détails de la note");
		ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
		noteDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
		GridPane noteForm = new GridPane();
		noteForm.setHgap(10);
		noteForm.setVgap(10);
		TextField nameField = new TextField();
		TextArea contentField = new TextArea();
		noteForm.add(new Label("Nom :"), 0, 0);
		noteForm.add(nameField, 1, 0);
		noteForm.add(new Label("Contenu :"), 0, 1);
		noteForm.add(contentField, 1, 1);
		noteDialog.getDialogPane().setContent(noteForm);
		Node saveButton = noteDialog.getDialogPane().lookupButton(saveButtonType);
		saveButton.setDisable(false);
		saveButton.disableProperty().bind(
			nameField.textProperty().isEmpty()
			.or(contentField.textProperty().isEmpty())
		);
		
		noteDialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				return new String[]{nameField.getText(), contentField.getText()};
			}
			return null;
		});
		
		Optional<String[]> result = noteDialog.showAndWait();
		currentContact = contactListView.getSelectionModel().getSelectedItem();
		result.ifPresent(noteDetails -> {
			String name = noteDetails[0];
			String content = noteDetails[1];
			// Create a new Note object with the provided details
			Note note = new Note(content);
			// Add the note to the current contact
			currentContact.addNote(name, note);
		});
		
		updateContactView(currentContact);
		edition.set(false);
		contactListView.setDisable(false);
		deleteButton.setDisable(false);
		addButton.setDisable(false);
	}

	/**
	 * Action to submit Contact modifications.
	 * Contact is then updated with UI values and refreshsed
	 * @param event event associated with this action
	 * @implNote if some fields in phones, e-mails, addresses, links or notes
	 * are blank, this means these elements should be deleted from Contact
	 */
	@FXML
	public void onSubmitAction(ActionEvent event)
	{
		logger.info("Action de soumission activée ...");
		currentContact = contactListView.getSelectionModel().getSelectedItem();

		if (currentContact != null) {
			// Update the contact with UI values
			
			// First name and Last name labels
			if (currentContact instanceof PersonalContact){
				PersonalContact pc = (PersonalContact) currentContact;
				boolean found = false;
				if (!firstName.getText().equals(pc.getFirstName())){
					if (!firstName.getText().equals("")){ //empeche les prenoms vides
						found = false;
						//on empeche les homonymes
						for (Contact contact : manager.getContacts()) {
							if (contact instanceof PersonalContact) {
								PersonalContact personalContactontact = (PersonalContact) contact;
								if (personalContactontact.getFirstName().equals(firstName.getText()) && personalContactontact.getName().equals(lastName.getText())) {
									found = true;
									break;
								}
							}
						}
						if (!found){
							if (pc.getCorporation()!=null){
								CorporateContact cc = pc.getCorporation();
								pc.setCorporation(null);
								pc.setFirstName(firstName.getText());
								pc.setCorporation(cc);
							}
							else{
								pc.setFirstName(firstName.getText());
							}
						}
					}
				}
				if (!lastName.getText().equals(pc.getName())){
					if (!lastName.getText().equals("")){ //empeche les noms vides
						found = false;
						//on empeche les homonymes
						for (Contact contact : manager.getContacts()) {
							if (contact instanceof PersonalContact) {
								PersonalContact personalContactontact = (PersonalContact) contact;
								if (personalContactontact.getFirstName().equals(firstName.getText()) && personalContactontact.getName().equals(lastName.getText())) {
									found = true;
									break;
								}
							}
						}
						if (!found){
							if (pc.getCorporation()!=null){
								CorporateContact cc = pc.getCorporation();
								pc.setCorporation(null);
								pc.setName(lastName.getText());
								pc.setCorporation(cc);
							}
							else{
								pc.setName(lastName.getText());
							}
						}
					}
				}
			}
			else{
				if (firstName.getText().equals("")){
					currentContact.setName(lastName.getText());
				}
				else{
					boolean found = false;
					//on empeche les homonymes
					for (Contact contact : manager.getContacts()) {
						if (contact instanceof CorporateContact) {
							CorporateContact corporateContact = (CorporateContact) contact;
							if (corporateContact.getName().equals(lastName.getText())) {
								found = true;
								break;
							}
						}
					}
					if (!found){
						currentContact.setName(firstName.getText());
					}
				}
			}


			// Phone labels
			for (Map.Entry<String, EditableLabel> entry : phonesEdit.entrySet()) {
				EditableLabel phoneLabel = entry.getValue();
				String phoneName = entry.getKey();
				if (phoneLabel.getText().equals("")) currentContact.removePhoneNumber(phoneName);
				else {
					if (!phoneLabel.getText().equals(currentContact.getPhoneNumber(phoneName).toString())){
						PhoneNumber phoneNumber = new PhoneNumber(phoneLabel.getText());
						currentContact.removePhoneNumber(phoneName);
						currentContact.addPhoneNumber(phoneName, phoneNumber);
					}
				}
			}

			// Email labels
			for (Map.Entry<String, EditableLabel> entry : emailsEdit.entrySet()) {
				EditableLabel emailLabel = entry.getValue();
				String emailName = entry.getKey();
				if (emailLabel.getText().equals("")) currentContact.removeEmail(emailName);
				else {
					if (!emailLabel.getText().equals(currentContact.getEmail(emailName).toString())){
						URI email = URI.create(emailLabel.getText());
						currentContact.removeEmail(emailName);
						currentContact.addEmail(emailName, email);
					}
				}
			}

			// Link labels
			for (Map.Entry<String, EditableLabel> entry : linksEdit.entrySet()) {
				EditableLabel linkLabel = entry.getValue();
				String linkName = entry.getKey();
				if (linkLabel.getText().equals("")) currentContact.removeLink(linkName);
				else {
					if (!linkLabel.getText().equals(currentContact.getLink(linkName).toString())){
						URI link = URI.create(linkLabel.getText());
						currentContact.removeLink(linkName);
						currentContact.addLink(linkName, link);
					}
				}
			}

			// Note labels
			for (Map.Entry<String, EditableLabel> entry : notesEdit.entrySet()) {
				EditableLabel noteLabel = entry.getValue();
				String noteName = entry.getKey();
				if (noteLabel.getText().equals("")) currentContact.removeNote(noteName);
				else {
					if (!noteLabel.getText().equals(currentContact.getNote(noteName).toString())){
						Note note = new Note(noteLabel.getText());
						currentContact.removeNote(noteName);
						currentContact.addNote(noteName, note);
					}
				}
			}

			// Company labels
			if (currentContact instanceof PersonalContact){
				PersonalContact pc = (PersonalContact) currentContact;

				if (companyEdit!=null){
					EditableLabel firstNameLabel = companyEdit;
					if (firstNameLabel.getText().equals("")){
						pc.setCorporation(null);
					}
					else{
						CorporateContact corporateContact;
						boolean found = false;
						for (Contact contact : manager.getContacts()) {
							if (contact instanceof CorporateContact) {
								corporateContact = (CorporateContact) contact;
								if (corporateContact.getName().equals(firstNameLabel.getText())) {
									pc.setCorporation(corporateContact);
									found = true;
									break;
								}
							}
						}
						if (!found){
							// If the company does not exist, we create it
							corporateContact = new CorporateContact(firstNameLabel.getText(), null, null, null, null, null);
							pc.setCorporation(null);
							pc.setCorporation(corporateContact);
							manager.add(corporateContact);
						}
					}
				}
			}
			else{
				CorporateContact cc = (CorporateContact) currentContact;
				for (Map.Entry<PersonalContact, HBox> entry : companyStaffEdit.entrySet()) {
					PersonalContact pc = entry.getKey();
					HBox hBox = entry.getValue();
					EditableLabel firstNameLabel = (EditableLabel) hBox.getChildren().get(0);
					EditableLabel lastNameLabel = (EditableLabel) hBox.getChildren().get(1);

					if (firstNameLabel.getText().equals("") || lastNameLabel.getText().equals("")){
						// The employee has to be removed
						pc.setCorporation(null);
					}
					else{
						Set<PersonalContact> ps = cc.getEmployees();
						boolean found = false;
						for (PersonalContact personalContact : ps) {
							if (personalContact.getFirstName().equals(firstNameLabel.getText()) && personalContact.getName().equals(lastNameLabel.getText())) {
								// The employee already exists
								found = true;
								break;
							}
						}
						if (!found){
							if (!(firstNameLabel.getText().equals(pc.getFirstName()) && lastNameLabel.getText().equals(pc.getName()))){
								for (Contact contact : manager.getContacts()) {
									if (contact instanceof PersonalContact) {
										PersonalContact personalContact = (PersonalContact) contact;
										if (personalContact.getFirstName().equals(firstNameLabel.getText()) && personalContact.getName().equals(lastNameLabel.getText())) {
											// The employee is still an existing contact
											pc.setCorporation(null);
											personalContact.setCorporation(cc);
											cc.add(personalContact);
											found = true;
											break;
										}
									}
								}
							}
						}
						if (!found){
							// The employee is not an existing contact so we have to create it
							PersonalContact personalContact = new PersonalContact(firstNameLabel.getText(), lastNameLabel.getText(), null, null, null, null, null, null);
							pc.setCorporation(null);
							manager.add(personalContact);
							cc.add(personalContact);
						}
					}
				}
			}


			// Address labels
			for (Map.Entry<String, VBox> entry : addressesEdit.entrySet()) {
				VBox addressVBox = entry.getValue();
				String addressName = entry.getKey();

				HBox addressHBox1 = (HBox) addressVBox.getChildren().get(0);
				HBox addressHBox2 = (HBox) addressVBox.getChildren().get(1);

				EditableLabel numberLabel = (EditableLabel) addressHBox1.getChildren().get(0);
				EditableLabel wayLabel = (EditableLabel) addressHBox1.getChildren().get(1);
				EditableLabel cityLabel = (EditableLabel) addressHBox2.getChildren().get(1);
				EditableLabel zipCodeLabel = (EditableLabel) addressHBox2.getChildren().get(0);
				EditableLabel localeLabel = (EditableLabel) addressHBox2.getChildren().get(2);


				if (wayLabel.getText().equals("") && cityLabel.getText().equals("") && zipCodeLabel.getText().equals("")) {
					// if way, city and zipCode are empty, we remove the address
					currentContact.removeAddress(addressName);
				}
				else{
					String city;
					String zipCode;
					String way;
					Locale locale = null;

					Address currentAddress = currentContact.getAddress(addressName);
					if (!wayLabel.getText().equals(currentAddress.getWay())){
						way = wayLabel.getText();
					}
					else{
						way = currentAddress.getWay();
					}
					if (!cityLabel.getText().equals(currentAddress.getCity())){
						city = cityLabel.getText();
					}
					else{
						city = currentAddress.getCity();
					}
					if (!zipCodeLabel.getText().equals(currentAddress.getZipCode())){
						zipCode = zipCodeLabel.getText();
					}
					else{
						zipCode = currentAddress.getZipCode();
					}
					if (!localeLabel.getText().equals(currentAddress.getLocale().toString())){
						// locale = new Locale(localeLabel.getText());

						for (Locale elt : Locale.getAvailableLocales()) {
							if (elt.getDisplayCountry().equals(localeLabel.getText()) || elt.getCountry().equals(localeLabel.getText())) {
								locale = elt;
								break;
							}
						}

						if (locale == null) {
							// Aucun Locale trouvé avec ce nom de pays
							locale = currentAddress.getLocale();
							for (Locale elt : Locale.getAvailableLocales()) {
								if (elt.toString().contains(localeLabel.getText())) {
									locale = elt;
									break;
								}
							}
						}

					}
					else{
						locale = currentAddress.getLocale();
					}

					Address address;
					if (currentAddress.getNumber().isPresent()){
						if (numberLabel.getText().equals("")){
							address = new Address(way, city, zipCode, locale);
						}
						else{
							address = new Address(Integer.parseInt(numberLabel.getText()), way, city, zipCode, locale);
						}
					}
					else{
						if (!numberLabel.getText().equals("")){
							address = new Address(Integer.parseInt(numberLabel.getText()), way, city, zipCode, locale);
						}
						else{
							address = new Address(way, city, zipCode, locale);
						}
					}

					currentContact.removeAddress(addressName);
					currentContact.addAddress(addressName, address);
				}
			}

			// Disable editing mode
			edition.set(false);
			contactListView.setDisable(false);
			deleteButton.setDisable(false);
			addButton.setDisable(false);

			// Refresh the contact view
			updateContactView(currentContact);
		}
	}

	/**
	 * Action to Cancel Contact modifications
	 * Contact UI is just refreshed with Contact values
	 * @param event event associated with this action
	 */
	@FXML
	public void onCancelAction(ActionEvent event) {
		logger.info("Cancel Modifications action triggered ...") ;
		currentContact = contactListView.getSelectionModel().getSelectedItem() ;
		updateContactView(currentContact) ; }

	/**
	 * Action triggered when ListView is clicked
	 * @param event event associated with this action
	 */
	public void onListSelectionChanged(MouseEvent event)
	{
		logger.info("Contact selection changed");
		currentContact = contactListView.getSelectionModel().getSelectedItem();
		if (currentContact != null)
		{
			logger.info("Selected Contact = " + currentContact);
		}
		updateContactView(currentContact);
	}

	/**
	 * Callback triggered when filtering conditions changes either in
	 * {@link #typeComboBox} or {@link #searchField}
	 * @param event event associated with this action
	 */
	public void onFilterPredicateChanged(ActionEvent event)
	{
		logger.info("Filter changed (type = " + typeComboBox.getValue().toString()
		        + ", search = " + searchField.getText() + ")");
		manager.setPredicate(typeComboBox.getValue(), searchField.getText());
	}

	/**
	 * Action to show buttons with Graphics only
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithGraphicsOnlyAction(ActionEvent event)
	{
		logger.info("Display Buttons with Graphics only action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		});
		clearMessage();
	}

	/**
	 * Action to show buttons with Text and Graphics
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithTextAndGraphicsAction(ActionEvent event)
	{
		logger.info("Display Buttons with Text and Graphics action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.LEFT);
		});
		clearMessage();
	}

	/**
	 * Action to show buttons with Text only
	 * @param event event associated with this action
	 */
	@FXML
	public void onDisplayButtonsWithTextOnlyAction(ActionEvent event)
	{
		logger.info("Display Buttons with Text only action triggered");
		styleableButtons.forEach((Labeled labeled) -> {
			labeled.setContentDisplay(ContentDisplay.TEXT_ONLY);
		});
		clearMessage();
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#INFO}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToInfoAction(ActionEvent event)
	{
		logger.info("Set Logger level up to INFO");
		setLoggerLevel(Level.INFO);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#WARNING}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToWarningAction(ActionEvent event)
	{
		logger.info("Set Logger level up to WARNING");
		setLoggerLevel(Level.WARNING);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#SEVERE}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelUpToSevereAction(ActionEvent event)
	{
		logger.info("Set Logger level up to SEVERE");
		setLoggerLevel(Level.SEVERE);
	}

	/**
	 * Action to set {@link #logger} level to {@link Level#OFF}
	 * @param event event associated with this action
	 */
	@FXML
	public void onSetLoggerLevelOffAction(ActionEvent event)
	{
		logger.info("Set Logger level to OFF");
		setLoggerLevel(Level.OFF);
        clearMessage();
	}

	// ------------------------------------------------------------------------
	// Utility methods
	// ------------------------------------------------------------------------

	/**
	 * Sets parent logger
	 * @param logger the new parent logger
	 */
	public void setParentLogger(Logger logger)
	{
		this.logger.setParent(logger);
	}

	/**
	 * Set parent stage (so it can be closed on quit)
	 * @param stage the new parent stage to set
	 */
	public void setParentStage(Stage stage)
	{
		parentStage = stage;
	}

	/**
	 * Update Contact view with selected Contact in {@link #contactListView}
	 * @param c the selected contact to display
	 */
	private void updateContactView(Contact c)
	{
		if (c != null)
		{
			boolean isPersonal = c instanceof PersonalContact;
			boolean isCorporate = c instanceof CorporateContact;

			lastName.setVisible(!isCorporate);

			Image image = c.getImage();
			if (image != null)
			{
				iconView.setImage(image);
			}
			if (isPersonal)
			{
				if (image == null)
				{
					iconView.setImage(IconFactory.getIcon("contacts"));
				}
				PersonalContact pc = (PersonalContact) c;
				if (pc.getCorporation() != null)
				{
					companyStaffTitle.setText("companie");
				}
				else
				{
					companyStaffTitle.setText("");
				}
				firstName.setText(pc.getFirstName());
				lastName.setText(c.getName());
				companyStaffVBox.getChildren().clear();
				companyStaffEdit.clear();
				companyEdit = null;
				if (pc.getCorporation() != null){
					EditableLabel value = new EditableLabel(pc.getCorporation().getName());
					value.editableProperty().bind(edition);
					companyEdit = value;
					companyStaffVBox.getChildren().add(value);
				}

				companyButton.setVisible(true);
				staffButton.setVisible(false);
				
			}
			else if (isCorporate)
			{
				if (image == null)
				{
					iconView.setImage(IconFactory.getIcon("building"));
				}
				CorporateContact cc = (CorporateContact) c;
				Set<PersonalContact> ps = cc.getEmployees();
				if (!ps.isEmpty())
				{
					companyStaffTitle.setText("staff");
				}
				else
				{
					companyStaffTitle.setText("");
				}
				firstName.setText(c.getName());
				lastName.setText("");
				companyStaffEdit.clear();
				companyEdit = null;
				companyStaffVBox.getChildren().clear();
				for (PersonalContact pc : ps){
						EditableLabel firstName = new EditableLabel(pc.getFirstName());
						firstName.editableProperty().bind(edition);
						EditableLabel lastName = new EditableLabel(pc.getName());
						lastName.editableProperty().bind(edition);
						HBox companyHBox = new HBox(10);
						companyHBox.setAlignment(Pos.CENTER_LEFT);
						companyHBox.getChildren().addAll(firstName, lastName);
						companyStaffVBox.getChildren().add(companyHBox);
						companyStaffEdit.put(pc, companyHBox);
				}

				companyButton.setVisible(false);
				staffButton.setVisible(true);

			}
			phonesEdit.clear();
			phonesVBox.getChildren().clear();
			for (String key : c.getPhoneNumberKeySet()){
				Label phoneLabel = new Label(key);
				phoneLabel.setPrefWidth(50.0);
				EditableLabel value = new EditableLabel(c.getPhoneNumber(key).toString());
				phonesEdit.put(key, value);
				value.editableProperty().bind(edition);
				HBox phoneHBox = new HBox(10);
				phoneHBox.setAlignment(Pos.CENTER_LEFT);
				phoneHBox.getChildren().addAll(phoneLabel, value);
				phonesVBox.getChildren().add(phoneHBox);
			}

			emailsEdit.clear();
			emailsVBox.getChildren().clear();
			for (String key : c.getEmailKeySet()){
				Label emailLabel = new Label(key);
				emailLabel.setPrefWidth(50.0);
				EditableLabel value = new EditableLabel(c.getEmail(key).toString());
				emailsEdit.put(key, value);
				value.editableProperty().bind(edition);
				HBox emailHBox = new HBox(10);
				emailHBox.setAlignment(Pos.CENTER_LEFT);
				emailHBox.getChildren().addAll(emailLabel, value);
				emailsVBox.getChildren().add(emailHBox);
			}

			addressesVBox.getChildren().clear();
			addressesEdit.clear();
			for (String key : c.getAddressKeySet()){
				Label addressLabel = new Label(key);
				addressLabel.setPrefWidth(50.0);

				Optional <Integer> number = c.getAddress(key).getNumber();
				EditableLabel numberLabel;
				if (number.isPresent()){
					numberLabel = new EditableLabel(c.getAddress(key).getNumber().get().toString());
				}
				else {
					numberLabel = new EditableLabel("");
				}
				numberLabel.editableProperty().bind(edition);
				numberLabel.setPrefWidth(70);

				EditableLabel way = new EditableLabel(c.getAddress(key).getWay());
				way.editableProperty().bind(edition);
				HBox addressHBox1 = new HBox();
				addressHBox1.setAlignment(Pos.CENTER_LEFT);
				addressHBox1.getChildren().addAll(numberLabel, way);

				EditableLabel city = new EditableLabel(c.getAddress(key).getCity());
				city.editableProperty().bind(edition);
				EditableLabel zipcode = new EditableLabel(c.getAddress(key).getZipCode());
				zipcode.editableProperty().bind(edition);
				zipcode.setPrefWidth(70);
				HBox addressHBox2 = new HBox();
				addressHBox2.setAlignment(Pos.CENTER_LEFT);
				addressHBox2.getChildren().addAll(zipcode, city);

				EditableLabel localeLabel;
				Locale locale = c.getAddress(key).getLocale();
				if (!locale.getDisplayCountry().equals("France")){
					localeLabel = new EditableLabel(c.getAddress(key).getLocale().getDisplayCountry());
				}
				else {
					localeLabel = new EditableLabel("");
				}
				localeLabel.editableProperty().bind(edition);
				addressHBox2.getChildren().add(localeLabel);


				VBox addressVBox = new VBox();
				addressVBox.setAlignment(Pos.CENTER_LEFT);
				addressVBox.getChildren().addAll(addressHBox1, addressHBox2);

				HBox addressHBox = new HBox(10);
				addressHBox.setAlignment(Pos.CENTER_LEFT);
				addressHBox.getChildren().addAll(addressLabel, addressVBox);

				addressesEdit.put(key, addressVBox);

				addressesVBox.getChildren().add(addressHBox);
			}

			linksEdit.clear();
			linksVBox.getChildren().clear();
			for (String key : c.getLinksKeySet()){
				Label linkLabel = new Label(key);
				linkLabel.setPrefWidth(50.0);
				EditableLabel value = new EditableLabel(c.getLink(key).toString());
				linksEdit.put(key, value);
				value.editableProperty().bind(edition);
				HBox linkHBox = new HBox(10);
				linkHBox.setAlignment(Pos.CENTER_LEFT);
				linkHBox.getChildren().addAll(linkLabel, value);
				linksVBox.getChildren().add(linkHBox);
			}

			notesEdit.clear();
			notesVBox.getChildren().clear();
			for (String key : c.getNotesKeySet()){
				Label noteLabel = new Label(key);
				noteLabel.setPrefWidth(50.0);
				EditableLabel value = new EditableLabel(c.getNote(key).toString());
				notesEdit.put(key, value);
				value.editableProperty().bind(edition);
				HBox noteHBox = new HBox(10);
				noteHBox.setAlignment(Pos.CENTER_LEFT);
				noteHBox.getChildren().addAll(noteLabel, value);
				notesVBox.getChildren().add(noteHBox);
			}

		}
		else	// c == null
		{
			firstName.setText("");
			lastName.setText("");
			companyStaffTitle.setText("");
			companyStaffVBox.getChildren().clear();
			phonesVBox.getChildren().clear();
			emailsVBox.getChildren().clear();
			addressesVBox.getChildren().clear();
			linksVBox.getChildren().clear();
			notesVBox.getChildren().clear();

			phonesEdit.clear();
			emailsEdit.clear();
			linksEdit.clear();
			notesEdit.clear();
			addressesEdit.clear();
			companyStaffEdit.clear();
			companyEdit = null;
		}
	}

	/**
	 * Implementation of the quit logic.
	 * Closes the stage.
	 * @param event the event passed to this callback (either {@link ActionEvent}
	 * or {@link WindowEvent} depending on what triggered this action).
	 */
	protected void quitActionImpl(Event event)
	{
		/*
		 * 	- closes the stage by
		 * 		- getting the stage from source if event is a WindowEvent
		 * 		- getting the stage from #parentStage or otherwise if event is
		 * 		an ActionEvent
		 */
		logger.info("Quit action triggered");

		Object source = event.getSource();
		Stage stage = null;

		if (event instanceof WindowEvent)
		{
			// Stage is the source
			stage = (Stage) source;
		}
		else if (event instanceof ActionEvent)
		{
			if (parentStage != null)
			{
				// We already have a registered stage
				stage = parentStage;
			}
			else
			{
				// Search for the stage
				if (source instanceof Button)
				{
					Button sourceButton = (Button) source;
					stage = (Stage) sourceButton.getScene().getWindow();
				}
				else
				{
					logger.warning("Unable to get Stage to close from: "
					    + source.getClass().getSimpleName());
				}
			}
		}
		else
		{
			logger.warning("Unknwon event source: " + event.getSource());
		}

		if (stage != null)
		{
			stage.close();
		}
		else
		{
			logger.warning("Window not closed");
		}
	}

	/**
	 * Clear message at the bottom of UI
	 * To be used in every any callback which doesn't show info message
	 */
	private void clearMessage()
	{
		messageLabel.setText(null);
	}

	/**
	 * Set {@link #logger} level
	 * @param level the level to set on {@link #logger}
	 */
	private void setLoggerLevel(Level level)
	{
		if (logger != null)
		{
			logger.setLevel(level);
		}
	}
}
