package db.mysql;

public class MySQLDBUtil {
	private static final String HOSTNAME = "db";
	private static final String PORT_NUM = "3306"; // change it to your mysql port number
	public static final String DB_NAME = "memeExchange";
	private static final String USERNAME = "tzenglearning";
	private static final String PASSWORD = "12345";
	public static final String URL = "jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";
}
  