package net.studymongolian.chimee;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yonghu on 16-11-1.
 */

public class KeyboardQwerty extends Keyboard {

    private static Map<Integer, Character> idToShort = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToLong = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToShortPunctuation = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToLongPunctuation = new HashMap<Integer, Character>();


    //private final String DEBUG_TAG = "debug tag";
    MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
    //Boolean punctuationOn = false;
    KeyMode currentKeyMode = KeyMode.Mongol;
    private CurrentFvsSelection currentFvsSelection = CurrentFvsSelection.FVS1;


    TextView tvQ;
    TextView tvW;
    TextView tvE;
    TextView tvR;
    TextView tvT;
    TextView tvY;
    TextView tvU;
    TextView tvI;
    TextView tvO;
    TextView tvP;
    TextView tvA;
    TextView tvS;
    TextView tvD;
    TextView tvF;
    TextView tvG;
    TextView tvH;
    TextView tvJ;
    TextView tvK;
    TextView tvL;
    TextView tvNg;
    TextView tvZ;
    TextView tvX;
    TextView tvC;
    TextView tvV;
    TextView tvB;
    TextView tvN;
    TextView tvM;

    TextView tvQlong;
    TextView tvWlong;
    TextView tvElong;
    TextView tvRlong;
    TextView tvTlong;
    TextView tvYlong;
    TextView tvUlong;
    TextView tvIlong;
    TextView tvOlong;
    TextView tvPlong;
    TextView tvHlong;
    TextView tvJlong;
    TextView tvLlong;
    TextView tvZlong;

    TextView tvNamalaga;
    TextView tvCaseSuffix;
    TextView tvFvs1Top;
    TextView tvFvs1Bottom;
    TextView tvFvs2Top;
    TextView tvFvs2Bottom;
    TextView tvFvs3Top;
    TextView tvFvs3Bottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // map the keys to their characters
        initMap();

        // inflate layout and add listeners to each key
        View layout = inflater.inflate(R.layout.fragment_keyboard_qwerty, container, false);
        addListeners(layout);
        //popupView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fvs_chooser, null);


