# Jogger

[![Build Status](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/badge/icon)](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/)

Inspired by [Express.js](http://expressjs.com/), [Ruby on Rails](http://rubyonrails.org/), [Sinatra](http://www.sinatrarb.com/) and [Flask](http://flask.pocoo.org/), Jogger is a lightweight web framework that moves away from the Servlet API (which sucks!) and provides a simple and elegant way of creating web applications programatically.

Instead of cloning another web framework, Jogger brings the best ideas of multiple frameworks (from multiple languages) and creates a solution that doesn't feel foreign in the Java language.

A simple Jogger web application looks like this:

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

Notice that you don't need a Servlet or Application Container!
