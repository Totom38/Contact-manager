package examples;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * Main program to test {@link Desktop} capabilities to launch brower, send
 * an e-mail or open directory with file explorer
 */
public class DesktopExample
{
	/**
	 * Main program
	 * @param args arguments
	 */
	public static void main(String[] args)
	{
		if (!Desktop.isDesktopSupported())
		{
			System.err.println("Sorry Desktop not supported");
			return;
		}
		Desktop desktop = Desktop.getDesktop();

		URI googleURI = null;
		try
		{
			googleURI = URI.create("https://www.google.com");
			System.out.println("URI scheme = " + googleURI.getScheme());
			System.out.println("URI scheme specific part = " + googleURI.getSchemeSpecificPart());
			System.out.println("URI host = " + googleURI.getHost());
			System.out.println("URI user info = " + googleURI.getUserInfo());
			System.out.println("URI authority = " + googleURI.getAuthority());
			System.out.println("URI path = " + googleURI.getPath());
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		Objects.requireNonNull(desktop);
		Objects.requireNonNull(googleURI);
		try
		{
			desktop.browse(googleURI);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}

		URI mailURI = null;
		try
		{
			mailURI = URI.create("mailto:david.roussel@ensiie.fr");
			System.out.println("URI scheme = " + mailURI.getScheme());
			System.out.println("URI scheme specific part = " + mailURI.getSchemeSpecificPart());
			System.out.println("URI host = " + mailURI.getHost());
			System.out.println("URI user info = " + mailURI.getUserInfo());
			System.out.println("URI authority = " + mailURI.getAuthority());
			System.out.println("URI path = " + mailURI.getPath());
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}

		Objects.requireNonNull(mailURI);

		try
		{
			desktop.mail(mailURI);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}

		URI fileURI = null;
		try
		{
			String userDir = System.getProperty("user.home");
			fileURI = URI.create("file://" + userDir);
			System.out.println("URI scheme = " + fileURI.getScheme());
			System.out.println("URI scheme specific part = " + fileURI.getSchemeSpecificPart());
			System.out.println("URI host = " + fileURI.getHost());
			System.out.println("URI user info = " + fileURI.getUserInfo());
			System.out.println("URI authority = " + fileURI.getAuthority());
			System.out.println("URI path = " + fileURI.getPath());
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}

		Objects.requireNonNull(fileURI);
		File dir = new File(fileURI);
		try
		{
			desktop.browseFileDirectory(dir);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}
}
