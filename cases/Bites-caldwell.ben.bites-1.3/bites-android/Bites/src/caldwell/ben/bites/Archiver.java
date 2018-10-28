package caldwell.ben.bites;

import java.io.File;

public class Archiver {
	
	/**
	 * @author Ben Caldwell
	 * @param file
	 * @return True if successful
	 * 
	 * Archives the entire recipe book to "file"
	 */
	public static Boolean archive(File file) {
		//TODO: create an xml recipe book from all recipes in content provider
		return true;
	}
	
	/**
	 * @author Ben Caldwell
	 * @param file
	 * @return True if successful
	 * 
	 * Retrieves a recipe book from a recipe archive file in "file"
	 */
	public static Boolean retrieve(File file) {
		//TODO: pass xml document in file and add recipes to content provider
		return true;
	}
	
}
