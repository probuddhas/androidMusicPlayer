package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,OnSeekBarChangeListener,OnCompletionListener
{
	SQLiteDatabase db;
	Cursor c;
	ArrayList a1 = new ArrayList();
	String[] str;
	int current=0,total,tbl=0;
	ImageView im1,im2,imf,imn,imb,imp,ivlist,album_art;
	TextView cd,td,stv,albumtv,artisttv;
	static MediaPlayer player;
	SeekBar mpseek;
	Thread autoseek;
	int flag=0,lock=0;
	MediaMetadataRetriever mtr = new MediaMetadataRetriever();
	byte[] art;
/*	NotificationManager nm;
	static final int id=3338675;*/
	String songpath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(id);*/
		im1=(ImageView) findViewById(R.id.iv1);
		im2=(ImageView) findViewById(R.id.iv2);
		imf=(ImageView) findViewById(R.id.ivf);
		imb=(ImageView) findViewById(R.id.ivb);
		imn=(ImageView) findViewById(R.id.ivnext);
		imp=(ImageView) findViewById(R.id.ivprev);
		ivlist=(ImageView) findViewById(R.id.ivlist);
		cd=(TextView) findViewById(R.id.tvcd);
		td=(TextView) findViewById(R.id.tvtd);
		stv=(TextView) findViewById(R.id.stv);
		album_art=(ImageView) findViewById(R.id.imageView1);
		albumtv=(TextView) findViewById(R.id.albumtv);
		artisttv=(TextView) findViewById(R.id.artisttv);
		im1.setOnClickListener(this);
		im2.setOnClickListener(this);
		imn.setOnClickListener(this);
		imf.setOnClickListener(this);
		imp.setOnClickListener(this);
		imb.setOnClickListener(this);
		ivlist.setOnClickListener(this);
		
		mpseek = (SeekBar)findViewById(R.id.mpsb1);
		mpseek.setOnSeekBarChangeListener(this);
		
		im2.setVisibility(android.view.View.INVISIBLE);
		
		
		File sdcard=Environment.getExternalStorageDirectory();

		
		list();
		if(tbl==1) {
		
		if(player==null) {
				db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
			//player = MediaPlayer.create(this,R.raw.mymusic);
				player=new MediaPlayer();
				player.setOnCompletionListener(this);
				//String songpath=sdcard.getPath()+"/mymusic.mp3";
				String q="select * from hpsongstab where sno="+current;
				c = db.rawQuery(q, null);
				c.moveToNext();
				songpath=c.getString(1);
				try {
					player.setDataSource(songpath);
					player.prepare();
				}
				catch (Exception e) {
					//Toast.makeText(this,"File not found",1000).show();
				}
				//stv.setText(songpath.substring(songpath.lastIndexOf('/')+1));
				showmeta(songpath);
			
		}
		else if(player.isPlaying()==true){
			im1.setVisibility(android.view.View.INVISIBLE);
			im2.setVisibility(android.view.View.VISIBLE);
			
				db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
				String q="select * from hpsongstab where sno="+current;
				c = db.rawQuery(q, null);
				c.moveToNext();
				songpath=c.getString(1);
				showmeta(songpath);
		}
		else if(player.isPlaying()==false){
			im1.setVisibility(android.view.View.VISIBLE);
			im2.setVisibility(android.view.View.INVISIBLE);
			
				db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
				String q="select * from hpsongstab where sno="+current;
				c = db.rawQuery(q, null);
				c.moveToNext();
				songpath=c.getString(1);
				showmeta(songpath);
		}
		
		
		mpseek.setMax(player.getDuration());
		td.setText(convert(player.getDuration()));
		

			autoseek = new Thread()
			  {
				public void run()
				{
				 while(true)
				 {
					  if(lock==0) {
						  mpseek.setProgress(player.getCurrentPosition());
						  if(flag==1) {
							  mpseek.setProgress(0);
						  }
					  }
				 }
				}
			  };//END OF THREAD
			  autoseek.start();
		}
		else {
			Toast.makeText(this,"PLEASE UPDATE DB FROM THE MENU BUTTON. OTHERWISE EXCEPTIONS WILL ARISE.  MEDIA FILE NAMES SHOULD NOT HAVE SPECIAL CHARS LIKE ',\" etc. ",Toast.LENGTH_LONG).show();
		}
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.iv1){
			list();
			if(tbl==1) {
		 if(player.isPlaying()==false){
			player.start();
			flag=0;
			im1.setVisibility(android.view.View.INVISIBLE);
			im2.setVisibility(android.view.View.VISIBLE);
		 }
		}
		else {
				Toast.makeText(this,"PLEASE UPDATE DB FROM THE MENU BUTTON. OTHERWISE EXCEPTIONS WILL ARISE.MEDIA FILE NAMES SHOULD NOT HAVE SPECIAL CHARS LIKE ',\" etc.",Toast.LENGTH_LONG).show();
			}
		}
		else if(v.getId()==R.id.iv2){
		  if(player.isPlaying()==true){
			player.pause();
			im2.setVisibility(android.view.View.INVISIBLE);
			im1.setVisibility(android.view.View.VISIBLE);
		  }
		}
		else if(v.getId()==R.id.ivlist) {
			Intent i = new Intent(this,TrackList.class);
			startActivityForResult(i, 10);
		}
		else if(v.getId()==R.id.ivnext) {
			list();
			if(tbl==1) {
				current++;
				if(current<total) {
					String q="select * from hpsongstab where sno="+current;
					c = db.rawQuery(q, null);
					c.moveToNext();
					String path=c.getString(1);
					try {
						lock=1;
						player.reset();
						player.setDataSource(path);
						player.prepare();
						lock=0;
					}
					catch (Exception e) {
						//Toast.makeText(this,"File not found",1000).show();
					}
					mpseek.setMax(player.getDuration());
					td.setText(convert(player.getDuration()));
					player.start();
					showmeta(path);
					flag=0;
					im1.setVisibility(android.view.View.INVISIBLE);
					im2.setVisibility(android.view.View.VISIBLE);
				}
				else {
					Toast.makeText(this,"No more files after this",1000).show();
					current--;
				}
				SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt("current", current);
				editor.commit();
				
			}
		}
		else if(v.getId()==R.id.ivprev) {
			list();
			if(tbl==1) {
				current--;
				if(current>=0) {
					String q="select * from hpsongstab where sno="+current;
					c = db.rawQuery(q, null);
					c.moveToNext();
					String path=c.getString(1);
					try {
						lock=1;
						player.reset();
						player.setDataSource(path);
						player.prepare();
						lock=0;
					}
					catch (Exception e) {
						//Toast.makeText(this,"File not found",1000).show();
					}
					mpseek.setMax(player.getDuration());
					td.setText(convert(player.getDuration()));
					player.start();
					showmeta(path);
					flag=0;
					im1.setVisibility(android.view.View.INVISIBLE);
					im2.setVisibility(android.view.View.VISIBLE);
				}
				else {
					Toast.makeText(this,"No more files before this",1000).show();
					current++;
				}
				SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putInt("current", current);
				editor.commit();
				
			}
		}
		else if(v.getId()==R.id.ivf){
			list();
			if(tbl==1) {
			int np=player.getCurrentPosition()+5000;
			if(np<player.getDuration())
			{
				player.seekTo(np);
			}
			}
		}
		else if(v.getId()==R.id.ivb) {
			list();
			if(tbl==1) {
			int np=player.getCurrentPosition()-5000;
			if(np>=0)
			{
				player.seekTo(np);
			}
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		if(flag==1) {
			  im2.setVisibility(android.view.View.INVISIBLE);
			  im1.setVisibility(android.view.View.VISIBLE);
		  }
		if(fromUser){
			player.seekTo(progress);
		}
		cd.setText(convert(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
     

	//CONVERT DURATION TO DISPLAY
	String convert(long duration){
		String ttd="";
		duration = duration/1000;
		ttd = (duration/60)+" : "+(duration%60);
		return ttd;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		flag=1;
		
		list();
		if(tbl==1) {
			
			current++;
			if(current<total) {
				
				String q="select * from hpsongstab where sno="+current;
				c = db.rawQuery(q, null);
				c.moveToNext();
				String path=c.getString(1);
				try {
					lock=1;
					player.reset();
					player.setDataSource(path);
					player.prepare();
					lock=0;
				}
				catch (Exception e) {
					//Toast.makeText(this,"File not found",1000).show();
				}
				mpseek.setMax(player.getDuration());
				td.setText(convert(player.getDuration()));
				player.start();
				showmeta(path);
				flag=0;
				im1.setVisibility(android.view.View.INVISIBLE);
				im2.setVisibility(android.view.View.VISIBLE);
			}
			else {
				Toast.makeText(this,"No more files after this",1000).show();
				current--;
			}
			SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt("current", current);
			editor.commit();
			
			
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent rri) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, rri);
		try {
		list();
		if(tbl==1) {
			if(player==null) {
				player=new MediaPlayer();
				player.setOnCompletionListener(this);
				db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
				
				/*autoseek = new Thread()
				  {
					public void run()
					{
					 while(true)
					 {
						  if(lock==0) {
							  mpseek.setProgress(player.getCurrentPosition());
							  if(flag==1) {
								  mpseek.setProgress(0);
							  }
						  }
					 }
					}
				  };//END OF THREAD
				  autoseek.start();*/
				
				}
		Bundle b = rri.getExtras();
		String path = b.getString("path");
		if(path.equals("")==false) {
			try {
				lock=1;
				player.reset();
				player.setDataSource(path);
				player.prepare();
				lock=0;
			}
			catch (Exception e) {
				//Toast.makeText(this,"File not found",1000).show();
			}
			mpseek.setMax(player.getDuration());
			td.setText(convert(player.getDuration()));
			player.start();
			showmeta(path);
			flag=0;
			im1.setVisibility(android.view.View.INVISIBLE);
			im2.setVisibility(android.view.View.VISIBLE);
		}
		}
		else {
			Toast.makeText(this,"PLEASE UPDATE DB FROM THE MENU BUTTON. OTHERWISE EXCEPTIONS WILL ARISE. MEDIA FILE NAMES SHOULD NOT HAVE SPECIAL CHARS LIKE ',\" etc. ",Toast.LENGTH_LONG).show();
		}
		}
		catch(Exception e) {}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list();
		db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
		
	}
	
	void list() {
		SharedPreferences pref;
	    pref = getSharedPreferences("songlist",MODE_WORLD_WRITEABLE);
	    int current1=pref.getInt("current", 0);
	    if(current1!=-1) {
	    	current=current1;
	    }
	    total=pref.getInt("total", 0);
	    tbl=pref.getInt("table", 0);
	    //SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("current", current);
		editor.commit();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("current", current);
		editor.commit();
	}
	
	void showmeta(String str) {
		mtr.setDataSource(str);
		art=mtr.getEmbeddedPicture();
		if(mtr.getEmbeddedPicture()==null) {
			album_art.setImageResource(R.drawable.player);
		}
		else {
			Bitmap songimg = BitmapFactory.decodeByteArray(art, 0, art.length);
			album_art.setImageBitmap(songimg);
		}
		if(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)==null) {
			stv.setText(str.substring((str.lastIndexOf('/')+1),str.lastIndexOf('.')));
		}
		else {
			stv.setText(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
		}
		if(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)==null) {
			albumtv.setText("Unknown Album");
		}
		else {
			albumtv.setText(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
		}
		if(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)==null) {
			artisttv.setText("Unknown Artist");
		}
		else {
			artisttv.setText(mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
		}
	}
	
	/*@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		String body = "New notif body";
		String title = "From THE app";
		Notification n = new Notification(R.drawable.logo, body, System.currentTimeMillis());
		n.setLatestEventInfo(this, title, body, pi);
		//n.defaults = Notification.DEFAULT_ALL;
		n.flags=Notification.FLAG_NO_CLEAR;
		nm.notify(id, n);
		super.finish();
	}*/
	

}