        return layout;
    }

    @Override
    public void initMap() {

        // unicode characters for key taps
        idToShort.put(R.id.key_q, MongolUnicodeRenderer.Uni.CHA);
        idToShort.put(R.id.key_w, MongolUnicodeRenderer.Uni.WA);
        idToShort.put(R.id.key_e, MongolUnicodeRenderer.Uni.E);
        idToShort.put(R.id.key_r, MongolUnicodeRenderer.Uni.RA);
        idToShort.put(R.id.key_t, MongolUnicodeRenderer.Uni.TA);
        idToShort.put(R.id.key_y, MongolUnicodeRenderer.Uni.YA);
        idToShort.put(R.id.key_u, MongolUnicodeRenderer.Uni.UE);
        idToShort.put(R.id.key_i, MongolUnicodeRenderer.Uni.I);
        idToShort.put(R.id.key_o, MongolUnicodeRenderer.Uni.OE);
        idToShort.put(R.id.key_p, MongolUnicodeRenderer.Uni.PA);
        idToShort.put(R.id.key_a, MongolUnicodeRenderer.Uni.A);
        idToShort.put(R.id.key_s, MongolUnicodeRenderer.Uni.SA);
        idToShort.put(R.id.key_d, MongolUnicodeRenderer.Uni.DA);
        idToShort.put(R.id.key_f, MongolUnicodeRenderer.Uni.FA);
        idToShort.put(R.id.key_g, MongolUnicodeRenderer.Uni.GA);
        idToShort.put(R.id.key_h, MongolUnicodeRenderer.Uni.QA);
        idToShort.put(R.id.key_j, MongolUnicodeRenderer.Uni.JA);
        idToShort.put(R.id.key_k, MongolUnicodeRenderer.Uni.KA);
        idToShort.put(R.id.key_l, MongolUnicodeRenderer.Uni.LA);
        idToShort.put(R.id.key_ng, MongolUnicodeRenderer.Uni.ANG);
        idToShort.put(R.id.key_z, MongolUnicodeRenderer.Uni.ZA);
        idToShort.put(R.id.key_x, MongolUnicodeRenderer.Uni.SHA);
        idToShort.put(R.id.key_c, MongolUnicodeRenderer.Uni.O);
        idToShort.put(R.id.key_v, MongolUnicodeRenderer.Uni.U);
        idToShort.put(R.id.key_b, MongolUnicodeRenderer.Uni.BA);
        idToShort.put(R.id.key_n, MongolUnicodeRenderer.Uni.NA);
        idToShort.put(R.id.key_m, MongolUnicodeRenderer.Uni.MA);
        idToShort.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_COMMA);
        idToShort.put(R.id.key_question, '?');
        idToShort.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.MVS);
        idToShort.put(R.id.key_return, NEW_LINE);

        // unicode characters for long key presses
        idToLong.put(R.id.key_q, MongolUnicodeRenderer.Uni.CHI);
        idToLong.put(R.id.key_w, MongolUnicodeRenderer.Uni.WA);
        idToLong.put(R.id.key_e, MongolUnicodeRenderer.Uni.EE);
        idToLong.put(R.id.key_r, MongolUnicodeRenderer.Uni.ZRA);
        idToLong.put(R.id.key_t, MongolUnicodeRenderer.Uni.TA);
        idToLong.put(R.id.key_y, MongolUnicodeRenderer.Uni.YA);
        idToLong.put(R.id.key_u, MongolUnicodeRenderer.Uni.UE);
        idToLong.put(R.id.key_i, MongolUnicodeRenderer.Uni.I);
        idToLong.put(R.id.key_o, MongolUnicodeRenderer.Uni.OE);
        idToLong.put(R.id.key_p, MongolUnicodeRenderer.Uni.PA);
        idToLong.put(R.id.key_h, MongolUnicodeRenderer.Uni.HAA);
        idToLong.put(R.id.key_j, MongolUnicodeRenderer.Uni.ZHI);
        idToLong.put(R.id.key_l, MongolUnicodeRenderer.Uni.LHA);
        idToLong.put(R.id.key_z, MongolUnicodeRenderer.Uni.TSA);
        idToLong.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_FULL_STOP);
        idToLong.put(R.id.key_question, '!');
        idToLong.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.ZWJ);

        // punctuation for key taps
        idToShortPunctuation.put(R.id.key_q, '1');
        idToShortPunctuation.put(R.id.key_w, '2');
        idToShortPunctuation.put(R.id.key_e, '3');
        idToShortPunctuation.put(R.id.key_r, '4');
        idToShortPunctuation.put(R.id.key_t, '5');
        idToShortPunctuation.put(R.id.key_y, '6');
        idToShortPunctuation.put(R.id.key_u, '7');
        idToShortPunctuation.put(R.id.key_i, '8');
        idToShortPunctuation.put(R.id.key_o, '9');
        idToShortPunctuation.put(R.id.key_p, '0');
        idToShortPunctuation.put(R.id.key_a, '(');
        idToShortPunctuation.put(R.id.key_s, ')');
        idToShortPunctuation.put(R.id.key_d, '<');
        idToShortPunctuation.put(R.id.key_f, '>');
        idToShortPunctuation.put(R.id.key_g, PUNCTUATION_DOUBLEQUOTE_TOP);
        idToShortPunctuation.put(R.id.key_h, PUNCTUATION_DOUBLEQUOTE_BOTTOM);
        idToShortPunctuation.put(R.id.key_j, PUNCTUATION_QUESTION_EXCLAMATION);
        idToShortPunctuation.put(R.id.key_k, PUNCTUATION_EXCLAMATION_QUESTION);
        idToShortPunctuation.put(R.id.key_l, PUNCTUATION_EXCLAMATION_EXCLAMATION);
        idToShortPunctuation.put(R.id.key_ng, MongolUnicodeRenderer.Uni.MONGOLIAN_COLON);
        idToShortPunctuation.put(R.id.key_z, MongolUnicodeRenderer.Uni.MONGOLIAN_ELLIPSIS);
        idToShortPunctuation.put(R.id.key_x, MongolUnicodeRenderer.Uni.MONGOLIAN_FOUR_DOTS);
        idToShortPunctuation.put(R.id.key_c, MONGOLIAN_DOT);
        idToShortPunctuation.put(R.id.key_v, '.');
        idToShortPunctuation.put(R.id.key_b, MongolUnicodeRenderer.Uni.MONGOLIAN_NIRUGU);
        idToShortPunctuation.put(R.id.key_n, MONGOLIAN_DASH);
        idToShortPunctuation.put(R.id.key_m, ';');
        idToShortPunctuation.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_COMMA);
        idToShortPunctuation.put(R.id.key_question, '?');
        idToShortPunctuation.put(R.id.key_return, NEW_LINE);

        // punctuation for long key presses
        idToLongPunctuation.put(R.id.key_q, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_ONE);
        idToLongPunctuation.put(R.id.key_w, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_TWO);
        idToLongPunctuation.put(R.id.key_e, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_THREE);
        idToLongPunctuation.put(R.id.key_r, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_FOUR);
        idToLongPunctuation.put(R.id.key_t, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_FIVE);
        idToLongPunctuation.put(R.id.key_y, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_SIX);
        idToLongPunctuation.put(R.id.key_u, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_SEVEN);
        idToLongPunctuation.put(R.id.key_i, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_EIGHT);
        idToLongPunctuation.put(R.id.key_o, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_NINE);
        idToLongPunctuation.put(R.id.key_p, MongolUnicodeRenderer.Uni.MONGOLIAN_DIGIT_ZERO);
        idToLongPunctuation.put(R.id.key_h, PUNCTUATION_DOUBLEQUOTE_BOTTOM);
        idToLongPunctuation.put(R.id.key_j, PUNCTUATION_QUESTION_EXCLAMATION);
        idToLongPunctuation.put(R.id.key_l, PUNCTUATION_EXCLAMATION_EXCLAMATION);
        idToLongPunctuation.put(R.id.key_z, MongolUnicodeRenderer.Uni.MONGOLIAN_ELLIPSIS);
        idToLongPunctuation.put(R.id.key_comma, MongolUnicodeRenderer.Uni.MONGOLIAN_FULL_STOP);
        idToLongPunctuation.put(R.id.key_question, '!');
        idToLongPunctuation.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.ZWJ);

    }



    private void addListeners(View v) {

        // *** Normal Keys ***

        // click
        v.findViewById(R.id.key_q).setOnClickListener(this);
        v.findViewById(R.id.key_w).setOnClickListener(this);
        v.findViewById(R.id.key_e).setOnClickListener(this);
        v.findViewById(R.id.key_r).setOnClickListener(this);
        v.findViewById(R.id.key_t).setOnClickListener(this);
        v.findViewById(R.id.key_y).setOnClickListener(this);
        v.findViewById(R.id.key_u).setOnClickListener(this);
        v.findViewById(R.id.key_i).setOnClickListener(this);
        v.findViewById(R.id.key_o).setOnClickListener(this);
        v.findViewById(R.id.key_p).setOnClickListener(this);
        v.findViewById(R.id.key_a).setOnClickListener(this);
        v.findViewById(R.id.key_s).setOnClickListener(this);
        v.findViewById(R.id.key_d).setOnClickListener(this);
        v.findViewById(R.id.key_f).setOnClickListener(this);
        v.findViewById(R.id.key_g).setOnClickListener(this);
        v.findViewById(R.id.key_h).setOnClickListener(this);
        v.findViewById(R.id.key_j).setOnClickListener(this);
        v.findViewById(R.id.key_k).setOnClickListener(this);
        v.findViewById(R.id.key_l).setOnClickListener(this);
        v.findViewById(R.id.key_ng).setOnClickListener(this);
        v.findViewById(R.id.key_z).setOnClickListener(this);
        v.findViewById(R.id.key_x).setOnClickListener(this);
        v.findViewById(R.id.key_c).setOnClickListener(this);
        v.findViewById(R.id.key_v).setOnClickListener(this);
        v.findViewById(R.id.key_b).setOnClickListener(this);
        v.findViewById(R.id.key_n).setOnClickListener(this);
        v.findViewById(R.id.key_m).setOnClickListener(this);
        v.findViewById(R.id.key_comma).setOnClickListener(this);
        v.findViewById(R.id.key_question).setOnClickListener(this);
        v.findViewById(R.id.key_return).setOnClickListener(this);

        // long click
        v.findViewById(R.id.key_q).setOnLongClickListener(this);
        v.findViewById(R.id.key_w).setOnLongClickListener(this);
        v.findViewById(R.id.key_e).setOnLongClickListener(this);
        v.findViewById(R.id.key_r).setOnLongClickListener(this);
        v.findViewById(R.id.key_t).setOnLongClickListener(this);
        v.findViewById(R.id.key_y).setOnLongClickListener(this);
        v.findViewById(R.id.key_u).setOnLongClickListener(this);
        v.findViewById(R.id.key_i).setOnLongClickListener(this);
        v.findViewById(R.id.key_o).setOnLongClickListener(this);
        v.findViewById(R.id.key_p).setOnLongClickListener(this);
        v.findViewById(R.id.key_h).setOnLongClickListener(this);
        v.findViewById(R.id.key_j).setOnLongClickListener(this);
        v.findViewById(R.id.key_l).setOnLongClickListener(this);
        v.findViewById(R.id.key_z).setOnLongClickListener(this);
        v.findViewById(R.id.key_comma).setOnLongClickListener(this);
        v.findViewById(R.id.key_question).setOnLongClickListener(this);
        v.findViewById(R.id.key_namalaga).setOnLongClickListener(this);


        // *** Special Keys ***

        v.findViewById(R.id.key_fvs).setOnTouchListener(handleFvsTouch);
        v.findViewById(R.id.key_namalaga).setOnClickListener(handleMvsClick);
        v.findViewById(R.id.key_case_suffix).setOnClickListener(handleCaseSuffixClick);
        v.findViewById(R.id.key_backspace).setOnTouchListener(handleBackspace);
        v.findViewById(R.id.key_space).setOnTouchListener(handleSpace);

        // input key
        ArrayList<KeyboardType> displayOrder = new ArrayList<>();
        displayOrder.add(KeyboardType.English);
        displayOrder.add(KeyboardType.Cyrillic);
        displayOrder.add(KeyboardType.Aeiou);
        super.setOnTouchListenerForKeybordSwitcherView(v.findViewById(R.id.key_input), displayOrder);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvQ = (TextView) getView().findViewById(R.id.tvMkeyQ);
        tvW = (TextView) getView().findViewById(R.id.tvMkeyW);
        tvE = (TextView) getView().findViewById(R.id.tvMkeyE);
        tvR = (TextView) getView().findViewById(R.id.tvMkeyR);
        tvT = (TextView) getView().findViewById(R.id.tvMkeyT);
        tvY = (TextView) getView().findViewById(R.id.tvMkeyY);
        tvU = (TextView) getView().findViewById(R.id.tvMkeyU);
        tvI = (TextView) getView().findViewById(R.id.tvMkeyI);
        tvO = (TextView) getView().findViewById(R.id.tvMkeyO);
        tvP = (TextView) getView().findViewById(R.id.tvMkeyP);
        tvA = (TextView) getView().findViewById(R.id.tvMkeyA);
        tvS = (TextView) getView().findViewById(R.id.tvMkeyS);
        tvD = (TextView) getView().findViewById(R.id.tvMkeyD);
        tvF = (TextView) getView().findViewById(R.id.tvMkeyF);
        tvG = (TextView) getView().findViewById(R.id.tvMkeyG);
        tvH = (TextView) getView().findViewById(R.id.tvMkeyH);
        tvJ = (TextView) getView().findViewById(R.id.tvMkeyJ);
        tvK = (TextView) getView().findViewById(R.id.tvMkeyK);
        tvL = (TextView) getView().findViewById(R.id.tvMkeyL);
        tvNg = (TextView) getView().findViewById(R.id.tvMkeyNg);
        tvZ = (TextView) getView().findViewById(R.id.tvMkeyZ);
        tvX = (TextView) getView().findViewById(R.id.tvMkeyX);
        tvC = (TextView) getView().findViewById(R.id.tvMkeyC);
        tvV = (TextView) getView().findViewById(R.id.tvMkeyV);
        tvB = (TextView) getView().findViewById(R.id.tvMkeyB);
        tvN = (TextView) getView().findViewById(R.id.tvMkeyN);
        tvM = (TextView) getView().findViewById(R.id.tvMkeyM);

        tvQlong = (TextView) getView().findViewById(R.id.tvMkeyQlong);
        tvWlong = (TextView) getView().findViewById(R.id.tvMkeyWlong);
        tvElong = (TextView) getView().findViewById(R.id.tvMkeyElong);
        tvRlong = (TextView) getView().findViewById(R.id.tvMkeyRlong);
        tvTlong = (TextView) getView().findViewById(R.id.tvMkeyTlong);
        tvYlong = (TextView) getView().findViewById(R.id.tvMkeyYlong);
        tvUlong = (TextView) getView().findViewById(R.id.tvMkeyUlong);
        tvIlong = (TextView) getView().findViewById(R.id.tvMkeyIlong);
        tvOlong = (TextView) getView().findViewById(R.id.tvMkeyOlong);
        tvPlong = (TextView) getView().findViewById(R.id.tvMkeyPlong);
        tvHlong = (TextView) getView().findViewById(R.id.tvMkeyHlong);
        tvJlong = (TextView) getView().findViewById(R.id.tvMkeyJlong);
        tvLlong = (TextView) getView().findViewById(R.id.tvMkeyLlong);
        tvZlong = (TextView) getView().findViewById(R.id.tvMkeyZlong);

        tvNamalaga = (TextView) getView().findViewById(R.id.tvMkeyNamalaga);
        tvCaseSuffix = (TextView) getView().findViewById(R.id.tvMkeyCaseSuffix);
        tvFvs1Top = (TextView) getView().findViewById(R.id.tvFvs1Top);
        tvFvs1Bottom = (TextView) getView().findViewById(R.id.tvFvs1Bottom);
        tvFvs2Top = (TextView) getView().findViewById(R.id.tvFvs2Top);
        tvFvs2Bottom = (TextView) getView().findViewById(R.id.tvFvs2Bottom);
        tvFvs3Top = (TextView) getView().findViewById(R.id.tvFvs3Top);
        tvFvs3Bottom = (TextView) getView().findViewById(R.id.tvFvs3Bottom);

    }

    @Override
    public void onClick(View v) {

        if (currentKeyMode == KeyMode.Punctuation) {
            mListener.keyWasTapped(idToShortPunctuation.get(v.getId()));
        } else if (currentKeyMode == KeyMode.Mongol) {
            char inputChar = idToShort.get(v.getId());
            mListener.keyWasTapped(inputChar);
            updateFvsKeys(inputChar);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        if (currentKeyMode == KeyMode.Punctuation) {
            mListener.keyWasTapped(idToLongPunctuation.get(v.getId()));
        } else if (currentKeyMode == KeyMode.Mongol) {
            char inputChar = idToLong.get(v.getId());
            mListener.keyWasTapped(inputChar);
            updateFvsKeys(inputChar);
        }

        return true;
    }

    private View.OnTouchListener handleFvsTouch = new View.OnTouchListener() {

        View popupView;
        int popupWidth;
        PopupWindow popupWindow;
        LinearLayout llFvs1;
        LinearLayout llFvs2;
        LinearLayout llFvs3;
        int numberOfFvsChoices = 3;


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // TODO show fvs chooser view on touch down
            // TODO update hilighted on touch move
            // TODO hide fvs chooser view and send fvs char on touch up



            int action = MotionEventCompat.getActionMasked(event);

            switch(action) {
                case (MotionEvent.ACTION_DOWN) :
                    Log.d("DEBUG_TAG","Action was DOWN");

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
                    Log.d("DEBUG_TAG", "Action was MOVE " + event.getX());

                    float x = event.getX();
                    //int padding = 0; // TODO is this needed?
                    float unit = popupWidth / numberOfFvsChoices; // TODO what about for 2 buttons?

                    // select FVS1-3 and set highlight background color
                    if (x < 0) {
                        if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.OutOfBoundsLeft) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.OutOfBoundsLeft;
                        }
                    } else if (x > 0 && x <= unit) {
                        if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.FVS1) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.FVS1;
                        }
                    } else if (x > unit && x <= 2 * unit) {
                        if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.FVS2) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.FVS2;
                        }
                    } else if (x > 2 * unit && x <= 3 * unit) {
                        if (numberOfFvsChoices == 2) {
                            if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.OutOfBoundsRight) {
                                llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.OutOfBoundsRight;
                            }
                        } else if (numberOfFvsChoices == 3) {
                            if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.FVS3) {
                                llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                                llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
                                currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.FVS3;
                            }
                        }
                    } else if (x > 3 * unit) {
                        if (currentFvsSelection != KeyboardAeiou.CurrentFvsSelection.OutOfBoundsRight) {
                            llFvs1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            llFvs3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                            currentFvsSelection = KeyboardAeiou.CurrentFvsSelection.OutOfBoundsRight;
                        }
                    }

                    return true;
                case (MotionEvent.ACTION_UP) :
                    // allow to fall through to the default (dismiss the popup window)
                    if (currentFvsSelection == KeyboardAeiou.CurrentFvsSelection.FVS1) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS1);
                        clearFvsKeys();
                    } else if (currentFvsSelection == KeyboardAeiou.CurrentFvsSelection.FVS2) {
                        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.FVS2);
                        clearFvsKeys();
                    } else if (currentFvsSelection == KeyboardAeiou.CurrentFvsSelection.FVS3) {
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

    private View.OnClickListener handleMvsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearFvsKeys();
            if (currentKeyMode == KeyMode.Punctuation) return;
            mListener.keyMvs();
        }
    };

    private View.OnClickListener handleCaseSuffixClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearFvsKeys();
            if (currentKeyMode == KeyMode.Punctuation) return;
            mListener.keySuffix();
        }
    };

