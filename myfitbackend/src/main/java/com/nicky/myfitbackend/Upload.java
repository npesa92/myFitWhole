package com.nicky.myfitbackend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;


import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nicholas on 11/21/14.
 */
public class Upload extends HttpServlet{

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<BlobKey> blobs = blobstoreService.getUploads(req).get("myFile");
        BlobKey blobKey = blobs.get(0);
        PrintWriter out = res.getWriter();
        if (blobKey == null) {
            out.print("");
            out.flush();
            out.close();
        } else {
            ImagesService imageService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);

            String servingUrl = imageService.getServingUrl(servingOptions);

            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json");

            JSONObject json = new JSONObject();
            json.put("servingUrl", servingUrl);
            json.put("blobKey", blobKey.getKeyString());

            out.print(json.toString());
            out.flush();
            out.close();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
        String uploadURL = blobstoreService.createUploadUrl("/blob/upload");
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        out.print(uploadURL);
        out.flush();
        out.close();
    }
}
