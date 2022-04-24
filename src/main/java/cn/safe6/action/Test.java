package cn.safe6.action;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * 反序列化测试
 */
@WebServlet("/test")
public class Test extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();

        if (inputStream!=null){
            try {
                ObjectInputStream obs = new ObjectInputStream(inputStream);
                Object o = obs.readObject();
                o.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
