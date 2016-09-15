package net.studymongolian.chimee;

import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/// See Keyboard.java for implementation common to all keyboards

public class KeyboardAeiou extends Keyboard {

    private static Map<Integer, Character> idToShort = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToLong = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToShortPunctuation = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToLongPunctuation = new HashMap<Integer, Character>();


    MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
    //SimpleCursorAdapter cursorAdapter; // adapter for db words
    //ListView lvSuggestions;
    //String suggestionsParent = ""; // keep track of parent of following list
    //List<String> suggestionsUnicode = new ArrayList<String>(); // following
    //protected static final int WORDS_LOADER_ID = 0;
    Boolean punctuationOn = false;

    protected static final char NULL_CHAR = '\u0000';
    protected static final char BACKSPACE = '\u232b';
    protected static final char SPACE = ' ';
    protected static final char NEW_LINE = '\n';
    protected static final char ZWJ = MongolUnicodeRenderer.Uni.ZWJ;// ZeroWidthJoiner
    protected static final char NNBS = MongolUnicodeRenderer.Uni.NNBS;// NarrowNonBreakingSpace
    protected static final char FVS1 = MongolUnicodeRenderer.Uni.FVS1;// FreeVariationSelector
    protected static final char FVS2 = MongolUnicodeRenderer.Uni.FVS2;
    protected static final char FVS3 = MongolUnicodeRenderer.Uni.FVS3;
    protected static final char MVS = MongolUnicodeRenderer.Uni.MVS;// VOWEL SEPARATOR
    protected static final char MONGOLIAN_DOT = '\u00b7';
    protected static final char MONGOLIAN_DASH = '\ufe31';
    protected static final char PUNCTUATION_QUESTION_EXCLAMATION = '\u2048';
    protected static final char PUNCTUATION_EXCLAMATION_QUESTION = '\u2049';
    protected static final char PUNCTUATION_EXCLAMATION_EXCLAMATION = '\u203c';
    protected static final char PUNCTUATION_DOUBLEQUOTE_TOP = '\u00ab';
    protected static final char PUNCTUATION_DOUBLEQUOTE_BOTTOM = '\u00bb';


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // map the keys to their characters
        initMap();

        // inflate layout and add listeners to each key
        View layout = inflater.inflate(R.layout.fragment_keyboard_aeiou, container, false);
        addListeners(layout);

