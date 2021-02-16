package ru.job4j.dreamjob.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        response.setContentType("name=" + name);
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        File file = new File("images" + File.separator + name);
        try (FileInputStream in = new FileInputStream(file)) {
            response.getOutputStream().write(in.readAllBytes());
        }
    }
}
