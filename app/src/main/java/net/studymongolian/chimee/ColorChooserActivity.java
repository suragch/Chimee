package net.studymongolian.chimee;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ColorChooserActivity extends AppCompatActivity implements OnClickListener {

	private static Map<Integer, Integer> idToLightColor = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> idToDarkColor = new HashMap<Integer, Integer>();
	int backgroundColor;
	int textColor;
	FrameLayout flSampleInputWindow;
	TextView tvSampleInputWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color_chooser);

		// setup toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// Map<Integer, Integer> idToColor = new Map<Integer, Integer>();
		initMap();

		// Get saved colors
		SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
		backgroundColor = settings.getInt(SettingsActivity.BGCOLOR_KEY,
				SettingsActivity.BGCOLOR_DEFAULT);
		textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
				SettingsActivity.TEXTCOLOR_DEFAULT);

		// Set colors of sample input window
		flSampleInputWindow = (FrameLayout) findViewById(R.id.flColorSampleInputWindow);
		flSampleInputWindow.setBackgroundColor(backgroundColor);
		tvSampleInputWindow = (TextView) findViewById(R.id.tvColorSampleInputWindow);
		tvSampleInputWindow.setTextColor(textColor);

		// Set listeners for all color views
		setColorViewListeners();

	}


	public void finishedClick(View v) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("resultBackground", backgroundColor);
		returnIntent.putExtra("resultText", textColor);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	@Override
	public void onClick(View view) {


		Integer lightColor = idToLightColor.get(view.getId());
		Integer darkColor = idToDarkColor.get(view.getId());
		if (lightColor != null) {
			backgroundColor = getResources().getColor(lightColor);
			textColor = getResources().getColor(R.color.black);
			flSampleInputWindow.setBackgroundColor(backgroundColor);
			tvSampleInputWindow.setTextColor(textColor);
		} else if (darkColor != null) {
			backgroundColor = getResources().getColor(darkColor);
			textColor = getResources().getColor(R.color.white);
			flSampleInputWindow.setBackgroundColor(backgroundColor);
			tvSampleInputWindow.setTextColor(textColor);
		}
	}



	private void setColorViewListeners() {


		// color views
		View v_black =  findViewById(R.id.v_black);
		v_black.setOnClickListener(this);
		View v_white =  findViewById(R.id.v_white);
		v_white.setOnClickListener(this);
		View v_red_800 =  findViewById(R.id.v_red_800);
		v_red_800.setOnClickListener(this);
		View v_pink_100 =  findViewById(R.id.v_pink_100);
		v_pink_100.setOnClickListener(this);
		View v_pink_800 =  findViewById(R.id.v_pink_800);
		v_pink_800.setOnClickListener(this);
		View v_purple_800 = findViewById(R.id.v_purple_800);
		v_purple_800.setOnClickListener(this);
		View v_deep_purple_100 =  findViewById(R.id.v_deep_purple_100);
		v_deep_purple_100.setOnClickListener(this);
		View v_indigo_800 =  findViewById(R.id.v_indigo_800);
		v_indigo_800.setOnClickListener(this);
		View v_blue_100 =  findViewById(R.id.v_blue_100);
		v_blue_100.setOnClickListener(this);
		View v_blue_500 =  findViewById(R.id.v_blue_500);
		v_blue_500.setOnClickListener(this);
		View v_blue_800 =  findViewById(R.id.v_blue_800);
		v_blue_800.setOnClickListener(this);
		View v_cyan_100 =  findViewById(R.id.v_cyan_100);
		v_cyan_100.setOnClickListener(this);
		View v_cyan_800 =  findViewById(R.id.v_cyan_800);
		v_cyan_800.setOnClickListener(this);
		View v_green_100 =  findViewById(R.id.v_green_100);
		v_green_100.setOnClickListener(this);
		View v_green_800 =  findViewById(R.id.v_green_800);
		v_green_800.setOnClickListener(this);
		View v_yellow_100 =  findViewById(R.id.v_yellow_100);
		v_yellow_100.setOnClickListener(this);
		View v_orange_800 = findViewById(R.id.v_orange_800);
		v_orange_800.setOnClickListener(this);
		View v_deep_orange_100 = findViewById(R.id.v_deep_orange_100);
		v_deep_orange_100.setOnClickListener(this);
		View v_brown_800 =  findViewById(R.id.v_brown_800);
		v_brown_800.setOnClickListener(this);
		View v_grey_200 = findViewById(R.id.v_grey_200);
		v_grey_200.setOnClickListener(this);

	}

	private void initMap() {

		idToDarkColor.put(R.id.v_black, R.color.black);
		idToLightColor.put(R.id.v_white, R.color.white);
		idToDarkColor.put(R.id.v_red_800, R.color.red_800);
		idToLightColor.put(R.id.v_pink_100, R.color.pink_100);
		idToDarkColor.put(R.id.v_pink_800, R.color.pink_800);
		idToDarkColor.put(R.id.v_purple_800, R.color.purple_800);
		idToLightColor.put(R.id.v_deep_purple_100, R.color.deep_purple_100);
		idToDarkColor.put(R.id.v_indigo_800, R.color.indigo_800);
		idToLightColor.put(R.id.v_blue_100, R.color.blue_100);
		idToLightColor.put(R.id.v_blue_500, R.color.blue_500);
		idToDarkColor.put(R.id.v_blue_800, R.color.blue_800);
		idToLightColor.put(R.id.v_cyan_100, R.color.cyan_100);
		idToDarkColor.put(R.id.v_cyan_800, R.color.cyan_800);
		idToLightColor.put(R.id.v_green_100, R.color.green_100);
		idToDarkColor.put(R.id.v_green_800, R.color.green_800);
		idToLightColor.put(R.id.v_yellow_100, R.color.yellow_100);
		idToDarkColor.put(R.id.v_orange_800, R.color.orange_800);
		idToLightColor.put(R.id.v_deep_orange_100, R.color.deep_orange_100);
		idToDarkColor.put(R.id.v_brown_800, R.color.brown_800);
		idToLightColor.put(R.id.v_grey_200, R.color.grey_200);

	}
}
