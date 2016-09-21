package net.studymongolian.chimee;



import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public abstract class Keyboard extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    OnKeyboardListener mListener;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        // check if parent Fragment implements listener
        if (getParentFragment() instanceof OnKeyboardListener) {
            mListener = (OnKeyboardListener) getParentFragment();
        } else {
            throw new RuntimeException("Parent fragment must implement OnKeyboardListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public abstract void onClick(View v);

    @Override
    public abstract boolean onLongClick(View v);

    public abstract void initMap();

    public abstract void switchPunctuation();

    // Handles continuous space and backspace presses
    protected View.OnTouchListener handleSpace = new View.OnTouchListener() {

        private Handler handler;
        final int INITIAL_DELAY = 500;
        final int REPEAT_DELAY = 50;

        @Override
        public boolean onTouch(View view, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clearFvsKeys();
                    view.setPressed(true);
                    doSpace();
                    if (handler != null)
                        return true;
                    handler = new Handler();
                    handler.postDelayed(actionSpace, INITIAL_DELAY);
                    break;
                case MotionEvent.ACTION_UP:
                    view.setPressed(false);
                    if (handler == null)
                        return true;
                    handler.removeCallbacks(actionSpace);
                    handler = null;
                    break;
            }


            return true;
        }

        private void doSpace(){
            mListener.keyWasTapped(SPACE);
            //doPostKeyPressActivities(SPACE);
        }

        Runnable actionSpace = new Runnable() {
            @Override
            public void run() {
                doSpace();
                handler.postDelayed(this, REPEAT_DELAY);
            }
        };

    };



    protected View.OnTouchListener handleBackspace = new View.OnTouchListener() {

        private Handler handler;
        final int INITIAL_DELAY = 500;
        final int REPEAT_DELAY = 50;

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clearFvsKeys();
                    view.setPressed(true);
                    doBackspace();
                    if (handler != null)
                        return true;
                    handler = new Handler();
                    handler.postDelayed(actionBackspace, INITIAL_DELAY);
                    break;
                case MotionEvent.ACTION_UP:
                    view.setPressed(false);
                    if (handler == null)
                        return true;
                    handler.removeCallbacks(actionBackspace);
                    handler = null;
                    break;
            }

            return true;
        }

        private void doBackspace(){
            mListener.keyBackspace();
        }

        Runnable actionBackspace = new Runnable() {
            @Override
            public void run() {
                doBackspace();
                handler.postDelayed(this, REPEAT_DELAY);
            }
        };

    };

    public void clearFvsKeys() {
        // This method may be overridden by subclasses
    }

    public interface OnKeyboardListener {
        void keyWasTapped(char character);
        void keyBackspace();
        void keySuffix();
        void keyMvs();
        void keyNewKeyboardChosen(KeyboardType type);
        char getCharBeforeCursor();
    }

    protected final class Suffix {
        public final String YIN = getResources().getString(R.string.suffix_yin);
        public final String ON = getResources().getString(R.string.suffix_on);
        public final String UN = getResources().getString(R.string.suffix_un);
        public final String O = getResources().getString(R.string.suffix_o);
        public final String U = getResources().getString(R.string.suffix_u);
        public final String I = getResources().getString(R.string.suffix_i);
        public final String YI = getResources().getString(R.string.suffix_yi);
        public final String DO = getResources().getString(R.string.suffix_do);
        public final String DU = getResources().getString(R.string.suffix_du);
        public final String TO = getResources().getString(R.string.suffix_to);
        public final String TU = getResources().getString(R.string.suffix_tu);
        public final String ACHA = getResources().getString(R.string.suffix_acha);
        public final String ECHE = getResources().getString(R.string.suffix_eche);
        public final String BAR = getResources().getString(R.string.suffix_bar);
        public final String BER = getResources().getString(R.string.suffix_ber);
        public final String IYAR = getResources().getString(R.string.suffix_iyar);
        public final String IYER = getResources().getString(R.string.suffix_iyer);
        public final String TAI = getResources().getString(R.string.suffix_tai);
        public final String TEI = getResources().getString(R.string.suffix_tei);
        public final String IYAN = getResources().getString(R.string.suffix_iyan);
        public final String IYEN = getResources().getString(R.string.suffix_iyen);
        public final String BAN = getResources().getString(R.string.suffix_ban);
        public final String BEN = getResources().getString(R.string.suffix_ben);
        public final String OO = getResources().getString(R.string.suffix_oo);
        public final String UU = getResources().getString(R.string.suffix_uu);
        public final String YOGAN = getResources().getString(R.string.suffix_yogan);
        public final String YUGEN = getResources().getString(R.string.suffix_yugen);
        public final String DAGAN = getResources().getString(R.string.suffix_dagan);
        public final String DEGEN = getResources().getString(R.string.suffix_degen);
        public final String TAGAN = getResources().getString(R.string.suffix_tagan);
        public final String TEGEN = getResources().getString(R.string.suffix_tegen);
        public final String ACHAGAN = getResources().getString(R.string.suffix_achagan);
        public final String ECHEGEN = getResources().getString(R.string.suffix_echegen);
        public final String TAIGAN = getResources().getString(R.string.suffix_taigan);
        public final String TEIGEN = getResources().getString(R.string.suffix_teigen);
        public final String OD = getResources().getString(R.string.suffix_od);
        public final String UD = getResources().getString(R.string.suffix_ud);
        public final String NOGOD = getResources().getString(R.string.suffix_nogod);
        public final String NUGUD = getResources().getString(R.string.suffix_nugud);
        public final String NAR = getResources().getString(R.string.suffix_nar);
        public final String NER = getResources().getString(R.string.suffix_ner);
    }
}
