package external;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import db.mysql.MySQLConnection;

import java.nio.file.Paths;

import external.GCPUtil;

//import entity.Item;
//import entity.Item.ItemBuilder;

import java.util.List;
import java.util.Set;

public class TemplateCreation {


	public void createTemplate() {

		// apikey=abcde&geoPoint=xyz123&keyword=&radius=50
		String url = String.format("https://api.memegen.link/images");
		MySQLConnection sqlConn = new MySQLConnection();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				System.err.println("Error");
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
           
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
	        JSONArray jsonObjectArray = new JSONArray(response.toString());
	        

	        for(int i = 0; i < jsonObjectArray.length(); i++) {

	        	//construct url
	        	String link = jsonObjectArray.getJSONObject(i).getString("template");
	        	String objectName = link.substring(link.lastIndexOf('/')+1, link.length());
	        	String requestUrl = String.format("https://api.memegen.link/images/%s.png", objectName);
	        	
	        	//make connection to download templates
	        	connection = (HttpURLConnection) new URL(requestUrl).openConnection();
				connection.setRequestMethod("GET");
				
				responseCode = connection.getResponseCode();
				System.out.println("Sending request to url: " + url);
				System.out.println("Response code: " + responseCode);
				
				if (responseCode != 200) {
					System.err.println("Error");
				}
				
				InputStream is = new BufferedInputStream(connection.getInputStream());

		        // Read the image and close the stream	
				BufferedImage originalImage = ImageIO.read(is);
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				ImageIO.write(originalImage, "png", baos );
				byte[] image=baos.toByteArray();
				is.close();	
				
				//insert into sql
				
				sqlConn.insertTemplate(objectName);
				System.out.println(objectName + " inserted into sql");
				
//			    //save to gcp
				Storage storage = StorageOptions.newBuilder().setProjectId(GCPUtil.projectId).build().getService();
			    BlobId blobId = BlobId.of(GCPUtil.bucketName, objectName+".png");
			    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
			    Blob blob = storage.create(blobInfo, image);
			    
			    System.out.println(objectName + " uploaded to gcp");
				
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sqlConn.close();
		return;
	}


	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TemplateCreation test = new TemplateCreation();
		test.createTemplate();
	}



}
