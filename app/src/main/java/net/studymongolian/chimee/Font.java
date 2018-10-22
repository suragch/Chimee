package net.studymongolian.chimee;

import android.content.Context;

import net.studymongolian.mongollibrary.MongolFont;

import java.util.ArrayList;
import java.util.List;

public class Font {

    static final String QAGAN = MongolFont.QAGAN;                    // Normal
    static final String GARQAG = "fonts/MGQ8102.ttf";                // Title
    static final String HARA = "fonts/MHR8102.ttf";                  // Bold
    static final String SCNIN = "fonts/MSN8102.ttf";                 // News
    static final String HAWANG = "fonts/MHW8102.ttf";                // Handwriting
    static final String QIMED = "fonts/MQD8102.ttf";                 // Handwriting pen
    static final String NARIN = "fonts/MNR8102.ttf";                 // Thin
    static final String MCDVNBAR = "fonts/MMB8102.ttf";              // Wood carving
    static final String AMGLANG = "fonts/MAM8102.ttf";               // Brush
    static final String SIDAM = "fonts/MBN8102.ttf";                 // Fat round
    static final String QINGMING = "fonts/MQI8102.ttf";              // Qing Ming style
    static final String ONQA_HARA = "fonts/MTH8102.ttf";             // Thick stem
    static final String SVGVNAG = "fonts/MSO8102.ttf";               // Thick stem thin lines
    static final String SVLBIYA = "fonts/MBJ8102.ttf";               // Double stem
    static final String JCLGQ = "fonts/MenksoftJclgq.ttf";           // Computer
    static final String TVGVRAI = "fonts/MTR8102.ttf";               // Hoof print (Winding tail)
    static final String ERIHE = "fonts/MER8102.ttf";                 // Pearls (lumpy)
    static final String HVLVSVN = "fonts/MHL8102.ttf";               // Bamboo (thick/thin)
    static final String QIMEG = "fonts/MQM8102.ttf";                 // like news
    static final String UJUG = "fonts/MWJ8102.ttf";                  // Bamboo pen
    static final String UYANGA = "fonts/MVY8102.ttf";                // Thick wavy stem

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
        fonts.add(new Font(context.getString(R.string.font_name_qagan), QAGAN));
        fonts.add(new Font(context.getString(R.string.font_name_hawang), HAWANG));
        fonts.add(new Font(context.getString(R.string.font_name_qimed), QIMED));
        fonts.add(new Font(context.getString(R.string.font_name_scnin), SCNIN));
        fonts.add(new Font(context.getString(R.string.font_name_qimeg), QIMEG));
        fonts.add(new Font(context.getString(R.string.font_name_hara), HARA));
        fonts.add(new Font(context.getString(R.string.font_name_narin), NARIN));
        fonts.add(new Font(context.getString(R.string.font_name_mcdcnbar), MCDVNBAR));
        fonts.add(new Font(context.getString(R.string.font_name_amglang), AMGLANG));
        fonts.add(new Font(context.getString(R.string.font_name_sidam), SIDAM));
        fonts.add(new Font(context.getString(R.string.font_name_qingming), QINGMING));
        fonts.add(new Font(context.getString(R.string.font_name_jclgq), JCLGQ));
        fonts.add(new Font(context.getString(R.string.font_name_ujug), UJUG));
        fonts.add(new Font(context.getString(R.string.font_name_garqag), GARQAG));
        fonts.add(new Font(context.getString(R.string.font_name_hvlvsvn), HVLVSVN));
        fonts.add(new Font(context.getString(R.string.font_name_tvgvrai), TVGVRAI));
        fonts.add(new Font(context.getString(R.string.font_name_erihe), ERIHE));
        fonts.add(new Font(context.getString(R.string.font_name_uyanga), UYANGA));
        fonts.add(new Font(context.getString(R.string.font_name_onqa_hara), ONQA_HARA));
        fonts.add(new Font(context.getString(R.string.font_name_svgvnag), SVGVNAG));
        fonts.add(new Font(context.getString(R.string.font_name_svlbiya), SVLBIYA));
        return fonts;
    }
}
