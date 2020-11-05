package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class FollowUser
 */
@WebServlet("/follow")
public class FollowUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowUser() {
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
		DBConnection connection = DBConnectionFactory.getConnection();
		
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		
		if(session == null){
			response.setStatus(403);
			return;	
		}
		
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = session.getAttribute("user_id").toString();
			String toUserId = input.getString("to_user_id");
			
            if(!connection.searchUser(userId)) {
            	obj.put("status", "User Does not even exist");
            }
            else if (connection.followUser(userId, toUserId)) {
				obj.put("status", "OK");
			} else {
				obj.put("status", "Operation Failed");
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
