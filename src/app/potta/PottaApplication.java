package app.potta;

import twitter4j.Twitter;

import android.app.Application;
import android.widget.Toast;

import app.potta.authorization.OAuthHelper;

public class PottaApplication extends Application {

	private String APP = "Potta";
	private OAuthHelper oAuthHelper;
	private Twitter twitter;

	@Override
	public void onCreate() {
		super.onCreate();
		oAuthHelper = new OAuthHelper(this);
		twitter = oAuthHelper.loadTwitter();
	}
	
	public Twitter getTwitter() { return twitter; }
	public OAuthHelper getOAuthHelper() { return oAuthHelper; }
	
	public String getAuthorizationUrl() { 
		this.twitter = oAuthHelper.loadTwitter();
		return oAuthHelper.getAuthorizationUrl(APP, twitter); 
	}
	
	public void setAccessToken() { oAuthHelper.setAccessToken(twitter); }
	
	public Toast makeToast(String text) { return Toast.makeText(this, text, Toast.LENGTH_LONG); }

}