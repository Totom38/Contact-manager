package application.widgets;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * A label (actually a {@link Button}) that can be clicked to edit its text
 * content in a {@link TextField} replacing the {@link Button}.
 * When edition is complete the {@link TextField} is the replaced by the
 * {@link Button}
 */
public class EditableLabel extends StackPane implements Editable
{
	/**
	 * The transparent button on top
	 */
	@FXML
	private Button button;

	/**
	 * The Text Field underneath
	 */
	@FXML
	private TextField textField;

	/**
	 * Text content property
	 */
	private StringProperty text;

	/**
	 * Font property
	 */
	private ObjectProperty<Font> font;

	/**
	 * Property indicating Edition is on
	 */
	private BooleanProperty edition;

	/**
	 * Property indicating edtion is possible or not
	 */
	private BooleanProperty editable;

	/**
	 * Default Constructor
	 */
	public EditableLabel()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editable_label.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try
		{
			fxmlLoader.load();
		}
		catch (IOException e)
		{
			System.err.println("Can't load EditableLabel FXML file " + e.getMessage());
			throw new RuntimeException(e);
		}

		text = new SimpleStringProperty();
		text.bindBidirectional(button.textProperty());
		text.bindBidirectional(textField.textProperty());

		font = new SimpleObjectProperty<Font>();
		font.bindBidirectional(button.fontProperty());
		font.bindBidirectional(textField.fontProperty());

		edition = new SimpleBooleanProperty(false);
		editable = new SimpleBooleanProperty(false);

		button.setVisible(true);
		textField.setVisible(false);
	}

	/**
	 * Valued constructor
	 * @param text the text to set
	 */
	public EditableLabel(String text)
	{
		this();
		setText(text);
	}

	/**
	 * Valued constructor
	 * @param text the text to set
	 * @param font the font to set
	 */
	public EditableLabel(String text, Font font)
	{
		this();
		setText(text);
		setFont(font);
	}

	/**
	 * Text property access
	 * @return the text content property
	 */
	public final StringProperty textProperty()
	{
		return text;
	}

	/**
	 * Text content accessor
	 * @return the text content
	 */
	public final String getText()
	{
		return textProperty().get();
	}

	/**
	 * Text content mutator
	 * @param text the text to set
	 */
	public final void setText(final String text)
	{
		textProperty().set(text);
	}

	/**
	 * Font property access
	 * @return the font property
	 */
	public final ObjectProperty<Font> fontProperty()
	{
		return font;
	}

	/**
	 * Font accessor
	 * @return the font
	 */
	public final Font getFont()
	{
		return fontProperty().get();
	}

	/**
	 * Font mutator
	 * @param font the new font to set
	 */
	public final void setFont(final Font font)
	{
		fontProperty().set(font);
	}

	/**
	 * Edition Property access
	 * @return the edition property
	 */
	public final BooleanProperty editionProperty()
	{
		return edition;
	}

	/**
	 * Edition status accessor
	 * @return the edition status
	 */
	public final boolean isEdition()
	{
		return editionProperty().get();
	}

	/**
	 * Edition status mutator
	 * @param edition the new edition status
	 */
	public final void setEdition(final boolean edition)
	{
		editionProperty().set(edition);
	}

	/**
	 * Editable Property access
	 * @return the ditable property
	 */
	@Override
	public final BooleanProperty editableProperty()
	{
		return editable;
	}

	/**
	 * Get the editable status
	 * @return the editable status
	 */
	@Override
	public final boolean isEditable()
	{
		return editableProperty().get();
	}


	/**
	 * Set new editable status
	 * @param editable the new status to set
	 */
	@Override
	public final void setEditable(final boolean editable)
	{
		editableProperty().set(editable);
	}

	/**
	 * Callback triggered when {@link #button} is clicked
	 * @param event the event
	 */
	@FXML
	private void onEditionRequired(ActionEvent event)
	{
		if (editable.get())
		{
			if (!edition.isBound())
			{
				edition.set(true);
			}
			button.setVisible(false);
			textField.setVisible(true);
		}
	}

	/**
	 * Callback triggered when edition in {@link #textField} is completed
	 * @param event the event
	 */
	@FXML
	private void onEditionCompleted(ActionEvent event)
	{
		if (!edition.isBound())
		{
			edition.set(false);
		}
		textField.setVisible(false);
		button.setVisible(true);
	}
}
