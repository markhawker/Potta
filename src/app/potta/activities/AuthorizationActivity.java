package app.potta.activities;

import app.potta.R;
import app.potta.PottaApplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthorizationActivity extends Activity {

	private PottaApplication app;
	private WebView webView;
	private WebViewClient webViewClient = new WebViewClient() {
		
		@Override
		public void onLoadResource(WebView view, String url) {
			Uri uri = Uri.parse(url);
			if (uri.getHost().equals("potta.com")) {
				String authToken = uri.getQueryParameter("oauth_token");
				if (authToken != null) {
					webView.setVisibility(View.INVISIBLE);
					app.setAccessToken();
					finish();
				} else {
					app.makeToast("You must be authorized to use this application. Please try again.").show();
					loadAuthorizationUrl();
				}
			} else { 
				super.onLoadResource(view, url); 
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (PottaApplication)getApplication();
		setContentView(R.layout.authorization);
		webView = (WebView)findViewById(R.id.web_view);
		webView.setWebViewClient(webViewClient);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadAuthorizationUrl();	
	}
	
	private void loadAuthorizationUrl() {
		String authUrl = app.getAuthorizationUrl();
		webView.loadUrl(authUrl);	
	}
	
}