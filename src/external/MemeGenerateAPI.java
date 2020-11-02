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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Paths;

//import entity.Item;
//import entity.Item.ItemBuilder;

import java.util.List;
import java.util.Set;

public class MemeGenerateAPI {
	
	public byte[] createMeme(String template, String upText, String downText) {


		// apikey=abcde&geoPoint=xyz123&keyword=&radius=50
		String url = String.format("https://api.memegen.link/images/%s/%s/%s.png", template, upText, downText);

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);

			if (responseCode != 200) {
				return null;
			}
			
			InputStream is = new BufferedInputStream(connection.getInputStream());

		        // Read the image and close the stream
		    BufferedImage originalImage = ImageIO.read(is);
		    ByteArrayOutputStream baos=new ByteArrayOutputStream();
		    ImageIO.write(originalImage, "png", baos );
		    byte[] imageInByte=baos.toByteArray();
		    
		    is.close();
		    
		    return imageInByte;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		MemeGenerateAPI tmApi = new MemeGenerateAPI();

		tmApi.createMeme("buzz", "test", "completed");
	}



}
