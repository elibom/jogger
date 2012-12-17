# Jogger

[![Build Status](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/badge/icon)](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/)

Inspired by [Express.js](http://expressjs.com/), [Ruby on Rails](http://rubyonrails.org/), [Sinatra](http://www.sinatrarb.com/) and [Flask](http://flask.pocoo.org/), Jogger is a lightweight web framework that moves away from the Servlet API (which sucks!) and provides a simple and elegant way of creating web applications programatically.

Instead of cloning another web framework, Jogger brings the best ideas of multiple frameworks (from multiple languages) and creates a solution that doesn't feel foreign in the Java language.

Let's take a look at the following (basic) example:

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
That's it! *Run it* and point your browser to [http://localhost:5000](http://localhost:5000). You are now ready to hack on your project!

## What's provided?

Out of the box Jogger comes with the following:

* An embedded [Jetty server](http://www.eclipse.org/jetty/).
* An asset handler that allows you to serve static files (CSS, Javascript, images, etc.)
* A routing mechanism that allows you to map HTTP requests to Java methods.
* A view template engine. The default is [Freemarker](http://freemarker.sourceforge.net/) but [Jade](https://github.com/neuland/jade4j) is also supported.
* A testing framework that allows you to create integration tests easily.

## Getting started
