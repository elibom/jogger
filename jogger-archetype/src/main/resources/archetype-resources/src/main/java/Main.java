package ${package};

import org.jogger.JoggerServer;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// start the server
		JoggerServer server = new JoggerServer(new ApplicationFactory());
		server.listen(5000);
		System.out.println("Jogger is now running on port 5000 ... ");
	
		server.join();
	}

}
