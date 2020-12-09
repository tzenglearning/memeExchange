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
import entity.Meme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
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
			String userId = request.getParameter("userId");
			System.out.println(userId);
		    
		    //get memes the user created
			Set<Meme> set = connection.getUserMemes(userId);
			
			JSONObject result = new JSONObject();
			JSONArray array = new JSONArray();
			System.out.println(set.size());
			
			
			 for(Meme meme : set) {
	            	JSONObject obj = meme.toJSONObject();
	            	int memeId = obj.getInt("id");
	            	String authorId = obj.getString("userId");
	            	
	            	//user liked this meme before?
	            	obj.put("favorite", connection.likedMeme(userId, memeId));
	            	//get number of likes
	            	obj.put("numberOfLikes", connection.getNumberOfLikes(memeId));
	            	//user followed this author before?
	            	obj.put("follow", connection.followedUser(userId, authorId));
	            	
	    		    array.put(obj);
	            }
		 
			result.put("memes", array);
			//get user first name last name
			result.put("author_id", userId);
			
			//get number of memes users created
			result.put("numOfMemes", array.length());
			
			//get number of followers 
			result.put("numOfFollowers", connection.getNumberOfFollowers(userId));
			
			//get number of people he followed
			result.put("numOfFollowing", connection.getNumberOfFollowing(userId));
			
			result.put("profilePicture", "https://thumbs.dreamstime.com/b/"
					+ "default-avatar-profile-icon-grey-photo-placeholder-"
					+ "illustrations-vectors-default-avatar-profile-icon-grey-photo-placeholder-99724602.jpg");
			
			
			RpcHelper.writeJsonObject(response, result);
			
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
			Random rand = new Random();
			//store it in the google cloud  
			String objectName = userId + String.valueOf(rand.nextInt(100000))+  ".png";
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
