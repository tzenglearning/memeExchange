package rpc;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import external.GCPUtil;
import external.MemeGenerateAPI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import db.DBConnection;
import db.DBConnectionFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Servlet implementation class UploadImage
 */
@WebServlet("/create")
public class CreateMeme extends HttpServlet {
	private static final long serialVersionUID = 1L;   
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
		DBConnection connection = DBConnectionFactory.getConnection();
		HttpSession session = request.getSession(false);
		
		try {
			String userId = "mememaster";
					//session.getAttribute("user_id").toString();
			Set<String> result = connection.searchUserMemes(userId);
			JSONArray array = new JSONArray();
			for(String image_url : result) {
				JSONObject obj = new JSONObject();
				obj.put("image_url",image_url);
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		JSONObject input = RpcHelper.readJSONObject(request);
		String templateId = input.getString("templateId");
		String category = input.getString("category");
		String caption = input.getString("caption");
		String upText = input.getString("upText");
		String downText = input.getString("downText");
		
		
		
		DBConnection connection = DBConnectionFactory.getConnection();
		MemeGenerateAPI api = new MemeGenerateAPI();
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		byte[] image = null;
		
		
		//create memes via the api
		try {
			image = api.createMeme(templateId, upText, downText);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			String userId = session.getAttribute("user_id").toString();

			//store it in the google cloud  
			String objectName = userId + upText + downText+ ".png";
			Storage storage = StorageOptions.newBuilder().setProjectId(GCPUtil.projectId).build().getService();
		    BlobId blobId = BlobId.of(GCPUtil.bucketName, objectName);
		    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		    Blob blob = storage.create(blobInfo, image);
		    
		    System.out.println(blob.getName() + " uploaded to gcp");
			
            
		    String imageUrl = "//storage.googleapis.com/meme_generator/" +blob.getName();
			//store the relevant information in the sql server
		    int memeId = connection.insertMemes(userId, templateId, category, caption,
		    		imageUrl);
		    
		    connection.insertFeeds(userId,memeId);
		    
		    obj.put("status", "OK").put("userId", userId).put("image_url", imageUrl).put("caption",caption);
		    JSONArray array = new JSONArray();
		    array.put(obj);
		    RpcHelper.writeJsonArray(response, array);
		    
		    
	   }catch(Exception e) {
		    e.printStackTrace();   
	   }finally {
		    connection.close();
	   }
	}

}
