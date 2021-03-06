package db;

import java.util.List;
import java.util.Set;

import entity.Meme;

//import entity.Item;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();

	/**
	 * Insert the favorite items for a user.
	 * 
	 * @param userId
	 * @param itemIds
	 */
	public void setFavoriteItems(String userId, List<String> itemIds);

	/**
	 * Delete the favorite items for a user.
	 * 
	 * @param userId
	 * @param itemIds
	 */
	public void unsetFavoriteItems(String userId, List<String> itemIds);

	/**
	 * Get the favorite item id for a user.
	 * 
	 * @param userId
	 * @return itemIds
	 */
	public Set<String> getFavoriteItemIds(String userId);

	/**
	 * Get the favorite items for a user.
	 * 
	 * @param userId
	 * @return items
	 */
	//public Set<Item> getFavoriteItems(String userId);

	/**
	 * Gets categories based on item id
	 * 
	 * @param itemId
	 * @return set of categories
	 */
	public Set<String> getCategories(String itemId);

	/**
	 * Search items near a geolocation and a term (optional).
	 * 
	 * @param userId
	 * @param lat
	 * @param lon
	 * @param term
	 *            (Nullable)
	 * @return list of items
	 */
	//public List<Item> searchItems(double lat, double lon, String term);

	/**
	 * Save item into db.
	 * 
	 * @param item
	 */
	//public void saveItem(Item item);

	/**
	 * Get full name of a user. (This is not needed for main course, just for demo
	 * and extension).
	 * 
	 * @param userId
	 * @return full name of the user
	 */
	public String getFullname(String userId);

	/**
	 * Return whether the credential is correct. (This is not needed for main
	 * course, just for demo and extension)
	 * 
	 * @param userId
	 * @param password
	 * @return boolean
	 */
	public boolean verifyLogin(String userId, String password);

	public boolean registerUser(String userId, String password, String firstname, String lastname);

	public void insertTemplate(String templateName);

	public Set<String> searchTemplate();
	
	public void followUser(String fromUserId, String toUserId);
	
	public boolean followedUser(String fromUserId, String toUserId);
	
	public boolean searchUser(String userId);
	
	public Set<String> getUsers(String userId);

	public Set<String> searchFollowedUser(String fromUserId);
	
	public Set<String> searchFollowers(String toUserId);
	
	public boolean unFollowUser(String fromUserId, String toUserId);
	
	public Integer insertMemes(String userId, String templateId, String category, String caption, String image_url);
	
	public Set<Meme> getUserMemes(String userId);
	
	public void likeMeme(String userId, int memeId);
	
	public boolean likedMeme(String userId, int memeId);
	
	public void unlikeMeme(String userId, int memeId);

	public int getNumberOfLikes(int memeId);
	
	public Set<Meme> getFeeds(String userId);
	
	public void insertFeeds(String fromUserId, int memeId);

	public int getNumberOfFollowers(String userId);
	
	public int getNumberOfFollowing(String userId);
	
	public Set<Meme> getRecommendation(String userId);
	
	
}

