# Jogger

[![Build Status](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/badge/icon)](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/)

A **micro-web framework** that provides a simple and elegant way of creating web applications programatically. Jogger brings the best ideas of other frameworks ([Express.js](http://expressjs.com/), [Ruby on Rails](http://rubyonrails.org/), [Sinatra](http://www.sinatrarb.com/) and [Flask](http://flask.pocoo.org/)) to create a solution that doesn't feel foreign in the Java language.

## Getting started

To get started first [include the Jogger library](Including Jogger) and then create your first Jogger app like this:

```java
public class Main {

    public static void main(String[] args) {
    
        Jogger app = new Jogger();
        app.get("/", new RouteHandler() {
            @Override
            public void handle(Request request, Response response) {
                response.write("<h1>Hello World!</h1>");
            }
        });
        
        JoggerServer server = new JoggerServer(app);
        server.listen(5000);
        server.join();
        
    }
}
```
Run it and point your browser to [http://localhost:5000](http://localhost:5000). That's it!

## What's provided?

Out of the box Jogger comes with the following:

* An embedded [Jetty server](http://www.eclipse.org/jetty/).
* An asset handler that allows you to serve static files (CSS, Javascript, images, etc.)
* A routing mechanism that allows you to map HTTP requests to Java methods.
* A view template engine. The default is [Freemarker](http://freemarker.sourceforge.net/) but [Jade](https://github.com/neuland/jade4j) is also supported.
* A testing framework that allows you to create integration tests easily.

## Documentation

TODO
