package com.elibom.jogger.middleware.router.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A concrete implementation of {@link AbstractFileRoutesLoader} that loads the routes file from a file that exists in
 * the file system.
 *
 * @author German Escobar
 */
public class FileSystemRoutesLoader extends AbstractFileRoutesLoader {

	private File file;

	public FileSystemRoutesLoader() {
	}

	public FileSystemRoutesLoader(String filePath) {
		this(new File(filePath));
	}

	public FileSystemRoutesLoader(File file) {
		this.file = file;
	}

	@Override
	protected InputStream getInputStream() throws Exception {
		return new FileInputStream(file);
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFilePath(String filePath) {
		this.file = new File(filePath);
	}
}
