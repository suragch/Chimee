package net.studymongolian.chimee;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

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

    protected enum CurrentFvsSelection {
        FVS1,
        FVS2,
        FVS3,
        Unselected
    }

    protected enum KeyMode {
        Mongol,
        Lowercase,
        Uppercase,
        Punctuation
    }

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

    public abstract void switchKeys(KeyMode mode);

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

    protected View.OnTouchListener handleFvsTouch = new View.OnTouchListener() {


        View popupView;
        int popupWidth;
        PopupWindow popupWindow;
        LinearLayout llFvs1;
        LinearLayout llFvs2;
        LinearLayout llFvs3;
        int numberOfFvsChoices = 3;
        CurrentFvsSelection currentFvsSelection = CurrentFvsSelection.FVS1;


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // show fvs chooser view on touch down
            // update hilighted on touch move
            // hide fvs chooser view and send fvs char on touch up



            int action = MotionEventCompat.getActionMasked(event);

            switch(action) {
                case (MotionEvent.ACTION_DOWN) :


                    TextView tvFvs1Top = (TextView) v.findViewById(R.id.tvFvs1Top);
                    TextView tvFvs1Bottom = (TextView) v.findViewById(R.id.tvFvs1Bottom);
                    TextView tvFvs2Top = (TextView) v.findViewById(R.id.tvFvs2Top);
                    TextView tvFvs2Bottom = (TextView) v.findViewById(R.id.tvFvs2Bottom);
                    TextView tvFvs3Top = (TextView) v.findViewById(R.id.tvFvs3Top);
                    TextView tvFvs3Bottom = (TextView) v.findViewById(R.id.tvFvs3Bottom);

                    // No input values, so cancel touch events
                    if (TextUtils.isEmpty(tvFvs1Top.getText()) && TextUtils.isEmpty(tvFvs1Bottom.getText())) {
                        return false;
                    }

                    // If only FVS1 is available
                    if (TextUtils.isEmpty(tvFvs2Top.getText()) && TextUtils.isEmpty(tvFvs2Bottom.getText())) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS1);
                        clearFvsKeys();
                        return false;
                    } else {
                        // set text for FVS1 and FVS2
                        popupView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fvs_chooser, null);
                        TextView tv1Top = (TextView) popupView.findViewById(R.id.tvFvs1Top);
                        tv1Top.setText(tvFvs1Top.getText());
                        TextView tv1Bottom = (TextView) popupView.findViewById(R.id.tvFvs1Bottom);
                        tv1Bottom.setText(tvFvs1Bottom.getText());
                        TextView tv2Top = (TextView) popupView.findViewById(R.id.tvFvs2Top);
                        tv2Top.setText(tvFvs2Top.getText());
                        TextView tv2Bottom = (TextView) popupView.findViewById(R.id.tvFvs2Bottom);
                        tv2Bottom.setText(tvFvs2Bottom.getText());

                        llFvs1 = (LinearLayout) popupView.findViewById(R.id.key_fvs1);
                        llFvs2 = (LinearLayout) popupView.findViewById(R.id.key_fvs2);
                        llFvs3 = (LinearLayout) popupView.findViewById(R.id.key_fvs3);

                        llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                        llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                        llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    }

                    // If only FVS1 and FVS2 are available, hide 3rd button
                    if (TextUtils.isEmpty(tvFvs3Top.getText()) && TextUtils.isEmpty(tvFvs3Bottom.getText())) {
                        llFvs3.setVisibility(View.GONE);
                        numberOfFvsChoices = 2;
                    } else {
                        // set text for FVS3
                        TextView tv3Top = (TextView) popupView.findViewById(R.id.tvFvs3Top);
                        tv3Top.setText(tvFvs3Top.getText());
                        TextView tv3Bottom = (TextView) popupView.findViewById(R.id.tvFvs3Bottom);
                        tv3Bottom.setText(tvFvs3Bottom.getText());
                        numberOfFvsChoices = 3;
                    }

                    // Show popup window above fvs key
                    popupWindow = new PopupWindow(popupView,
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    int location[] = new int[2];
                    v.getLocationOnScreen(location);
                    //View popupLayout = getActivity().getLayoutInflater().inflate(R.layout.linearlayout_popup, base);
                    popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    popupWidth = popupView.getMeasuredWidth();
                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popupView.getMeasuredHeight());

                    return true;
                case (MotionEvent.ACTION_MOVE):

                    float x = event.getX();
                    //int padding = 0; // TODO is this needed?
                    float unit = popupWidth / numberOfFvsChoices; // TODO what about for 2 buttons?

                    // select FVS1-3 and set highlight background color
                    if (x < 0 || x > 3 * unit) {
                        if (currentFvsSelection != CurrentFvsSelection.Unselected) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = CurrentFvsSelection.Unselected;
                        }
                    } else if (x > 0 && x <= unit) {
                        if (currentFvsSelection != CurrentFvsSelection.FVS1) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = CurrentFvsSelection.FVS1;
                        }
                    } else if (x > unit && x <= 2 * unit) {
                        if (currentFvsSelection != CurrentFvsSelection.FVS2) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = CurrentFvsSelection.FVS2;
                        }
                    } else if (x > 2 * unit && x <= 3 * unit) {
                        if (numberOfFvsChoices == 2) {
                            if (currentFvsSelection != CurrentFvsSelection.Unselected) {
                                llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                currentFvsSelection = CurrentFvsSelection.Unselected;
                            }
                        } else if (numberOfFvsChoices == 3) {
                            if (currentFvsSelection != CurrentFvsSelection.FVS3) {
                                llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                currentFvsSelection = CurrentFvsSelection.FVS3;
                            }
                        }
                    }

                    return true;
                case (MotionEvent.ACTION_UP) :
                    // allow to fall through to the default (dismiss the popup window)
                    if (currentFvsSelection == CurrentFvsSelection.FVS1) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS1);
                        clearFvsKeys();
                    } else if (currentFvsSelection == CurrentFvsSelection.FVS2) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS2);
                        clearFvsKeys();
                    } else if (currentFvsSelection == CurrentFvsSelection.FVS3) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS3);
                        clearFvsKeys();
                    }

                default :
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    return false;
            }
        }
    };

    protected void setOnTouchListenerForKeybordSwitcherView(View view, final ArrayList<KeyboardType> displayOrder) {

        final int LONGPRESS_THRESHOLD = 500; // milliseconds
        final int NUMBER_OF_OTHER_KEYBOARDS = 3;

        if (displayOrder.size() != NUMBER_OF_OTHER_KEYBOARDS) {
            throw new RuntimeException("The keyboard is currently set up to display exactly " + NUMBER_OF_OTHER_KEYBOARDS +
                    " other keyboards. If you wish to have a different number of keyboards then you" +
                    " need to edit this method and the popup xml file.");
        }


        view.setOnTouchListener(new View.OnTouchListener() {

            Handler handler;

            View popupView;
            int popupWidth;
            PopupWindow popupWindow;
            boolean showingPopup = false;
            KeyboardType currentSelection = displayOrder.get(0);
            FrameLayout fl1;
            FrameLayout fl2;
            FrameLayout fl3;

            @Override
            public boolean onTouch(final View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clearFvsKeys();
                        Log.i("TAG", "touched down");


                        handler = new Handler();

                        Runnable runnableCode = new Runnable() {
                            @Override
                            public void run() {

                                // get the popup view
                                popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_keyboard_chooser, null);
                                MongolTextView tvFirst = (MongolTextView) popupView.findViewById(R.id.tvKeyboardFirstChoice);
                                MongolTextView tvSecond = (MongolTextView) popupView.findViewById(R.id.tvKeyboardSecondChoice);
                                MongolTextView tvThird = (MongolTextView) popupView.findViewById(R.id.tvKeyboardThirdChoice);

                                tvFirst.setTextWithRenderedUnicode(displayOrder.get(0).toString());
                                tvSecond.setTextWithRenderedUnicode(displayOrder.get(1).toString());
                                tvThird.setTextWithRenderedUnicode(displayOrder.get(2).toString());

                                fl1 = (FrameLayout) popupView.findViewById(R.id.flKeyboardFirstChoice);
                                fl2 = (FrameLayout) popupView.findViewById(R.id.flKeyboardSecondChoice);
                                fl3 = (FrameLayout) popupView.findViewById(R.id.flKeyboardThirdChoice);

                                fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));


                                popupWindow = new PopupWindow(popupView,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                int location[] = new int[2];
                                v.getLocationOnScreen(location);
                                popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                popupWidth = popupView.getMeasuredWidth();
                                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popupView.getMeasuredHeight());

                                showingPopup = true;

                            }
                        };

                        handler.postDelayed(runnableCode, LONGPRESS_THRESHOLD);

                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (!showingPopup) {
                            break;
                        }

                        float x = event.getX();
                        float unit = popupWidth / NUMBER_OF_OTHER_KEYBOARDS;

                        // select FVS1-3 and set highlight background color
                        if (x < 0 || x > 3 * unit) {
                            if (currentSelection != KeyboardType.Unselected) {
                                fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                currentSelection = KeyboardType.Unselected;
                            }
                        } else if (x >= 0 && x <= unit) {
                            if (currentSelection != displayOrder.get(0)) {
                                fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                currentSelection = displayOrder.get(0);
                            }
                        } else if (x > unit && x <= 2 * unit) {
                            if (currentSelection != displayOrder.get(1)) {
                                fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                currentSelection = displayOrder.get(1);
                            }
                        } else if (x > 2 * unit && x <= 3 * unit) {
                            if (currentSelection != displayOrder.get(2)) {
                                fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                currentSelection = displayOrder.get(2);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("TAG", "touched up");

                        handler.removeCallbacksAndMessages(null);

                        if (showingPopup) {
                            // hide popup
                            if (popupWindow != null) {
                                popupWindow.dismiss();
                                showingPopup = false;
                                if (currentSelection != KeyboardType.Unselected) {
                                    mListener.keyNewKeyboardChosen(currentSelection);
                                }
                            }
                        } else {
                            switchKeys(KeyMode.Punctuation);
                        }

                        break;
                }

                return true;
            }
        });

    }


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

