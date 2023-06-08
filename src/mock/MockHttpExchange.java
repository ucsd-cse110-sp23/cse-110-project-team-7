import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * A mock class extending the HttpExchange
 *   concrete class, for use with testing.
 *
 * This is an example of the dirty pattern idiom.
 */
class MockHttpExchange extends HttpExchange {
  String method;
  URI uri;
  InputStream is;
  OutputStream os;

  MockHttpExchange(String m, URI u, InputStream i, OutputStream o) {
    method = m;
    uri = u;
    is = i;
    os = o;
  }

  @Override
  public String getRequestMethod() {
    return method;
  }

  @Override
  public URI getRequestURI() {
    return uri;
  }

  @Override
  public InputStream getRequestBody() {
    return is;
  }

  @Override
  public void sendResponseHeaders(int code, long len) { }

  @Override
  public OutputStream getResponseBody() {
    return os;
  }

  /* Unused, required to override */
  public HttpPrincipal getPrincipal() {
    return null;
  }

  public void setStreams(InputStream i, OutputStream o) {
    is = i;
    os = o;
  }

  public void setAttribute(String name, Object value) { }

  public Headers getResponseHeaders() {
    return null;
  }

  public Headers getRequestHeaders() {
    return null;
  }

  public int getResponseCode() {
    return 0;
  }

  public String getProtocol() {
    return null;
  }

  public String getAttribute(String name) {
    return null;
  }

  public InetSocketAddress getLocalAddress() {
    return null;
  }

  public InetSocketAddress getRemoteAddress() {
    return null;
  }

  public void close() { }

  public HttpContext getHttpContext() {
    return null;
  }
}
