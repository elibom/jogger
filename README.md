# Jogger

[![Build Status](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/badge/icon)](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/)

Inspired by Express.js, Ruby on Rails, Sinatra (Ruby) and Flask (Python), Jogger is a lightweight web framework for the Java community that moves away from the Servlet API (which sucks!) and provides a simple and elegant way of creating web applications programatically.

Instead of cloning another web framework, Jogger brings the best ideas of multiple frameworks (from multiple languages) and creates a solution that doesn't feels foreign in the Java language.

```java
public class Main {

    public static void main(String[] args) {
        Jogger app = new Jogger();
        app.get("/", new RouteHandler() {
            @Override
            public void handle(Request request, Response response) {
                response.write("<h1>Hello World!</h1>")
            }
        });
        
        JoggerServer server = new JoggerServer(app);
        server.listen(5000);
        server.join();
    }
}
```