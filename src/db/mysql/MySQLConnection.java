package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
//import entity.Item;
//import entity.Item.ItemBuilder;
//import external.TicketMasterAPI;


public class MySQLConnection implements DBConnection {
    private Connection conn;
    
    public MySQLConnection() {
     	 try {
     		 Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
     		 conn = DriverManager.getConnection(MySQLDBUtil.URL);
     		
     	 } catch (Exception e) {
     		 e.printStackTrace();
     	 }
    }

	@Override
	public void close() {
		// TODO Auto-generated method stub
	  	 if (conn != null) {
	  		 try {
	  			 conn.close();
	  		 } catch (Exception e) {
	  			 e.printStackTrace();
	  		 }
	  	 }
	}
	
	@Override
	public void insertTemplate(String templateName) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        
		String url = String.format("storage.googleapis.com/meme_generator/%s.png", templateName);
		try {
			String sql = "INSERT IGNORE INTO Templates(name, image_url) VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, templateName);
			ps.setString(2, url);
			ps.execute();
			
	   }catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String searchTemplate(String templateName) {
        if (conn == null) {
            System.err.println("DB connection failed");
        }
        
		try {
			String sql = "SELECT image_url FROM Templates WHERE name = ? ";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, templateName);
			ResultSet rs= ps.executeQuery();
			String image_url = "" ;
			while(rs.next()) {
				image_url = rs.getString("image_url");
			}
			return image_url;
			
	   }catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }

		try {
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
        if (conn == null) {
        	System.err.println("DB connection failed");
        	return;
        }

        try {
        	String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        	PreparedStatement ps = conn.prepareStatement(sql);
        	ps.setString(1, userId);
        	for (String itemId : itemIds) {
        		ps.setString(2, itemId);
        		ps.execute();
        	}

        } catch (Exception e) {
        	e.printStackTrace();
        }

	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}
	
		try {
			String sql="SELECT * FROM users WHERE user_id = ? AND password = ? ";
			PreparedStatement statement=conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs=statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

	
		return false;
	}
	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;	
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}


}
