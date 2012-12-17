package org.jogger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jogger.Route.HttpMethod;
import org.jogger.asset.AssetLoader;
import org.jogger.asset.FileAssetLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorEntry;
import org.jogger.routes.RoutesException;
import org.jogger.template.FreemarkerTemplateEngine;
import org.jogger.template.TemplateEngine;

/**
 * 
 * 
 * @author German Escobar
 */
public class Jogger {

	private List<Route> routes = new CopyOnWriteArrayList<Route>();

	private List<InterceptorEntry> interceptors = new CopyOnWriteArrayList<InterceptorEntry>();

	private AssetLoader assetLoader = new FileAssetLoader();
	
	private TemplateEngine templateEngine = new FreemarkerTemplateEngine();

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		if (routes == null) {
			throw new IllegalArgumentException("No routes provided");
		}
		this.routes = routes;
	}
	
	public void addRoute(Route route) {
		if (route == null) {
			throw new IllegalArgumentException("No route provided");
		}
		
		this.routes.add(route);
	}

	public void addRoute(HttpMethod httpMethod, String path, Object controller, String methodName) 
			throws NoSuchMethodException {
		
		if (controller == null) {
			throw new IllegalArgumentException("No controller provided");
		}

		Method method = controller.getClass().getMethod(methodName, Request.class, Response.class);
		addRoute(httpMethod, path, controller, method);
		
	}

	public void addRoute(HttpMethod httpMethod, String path, Object controller, Method method) {

		// validate signature
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length != 2 || !paramTypes[0].equals(Request.class) || !paramTypes[1].equals(Response.class)) {
			throw new RoutesException("Expecting two params of type org.jogger.http.Request and org.jogger.http.Response "
					+ "respectively");
		}

		method.setAccessible(true); // to access methods from anonymous classes

		routes.add(new Route(httpMethod, path, controller, method));

	}

	public void get(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.GET, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen ... unless we change the name of the method RouteHandler#handle
			throw new JoggerException(e);
		}
	}

	public void post(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.POST, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen ... unless we change the name of the method RouteHandler#handle
			throw new JoggerException(e);
		}
	}

	public List<InterceptorEntry> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<InterceptorEntry> interceptors) {
		if (interceptors == null) {
			throw new IllegalArgumentException("No interceptors provided");
		}
		this.interceptors = interceptors;
	}

	public void addInterceptor(Interceptor interceptor, String... paths) {
		if (interceptor == null) {
			throw new IllegalArgumentException("No interceptor provided");
		}
		interceptors.add(new InterceptorEntry(interceptor, paths));
	}

	public AssetLoader getAssetLoader() {
		return assetLoader;
	}

	public void setAssetLoader(AssetLoader assetLoader) {
		if (assetLoader == null) {
			throw new IllegalArgumentException("No assetLoader provided");
		}
		this.assetLoader = assetLoader;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		if (templateEngine == null) {
			throw new IllegalArgumentException("No templateEngine provided");
		}
		this.templateEngine = templateEngine;
	}

}
