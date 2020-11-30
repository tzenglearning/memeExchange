package entity;

import java.util.Date;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Meme {
	private int id;
	private String userId;
	private String templateId;
	private String category;
	private String caption;
	private String imageUrl;
	
	private Date createdDateTime;
	
	public int getId() {
		return id;
	};
	
	public String getUserId() {
		return userId;
	};
	
	public String getTemplateId() {
		return templateId;
	};
	public String getCategory() {
		return category;
	};
	public String getCaption() {
		return caption;
	};
	public String getImageUrl() {
		return imageUrl;
	};
	public Date getTime() {
		return createdDateTime;
	};
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			
			obj.put("id", id);
			obj.put("userId", userId);
			obj.put("templateId", templateId);
			obj.put("category", category);
			obj.put("caption", caption);
			obj.put("image_url", imageUrl);
			obj.put("createdDateTime", createdDateTime);
			;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * This is a builder pattern in Java.
	 */
	private Meme(MemeBuilder builder) {
		this.id = builder.id;
		this.userId = builder.userId;
		this.templateId = builder.templateId;
		this.category = builder.category;
		this.caption = builder.caption;
		this.imageUrl = builder.imageUrl;
		this.createdDateTime = builder.createdDateTime;
	
	}

	public static class MemeBuilder {
		private int id;
		private String userId;
		private String templateId;
		private String category;
		private String caption;
		private String imageUrl;
		private Date createdDateTime;
		
		public void setId(int id) {
			this.id = id;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public void setTime(Date createdDateTime) {
			this.createdDateTime = createdDateTime;
		}

		public Meme build() {
			return new Meme(this);
		}
	}



}
