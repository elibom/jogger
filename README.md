# Jogger

[![Build Status](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/badge/icon)](https://buildhive.cloudbees.com/job/germanescobar/job/jogger/)

A **micro-web framework** that provides a simple and elegant way of creating web applications programatically. Jogger brings the best ideas of other frameworks ([Express.js](http://expressjs.com/), [Ruby on Rails](http://rubyonrails.org/), [Sinatra](http://www.sinatrarb.com/) and [Flask](http://flask.pocoo.org/)) to create a solution that doesn't feel foreign in the Java language.

Jogger can be [integrated with the Spring framework](https://github.com/germanescobar/jogger/wiki/Spring-Integration-Guide). It can also run [on Heroku](https://github.com/germanescobar/jogger/wiki/Getting-Started-with-Heroku).

## Getting started

To get started, first [include the Jogger library](https://github.com/germanescobar/jogger/wiki/Including-Jogger) and then create a class with a *main* method like this:

```java
public class Main {

    public static void main(String[] args) throws Exception {
        RouterMiddleware router = new RouterMiddleware();
        router.get("/", new RouteHandler() {
            @Override
            public void handle(Request request, Response response) {
                response.write("<h1>Hello World!</h1>");
            }
        });
        
        Jogger app = new Jogger(router);
        app.listen(5000);
        app.join();
    }
}
```
Run it and point your browser to [http://localhost:5000](http://localhost:5000). There you go, you've just created your first Jogger app!

There is also a [Maven archetype](https://github.com/germanescobar/jogger/wiki/Getting-Started-with-the-Maven-Archetype) that will help you get started in seconds.

## Middlewares

In the previous example, we instantiated a `RouterMiddleware` and then passed it to the `Jogger` constructor. Middlewares are implementations of the `org.jogger.Middleware` interface that provide some functionality to the request/response lifecycle. Currently, there are two built-in middlewares:

* **StaticMiddleware**: serves static files (CSS, Javascript, images, etc.). [Learn more](https://github.com/germanescobar/jogger/wiki/StaticMiddleware).
* **RouterMiddleware**: routes requests to Java methods (controllers and actions). [Learn more](https://github.com/germanescobar/jogger/wiki/RouterMiddleware).

The following example shows how middlewares are instantiated and passed to the `Jogger` instance:

```java
public class Main {

    public static void main(String[] args) throws Exception {
        // assuming you have all your static files in a folder called public
        StaticMiddleware statik = new StaticMiddleware("public"); 
    
        RouterMiddleware router = new RouterMiddleware();
        // add routes
        
        // instantiate Jogger with both middlewares
        Jogger app = new Jogger(statik, router);
        app.listen(5000);
        app.join();
    }
}
```

If you run `Jogger` without providing at least one middleware, every request will return 404 Not Found response.

You can also create your own middlewares by implementing the `org.jogger.Middleware` interface. However, most of the times you'll be using interceptors instead (which are part of the `RouterMiddleware`).

## Rendering Templates

By default, Jogger is configured with a view template engine called [Freemarker](http://freemarker.sourceforge.net/) but [Jade](https://github.com/neuland/jade4j) is also supported.

You can render templates from your controllers by calling the `render(String templateName)` method of the `org.jogger.http.Response` class. Let's take a look at the following example:

```java
public class Pages {

    public void index(Request request, Response response) {
        response.render("index.ftl");
    }
}
```

This will render the `index.ftl` file in the root of your project. You can learn more about how to render templates [here](https://github.com/germanescobar/jogger/wiki/Templating-Guide).

## Testing

Jogger provides a testing framework that allows you to create integration tests easily. Let's see how this works with an example:

```java
public class PagesTest extends JoggerTest {
	
    @Test
    public void shouldRenderIndex() throws Exception {
		
        MockResponse response = get("/").run();
		
        Assert.assertEquals( response.getStatus(), Response.OK );
        Assert.assertEquals( response.getRenderedTemplate(), "index.ftl" );
        Assert.assertTrue( response.getOutputAsString().contains("test") );	
    }

    @Override
    protected Jogger getJogger() {
        Jogger app = new Jogger();
        // add configuration, routes and interceptors
        return app; 

        // a better approach is to have a JoggerFactory class that creates our Jogger object.
    }
}
```

You can learn more about testing [here](https://github.com/germanescobar/jogger/wiki/Testing-Guide).

## API docs (Javadocs)

You can find the API docs [here](http://germanescobar.net/projects/jogger/api/0.9.0/core/).
