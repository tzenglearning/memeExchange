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
import entity.Meme;
import entity.Meme.MemeBuilder;
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
			String sql = "INSERT IGNORE INTO Templates(template_id, image_url) VALUES (?,?)";
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
			String sql="SELECT * FROM Users WHERE user_id = ? AND password = ? ";
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
			String sql = "INSERT IGNORE INTO Users VALUES (?, ?, ?, ?)";
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
		if (conn == null) {
			return "";
		}		
		String name = "";
		try {
			String sql = "SELECT first_name, last_name FROM Users WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;

	}
	
    @Override
	public void followUser(String fromUserId, String toUserId){
		if(conn == null) {
			System.err.println("DB Connection Failed");
			return;
		}

		try {
			String sql = "INSERT IGNORE INTO Relationships (FromUserId, ToUserId) VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,fromUserId);
			ps.setString(2,toUserId);
			ps.execute();
			
			return;
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		}
		
		return;
	}
    
    @Override 
    public boolean followedUser(String fromUserId, String toUserId) {
		if(conn == null) {
			System.err.println("DB Connection Failed");
			return false;
		}

		try {
			String sql = "SELECT COUNT(*) from Relationships WHERE FromUserId =  ? AND ToUserId = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1,fromUserId);
			statement.setString(2,toUserId);
			
			ResultSet rs = statement.executeQuery();
			int num = 0;
			
			while(rs.next()) {
				num = rs.getInt("COUNT(*)");
			}
			return num == 1;
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		}
		
		return false;
    	
    }
    
    @Override
    public boolean searchUser(String userId) {
    	if (conn == null) {
    		System.err.println("DB Connection Failed");
			return false;
		}		
		String name = "";
		try {
			String sql = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			int num = 0;
			while(rs.next()) {
			    num = rs.getInt("COUNT(*)");
			}
            return num == 1;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;

    	
    }
	@Override
	public Set<String> searchFollowedUser(String fromUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> searchFollowers(String toUserId) {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
	public boolean unFollowUser(String fromUserId, String toUserId){
		if(conn == null) {
			System.err.println("DB Connection Failed");
			return false;
		}

		try {
			String sql = "DELETE FROM Relationships WHERE FromUserId = ? AND ToUserId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,fromUserId);
			ps.setString(2,toUserId);
			
			return ps.executeUpdate() == 1;
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		}
		
		return true;
	}
    
    @Override
    public void insertMemes(String userId, String templateId, String category, String caption, String image_url) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        
		try {
			String sql = "INSERT IGNORE INTO Memes(user_id, template_id, category, caption, image_url) VALUES (?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, templateId);
			ps.setString(3, category);
			ps.setString(4, caption);
			ps.setString(5, image_url);
			ps.execute();
			
	   }catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public Set<String> searchUserMemes(String userId) {
    	if (conn == null) {
    		System.err.println("DB Connection Failed");
			return null;
		}		
		Set<String> set = new HashSet<>(); 
		try {
			
			String sql = "SELECT image_url FROM Memes WHERE user_id = ? ";
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
            while(rs.next()) {
            	set.add(rs.getString("image_url"));
            }
            return set;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;

    	
    }
    
	@Override
	public void likeMeme(String userId, int memeId) {
		// TODO Auto-generated method stub
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }

		try {
			String sql = "INSERT IGNORE INTO History(user_id, meme_id) VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, memeId);
			ps.execute();
			return;
		} catch (Exception e) {
		    e.printStackTrace();
		}

	}
   
	@Override
	public boolean likedMeme(String userId, int memeId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return false;
        }

		try {
        	String sql = "SELECT COUNT(*) FROM History WHERE user_id = ? AND meme_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setInt(2, memeId);

			ResultSet rs = statement.executeQuery();
			int num = 0;
			
			while(rs.next()) {
				num = rs.getInt("COUNT(*)");
			}
			return num == 1;
			
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void unlikeMeme(String userId, int memeId) {
		// TODO Auto-generated method stub
        if (conn == null) {
        	System.err.println("DB connection failed");
        	return;
        }

        try {
        	String sql = "DELETE FROM History WHERE user_id = ? AND meme_id = ?";
        	PreparedStatement ps = conn.prepareStatement(sql);
        	ps.setString(1, userId);
        	ps.setInt(2, memeId);
        	ps.execute();
            return;
        } catch (Exception e) {
        	e.printStackTrace();
        }

	}
    
    @Override
	public int getNumberOfLikes(int memeId) {
        if (conn == null) {
        	System.err.println("DB connection failed");
        	return -1;
        }

        try {
        	String sql = "SELECT COUNT(*) FROM History WHERE meme_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, memeId);

			ResultSet rs = statement.executeQuery();
			int num = 0;
			while(rs.next()) {
				num = rs.getInt("COUNT(*)");
			}
			return num;
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return -1;
    	
		
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Set<Meme> getFeeds(String userId){
		
    	if (conn == null) {
    		System.err.println("DB Connection Failed");
			return new HashSet<>();
		}		
		Set<Meme> set = new HashSet<>(); 
		
		try {
			
			String sql = "SELECT * FROM Feeds INNER JOIN Memes ON Feeds.meme_id = Memes.id WHERE subscriber_id = ?";
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();

			
            while(rs.next()) {
    			MemeBuilder builder = new MemeBuilder();
            	builder.setId(rs.getInt("meme_id"));
            	String url = rs.getString("image_url");
            	builder.setImageUrl(url.substring(5,url.length()));
            	builder.setCaption(rs.getString("caption"));
            	builder.setUserId(rs.getString("user_id"));
            	builder.setCategory(rs.getString("category"));
            	builder.setTime(rs.getTimestamp("CreatedDateTime"));
            	
            	set.add(builder.build());
            	
            }
            return set;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return new HashSet<>();
		
	}

    


}
