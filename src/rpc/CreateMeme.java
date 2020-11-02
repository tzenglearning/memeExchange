package rpc;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import external.MemeGenerateAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Servlet implementation class UploadImage
 */
@WebServlet("/create")
public class CreateMeme extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // The ID of your GCP project
    private static final String projectId = "memegenerator-293602";

    // The ID of your GCS bucket
    private static final String bucketName = "meme_generator";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateMeme() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String template = request.getParameter("template");
		String upText = request.getParameter("upText");
		String downText = request.getParameter("downText");
		
		MemeGenerateAPI api = new MemeGenerateAPI();
		byte[] image = null;
		
		
		//create memes via the api
		try {
			image = api.createMeme(template, upText, downText);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			//store the relevant information in the sql server
			
			//store it in the google cloud  
			String objectName = "test.png";
			Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		    BlobId blobId = BlobId.of(bucketName, objectName);
		    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		    Blob blob = storage.create(blobInfo, image);
		    
	   }catch(Exception e) {
		    e.printStackTrace();   
	   }
	}

}
