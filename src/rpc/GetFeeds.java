package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Meme;

/**
 * Servlet implementation class GetFollowedMemes
 */
@WebServlet("/feed")
public class GetFeeds extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFeeds() {
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

		JSONArray array = new JSONArray();
		
		if(session == null){
			response.setStatus(403);
			return;	
		}
		
		try {
			String userId = session.getAttribute("user_id").toString();	
            Set<Meme> set = connection.getFeeds(userId);  
            
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
		    
			RpcHelper.writeJsonArray(response, array);	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
