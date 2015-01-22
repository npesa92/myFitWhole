package com.nicky.myfitbackend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nicholas on 11/21/14.
 */
public class Delete extends HttpServlet {

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    ImagesService imageService = ImagesServiceFactory.getImagesService();


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        try {
            String blobString = req.getParameter("blobString");
            BlobKey blobKey = new BlobKey(blobString);
            imageService.deleteServingUrl(blobKey);
            blobstoreService.delete(blobKey);

            out.print("1");
            out.flush();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
            out.print("2");
            out.print(e.getMessage());
            out.flush();
            out.close();
        }
    }
}
