package examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Contact;
import model.JSONLoader;
import model.Loader;

/**
 * New example parsing example.json file
 */
public class JacksonExample2 extends Application
{
	/**
	 * Main program parsing example.json
	 * @param args program arguments
	 * @throws FileNotFoundException if file can't be opened
	 * @throws ParseException whenever a parse error occurs
	 * @throws NullPointerException whenever an unexpected JsonNode is encountered
	 * @throws IOException if {@link ObjectMapper} can't read file
	 */
	public static void main(String[] args)
	    throws FileNotFoundException,
	    ParseException,
	    NullPointerException,
	    IOException
	{
		launch(args);
	}

	/**
	 * The main entry point for all JavaFX applications.
	 * The start method is called after the init method has returned,
	 * and after the system is ready for the application to begin running.
	 * NOTE: This method is called on the JavaFX Application Thread.
	 * @throws FileNotFoundException if file can't be opened
	 * @throws ParseException whenever a parse error occurs
	 * @throws NullPointerException whenever an unexpected JsonNode is encountered
	 * @throws IOException if {@link ObjectMapper} can't read file
	 */
	@Override
	public void start(Stage primaryStage)
	    throws FileNotFoundException,
	    ParseException,
	    NullPointerException,
	    IOException,
	    Exception
	{
		// --------------------------------------------------------------------
		// JavaFX Setup (to ensure Image is valid)
		//---------------------------------------------------------------------
		VBox box  = new VBox();
		Label label = new Label("Test completed");
		Button button = new Button("Quit");
		button.setOnAction(event -> {primaryStage.close();});
		box.getChildren().addAll(label, button);
		Scene scene = new Scene(box, 200, 200, true, SceneAntialiasing.BALANCED);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Jackson Contacts Parsing example");
		Image image = new Image("icons/save-48.png");
		if (!image.isError())
		{
			primaryStage.getIcons().add(image);
		}
		primaryStage.show();

		// --------------------------------------------------------------------
		// Jackson Parsing from example.json
		// --------------------------------------------------------------------
		String inputPath = "../data/example.json";
		URL inputURL = JacksonExample2.class.getResource(inputPath);
		File inputFile = new File(inputURL.getFile());
		if (!inputFile.exists())
		{
			throw new FileNotFoundException(inputFile.getPath() + " doesn't exist");
		}

		Loader loader = new JSONLoader(inputFile);
		Set<Contact> contacts = null;
		try
		{
			contacts = loader.load();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// --------------------------------------------------------------------
		// Jackson writing to output.json
		// --------------------------------------------------------------------
		String outputPath = "output.json";
//		URL outputURL = JacksonExample2.class.getResource(outputPath);
		File outputFile = new File(outputPath);

		loader.setFile(outputFile);
		if (contacts != null)
		{
			try
			{
				loader.save(contacts);
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
