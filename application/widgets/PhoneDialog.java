package application.widgets;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.PhoneNumber;

/**
 * A Dialog to provide a valid pair <String, {@link PhoneNumber}>
 * by checking both name and number content at every change
 */
public class PhoneDialog extends Dialog<AbstractMap.SimpleImmutableEntry<String, PhoneNumber>>
{
	/**
	 * Text Field containing phone number's name
	 */
	@FXML
	private TextField nameTextField;

	/**
	 * Text Field containing phone number's value
	 * @implNote to be checked with {@link PhoneNumber#matcher(String)}
	 */
	@FXML
	private TextField numberTextField;

	/**
	 * Property indicating {@link #nameTextField}'s content is valid
	 */
	private BooleanProperty validName;

	/**
	 * Property indicating {@link #numberTextField}'s content is valid
	 * according to {@link PhoneNumber#matcher(String)}'s result.
	 */
	private BooleanProperty validNumber;

	/**
	 * Valid property indicating inputs in {@link #nameTextField} and
	 * {@link #numberTextField} are valid
	 * @implNote the idea here is to disable the {@link ButtonType#OK} button
	 * of the Dialog until both {@link #validName} and {@link #validNumber} are
	 * Ok
	 */
	private BooleanProperty valid;

	/**
	 * Default Constructor
	 */
	public PhoneDialog()
	{
		valid = new SimpleBooleanProperty(false);
		validName = new SimpleBooleanProperty(false);
		validNumber = new SimpleBooleanProperty(false);

		DialogPane pane = null;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("phone_dialog.fxml"));
		fxmlLoader.setController(this);
		try
		{
			pane = fxmlLoader.<DialogPane>load();
		}
		catch (IOException e)
		{
			System.err.println("Can't load EditableLabel FXML file " + e.getMessage());
			throw new RuntimeException(e);
		}

		setDialogPane(pane);

		/*
		 * Set valid when both validName && vaildNumber are Ok
		 */
		validName.addListener((observable, oldValue, newValue) -> {
			valid.set(validName.get() && validNumber.get());
		});
		validNumber.addListener((observable, oldValue, newValue) -> {
			valid.set(validName.get() && validNumber.get());
		});

		/*
		 * Check #numberTextField's content at every change
		 */
		numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			Matcher matcher = PhoneNumber.matcher(numberTextField.getText());
			boolean matches = matcher.matches();
//			System.out.println(numberTextField.getText() + (matches ? " matches" : " doesn't match")  + " valid phone number");
			validNumber.set(matches);
		});

		/*
		 * Check #nameTextField's content at every change
		 */
		nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			boolean matches = Pattern.matches("[A-Za-z0-9]+", nameTextField.getText());
//			System.out.println(nameTextField.getText() + (matches ? " matches" : " doesn't match")  + " [A-Za-z0-9]+");
			validName.set(matches);
		});

		/*
		 * Bind Ok button disable property to !valid property
		 */
		pane.lookupButton(ButtonType.OK).disableProperty().bind(valid.not());

		/*
		 * Set result converter to return a new <String, PhoneNumber> pair if
		 * ButtonType.OK is pressed and null otherwise
		 */
		setResultConverter(new Callback<ButtonType, AbstractMap.SimpleImmutableEntry<String, PhoneNumber>>()
		{
			@Override
			public AbstractMap.SimpleImmutableEntry<String, PhoneNumber> call(ButtonType param)
			{
				if (param == ButtonType.OK)
				{
					return new AbstractMap.SimpleImmutableEntry<String, PhoneNumber>(nameTextField.getText(), PhoneNumber.parse(numberTextField.getText()));
				}
				else
				{
					return null;
				}
			}
		});
	}
}