//    private View.OnTouchListener handleInputTouch = new View.OnTouchListener() {
//
//        Handler handler = new Handler();
//        int LONGPRESS_THRESHOLD = 500; // milliseconds
//        View popupView;
//        int popupWidth;
//        PopupWindow popupWindow;
//        boolean showingPopup = false;
//        KeyboardType currentSelection = KeyboardType.English;
//        FrameLayout fl1;
//        FrameLayout fl2;
//        FrameLayout fl3;
//
//        @Override
//        public boolean onTouch(final View v, MotionEvent event) {
//
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    clearFvsKeys();
//                    Log.i("TAG", "touched down");
//
//
//                    handler = new Handler();
//
//                    Runnable runnableCode = new Runnable() {
//                        @Override
//                        public void run() {
//
//
//
//                            // TODO show popup
//                            Log.i("TAG", "popup shown");
//                            // Show popup window above keyboard chooser key
//                            popupView = getActivity().getLayoutInflater().inflate(R.layout.popup_keyboard_chooser, null);
//                            TextView tvFirst = (TextView) popupView.findViewById(R.id.tvKeyboardFirstChoice);
//                            tvFirst.setText(getString(R.string.keyboard_abc));
//                            TextView tvSecond = (TextView) popupView.findViewById(R.id.tvKeyboardSecondChoice);
//                            tvSecond.setText(getString(R.string.keyboard_cyrillic));
//                            TextView tvThird = (TextView) popupView.findViewById(R.id.tvKeyboardThirdChoice);
//                            tvThird.setText(getString(R.string.keyboard_aeiou_short));
//
//                            fl1 = (FrameLayout) popupView.findViewById(R.id.flKeyboardFirstChoice);
//                            fl2 = (FrameLayout) popupView.findViewById(R.id.flKeyboardSecondChoice);
//                            fl3 = (FrameLayout) popupView.findViewById(R.id.flKeyboardThirdChoice);
//
//                            fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
//                            fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//
//
//                            popupWindow = new PopupWindow(popupView,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            int location[] = new int[2];
//                            v.getLocationOnScreen(location);
//                            popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                            popupWidth = popupView.getMeasuredWidth();
//                            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popupView.getMeasuredHeight());
//
//                            showingPopup = true;
//
//                        }
//                    };
//
//                    handler.postDelayed(runnableCode, LONGPRESS_THRESHOLD);
//
//
//
//                    break;
//                case MotionEvent.ACTION_MOVE:
//
//                    if (!showingPopup) {
//                        break;
//                    }
//
//                    float x = event.getX();
//                    float unit = popupWidth / 3;
//
//                    // select FVS1-3 and set highlight background color
//                    if (x < 0 || x > 3 * unit) {
//                        if (currentSelection != KeyboardType.Unselected) {
//                            fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            currentSelection = KeyboardType.Unselected;
//                        }
//                    } else if (x >= 0 && x <= unit) {
//                        if (currentSelection != KeyboardType.English) {
//                            fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
//                            fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            currentSelection = KeyboardType.English;
//                        }
//                    } else if (x > unit && x <= 2 * unit) {
//                        if (currentSelection != KeyboardType.Cyrillic) {
//                            fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
//                            fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            currentSelection = KeyboardType.Cyrillic;
//                        }
//                    } else if (x > 2 * unit && x <= 3 * unit) {
//                        if (currentSelection != KeyboardType.Aeiou) {
//                            fl1.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl2.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
//                            fl3.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, null));
//                            currentSelection = KeyboardType.Aeiou;
//                        }
//                    }
//
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.i("TAG", "touched up");
//
//                    handler.removeCallbacksAndMessages(null);
//
//                    if (showingPopup) {
//                        // hide popup
//                        if (popupWindow != null) {
//                            popupWindow.dismiss();
//                            showingPopup = false;
//                            if (currentSelection != KeyboardType.Unselected) {
//                                mListener.keyNewKeyboardChosen(currentSelection);
//                            }
//                        }
//                    } else {
//                        switchPunctuation();
//                    }
//
//                    break;
//            }
//
//            return true;
//        }
//    };

    @Override
    public void clearFvsKeys() {
        tvFvs1Top.setText("");
        tvFvs1Bottom.setText("");
        tvFvs2Top.setText("");
        tvFvs2Bottom.setText("");
        tvFvs3Top.setText("");
        tvFvs3Bottom.setText("");
    }

    private void updateFvsKeys(char currentChar) {

        clearFvsKeys();

        // get last char before cursor
        char previousChar = mListener.getCharBeforeCursor();

        // Only do Mongolian characters
        if (!MongolUnicodeRenderer.isMongolianAlphabet(currentChar)) {
            return;
        }

        // According to position do lookup
        // Update keys
        if (MongolUnicodeRenderer.isMongolian(previousChar)) { // medial or final
            tvFvs1Top.setText(renderer.getMedial("" + currentChar + FVS1));
            tvFvs2Top.setText(renderer.getMedial("" + currentChar + FVS2));
            tvFvs3Top.setText(renderer.getMedial("" + currentChar + FVS3));
            tvFvs1Bottom.setText(renderer.getFinal("" + currentChar + FVS1));
            tvFvs2Bottom.setText(renderer.getFinal("" + currentChar + FVS2));
            tvFvs3Bottom.setText(renderer.getFinal("" + currentChar + FVS3));
        } else {
            tvFvs1Top.setText(renderer.getInitial("" + currentChar + FVS1));
            tvFvs2Top.setText(renderer.getInitial("" + currentChar + FVS2));
            tvFvs3Top.setText(renderer.getInitial("" + currentChar + FVS3));
            tvFvs1Bottom.setText(renderer.getIsolate("" + currentChar + FVS1));
            tvFvs2Bottom.setText(renderer.getIsolate("" + currentChar + FVS2));
            tvFvs3Bottom.setText(renderer.getIsolate("" + currentChar + FVS3));
        }

    }

    public void switchKeys(KeyMode mode) {

        // swap between Mongol and Punctuation
        if (mode == currentKeyMode) {
            if (mode == KeyMode.Mongol) {
                currentKeyMode = KeyMode.Punctuation;
            } else {
                currentKeyMode = KeyMode.Mongol;
            }
        } else {
            currentKeyMode = mode;
        }

        if (currentKeyMode == KeyMode.Mongol) {

            tvQ.setText(getString(R.string.m_cha));
            tvW.setText(getString(R.string.m_wa));
            tvE.setText(getString(R.string.m_e));
            tvR.setText(getString(R.string.m_ra));
            tvT.setText(getString(R.string.m_ta));
            tvY.setText(getString(R.string.m_ya));
            tvU.setText(getString(R.string.m_ue));
            tvI.setText(getString(R.string.m_i));
            tvO.setText(getString(R.string.m_oe));
            tvP.setText(getString(R.string.m_pa));
            tvA.setText(getString(R.string.m_a));
            tvS.setText(getString(R.string.m_sa));
            tvD.setText(getString(R.string.m_da));
            tvF.setText(getString(R.string.m_fa));
            tvG.setText(getString(R.string.m_ga));
            tvH.setText(getString(R.string.m_qa));
            tvJ.setText(getString(R.string.m_ja));
            tvK.setText(getString(R.string.m_ka));
            tvL.setText(getString(R.string.m_la));
            tvNg.setText(getString(R.string.m_ang));
            tvZ.setText(getString(R.string.m_za));
            tvX.setText(getString(R.string.m_sha));
            tvC.setText(getString(R.string.m_o));
            tvV.setText(getString(R.string.m_u));
            tvB.setText(getString(R.string.m_ba));
            tvN.setText(getString(R.string.m_na));
            tvM.setText(getString(R.string.m_ma));

            tvQlong.setText(getString(R.string.m_chi));
            tvWlong.setText("");
            tvElong.setText(getString(R.string.m_ee));
            tvRlong.setText(getString(R.string.m_zra));
            tvTlong.setText("");
            tvYlong.setText("");
            tvUlong.setText("");
            tvIlong.setText("");
            tvOlong.setText("");
            tvPlong.setText("");
            tvHlong.setText(getString(R.string.m_haa));
            tvJlong.setText(getString(R.string.m_zhi));
            tvLlong.setText(getString(R.string.m_lha));
            tvZlong.setText(getString(R.string.m_tsa));

        } else if (currentKeyMode == KeyMode.Punctuation) {

            tvQ.setText(getString(R.string.m_key_p_1));
            tvW.setText(getString(R.string.m_key_p_2));
            tvE.setText(getString(R.string.m_key_p_3));
            tvR.setText(getString(R.string.m_key_p_4));
            tvT.setText(getString(R.string.m_key_p_5));
            tvY.setText(getString(R.string.m_key_p_6));
            tvU.setText(getString(R.string.m_key_p_7));
            tvI.setText(getString(R.string.m_key_p_8));
            tvO.setText(getString(R.string.m_key_p_9));
            tvP.setText(getString(R.string.m_key_p_0));
            tvA.setText(getString(R.string.m_key_p_top_paranthesis));
            tvS.setText(getString(R.string.m_key_p_bottom_paranthesis));
            tvD.setText(getString(R.string.m_key_p_top_single_quote));
            tvF.setText(getString(R.string.m_key_p_bottom_single_quote));
            tvG.setText(getString(R.string.m_key_p_top_double_quote));
            tvH.setText(getString(R.string.m_key_p_bottom_double_quote));
            tvJ.setText(getString(R.string.m_key_p_question_exclamation));
            tvK.setText(getString(R.string.m_key_p_exclamation_question));
            tvL.setText(getString(R.string.m_key_p_exclamation_exclamation));
            tvNg.setText(getString(R.string.m_key_p_colon));
            tvZ.setText(getString(R.string.m_key_p_ellipsis));
            tvX.setText(getString(R.string.m_key_p_four_dots));
            tvC.setText(getString(R.string.m_key_p_dot));
            tvV.setText(getString(R.string.m_key_p_full_stop));
            tvB.setText(getString(R.string.m_key_niguru));
            tvN.setText(getString(R.string.m_key_p_dash));
            tvM.setText(getString(R.string.m_key_p_semicolon));


            tvQlong.setText(getString(R.string.m_key_p_mongol_1));
            tvWlong.setText(getString(R.string.m_key_p_mongol_2));
            tvElong.setText(getString(R.string.m_key_p_mongol_3));
            tvRlong.setText(getString(R.string.m_key_p_mongol_4));
            tvTlong.setText(getString(R.string.m_key_p_mongol_5));
            tvYlong.setText(getString(R.string.m_key_p_mongol_6));
            tvUlong.setText(getString(R.string.m_key_p_mongol_7));
            tvIlong.setText(getString(R.string.m_key_p_mongol_8));
            tvOlong.setText(getString(R.string.m_key_p_mongol_9));
            tvPlong.setText(getString(R.string.m_key_p_mongol_0));
            tvHlong.setText("");
            tvJlong.setText("");
            tvLlong.setText("");
            tvZlong.setText("");

            tvFvs1Top.setText("");
            tvFvs1Bottom.setText("");
            tvFvs2Top.setText("");
            tvFvs2Bottom.setText("");
            tvFvs3Top.setText("");
            tvFvs3Bottom.setText("");

        }

    }

}
