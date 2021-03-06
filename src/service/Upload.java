package service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by 28713 on 2017/5/18.
 */
@WebServlet(value = "/servlet/Upload", name = "Upload")
public class Upload extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Logger logger = LogManager.getLogger();
        String savePath = getServletContext().getRealPath("/image");
        savePath = savePath + "/";
//        String savePath="D:\\IdeaProjects\\Striker\\web\\image\\";
        response.setCharacterEncoding("UTF-8");
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            if (!ServletFileUpload.isMultipartContent(request)) {
                logger.error("isMultipartContent: false");
                response.setStatus(500);
                return;
            }
            List<FileItem> list = upload.parseRequest(request);
            for (int i = 0; i < list.size(); i = i + 2) {
                FileItem item = list.get(i);
                String imgName = item.getString();
                System.out.println("image: " + i + " " + imgName);
                item = list.get(i + 1);
                String ContentType = item.getContentType();
                if (ContentType.startsWith("image/")) {
                    InputStream in = item.getInputStream();
                    File imgFile = new File(savePath + imgName);
                    logger.info("create file: " + imgFile);
                    FileOutputStream out = new FileOutputStream(imgFile);
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    imgFile.setReadable(true, false);
                    in.close();
                    out.close();
                    item.delete();
                } else {
                    logger.info("This is not an image file");
                    return;
                }
            }
        } catch (Exception e) {
            logger.error(e);
            response.setStatus(500);
        }
    }
}
