package net.studymongolian.chimee;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;

public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
            	if (name.equals(SettingsActivity.FONT_WHITE)){
					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/ChimeeWhiteMirrored.ttf");
				}else if (name.equals(SettingsActivity.FONT_WRITING)){
					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/ChimeeWritingMirrored.ttf");
				}else if (name.equals(SettingsActivity.FONT_ART)){
					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/ChimeeArtMirrored.ttf");
				}else{ // FONT_TITLE
					tf = Typeface.createFromAsset(context.getAssets(),
							"fonts/ChimeeTitleMirrored.ttf");
				}
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);

        }
        return tf;
    }


}