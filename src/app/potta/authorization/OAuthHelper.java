package app.potta.authorization;

import java.io.InputStream;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import app.potta.R;

public class OAuthHelper {

	private String consumerKey;
	private String consumerSecret;
	private Context context;
	private SharedPreferences preferences;
	private AccessToken accessToken;
	
	private static final String ACCESS_TOKEN = "access_token";
	private static final String TOKEN_SECRET = "token_secret";
	private static final String APPLICATION_PREFERENCES = "app_preferences";

	public OAuthHelper(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
		loadConsumerKeys();
		loadAccessToken();
	}
	
	private void loadConsumerKeys() {
		try {
			Properties props = new Properties();
			InputStream stream = context.getResources().openRawResource(R.raw.oauth);
			props.load(stream);
			this.consumerKey = (String)props.getProperty("consumer_key");
			this.consumerSecret = (String)props.getProperty("consumer_secret");
		} catch (Exception e) {
			throw new RuntimeException("Unable to load Consumer Key and Consumer Secret.", e);
		}
	}
	
	private void loadAccessToken() {
		String accessToken = preferences.getString(ACCESS_TOKEN, null);
		String tokenSecret = preferences.getString(TOKEN_SECRET, null);
		if (null != accessToken && null != tokenSecret) {
			this.accessToken = new AccessToken(accessToken, tokenSecret);
		} else {
			this.accessToken = null;
		}
	}
	
	public boolean isAuthorized() { return accessToken != null; }
	
	public Twitter loadTwitter() {
		if (isAuthorized()) { 
			return new TwitterFactory().getOAuthAuthorizedInstance(consumerKey, consumerSecret, accessToken);
		} else {
			return new TwitterFactory().getOAuthAuthorizedInstance(consumerKey, consumerSecret);
		}
	}
	
	public String getAuthorizationUrl(String application, Twitter twitter) {
		try {
			RequestToken requestToken = twitter.getOAuthRequestToken();
			return requestToken.getAuthorizationURL();
		} catch (TwitterException e) {
			Log.e(application, "Unable to get Authorization URL.");
			return null;
		}
	}
	
	public void setAccessToken(Twitter twitter) {
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken();
			storeAccessToken(accessToken);
		} catch (TwitterException e) {
			throw new RuntimeException("Unable to authorize user.", e);
		}
	}
	
	private void storeAccessToken(AccessToken accessToken) {
		Editor editor = preferences.edit();
		editor.putString(ACCESS_TOKEN, accessToken.getToken());
		editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
		editor.commit();
		this.accessToken = accessToken;
	}
	
}