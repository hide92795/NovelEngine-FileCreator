package hide92795.novelengine.filecreator;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter implements FilenameFilter {

	private final String extension;

	public FileExtensionFilter(String extension) {
		this.extension = extension;
	}

	public boolean accept(File dir, String name) {
		File file = new File(name);
		if (file.isDirectory()) {
			return false;
		}
		return (name.endsWith(extension));
	}

}
