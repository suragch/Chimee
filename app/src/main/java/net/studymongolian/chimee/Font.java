package net.studymongolian.chimee;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Font {

    private String displayName;
    private String fileLocation;

    Font(String displayName, String fileLocation) {
        this.displayName = displayName;
        this.fileLocation = fileLocation;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    static List<Font> getAvailableFonts(Context context) {
        List<Font> fonts = new ArrayList<>();
        fonts.add(new Font(context.getString(R.string.font_name_qagan), SettingsActivity.FONT_QAGAN));
        fonts.add(new Font(context.getString(R.string.font_name_garqag), SettingsActivity.FONT_GARQAG));
        fonts.add(new Font(context.getString(R.string.font_name_hara), SettingsActivity.FONT_HARA));
        fonts.add(new Font(context.getString(R.string.font_name_scnin), SettingsActivity.FONT_SCNIN));
        fonts.add(new Font(context.getString(R.string.font_name_hawang), SettingsActivity.FONT_HAWANG));
        fonts.add(new Font(context.getString(R.string.font_name_qimed), SettingsActivity.FONT_QIMED));
        fonts.add(new Font(context.getString(R.string.font_name_narin), SettingsActivity.FONT_NARIN));
        fonts.add(new Font(context.getString(R.string.font_name_mcdcnbar), SettingsActivity.FONT_MCDVNBAR));
        fonts.add(new Font(context.getString(R.string.font_name_amglang), SettingsActivity.FONT_AMGLANG));
        fonts.add(new Font(context.getString(R.string.font_name_sidam), SettingsActivity.FONT_SIDAM));
        fonts.add(new Font(context.getString(R.string.font_name_qingming), SettingsActivity.FONT_QINGMING));
        fonts.add(new Font(context.getString(R.string.font_name_onqa_hara), SettingsActivity.FONT_ONQA_HARA));
        fonts.add(new Font(context.getString(R.string.font_name_svgvnag), SettingsActivity.FONT_SVGVNAG));
        fonts.add(new Font(context.getString(R.string.font_name_svlbiya), SettingsActivity.FONT_SVLBIYA));
        fonts.add(new Font(context.getString(R.string.font_name_jclgq), SettingsActivity.FONT_JCLGQ));
        return fonts;
    }
}
