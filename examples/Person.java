package examples;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple person class instrumented for Jackson Library
 */
public class Person
{
	/**
	 * Person's name
	 */
	private String name;
	/**
	 * Person's age
	 */
	private int age;
	/**
	 * Person's email
	 */
	private String email;

	/**
	 * Default constructor
	 */
	public Person()
	{
		name = null;
		age = 0;
		email = null;
	}

	/**
	 * Valued constructor instrumented for Jackson Library
	 * @param name person's name
	 * @param age person's age
	 * @param email person's email
	 */
	@JsonCreator
	public Person(@JsonProperty("name") String name,
	              @JsonProperty("age") int age,
	              @JsonProperty("e-mail") String email)
	{
		super();
		this.name = name;
		this.age = age;
		this.email = email;
	}

	/**
	 * Name accessor instrumented for Jackson Library
	 * @return the name
	 */
	@JsonProperty("name")
	public String getName()
	{
		return name;
	}

	/**
	 * Age Accessor instrumented for Jackson Library
	 * @return the age
	 */
	@JsonProperty("age")
	public int getAge()
	{
		return age;
	}

	/**
	 * Email accessor instrumented for Jackson Library
	 * @return the email
	 */
	@JsonProperty("e-mail")
	public String getEmail()
	{
		return email;
	}

	/**
	 * Name mutator instrumented for Jackson Library
	 * @param name the name to set
	 */
	@JsonProperty("name")
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Age Mutator instrumented for Jackson Library
	 * @param age the age to set
	 */
	@JsonProperty("age")
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 * Email mutator instrumented for Jackson Library
	 * @param email the email to set
	 */
	@JsonProperty("e-mail")
	public void setEmail(String email)
	{
		this.email = email;
	}

	@Override
	public String toString()
	{
		return String.format("Name: %s, age: %d, email: %s", name, age, email);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + age;
		result = (prime * result) + ((email == null) ? 0 : email.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Person other = (Person) obj;
		if (age != other.age)
		{
			return false;
		}
		if (email == null)
		{
			if (other.email != null)
			{
				return false;
			}
		}
		else if (!email.equals(other.email))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}
}
