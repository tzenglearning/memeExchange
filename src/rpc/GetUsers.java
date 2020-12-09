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
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Meme;

/**
 * Servlet implementation class GetUsers
 */
@WebServlet("/user")
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUsers() {
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
			String userId = request.getParameter("userId");
            Set<String> set = connection.getUsers(userId);  
            
            for(String userResult : set) {
                JSONObject obj = new JSONObject();
            	obj.put("userId", userResult);
            	//get number of likes
            	obj.put("image_url", "https://thumbs.dreamstime.com/b/default-avatar-profile-icon-grey-photo-placeholder-illustrations-vectors-default"
            			+ "-avatar-profile-icon-grey-photo-placeholder-99724602.jpg");
            	
    		    array.put(obj);
            }
		    
			RpcHelper.writeJsonArray(response, array);	
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
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
