
package db.mysql;


import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Step 1 Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
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
			
			sql = "DROP TABLE IF EXISTS Templates";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS Memes";
			statement.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS Users";
			statement.executeUpdate(sql);	

		
			
			sql = "CREATE TABLE Users ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (id)"
					+ ")";
			statement.executeUpdate(sql);
			
			
			sql = "CREATE TABLE Relationships ("
					+  "id INT NOT NULL AUTO_INCREMENT,"
				    + "FromUserId INT NOT NULL,"
				    + "ToUserId INT NOT NULL," 
				    + "CreatedDateTime DATETIME NOT NULL,"
				    + "CONSTRAINT F1 FOREIGN KEY (FromUserId) REFERENCES Users(id),"
				    + "CONSTRAINT F2 FOREIGN KEY (ToUserId) REFERENCES Users(id),"
				    + "PRIMARY KEY (id)"
				    + ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Templates ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "caption VARCHAR(255) NOT NULL,"
					+  "name VARCHAR(255) NOT NULL,"
         			+ "image_url VARCHAR(512) NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL,"
					+ "PRIMARY KEY (id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Memes ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "user_id INT NOT NULL,"
					+ "caption VARCHAR(255) NOT NULL,"
         			+ "image_url VARCHAR(512) NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (user_id) REFERENCES Users(id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE Feeds ("
					+ "id INT NOT NULL AUTO_INCREMENT,"
					+ "subscriber_id INT NOT NULL,"
					+ "meme_id INT NOT NULL,"
					+ "CreatedDateTime DATETIME NOT NULL,"
					+ "PRIMARY KEY (id),"
					+ "FOREIGN KEY (subscriber_id) REFERENCES Users(id),"
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

