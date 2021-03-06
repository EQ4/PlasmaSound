package com.rj.processing.plasmasound;

import processing.core.PApplet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.MTManager;
import com.rj.processing.mt.TouchListener;
import com.rj.processing.plasmasoundhd.PlasmaActivity;
import com.rj.processing.plasmasoundhd.SequencerActivity;
import com.rj.processing.plasmasoundhd.Visualization;
import com.rj.processing.plasmasoundhd.WaveformEditor;
import com.rj.processing.plasmasoundhd.pd.PDManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;
import com.rj.processing.plasmasoundhd.visuals.Grid;
import com.rj.processing.plasmasoundhd.visuals.PlasmaFluid;

public class PlasmaSound extends PApplet implements TouchListener, PlasmaActivity {

	public static final String SHARED_PREFERENCES_AUDIO = "shared_prefs_audio";
	
	public static final String PATCH_PATH = "simplesine.small.4.2.pd";
	
	
	public MTManager mtManager;
	
	public Visualization vis;
	public PDManager pdman;
	public Instrument inst;
	
	
	boolean touchupdated = false;
	boolean pdready = false;
	boolean startingup = true;
	Runnable readyrunnable = new Runnable() {
		public void run() {
			if (startingup == false) {
				pdready = false;
				if (pdman != null) {
					pdman.onResume();
				
				
					pdready = true;
					Log.v("PlasmaSoundReadyRunnable", "Destroying popup!");
				}
				runOnUiThread(new Runnable() { public void run() {loadingview.setVisibility(View.GONE);}});
			}
		}
	};
	
	public int sketchWidth() { return this.displayWidth; }
	public int sketchHeight() { return this.displayHeight; }
	public String sketchRenderer() { return PApplet.OPENGL; }
	public boolean keepTitlebar() { return false; }
	
	View loadingview;
	
	public void onCreate(final Bundle savedinstance) {
		super.onCreate(savedinstance);
		loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasound.R.layout.loadingscreenmall, null);
		this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	
	
	@Override
	public void setup() {
		hint(DISABLE_DEPTH_TEST);
		hint(DISABLE_OPENGL_ERRORS);
//		hint(PApplet.DISABLE_ACCURATE_TEXTURES);
		hint(PApplet.DISABLE_DEPTH_MASK);
		hint(PApplet.DISABLE_DEPTH_SORT);
	    frameRate(60);
	
	    mtManager = new MTManager();
	    mtManager.addTouchListener(this);
	    
	    //VISUALS CODE
	    vis = new Visualization(this);
	    vis.addVisual(new PlasmaFluid(this, null)); 
	    vis.addVisual(new Grid(this, this)); 
	    vis.addVisual(new AudioStats(this, this)); 
	    
	    asyncSetup.execute(new Void[0]);
	    debug();
	}
	AsyncTask<Void,Void,Void> asyncSetup = new AsyncTask<Void,Void,Void>() {
		@Override
		protected Void doInBackground(final Void... params) {
			startingup = true;
			Log.v("PlasmaSoundSetup", "creating pd");
		    //PD Stuff
		    pdman = new PDManager(PlasmaSound.this);
			Log.v("PlasmaSoundSetup", "launching pd");
		    pdready = false;
		    pdman.onResume();
		    
			Log.v("PlasmaSoundSetup", "Starting instrument");
		    //Make the Instrument
		    inst = new Instrument(pdman);
			Log.v("PlasmaSoundSetup", "setting instrument patch");
		    inst.setPatch(PATCH_PATH);
		    inst.setMidiMin(70);
		    inst.setMidiMax(87);
		    
			Log.v("PlasmaSoundSetup", "Reading settings");
			readSettings();	    
			Log.v("PlasmaSoundSetup", "Done!");
			return null;
		}
		@Override
		protected void onPostExecute(final Void params) {
			Log.v("PlasmaSoundSetup", "Destroying popup!");
			pdready = true;
			startingup = false;
			loadingview.setVisibility(View.GONE);
	//		loadingview = null;
	
		}
	};
	
	
	
	public void debug() {
		  // Place this inside your setup() method
		  final DisplayMetrics dm = new DisplayMetrics();
		  getWindowManager().getDefaultDisplay().getMetrics(dm);
		  final float density = dm.density; 
		  final int densityDpi = dm.densityDpi;
		  println("density is " + density); 
		  println("densityDpi is " + densityDpi);
		  
		  println("HEY! the screen size is "+width+"x"+height);
	}
	
	
	//mt version
	public boolean surfaceTouchEvent(final MotionEvent me) {
		if (mtManager != null) mtManager.surfaceTouchEvent(me);
		
	//	if (pdready)
	//		instTouchFix(me);
		return super.surfaceTouchEvent(me);
	}
	

	@Override
	public void touchAllUp(final Cursor c) {
		if (inst!=null) inst.allUp();
		
	}
	@Override
	public void touchDown(final Cursor c) {
		if (inst!=null) inst.touchDown(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
		
	}
	@Override
	public void touchMoved(final Cursor c) {
		if (inst!=null) inst.touchMove(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	
	}
	@Override
	public void touchUp(final Cursor c) {
		if (inst!=null) inst.touchUp(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	}
	
	
	
	@Override
	public void draw() {
		if (pdready) {
		    background(0);
		
		    vis.drawVisuals();
		    
		    if (this.frameCount % 100 == 0) println(this.frameRate+"");
		}
	
	}
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (loadingview == null)
			loadingview = this.findViewById(com.rj.processing.plasmasound.R.id.loadingview);
		loadingview.setVisibility(View.VISIBLE);
		if (pdready == true) {
		    pdready = false;
			if (pdman != null) pdman.onResume(readyrunnable);
		}
		readSettings();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (pdman != null) pdman.onPause();
	}
	
	@Override
	public void onDestroy() {
		if (pdman != null) pdman.cleanup();
		super.onDestroy();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	    final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rj.processing.plasmasound.R.menu.main_menu, menu);
	    return true;
	}
	
	/**
	 * its 1:42, and where is Jake?
	 * Rj is goofy as fuck.
	 */
	
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	    switch (item.getItemId()) {
	    case com.rj.processing.plasmasound.R.id.sequencer:
	        sequencer();
	        return true;
	    case com.rj.processing.plasmasound.R.id.instrument_settings:
	        instrumentSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.effects_settings:
	        effectSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.save_settings:
	        saveSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.load_settings:
	        loadSettings();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void waveformEditor() {
		final Intent i = new Intent(this, WaveformEditor.class);
		this.startActivity(i);
	}
	
	public void sequencer() {
		final Intent i = new Intent(this, SequencerActivity.class);
		this.startActivity(i);
	}

	
	public void instrumentSettings() {
		final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminAudioSettings.class);
		this.startActivity(i);
	}
	public void effectSettings() {
		final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminEffectsSettings.class);
		this.startActivity(i);
	}
	public void saveSettings() {
		JSONPresets.getPresets().showSaveMenu(this, this);
	}
	public void loadSettings() {
		JSONPresets.getPresets().showLoadMenu(this, this);
	}
	
	@Override
	public void onActivityResult(final int i, final int j, final Intent res) {
		super.onActivityResult(i, j, res);
		readSettings();
	}

    public void readSettings() {
        final SharedPreferences mPrefs = PlasmaSound.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
    	if (inst!=null) inst.updateSettings(this, mPrefs);
    }

    
	@Override
	public Instrument getInst() {
		return inst;
	}
	@Override
	public MTManager getMTManager() {
		return mtManager;
	}
	@Override
	public PDManager getPD() {
		return pdman;
	}

    

}
