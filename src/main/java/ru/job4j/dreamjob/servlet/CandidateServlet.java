package ru.job4j.dreamjob.servlet;

import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.store.MemStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CandidateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        System.out.println(req.getParameter("id"));
        System.out.println(req.getParameter("name"));
        MemStore.instOf().save(
                new Candidate(Integer.parseInt(req.getParameter("id")),
                        req.getParameter("name")));
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", MemStore.instOf().findAllCandidates());
        req.getRequestDispatcher("/candidate/candidates.jsp").forward(req, resp);
    }
}
