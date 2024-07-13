package examples;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main program to test Jackson Library
 * @apiNote Jackson Library (installed in /pub/FISE_LAOB12/jackson) should
 * be created as "User Library" in your IDE and included in your project.
 */
public class JacksonExample
{
	/**
	 * Main program
	 * @param args arguments
	 */
	public static void main(String[] args)
	{
		// A Json array of objects
		String jsonString = """
{"employees":[
  { "name":"John", "age":30, "e-mail":"john@example.com"  },
  { "name":"Anna", "age":32, "e-mail":"anna@example.com" },
  { "name":"Peter", "age":25, "e-mail":"peter@example.com" }
]}""";
		// A Json object
		String jsonString2 = """
{ "name":"John", "age":30, "e-mail":"john@example.com"  }
""";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		try
		{
			rootNode = mapper.readTree(jsonString);
		}
		catch (JsonMappingException e)
		{
			e.printStackTrace();
		}
		catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}

		Objects.requireNonNull(rootNode);
		// Test array
		boolean isArray = rootNode.isArray();
		// Test object
		boolean isObject = rootNode.isObject();
		// Test Plain Old Java Object (No inheritance, no interfaces)
		boolean isPojo = rootNode.isPojo();
		// Test Value
		boolean isValueNode = rootNode.isValueNode();
		// Test text
		boolean isTextual = rootNode.isTextual();
		System.out.println("root node = " + rootNode);
		System.out.println("root node is ...");
		System.out.println((isArray ? "" : "not ") + "an array");
		System.out.println((isObject ? "" : "not ") + "an object");
		System.out.println((isPojo ? "" : "not ") + "an POJO");
		System.out.println((isValueNode ? "" : "not ") + "a value node");
		System.out.println((isTextual ? "" : "not ") + "a text value");

		try
		{
			JsonNode employeesNode = rootNode.get("employees");
			Objects.requireNonNull(employeesNode);
			isArray = employeesNode.isArray();
			isObject = employeesNode.isObject();
			isPojo = employeesNode.isPojo();
			isValueNode = employeesNode.isValueNode();
			isTextual = employeesNode.isTextual();
			System.out.println("employees node = " + employeesNode);
			System.out.println("employees node is ...");
			System.out.println((isArray ? "" : "not ") + "an array");
			System.out.println((isObject ? "" : "not ") + "an object");
			System.out.println((isPojo ? "" : "not ") + "an POJO");
			System.out.println((isValueNode ? "" : "not ") + "a value node");
			System.out.println((isTextual ? "" : "not ") + "a text value");

			if (isArray)
			{
				for (JsonNode localNode : employeesNode)
				{
					System.out.println("localNode = " + localNode);
					Person p = mapper.treeToValue(localNode, Person.class);
					Objects.requireNonNull(p);
					System.out.println("Person = " + p);
				}
			}

			try
			{
				rootNode = mapper.readTree(jsonString2);
			}
			catch (JsonMappingException e)
			{
				e.printStackTrace();
			}
			catch (JsonProcessingException e)
			{
				e.printStackTrace();
			}
			isArray = rootNode.isArray();
			isObject = rootNode.isObject();
			isPojo = rootNode.isPojo();
			isValueNode = rootNode.isValueNode();
			isTextual = rootNode.isTextual();

			Person personFromNode = mapper.treeToValue(rootNode, Person.class);
			Objects.requireNonNull(personFromNode);
			System.out.println("Person from node = " + personFromNode);

			Person personFromMapper = mapper.readValue(jsonString2, Person.class);
			Objects.requireNonNull(personFromMapper);
			System.out.println("Person from string = " + personFromMapper);

			assert(personFromNode.equals(personFromMapper));
		}
		catch (Exception e)
		{
			// Handle the exception
			e.printStackTrace();
		}
	}
}
