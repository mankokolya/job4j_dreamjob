package ru.job4j.dreamjob.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UploadServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> images = new ArrayList<>();
        File folder = new File("/images");
        System.out.println(folder.getAbsolutePath());
        File[] files = folder.listFiles();
        if (files != null) {
            images = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }
        request.setAttribute("images", images);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/upload/upload.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(request);
            File folder = new File("/images");
            System.out.println("Folder " + folder.getAbsolutePath());
            if (!folder.exists()) {
                System.out.println(folder.mkdir());
            }
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    File file = new File(folder + File.separator + item.getName());
                    System.out.println("File " + file.getAbsolutePath());
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                }
            }
        } catch (FileUploadException e) {
            logger.info("There was an error when uploading file", e);
        }
        doGet(request, response);
    }
}