        return layout;
    }

    @Override
    public void initMap() {

        // unicode characters for key taps
        idToShort.put(R.id.key_a, MongolUnicodeRenderer.Uni.A);
        idToShort.put(R.id.key_e, MongolUnicodeRenderer.Uni.E);
        idToShort.put(R.id.key_i, MongolUnicodeRenderer.Uni.I);
        idToShort.put(R.id.key_v, MongolUnicodeRenderer.Uni.U);
        idToShort.put(R.id.key_u, MongolUnicodeRenderer.Uni.UE);
        idToShort.put(R.id.key_n, MongolUnicodeRenderer.Uni.NA);
        idToShort.put(R.id.key_b, MongolUnicodeRenderer.Uni.BA);
        idToShort.put(R.id.key_h, MongolUnicodeRenderer.Uni.QA);
        idToShort.put(R.id.key_g, MongolUnicodeRenderer.Uni.GA);
        idToShort.put(R.id.key_m, MongolUnicodeRenderer.Uni.MA);
        idToShort.put(R.id.key_l, MongolUnicodeRenderer.Uni.LA);
        idToShort.put(R.id.key_s, MongolUnicodeRenderer.Uni.SA);
        idToShort.put(R.id.key_d, MongolUnicodeRenderer.Uni.DA);
        idToShort.put(R.id.key_q, MongolUnicodeRenderer.Uni.CHA);
        idToShort.put(R.id.key_j, MongolUnicodeRenderer.Uni.JA);
        idToShort.put(R.id.key_y, MongolUnicodeRenderer.Uni.YA);
        idToShort.put(R.id.key_r, MongolUnicodeRenderer.Uni.RA);
        idToShort.put(R.id.key_w, MongolUnicodeRenderer.Uni.WA);
        idToShort.put(R.id.key_z, MongolUnicodeRenderer.Uni.ZA);
        idToShort.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_COMMA);
        idToShort.put(R.id.key_question, '?');
        idToShort.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.MVS);
        idToShort.put(R.id.key_return, NEW_LINE);

        // unicode characters for long key presses
        idToLong.put(R.id.key_a, MongolUnicodeRenderer.Uni.MONGOLIAN_NIRUGU);
        idToLong.put(R.id.key_e, MongolUnicodeRenderer.Uni.EE);
        idToLong.put(R.id.key_i, MongolUnicodeRenderer.Uni.I);
        idToLong.put(R.id.key_v, MongolUnicodeRenderer.Uni.O);
        idToLong.put(R.id.key_u, MongolUnicodeRenderer.Uni.OE);
        idToLong.put(R.id.key_n, MongolUnicodeRenderer.Uni.ANG);
        idToLong.put(R.id.key_b, MongolUnicodeRenderer.Uni.PA);
        idToLong.put(R.id.key_h, MongolUnicodeRenderer.Uni.HAA);
        idToLong.put(R.id.key_g, MongolUnicodeRenderer.Uni.KA);
        idToLong.put(R.id.key_m, MongolUnicodeRenderer.Uni.MA);
        idToLong.put(R.id.key_l, MongolUnicodeRenderer.Uni.LHA);
        idToLong.put(R.id.key_s, MongolUnicodeRenderer.Uni.SHA);
        idToLong.put(R.id.key_d, MongolUnicodeRenderer.Uni.TA);
        idToLong.put(R.id.key_q, MongolUnicodeRenderer.Uni.CHI);
        idToLong.put(R.id.key_j, MongolUnicodeRenderer.Uni.ZHI);
        idToLong.put(R.id.key_y, MongolUnicodeRenderer.Uni.YA);
        idToLong.put(R.id.key_r, MongolUnicodeRenderer.Uni.ZRA);
        idToLong.put(R.id.key_w, MongolUnicodeRenderer.Uni.FA);
        idToLong.put(R.id.key_z, MongolUnicodeRenderer.Uni.TSA);
        idToLong.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_FULL_STOP);
        idToLong.put(R.id.key_question, '!');
        idToLong.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.ZWJ);

        // punctuation for key taps
        idToShortPunctuation.put(R.id.key_a, '(');
        idToShortPunctuation.put(R.id.key_e, ')');
        idToShortPunctuation.put(R.id.key_i, PUNCTUATION_DOUBLEQUOTE_TOP);
        idToShortPunctuation.put(R.id.key_v, PUNCTUATION_DOUBLEQUOTE_BOTTOM);
        idToShortPunctuation.put(R.id.key_u, MONGOLIAN_DOT);
        idToShortPunctuation.put(R.id.key_n, '1');
        idToShortPunctuation.put(R.id.key_b, '2');
        idToShortPunctuation.put(R.id.key_h, '3');
        idToShortPunctuation.put(R.id.key_g, '4');
        idToShortPunctuation.put(R.id.key_m, '5');
        idToShortPunctuation.put(R.id.key_l, MONGOLIAN_DASH);
        idToShortPunctuation.put(R.id.key_s, '6');
        idToShortPunctuation.put(R.id.key_d, '7');
        idToShortPunctuation.put(R.id.key_q, '8');
        idToShortPunctuation.put(R.id.key_j, '9');
        idToShortPunctuation.put(R.id.key_y, '0');
        idToShortPunctuation.put(R.id.key_r, '.');
        idToShortPunctuation.put(R.id.key_w, PUNCTUATION_QUESTION_EXCLAMATION);
        idToShortPunctuation.put(R.id.key_z, PUNCTUATION_EXCLAMATION_EXCLAMATION);
        idToShortPunctuation.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_COMMA);
        idToShortPunctuation.put(R.id.key_question, '?');
        idToShortPunctuation.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.MVS);
        idToShortPunctuation.put(R.id.key_return, NEW_LINE);

        // punctuation for long key presses
        idToLongPunctuation.put(R.id.key_a, '[');
        idToLongPunctuation.put(R.id.key_e, ']');
        idToLongPunctuation.put(R.id.key_i, '<');
        idToLongPunctuation.put(R.id.key_v, '>');
        idToLongPunctuation.put(R.id.key_u, MongolUnicodeRenderer.Uni.MONGOLIAN_ELLIPSIS);
        idToLongPunctuation.put(R.id.key_n, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_ONE);
        idToLongPunctuation.put(R.id.key_b, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_TWO);
        idToLongPunctuation.put(R.id.key_h, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_THREE);
        idToLongPunctuation.put(R.id.key_g, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_FOUR);
        idToLongPunctuation.put(R.id.key_m, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_FIVE);
        idToLongPunctuation.put(R.id.key_l, MongolUnicodeRenderer.Uni.MONGOLIAN_BIRGA);
        idToLongPunctuation.put(R.id.key_s, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_SIX);
        idToLongPunctuation.put(R.id.key_d, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_SEVEN);
        idToLongPunctuation.put(R.id.key_q, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_EIGHT);
        idToLongPunctuation.put(R.id.key_j, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_NINE);
        idToLongPunctuation.put(R.id.key_y, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_ZERO);
        idToLongPunctuation.put(R.id.key_r, MongolUnicodeRenderer.Uni.MONGOLIAN_FOUR_DOTS);
        idToLongPunctuation.put(R.id.key_w, MongolUnicodeRenderer.Uni.MONGOLIAN_COLON);
        idToLongPunctuation.put(R.id.key_z, ';');
        idToLongPunctuation.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_FULL_STOP);
        idToLongPunctuation.put(R.id.key_question, '!');
        idToLongPunctuation.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.ZWJ);

    }



    private void addListeners(View v) {

        // *** Normal Keys ***

        // click
        v.findViewById(R.id.key_a).setOnClickListener(this);
        v.findViewById(R.id.key_e).setOnClickListener(this);
        v.findViewById(R.id.key_i).setOnClickListener(this);
        v.findViewById(R.id.key_v).setOnClickListener(this);
        v.findViewById(R.id.key_u).setOnClickListener(this);
        v.findViewById(R.id.key_n).setOnClickListener(this);
        v.findViewById(R.id.key_b).setOnClickListener(this);
        v.findViewById(R.id.key_h).setOnClickListener(this);
        v.findViewById(R.id.key_g).setOnClickListener(this);
        v.findViewById(R.id.key_m).setOnClickListener(this);
        v.findViewById(R.id.key_l).setOnClickListener(this);
        v.findViewById(R.id.key_s).setOnClickListener(this);
        v.findViewById(R.id.key_d).setOnClickListener(this);
        v.findViewById(R.id.key_q).setOnClickListener(this);
        v.findViewById(R.id.key_j).setOnClickListener(this);
        v.findViewById(R.id.key_y).setOnClickListener(this);
        v.findViewById(R.id.key_r).setOnClickListener(this);
        v.findViewById(R.id.key_w).setOnClickListener(this);
        v.findViewById(R.id.key_z).setOnClickListener(this);
        v.findViewById(R.id.key_comma).setOnClickListener(this);
        v.findViewById(R.id.key_question).setOnClickListener(this);
        v.findViewById(R.id.key_namalaga).setOnClickListener(this);
        v.findViewById(R.id.key_return).setOnClickListener(this);

        // long click
        v.findViewById(R.id.key_a).setOnLongClickListener(this);
        v.findViewById(R.id.key_e).setOnLongClickListener(this);
        v.findViewById(R.id.key_i).setOnLongClickListener(this);
        v.findViewById(R.id.key_v).setOnLongClickListener(this);
        v.findViewById(R.id.key_u).setOnLongClickListener(this);
        v.findViewById(R.id.key_n).setOnLongClickListener(this);
        v.findViewById(R.id.key_b).setOnLongClickListener(this);
        v.findViewById(R.id.key_h).setOnLongClickListener(this);
        v.findViewById(R.id.key_g).setOnLongClickListener(this);
        v.findViewById(R.id.key_m).setOnLongClickListener(this);
        v.findViewById(R.id.key_l).setOnLongClickListener(this);
        v.findViewById(R.id.key_s).setOnLongClickListener(this);
        v.findViewById(R.id.key_d).setOnLongClickListener(this);
        v.findViewById(R.id.key_q).setOnLongClickListener(this);
        v.findViewById(R.id.key_j).setOnLongClickListener(this);
        v.findViewById(R.id.key_y).setOnLongClickListener(this);
        v.findViewById(R.id.key_r).setOnLongClickListener(this);
        v.findViewById(R.id.key_w).setOnLongClickListener(this);
        v.findViewById(R.id.key_z).setOnLongClickListener(this);
        v.findViewById(R.id.key_comma).setOnLongClickListener(this);
        v.findViewById(R.id.key_question).setOnLongClickListener(this);
        v.findViewById(R.id.key_namalaga).setOnLongClickListener(this);


        // *** Special Keys ***

        v.findViewById(R.id.key_fvs).setOnTouchListener(handleFvsTouch);
        v.findViewById(R.id.key_case_suffix).setOnClickListener(handleCaseSuffixClick);
        v.findViewById(R.id.key_backspace).setOnTouchListener(handleBackspace);
        v.findViewById(R.id.key_input).setOnTouchListener(handleInputTouch);
        v.findViewById(R.id.key_space).setOnTouchListener(handleSpace);

    }

