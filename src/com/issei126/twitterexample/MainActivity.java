package com.issei126.twitterexample;

import java.util.ArrayList;
import java.util.List;

import com.loopj.android.image.SmartImageView;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private TweetAdapter mAdapter;
	private Twitter mTwitter;
	private TwitterStream mTwitterStream;
	
	private MenuItem mStartStream;
	private MenuItem mStopStream;
	
	private boolean mStreamEnabled = false;
	
	private String myScreenName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		if (!TwitterUtils.hasAccessToken(this)){
			Intent intent = new Intent(this, TwitterOAuthActivity.class);
			startActivity(intent);
			//finish();
		} else {
			mAdapter = new TweetAdapter(this);
			setListAdapter(mAdapter);
			
			mTwitter       = TwitterUtils.getTwitterInstance(this);
			mTwitterStream = TwitterUtils.getTwitterSteamInstance(this);
			
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
				protected Void doInBackground(Void... params){
					try {
						myScreenName = mTwitter.verifyCredentials().getScreenName();
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
			};
			task.execute();
			
			streamTimeLine();
		}
		
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		stopStream();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		//toggleStreamMenu();
		return true;
	}
	
	@Override public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		return true;
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.menu_refresh:
			reloadTimeLine();
			return true;
		case R.id.menu_tweet:
			Intent intent = new Intent(this, TweetActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void reloadTimeLine(){
		AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>(){
			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				try {
					return mTwitter.getHomeTimeline();
				} catch (TwitterException e){
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(List<twitter4j.Status> result ){
				if (result != null) {
					mAdapter.clear();
					for (twitter4j.Status status: result) {
						mAdapter.add(status);
					}
					getListView().setSelection(0);
				} else {
					showToast("タイムラインの取得に失敗しました。。。");
				}
			}
		};
		task.execute();
	}
	
	private void stopStream(){
		mTwitterStream.shutdown();
		showToast("Stop streaming");
	}
	private void streamTimeLine() {
		UserStreamListener listener = new UserStreamListener() {

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatus(final Status status) {
				
				runOnUiThread(new Runnable(){
					
					@Override
					public void run(){
						mAdapter.insert(status,0);
						mAdapter.notifyDataSetChanged();
						
						if(status.isRetweet()){
							String authorName = status.getRetweetedStatus().getUser().getScreenName();
							System.out.println(authorName);
							if(authorName.equals(myScreenName)){
								showToast("公式RTされたよ！！！");
							}
						}
					}
				});
				System.out.println(status.getUser().getScreenName() + ":" + status.getText());
					
			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBlock(User arg0, User arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeletionNotice(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDirectMessage(DirectMessage arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFavorite(User arg0, User arg1, Status arg2) {
					runOnUiThread(new Runnable(){
					
					@Override
					public void run(){
						showToast("ふぁぼられた！！！");
					}
				});
				
			}

			@Override
			public void onFollow(User arg0, User arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFriendList(long[] arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUnblock(User arg0, User arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUnfavorite(User arg0, User arg1, Status arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListCreation(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListDeletion(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListMemberAddition(User arg0, User arg1,
					UserList arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListMemberDeletion(User arg0, User arg1,
					UserList arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListSubscription(User arg0, User arg1,
					UserList arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListUnsubscription(User arg0, User arg1,
					UserList arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserListUpdate(User arg0, UserList arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onUserProfileUpdate(User arg0) {
				// TODO Auto-generated method stub
				
			}

	    };
		
		mTwitterStream.addListener(listener);
		mTwitterStream.user();
		showToast("Stream start");
	}
	
	private void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	

	
	private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
			
		private LayoutInflater mInflater;
		
		public TweetAdapter(Context context){
			super(context, android.R.layout.simple_list_item_1);
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_tweet, null);
			}
			Status item = getItem(position);
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(item.getUser().getName());
			TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
			screenName.setText("@" + item.getUser().getScreenName());
			TextView text = (TextView) convertView.findViewById(R.id.text);
			text.setText(item.getText());
			SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
			icon.setImageUrl(item.getUser().getProfileImageURL());
			return convertView;
			
		}
	}
}

