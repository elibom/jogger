package ${package};

import org.jogger.Jogger;
import org.jogger.JoggerFactory;
import org.jogger.http.Response;
import org.jogger.test.JoggerTest;
import org.jogger.test.MockResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApplicationTest extends JoggerTest {
	
	private JoggerFactory joggerFactory = new ApplicationFactory();
	
	@Test
	public void shouldRenderIndex() throws Exception {
		
		MockResponse response = get("/").run();
		
		Assert.assertEquals( response.getStatus(), Response.OK );
		Assert.assertEquals( response.getRenderedTemplate(), "hello.ftl" );
		Assert.assertTrue( response.getOutputAsString().contains("Hello World") );
		
	}

	@Override
	protected Jogger getJogger() throws Exception {
		return joggerFactory.configure();
	}

	
}
