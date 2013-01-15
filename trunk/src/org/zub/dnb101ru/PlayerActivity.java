package org.zub.dnb101ru;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


public class PlayerActivity extends Activity implements OnPreparedListener {

    private static final String TAG = "Online radio 101.ru";
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
	private AudioManager am;
	private String streamURL;
	private TextView title;
	//private MetadataTask mt;
	URL mstreamURL;
    
	@SuppressLint("HandlerLeak")
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			updateTrackTitle(msg.obj.toString());
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		//streamURL = "http://94.23.147.7:8000/c12_3?setst=011643100131331017220120824";   -  broken URL
		streamURL = "http://eu3.101.ru:8000/c12_3?setst=058145400135427718020121130";
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        
        setupVolumeBar();
        
        title = (TextView) findViewById(R.id.songTitle);
       // mt = new MetadataTask();
        //mt.execute();
        
        Thread t = new Thread(new Runnable() {
			public void run() {
				while (true)
					try {
						Message localMessage = new Message();
						localMessage.what = 0;
						localMessage.obj = getTrackTitle();
						handler.sendMessage(localMessage);
						Thread.sleep(60000);
					} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					}
			}
		});
		t.start();
        
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void setupVolumeBar() {
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currenVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setMax(maxVolume);
        mSeekBar.setProgress(currenVolume);
        
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
            	am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
	}

    @TargetApi(14)
	public void playAudio(View view) {
        Map<String,String> mHeadersMap = new HashMap<String, String>();

        // adding or set elements in Map by put method key and value pair
        mHeadersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
        mHeadersMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        mHeadersMap.put("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
        mHeadersMap.put("Accept-Encoding", "gzip, deflate");
        mHeadersMap.put("Connection", "keep-alive");
        mHeadersMap.put("Referer", "http://101.ru/101player/101player.swf");
        try {
        	
        	Uri url = Uri.parse(streamURL.toString());

            
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), url, mHeadersMap);
            mMediaPlayer.prepareAsync();
            
            //mMediaPlayer.start();
 
        } 
        
        catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }

    }

    @Override
    public void onPrepared(MediaPlayer mMediaPlayer) {
    	mMediaPlayer.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }
    
    public String getTrackTitle() {	    
    	InputStream is = null;
    	int len = 500;
		try {
			URL url = new URL("http://101.ru/a_php/channel/ajax/now_efir_out.php?channel=6");
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    // Starts the query
		    conn.connect();
		    int response = conn.getResponseCode();
		    Log.d(TAG, "The response is: " + response);
		    is = conn.getInputStream();

		    // Convert the InputStream into a string
		    String TrackTitle = readIt(is, len);
		    return TrackTitle.substring(0, TrackTitle.indexOf("\n"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
    	return null;
    }
    
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");        
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

	private void updateTrackTitle(String TrackTitle) {
		// TODO Auto-generated method stub
		title.setText(TrackTitle);
		
	}

	class MetadataTask extends AsyncTask<Void, Object, IcyStreamMetadata> {
		protected IcyStreamMetadata streamMeta;

/*		public MetadataTask(URL streamURL) {
			// TODO Auto-generated constructor stub
		}*/


		protected IcyStreamMetadata doInBackground(Void...params) {
	        try {
                mstreamURL = new URL("http://94.23.147.7:8000/c12_3?setst=011643100131331017220120824");
	        } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
	        }
			streamMeta = new IcyStreamMetadata(mstreamURL);
			try {
				streamMeta.refreshMeta();
			} catch (IOException e) {
				// TODO: Handle
				Log.e(MetadataTask.class.toString(), e.getMessage());
			}
			return streamMeta;
		}
	 
	
		protected void onPostExecute(IcyStreamMetadata result) {
			try {
				title.setText(streamMeta.getArtist() + " - " + streamMeta.getTitle());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}