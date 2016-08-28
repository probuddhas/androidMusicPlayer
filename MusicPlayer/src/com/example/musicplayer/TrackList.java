package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TrackList extends ListActivity implements OnClickListener {

	SQLiteDatabase db;
	Button b1;
	int pos=-1;
	ArrayList a1 = new ArrayList();
	ArrayList a2 = new ArrayList();
	MediaMetadataRetriever mtr = new MediaMetadataRetriever();
	String[] s,s2;
	String ci="";
	String song;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_list);
		b1=(Button) findViewById(R.id.dbbt);
		b1.setOnClickListener(this);
		
		find();
		
		Object ia[]=a1.toArray();
		s = Arrays.copyOf(ia, ia.length, String[].class);
		
		for(int i=0;i<s.length;i++) {
			try {
			mtr.setDataSource(s[i]);
			song = mtr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			if(song==null) {
				song = s[i];
				song = song.substring((song.lastIndexOf('/')+1),song.lastIndexOf('.'));
			}
			a2.add(song);
			}
			catch(Exception e) {
				
			}
		}
		Object ia2[]=a2.toArray();
		s2 = Arrays.copyOf(ia2, ia2.length, String[].class);
		
		ArrayAdapter<String> ad;
		ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,s2);
		setListAdapter(ad);
	}
	
	void find() {
		try {
		File mf=Environment.getExternalStorageDirectory();
		//File mf = new File(root,"Music");
		if(mf.exists()) {
			if(mf.isDirectory()) {
				File[] files=mf.listFiles();
				for(int i=0;i<files.length;i++)
				{
					if(files[i].isFile())
					{		
						if((files[i].getName().substring(files[i].getName().lastIndexOf('.')+1)).equals("mp3")) {
							a1.add(files[i].getAbsolutePath());
						}
					}
					else
					{
						recu(files[i].getAbsolutePath());
					}
				}
			}
		}
		}
		catch (Exception e) {}
	}
	
	void recu(String str) {
		try {
			File nf=new File(str);
			if(nf.exists()) {
				if(nf.isDirectory()) {
					File[] filesr=nf.listFiles();
					for(int i=0;i<filesr.length;i++)
					{
						if(filesr[i].isFile())
						{
								if((filesr[i].getName().substring(filesr[i].getName().lastIndexOf('.')+1)).equals("mp3")) {
									a1.add(filesr[i].getAbsolutePath());
								}
						}
						else
						{
							recu(filesr[i].getAbsolutePath());
						}
					}
				}
			}
			}
		catch(Exception e) { }
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		ci=s[position];
		pos=position;
		finish();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra("path",ci);
		setResult(RESULT_OK,i);
		super.finish();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("current", pos);
		editor.putInt("total", s.length);
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		try {
		// TODO Auto-generated method stub
		db = openOrCreateDatabase("hpsongdb", MODE_WORLD_READABLE, null);
		String q="create table if not exists hpsongstab(sno int,name varchar)";
		db.execSQL(q);
		String q2="delete from hpsongstab";
		db.execSQL(q2);
		
		for(int i=0;i<s.length;i++) {
			s[i]=s[i].replaceAll("'","''");
			String q1="insert into hpsongstab values("+i+",'"+s[i]+"')";
			db.execSQL(q1);
		}
		Toast.makeText(this, "UPDATION COMPLETED", 1000).show();
		SharedPreferences pref = getSharedPreferences("songlist", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("table", 1);
		editor.commit();
		}
		catch(Exception e) {
			Toast.makeText(this, "UPDATION FAILED. MAKE SURE ALL FILE NAMES DO NOT HAVE SPECIAL CHARS LIKE ',\" etc. AND TRY AGAIN", Toast.LENGTH_LONG).show();
		}
	}

}