//    private void initListView(View v) {
//        // set up the adapter for the list view
//        String[] fromColumns = { ChimeeUserDictionary.Words.WORD };
//        int[] toViews = { R.id.tvMongolListViewItem };
//        cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
//                R.layout.mongol_suggestions_listview, null, fromColumns, toViews, 0) {
//            // Format the unicode from the db for the font
//            @Override
//            public void setViewText(TextView v, String text) {
//                String renderedText = renderer.unicodeToGlyphs(text);
//                super.setViewText(v, renderedText);
//            }
//        };
//        lvSuggestions = (ListView) v.findViewById(R.id.lvSuggestions);
//        lvSuggestions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        lvSuggestions.setOnItemClickListener(this);
//        lvSuggestions.setOnItemLongClickListener(this);
//        lvSuggestions.setAdapter(cursorAdapter);
//    }

    @Override
    public void onClick(View v) {

        char inputChar;

        if (punctuationOn) {
            inputChar = idToShortPunctuation.get(v.getId());
        } else {
            inputChar = idToShort.get(v.getId());
        }

        mListener.keyWasTapped(inputChar);
    }

    @Override
    public boolean onLongClick(View v) {

        char inputChar;

        if (punctuationOn) {
            inputChar = idToLongPunctuation.get(v.getId());
        } else {
            inputChar = idToLong.get(v.getId());
        }

        mListener.keyWasTapped(inputChar);

        return true;
    }

    private View.OnTouchListener handleFvsTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // TODO show fvs chooser view on touch down
            // TODO update hilighted on touch move
            // TODO hide fvs chooser view and send fvs char on touch up

            return false;
        }
    };

    private View.OnClickListener handleCaseSuffixClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO send char input
            mListener.keySuffix();
            // TODO update suggestion bar
        }
    };

    private View.OnTouchListener handleInputTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // TODO if touch is shorter than threshold then switch punctuation mode
            // TODO if longer then show keyboard chooser

            return false;
        }
    };
}
