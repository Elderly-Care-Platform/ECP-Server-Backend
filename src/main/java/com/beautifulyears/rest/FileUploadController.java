package com.beautifulyears.rest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import com.beautifulyears.domain.FileUpload;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.Util;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    private static final int THUMBNAIL_IMG_WIDTH = 200;
    private static final int THUMBNAIL_IMG_HEIGHT = 200;
    private static final String imagePath = "/resources/";

    @RequestMapping(value = "/fileupload", headers = ("content-type=multipart/*"), method = RequestMethod.POST)
    public Object uploadResources(HttpServletRequest servletRequest, @ModelAttribute FileUpload userFile)
            throws Exception {
        // Get the uploaded files and store them
        List<MultipartFile> files = userFile.getImages();
        // List<String> fileNames = new ArrayList<String>();
        List<String> uploadedFile = new ArrayList<String>();
        if (null != files && files.size() > 0) {
            for (MultipartFile multipartFile : files) {

                String fileName = multipartFile.getOriginalFilename();

                long millis = System.currentTimeMillis() / 1000L;

                File imageFile = new File(servletRequest.getServletContext().getRealPath("/resources"),
                        Long.toString(millis) + fileName);
                        
                try {
                    multipartFile.transferTo(imageFile);

                    resizeImage(imageFile, THUMBNAIL_IMG_WIDTH, THUMBNAIL_IMG_HEIGHT,fileName.substring(fileName.lastIndexOf(".") + 1));

                    uploadedFile.add(imagePath + imageFile.getName());
                } catch (IOException e) {
                    Util.handleException(e);
                }
            }
        }

        // Here, you can save the product details in database

        // model.addAttribute("product", product);
        // return "viewProductDetail";
        return BYGenericResponseHandler.getResponse(uploadedFile);
    }

    private void resizeImage(File newFile, int width, int height, String extension) throws IOException {

        BufferedImage image = ImageIO.read(newFile);
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        int newHeight = 0;
        int newWidth = 0;

        double aspectRatio = (double) imageWidth / (double) imageHeight;
        if (imageWidth > width && imageHeight > height) {
            // both height and width are bigger
            if ((imageWidth - width) > (imageHeight - height)) {
                newWidth = width;
                newHeight = (int) (height / aspectRatio);
            } else {
                newHeight = height;
                newWidth = (int) (height * aspectRatio);
            }
        } else if (imageWidth > width) {
            // only width is bigger
            newWidth = width;
            newHeight = (int) (height / aspectRatio);
        } else if (imageHeight > height) {
            // only height is bigger
            newHeight = height;
            newWidth = (int) (width * aspectRatio);
        } else {
            // both are smaller then max
            newHeight = 0;
            newWidth = 0;
        }
        if (newHeight != 0 && newWidth != 0) {
            BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, newWidth,
                    newHeight, Scalr.OP_ANTIALIAS);
            // File f = File.createTempFile("new", ".jpg", new File(path));
            try {

                ImageIO.write(thumbnail, extension, newFile);
                System.out.println(ImageIO.write(thumbnail, extension, newFile));
            } catch (Exception e) {
                // TODO: handle exception
                throw (e);
            }

        } else {
            BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                    imageWidth, imageHeight, Scalr.OP_ANTIALIAS);
                    try {

                        ImageIO.write(thumbnail, extension, newFile);
                        System.out.println(ImageIO.write(thumbnail, extension, newFile));
                    } catch (Exception e) {
                        // TODO: handle exception
                        throw (e);
                    }
        
        }

    }

}