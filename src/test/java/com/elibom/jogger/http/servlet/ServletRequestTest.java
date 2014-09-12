package com.elibom.jogger.http.servlet;

import com.elibom.jogger.http.servlet.ServletRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import com.elibom.jogger.MockController;
import com.elibom.jogger.http.Cookie;
import com.elibom.jogger.http.FileItem;
import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;
import com.elibom.jogger.middleware.router.Route;
import com.elibom.jogger.middleware.router.Route.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ServletRequestTest {

    @Test
    public void shouldRetrieveHost() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getServerName()).thenReturn("localhost");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getHost(), "localhost");
    }

    @Test
    public void shouldRetrievePath() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("/context");
        when(servletRequest.getRequestURI()).thenReturn("/users/edit/1");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getPath(), "/users/edit/1");
    }

    @Test
    public void shouldRetrievePathVariables() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("");
        when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh/");

        Route route = new Route(HttpMethod.GET, "/users/{userId}/edit/{hash}", new MockController(),
                MockController.class.getMethod("init", Request.class, Response.class));
        Request request = new ServletRequest(servletRequest);
        request.setRoute(route);

        Map<String, String> pathVariables = request.getPathVariables();
        Assert.assertNotNull(pathVariables);
        Assert.assertEquals(pathVariables.size(), 2);
        Assert.assertEquals(pathVariables.get("userId"), "1");
        Assert.assertEquals(pathVariables.get("hash"), "th1s1s4hAsh");
    }

    @Test
    public void shouldNotRetrieveNonExistingPathVariable() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContextPath()).thenReturn("");
        when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh");

        Route route = new Route(HttpMethod.GET, "/users/1/edit/th1s1s4hAsh", new MockController(),
                MockController.class.getMethod("init", Request.class, Response.class));
        Request request = new ServletRequest(servletRequest);
        request.setRoute(route);

        Map<String, String> pathVariables = request.getPathVariables();
        Assert.assertNotNull(pathVariables);
        Assert.assertEquals(pathVariables.size(), 0);
    }

    @Test
    public void shouldRetrieveQueryString() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getQueryString()).thenReturn("method=test&action=success");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getQueryString(), "method=test&action=success");
    }

    @Test
    public void shouldRetrieveParams() throws Exception {
        Map<String, String[]> mockParams = new HashMap<String, String[]>();
        mockParams.put("param1", new String[]{"value1"});
        mockParams.put("param2", new String[]{"val1", "val2"});

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterMap()).thenReturn(mockParams);

        Request request = new ServletRequest(servletRequest);

        Map<String, String> params = request.getParameters();
        Assert.assertNotNull(params);
        Assert.assertEquals(params.size(), 2);

        String param1 = params.get("param1");
        Assert.assertNotNull(param1);
        Assert.assertEquals(param1, "value1");

        String param2 = params.get("param2");
        Assert.assertNotNull(param2);
        Assert.assertEquals(param2, "val1,val2");

        Assert.assertNull(params.get("notexistnet"));
    }

    @Test
    public void shouldRetrieveStringParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterValues("param1")).thenReturn(new String[]{"value1"});

        Request request = new ServletRequest(servletRequest);

        Assert.assertNotNull(request.getParameter("param1"));
        Assert.assertEquals(request.getParameter("param1"), "value1");
    }

    @Test
    public void shouldNotRetrieveNonExistingParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        Request request = new ServletRequest(servletRequest);
        Assert.assertNull(request.getParameter("nonexisting"));
    }

    @Test
    public void shouldRetrieveLongParam() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getParameterValues("param1")).thenReturn(new String[]{"1"});

        Request request = new ServletRequest(servletRequest);

        Assert.assertNotNull(request.getParameter("param1"));
        Assert.assertEquals(request.getParameter("param1"), "1");
    }

    @Test
    public void shouldRetrieveUrl() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://www.google.com:81/test"));

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getUrl(), "http://www.google.com:81/test");
    }

    @Test
    public void shouldRetrieveMethod() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest("GET");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getMethod(), "GET");
    }

    @Test
    public void shouldRetrieveRemoteAddress() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getRemoteAddr()).thenReturn("localhost");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getRemoteAddress(), "localhost");
    }

    @Test
    public void shouldRetrieveContentType() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getContentType()).thenReturn("application/json");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getContentType(), "application/json");
    }

    @Test
    public void shouldRetrievePort() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getServerPort()).thenReturn(1);

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getPort(), 1);
    }

    @Test
    public void shouldRetrieveIsSecure() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.isSecure()).thenReturn(true);

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.isSecure(), true);
    }

    @Test
    public void shouldRetrieveIsAjax() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getHeader("x-requested-with")).thenReturn("XMLHttpRequest");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.isAjax(), true);

        when(servletRequest.getHeader("x-requested-with")).thenReturn(null);
        Assert.assertEquals(request.isAjax(), false);

        when(servletRequest.getHeader("x-requested-with")).thenReturn("Another");
        Assert.assertEquals(request.isAjax(), false);
    }

    @Test
    public void shouldRetrieveCookie() throws Exception {
        javax.servlet.http.Cookie[] servletCookies = {new javax.servlet.http.Cookie("test-1", "1")};

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getCookies()).thenReturn(servletCookies);

        Request request = new ServletRequest(servletRequest);
        Map<String, Cookie> cookies = request.getCookies();

        Assert.assertEquals(cookies.size(), 1);

        Cookie cookie = cookies.get("test-1");
        Assert.assertNotNull(cookie);
        Assert.assertEquals(cookie.getValue(), "1");
        Assert.assertEquals(cookie.getMaxAge(), -1);
        Assert.assertNull(cookie.getDomain());
        Assert.assertNull(cookie.getPath());

        Assert.assertNotNull(request.getCookie("test-1"));
        Assert.assertNull(request.getCookie("not-existent"));
    }

    @Test
    public void shouldRetrieveEmptyCookies() throws Exception {
        javax.servlet.http.Cookie[] servletCookies = {};

        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getCookies()).thenReturn(servletCookies);

        Request request = new ServletRequest(servletRequest);
        Map<String, Cookie> cookies = request.getCookies();

        Assert.assertEquals(cookies.size(), 0);
    }

    @Test
    public void shoudlRetrieveHeader() throws Exception {
        HttpServletRequest servletRequest = mockServletRequest();
        when(servletRequest.getHeader("Authorization")).thenReturn("Basic ...");

        Request request = new ServletRequest(servletRequest);
        Assert.assertEquals(request.getHeader("Authorization"), "Basic ...");
    }

    @Test
    public void shouldRetrieveSingleFile() throws Exception {
        final InputStream bodyStream = getClass().getResourceAsStream("/multipart/single-file-fixed.txt");

        HttpServletRequest servletRequest = mockServletRequest("POST");
        when(servletRequest.getContentType()).thenReturn("multipart/form-data; boundary=AaB03x");
        when(servletRequest.getInputStream()).thenReturn(new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bodyStream.read();
            }

        });

        Request request = new ServletRequest(servletRequest);
        Assert.assertNotNull(request.getFiles());
        Assert.assertEquals(request.getFiles().length, 1);

        FileItem file = request.getFiles()[0];
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getContentType(), "text/plain");
        Assert.assertEquals(file.getFileName(), "file1.txt");

        Assert.assertNotNull(file.getHeaders());
        Assert.assertEquals(file.getHeaders().size(), 3);

        Assert.assertEquals(request.getParameter("submit-name"), "Larry");
    }

    @Test
    public void shouldRetrieveMultipleFiles() throws Exception {
        final InputStream bodyStream = getClass().getResourceAsStream("/multipart/multiple-files-fixed.txt");

        HttpServletRequest servletRequest = mockServletRequest("POST");
        when(servletRequest.getContentType()).thenReturn("multipart/form-data; boundary=AaB03x");
        when(servletRequest.getInputStream()).thenReturn(new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bodyStream.read();
            }

        });

        Request request = new ServletRequest(servletRequest);
        Assert.assertNotNull(request.getFiles());
        Assert.assertEquals(request.getFiles().length, 2);
    }

    private HttpServletRequest mockServletRequest() {
        return mockServletRequest("GET");
    }

    private HttpServletRequest mockServletRequest(String methodName) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getMethod()).thenReturn(methodName);

        return servletRequest;
    }

    private static void fixMultipartLineSeparators(String fileName) throws IOException {
        File file = new File("src/test/resources/multipart/" + fileName + "-fixed.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(ServletRequestTest.class.getResourceAsStream("/multipart/" + fileName + ".txt")));
            writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\r\n");
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String... args) throws IOException {
        //Warning 
        //Be carefull if shouldRetrieveSingleFile or shouldRetrieveMultipleFiles fail is probably because
        //your system change the line endings in the associated txt files.HTTP Multipart use CRLF as line ending
        //while Unix like systems use only LF.
        //If that is the problem runnign this main will fix it ;)
        fixMultipartLineSeparators("single-file");
        fixMultipartLineSeparators("multiple-files");
    }

}
