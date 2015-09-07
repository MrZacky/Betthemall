import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

public class Main extends HttpServlet {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
@Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
      showHome(req,resp);
  }

  private void showHome(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	  Date data = new Date();
	  resp.getWriter().println("Witaj. Testowy tekst. " + data.toString());
	 }
  
 
  public static void main(String[] args) throws Exception{
    Server server = new Server(Integer.valueOf(System.getenv("PORT")));
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);
    context.addServlet(new ServletHolder(new Main()),"/*");
    server.start();
    server.join();
  }
}
