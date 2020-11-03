package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import db.mysql.MySQLConnection;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/searchTemplates")
public class SearchTemplates extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchTemplates() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    
		//parameter
		String templateName = request.getParameter("name");
		//query the database
		DBConnection conn = DBConnectionFactory.getConnection();
		String image_url = conn.searchTemplate(templateName);
		
		
		JSONArray array = new JSONArray();

		try {
			array.put(new JSONObject().put("image_url", image_url));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		RpcHelper.writeJsonArray(response, array);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(request, response);
	}

}
