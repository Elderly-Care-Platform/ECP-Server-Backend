package com.beautifulyears.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.CDNConstants;
import com.beautifulyears.util.S3FileUploader;

public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String uploadDir = BYConstants.IMAGE_CDN_PATH;
	private String s3MediaBucketName;

	private static final int TITLE_IMG_WIDTH = 640;
	private static final int TITLE_IMG_HEIGHT = 650;

	private static final int THUMBNAIL_IMG_WIDTH = 135;
	private static final int THUMBNAIL_IMG_HEIGHT = 168;

	private static final Logger logger = Logger.getLogger(UploadFile.class);

	public void init() {
		if (null != System.getProperty("s3MediaBucketName")) {
			s3MediaBucketName = System.getProperty("s3MediaBucketName");
			System.out.println("s3MediaBucketName === " + s3MediaBucketName);
		}

//		AWSCredentials credentials = new BasicAWSCredentials(
//				"AKIAJGOEKIENWMH5NXBQ",
//				"LcHQh962s+0jyfv/agtrc0yZo0pX2lIINJ5Vgg4y");
//
//		Properties props = new Properties();
//		props.setProperty("mail.transport.protocol", "aws");
//		props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
//		props.setProperty("mail.aws.password", credentials.getAWSSecretKey());
//
//		Session session = Session.getInstance(props);
//
//		// Create a new Message
//		Message msg = new MimeMessage(session);
//		try {
//			msg.setFrom(new InternetAddress("admin@joyofage.org"));
//
//			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
//					"jainnitin.in@gmail.com"));
//			msg.setSubject("Hello AWS JavaMail World");
//			msg.setText("Sending email with the AWS JavaMail provider is easy!");
//			msg.saveChanges();
//
//			// Reuse one Transport object for sending all your messages
//			// for better performance
//			Transport t = new AWSJavaMailTransport(session, null);
//			t.connect();
//			t.sendMessage(msg, null);
//
//			// Close your transport when you're completely done sending
//			// all your messages.
//			t.close();
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//Destination destination = new Destination().withToAddresses(new String[]{"21784127542187ndsbvsd@joyofage.org"});
//        
//        // Create the subject and body of the message.
//        Content subject = new Content().withData("test java mail for AWS ses");
//        Content textBody = new Content().withData("test java mail for AWS ses"); 
//        Body body = new Body().withText(textBody);
//        
//        // Create a message with the specified subject and body.
//        Message message = new Message().withSubject(subject).withBody(body);
//        
//        // Assemble the email.
//        SendEmailRequest request = new SendEmailRequest().withSource("admin@joyofage.org").withDestination(destination).withMessage(message);
//        
//        try
//        {        
//            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
//        
//    		AWSCredentials credentials = new BasicAWSCredentials(
//			"AKIAJGOEKIENWMH5NXBQ",
//			"LcHQh962s+0jyfv/agtrc0yZo0pX2lIINJ5Vgg4y");
//            // Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials. 
//            // Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS credentials 
//            // using the default credential provider chain. The first place the chain looks for the credentials is in environment variables 
//            // AWS_ACCESS_KEY_ID and AWS_SECRET_KEY. 
//            // For more information, see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
//            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
//               
//            // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your sandbox 
//            // status, sending limits, and Amazon SES identity-related settings are specific to a given AWS 
//            // region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using 
//            // the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1 
//            // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html 
//            Region REGION = Region.getRegion(Regions.EU_WEST_1);
//            client.setRegion(REGION);
//       
//            // Send the email.
//            client.sendEmail(request);  
//            System.out.println("Email sent!");
//        }
//        catch (Exception ex) 
//        {
//            System.out.println("The email was not sent.");
//            System.out.println("Error message: " + ex.getMessage());
//        }
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.debug("request to upload the file arrived");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		response.setContentType("application/json");
		String origPath = null;
		String thumbnailPath = null;
		String titlePath = null;

		if (isMultipart) {
			logger.debug("request to upload the file arrived ---- multipart is true");
			FileItemFactory factory = new DiskFileItemFactory();
			response.setStatus(200);
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				List<FileItem> multiparts = upload.parseRequest(request);
				List<String> resImageArray = new ArrayList<String>();
				StringBuffer resImage = new StringBuffer("");
				for (FileItem item : multiparts) {
					resImage = new StringBuffer("");
					if (!item.isFormField()) {
						UUID fname = UUID.randomUUID();
						String name = new File(item.getName()).getName();
						String extension = name.substring(name.lastIndexOf(".") + 1);
						File newFile = File.createTempFile("orig", ".jpg");

						item.write(newFile);
						origPath = (new S3FileUploader(s3MediaBucketName,
								CDNConstants.IMAGE_CDN_ORIG_FOLDER + "/"
										+ fname + "." + extension, newFile))
								.uploadFile(false);

						if (null != request.getParameter("transcoding")
								&& true == Boolean.valueOf(request
										.getParameter("transcoding"))) {
							if (isAnimatedGif(newFile)) {
								File titleImage = File.createTempFile("title",
										".jpg");
								titlePath = (new S3FileUploader(
										s3MediaBucketName,
										CDNConstants.IMAGE_CDN_TITLE_FOLDER
												+ "/" + fname + "." + extension,
										newFile)).uploadFile(false);
								Files.copy(newFile.toPath(),
										titleImage.toPath());

								File thumbnail = File.createTempFile("thumb",
										".jpg");
								thumbnailPath = (new S3FileUploader(
										s3MediaBucketName,
										CDNConstants.IMAGE_CDN_THUMB_FOLDER
												+ "/" + fname + "." + extension,
										newFile)).uploadFile(true);
								Files.copy(newFile.toPath(), thumbnail.toPath());
							} else {
								titlePath = resizeImage(newFile,
										TITLE_IMG_WIDTH, TITLE_IMG_HEIGHT,
										uploadDir,
										CDNConstants.IMAGE_CDN_TITLE_FOLDER
												+ "/" + fname, extension, false);

								thumbnailPath = resizeImage(newFile,
										THUMBNAIL_IMG_WIDTH,
										THUMBNAIL_IMG_HEIGHT, uploadDir,
										CDNConstants.IMAGE_CDN_THUMB_FOLDER
												+ "/" + fname, extension, true);
							}

						}

						if (null != request.getParameter("type")
								&& "editor"
										.equals(request.getParameter("type"))) {
							// res.append("\"original\":");
							resImage.append(origPath);
						} else if (null != request.getParameter("transcoding")
								&& true == Boolean.valueOf(request
										.getParameter("transcoding"))) {
							resImage.append("{");
							resImage.append("\"original\":\"");
							resImage.append(origPath);
							resImage.append("\",");
							resImage.append("\"titleImage\":\"");
							resImage.append(titlePath);
							resImage.append("\",");
							resImage.append("\"thumbnailImage\":\"");
							resImage.append(thumbnailPath);
							resImage.append("\"}");
						}
					}
					resImageArray.add(resImage.toString());
				}
				if (null != request.getParameter("multi")
						&& true == Boolean.valueOf(request
								.getParameter("multi"))) {
					response.getWriter().write(resImageArray.toString());
				} else {
					response.getWriter().write(resImage.toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("upload upload failes");
				System.out.println("File upload failed");
			}
		}
	}

	private boolean isAnimatedGif(File f) {
		ImageReader is = ImageIO.getImageReadersBySuffix("GIF").next();
		ImageInputStream iis;
		boolean isAnimatedGif = false;
		try {
			iis = ImageIO.createImageInputStream(f);
			is.setInput(iis);
			int images = is.getNumImages(true);
			if (images > 1) {
				isAnimatedGif = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isAnimatedGif;
	}

	private String resizeImage(File newFile, int width, int height,
			String uploadDir, String fname, String extension, boolean async)
			throws IOException {

		String path = null;

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
			BufferedImage thumbnail = Scalr.resize(image,
					Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
					newWidth, newHeight, Scalr.OP_ANTIALIAS);
			File f = File.createTempFile(fname, ".jpg");
			ImageIO.write(thumbnail, extension, f);
			path = (new S3FileUploader(s3MediaBucketName, fname + "_" + width
					+ "_" + height + "." + extension, f)).uploadFile(async);

		} else {
			BufferedImage thumbnail = Scalr.resize(image,
					Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH,
					imageWidth, imageHeight, Scalr.OP_ANTIALIAS);
			File f = File.createTempFile(fname, ".jpg");
			ImageIO.write(thumbnail, extension, f);
			path = (new S3FileUploader(s3MediaBucketName, fname + "_" + width
					+ "_" + height + "." + extension, f)).uploadFile(async);
		}
		return path;

	}

}