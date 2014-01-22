package ${package};

import org.jogger.Jogger;

public class Main {

	public static void main(String[] args) throws Exception {
		// start the server
		Jogger app = JoggerFactory.create();
		app.listen(5000);
		System.out.println("Jogger is now running on port 5000 ... ");
	
		app.join();
	}

}
