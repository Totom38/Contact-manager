package application.widgets;

import javafx.beans.property.BooleanProperty;

/**
 * Interface implemented by all classes able to switch between edition on / off
 */
public interface Editable
{
	/**
	 * Editable Property access
	 * @return the ditable property
	 */
	BooleanProperty editableProperty();

	/**
	 * Get the editable status
	 * @return the editable status
	 */
	boolean isEditable();

	/**
	 * Set new editable status
	 * @param editable the new status to set
	 */
	void setEditable(boolean editable);
}
