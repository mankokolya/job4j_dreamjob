package ru.job4j.dreamjob.servlet;

import ru.job4j.dreamjob.model.Post;
import ru.job4j.dreamjob.store.MemStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PostServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        MemStore.instOf().save(
                new Post(
                        Integer.parseInt(request.getParameter("id")),
                        request.getParameter("name")));
        response.sendRedirect(request.getContextPath() + "/posts.do");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("posts", MemStore.instOf().findAllPosts());
        req.getRequestDispatcher("/post/posts.jsp").forward(req, resp);
    }
}
