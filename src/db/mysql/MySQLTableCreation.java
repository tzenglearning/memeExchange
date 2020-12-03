
package db.mysql;


import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Step 1 Connect to MySQL.
			//System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			String url = "jdbc:mysql://localhost:3309/memeExchange"
					+ "?user=tzenglearning&password=12345"
					+ "&autoReconnect=true"
					+ "&allowPublicKeyRetrieval=true&useSSL=false";
			Connection conn = DriverManager.getConnection(url);
			System.out.println("connected");
			if (conn == null) {
				return;
			}
			// Step 2 Drop tables in case they exist.
			Statement statement = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS Feeds";
			statement.executeUpdate(sql);
			
		    sql = "DROP TABLE IF EXISTS Posts";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS Relationships";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS History";
			statement.executeUpdate(sql);	
						
			sql = "DROP TABLE IF EXISTS Memes";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS Templates";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS Users";
			statement.executeUpdate(sql);	

		
			
			sql = "CREATE TABLE Users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			
			sql = "CREATE TABLE Relationships ("
					+  "id INT NOT NULL AUTO_INCREMENT,"
				    + "FromUserId VARCHAR(255) NOT NULL,"
				    + "ToUserId VARCHAR(255) NOT NULL," 
				    + "CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,"
				    + "CONSTRAINT F1 FOREIGN KEY (FromUserId) REFERENCES Users(user_id),"
				    + "CONSTRAINT F2 FOREIGN KEY (ToUserId) REFERENCES Users(user_id),"
				    + "PRIMARY KEY (id)"
				    + ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Templates ("
					+ "template_id VARCHAR(255) NOT NULL,"
					+ "caption VARCHAR(255) NOT NULL,"
         			+ "image_url VARCHAR(512) NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,"
					+ "PRIMARY KEY (template_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Memes ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "template_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "caption VARCHAR(255) NOT NULL,"
         			+ "image_url VARCHAR(512) NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (user_id) REFERENCES Users(user_id),"
					+ "FOREIGN KEY (template_id) REFERENCES Templates(template_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Feeds ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "subscriber_id VARCHAR(255) NOT NULL,"
					+ "meme_id INT NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (subscriber_id) REFERENCES Users(user_id),"
					+ "FOREIGN KEY (meme_id) REFERENCES Memes(id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE History ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "meme_id INT NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL default current_timestamp on update current_timestamp,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (user_id) REFERENCES Users(user_id),"
					+ "FOREIGN KEY (meme_id) REFERENCES Memes(id)"
					+ ")";
			statement.executeUpdate(sql);
						
			conn.close();
			System.out.println("Import done successfully");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

