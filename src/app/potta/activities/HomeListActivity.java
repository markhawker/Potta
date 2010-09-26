package app.potta.activities;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import app.potta.PottaApplication;
import app.potta.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class HomeListActivity extends ListActivity {
	
    private PottaApplication app;
	private Twitter twitter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (PottaApplication)getApplication();
        setContentView(R.layout.home_list);
    }

	@Override
	protected void onResume() {
		super.onResume();
		twitter = app.getTwitter();
		if (!app.getOAuthHelper().isAuthorized()) { 
			loadAuthorizationActivity();
		} else { 
			loadHomeTimelineIfNotLoaded();
		}
	}
	
	private void loadAuthorizationActivity() {
		Intent intent = new Intent(this, AuthorizationActivity.class);
		startActivity(intent);
	}
	
	private void loadHomeTimelineIfNotLoaded() {
		if (null == getListAdapter()) { 
			loadHomeTimeline(); 
		} else {
			app.makeToast("You already have the most recent timeline showing.").show();
		}
	}
	
	private void loadHomeTimeline() {
		try {
			ResponseList<Status> homeTimeline = twitter.getHomeTimeline();
			ArrayList<String> tweets = new ArrayList<String>();
			for (Status status : homeTimeline) {
				tweets.add(status.getText());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tweets);
			setListAdapter(adapter);
		} catch (TwitterException e) {
			//TODO Clear "dirty" access tokens, and prompt user to authorize the application again.
			throw new RuntimeException("Unable to load home timeline.", e);
		}
	}
	
}