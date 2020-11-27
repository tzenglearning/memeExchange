package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class LikeMemes
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
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
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
	  	DBConnection connection = DBConnectionFactory.getConnection();
	  	
	  	try {
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		 String userId = session.getAttribute("user_id").toString();
	  		 String memeId = input.getString("meme_id");

	  		 connection.likeMeme(userId, memeId);
	  		 int likes= connection.getNumberOfLikes(memeId);
	  		 
	  		 RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS").put("numberOfLikes", likes));
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }
	}
	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		DBConnection connection = DBConnectionFactory.getConnection();
		
	  	try {
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		 String userId = session.getAttribute("user_id").toString();
	  		 String memeId = input.getString("meme_id");
	  		 
	  		 connection.unlikeMeme(userId, memeId);
	  		 int likes= connection.getNumberOfLikes(memeId);
	  		 
	  		 RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS").put("numberOfLikes", likes));
	  		
	  	} catch (Exception e) {
	  		 e.printStackTrace();
	  	} finally {
	  		 connection.close();
	  	}
	}

}
