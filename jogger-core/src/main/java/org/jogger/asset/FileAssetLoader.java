package org.jogger.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jetty.http.MimeTypes;
import org.jogger.util.Preconditions;

/**
 * An {@link AssetLoader} implementation that uses the file system to retrieve assets.
 * 
 * @author German Escobar
 */
public class FileAssetLoader implements AssetLoader {
	
	private final static String DEFAULT_BASE_DIRECTORY = "assets";
	
	private File parent;
	
	/**
	 * Constructor. Initializes the object with the default base directory.
	 */
	public FileAssetLoader() {
		this(DEFAULT_BASE_DIRECTORY);
	}
	
	/**
	 * Constructor. Initializes the object with the specified base <code>directory</code>. 
	 * 
	 * @param directory
	 */
	public FileAssetLoader(String directory) {
		Preconditions.notEmpty(directory, "no directory provided");
		this.parent = new File(directory);
	}
	
	public FileAssetLoader(File parent) {
		Preconditions.notNull(parent, "no parent provided");
		this.parent = parent;
	}

	@Override
	public Asset load(String fileName) {
		try {
			File file = new File(parent, fileName);
			
			if (!file.exists() || !file.isFile()) {
				return null;
			}
			
			MimeTypes mimeTypes = new MimeTypes();
			String contentType = mimeTypes.getMimeByExtension(file.getName()).toString();
			
			return new Asset(new FileInputStream(file), file.getName(), contentType, file.length());
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
