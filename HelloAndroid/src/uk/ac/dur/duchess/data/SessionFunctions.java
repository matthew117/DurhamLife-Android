package uk.ac.dur.duchess.data;

import uk.ac.dur.duchess.User;

public class SessionFunctions
{
	/**
	 * Called when the user successfully authenticates to create a persistent
	 * user session.
	 * 
	 * @param user
	 *            - the user object representing the user to log in.
	 */
	public static void saveUserPreferences(User user)
	{

	}

	/**
	 * Uses by parts of the application that require information about the
	 * current user.
	 * 
	 * @return a user object representing the current session or
	 *         <code>null</code> if no user is currently signed in.
	 */
	public static User getCurrentUser()
	{
		return null;
	}

	/**
	 * Called when the user logs out to end their session and remove their
	 * personal information.
	 */
	public static void endUserSession()
	{

	}
}
