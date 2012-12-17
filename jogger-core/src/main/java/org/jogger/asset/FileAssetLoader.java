package org.jogger.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jetty.http.MimeTypes;

public class FileAssetLoader implements AssetLoader {
	
	private File parent;
	
	public FileAssetLoader() {
		this("assets");
	}
	
	public FileAssetLoader(String directory) {
		if (directory == null || "".equals(directory)) {
			throw new IllegalArgumentException("No directory specified");
		}
		
		this.parent = new File(directory);
	}
	
	public FileAssetLoader(File parent) {
		if (parent == null) {
			throw new IllegalArgumentException("No parent specified");
		}
		
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
