package com.elibom.jogger.asset;

import com.elibom.jogger.asset.FileAssetLoader;
import com.elibom.jogger.asset.Asset;
import junit.framework.Assert;

import org.testng.annotations.Test;

public class FileAssetLoaderTest {

	@Test
	public void shouldFindExistingFile() throws Exception {
		FileAssetLoader loader = new FileAssetLoader("src");
		Asset asset = loader.load("test/resources/assets/asset.css");

		Assert.assertNotNull(asset);
	}

	@Test
	public void shouldReturnNullForNonExistingFile() throws Exception {
		FileAssetLoader loader = new FileAssetLoader();
		Assert.assertNull(loader.load("not/existent/file"));
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailWithEmptyParentDirectory() throws Exception {
		new FileAssetLoader("");
	}

}
