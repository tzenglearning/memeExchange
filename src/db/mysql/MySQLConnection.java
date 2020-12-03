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
	public Set<String> searchTemplate() {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }
        
		try {
			String sql = "SELECT image_url FROM Templates limit 15";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs= ps.executeQuery();
			Set<String> set = new HashSet<>();

			while(rs.next()) {
				set.add(rs.getString("image_url"));
			}
			return set;
			
	   }catch(Exception e) {
			e.printStackTrace();
		}
		return new HashSet<String>();
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
			//update relationship table
			String sql = "INSERT IGNORE INTO Relationships (FromUserId, ToUserId) VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,fromUserId);
			ps.setString(2,toUserId);
			ps.execute();
			
			//select 
			sql = "SELECT id From Memes WHERE user_id = ? ORDER BY CreatedDateTime limit 5";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, toUserId);
			ResultSet rs = ps.executeQuery();
			
			Set<Integer> set = new HashSet<>();			
			while(rs.next()) {
            	set.add(rs.getInt("id"));      	
            }
			
            for(Integer meme_id : set) {
			//insert the followed users top 5 feed into the feed table
            	sql = "INSERT IGNORE INTO Feeds (subscriber_id, meme_id) VALUES (?,?)";
            	ps = conn.prepareStatement(sql);
            	ps.setString(1,fromUserId);
            	ps.setInt(2,meme_id);
            	ps.execute();
            }
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
			
			//update relation table
			String sql = "DELETE FROM Relationships WHERE FromUserId = ? AND ToUserId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,fromUserId);
			ps.setString(2,toUserId);
			
			ps.execute();
			
			
			//insert the followed users top 5 feed into the feed table
            sql = "DELETE Feeds FROM Feeds INNER JOIN Memes ON Feeds.meme_id = Memes.id where Memes.user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,toUserId);
            ps.execute();
            
			return true ;
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		}
		
		return true;
	}
    
    @Override
    public Integer insertMemes(String userId, String templateId, String category, String caption, String image_url) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return null;
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
			
			sql = "SELECT id FROM Memes WHERE user_id = ? AND image_url = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, image_url);
			ResultSet rs = ps.executeQuery();
			int num = 0;
			while(rs.next()) {
				num = rs.getInt("id");
			}
			
			return num;
	   }catch(Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @Override
    public Set<Meme> getUserMemes(String userId) {
    	if (conn == null) {
    		System.err.println("DB Connection Failed");
			return new HashSet<>();
		}		
    	
		Set<Meme> set = new HashSet<>(); 
		try {
			
			String sql = "SELECT * FROM Memes WHERE user_id = ? ";
			
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			
            while(rs.next()) {
    			MemeBuilder builder = new MemeBuilder();
            	builder.setId(rs.getInt("id"));
            	builder.setImageUrl(rs.getString("image_url"));
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
            	builder.setImageUrl(rs.getString("image_url"));
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
	
	@Override
	public void insertFeeds(String toUserId, int memeId) {
		if(conn == null) {
			System.err.println("DB Connection Failed");
			return;
		}

		try {
			//query subscribers from relationship table
			String sql = "SELECT FromUserId FROM Relationships WHERE ToUserId = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, toUserId);
			ResultSet rs = ps.executeQuery();
			
			Set<String> set = new HashSet<>();			
			while(rs.next()) {
            	set.add(rs.getString("FromUserId"));      	
            }
			
            for(String fromUserId : set) {
			//insert the followed users top 5 feed into the feed table
            	sql = "INSERT IGNORE INTO Feeds (subscriber_id, meme_id) VALUES (?,?)";
            	ps = conn.prepareStatement(sql);
            	ps.setString(1,fromUserId);
            	ps.setInt(2,memeId);
            	ps.execute();
            }
			return;
			
		} catch (SQLException e) {
		    System.out.println(e.getMessage());	
		}
		
		return;
	}

	@Override
	public int getNumberOfFollowers(String userId) {
		
        if (conn == null) {
        	System.err.println("DB connection failed");
        	return -1;
        }

        try {
        	String sql = "SELECT COUNT(*) FROM Relationships WHERE ToUserId = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);

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
	public int getNumberOfFollowing(String userId) {
        if (conn == null) {
        	System.err.println("DB connection failed");
        	return -1;
        }

        try {
        	String sql = "SELECT COUNT(*) FROM Relationships WHERE FromUserId = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);

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

    


}