//    protected final class Suffix {
//        public final String YIN = getResources().getString(R.string.suffix_yin);
//        public final String ON = getResources().getString(R.string.suffix_on);
//        public final String UN = getResources().getString(R.string.suffix_un);
//        public final String O = getResources().getString(R.string.suffix_o);
//        public final String U = getResources().getString(R.string.suffix_u);
//        public final String I = getResources().getString(R.string.suffix_i);
//        public final String YI = getResources().getString(R.string.suffix_yi);
//        public final String DO = getResources().getString(R.string.suffix_do);
//        public final String DU = getResources().getString(R.string.suffix_du);
//        public final String TO = getResources().getString(R.string.suffix_to);
//        public final String TU = getResources().getString(R.string.suffix_tu);
//        public final String ACHA = getResources().getString(R.string.suffix_acha);
//        public final String ECHE = getResources().getString(R.string.suffix_eche);
//        public final String BAR = getResources().getString(R.string.suffix_bar);
//        public final String BER = getResources().getString(R.string.suffix_ber);
//        public final String IYAR = getResources().getString(R.string.suffix_iyar);
//        public final String IYER = getResources().getString(R.string.suffix_iyer);
//        public final String TAI = getResources().getString(R.string.suffix_tai);
//        public final String TEI = getResources().getString(R.string.suffix_tei);
//        public final String IYAN = getResources().getString(R.string.suffix_iyan);
//        public final String IYEN = getResources().getString(R.string.suffix_iyen);
//        public final String BAN = getResources().getString(R.string.suffix_ban);
//        public final String BEN = getResources().getString(R.string.suffix_ben);
//        public final String OO = getResources().getString(R.string.suffix_oo);
//        public final String UU = getResources().getString(R.string.suffix_uu);
//        public final String YOGAN = getResources().getString(R.string.suffix_yogan);
//        public final String YUGEN = getResources().getString(R.string.suffix_yugen);
//        public final String DAGAN = getResources().getString(R.string.suffix_dagan);
//        public final String DEGEN = getResources().getString(R.string.suffix_degen);
//        public final String TAGAN = getResources().getString(R.string.suffix_tagan);
//        public final String TEGEN = getResources().getString(R.string.suffix_tegen);
//        public final String ACHAGAN = getResources().getString(R.string.suffix_achagan);
//        public final String ECHEGEN = getResources().getString(R.string.suffix_echegen);
//        public final String TAIGAN = getResources().getString(R.string.suffix_taigan);
//        public final String TEIGEN = getResources().getString(R.string.suffix_teigen);
//        public final String OD = getResources().getString(R.string.suffix_od);
//        public final String UD = getResources().getString(R.string.suffix_ud);
//        public final String NOGOD = getResources().getString(R.string.suffix_nogod);
//        public final String NUGUD = getResources().getString(R.string.suffix_nugud);
//        public final String NAR = getResources().getString(R.string.suffix_nar);
//        public final String NER = getResources().getString(R.string.suffix_ner);
//    }
}
