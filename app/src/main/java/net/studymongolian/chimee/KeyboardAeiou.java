package net.studymongolian.chimee;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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
    Boolean punctuationOn = false;

    //protected static final char NULL_CHAR = '\u0000';
    //protected static final char BACKSPACE = '\u232b';
    //protected static final char SPACE = ' ';


    TextView tvA;
    TextView tvE;
    TextView tvI;
    TextView tvV;
    TextView tvU;
    TextView tvN;
    TextView tvB;
    TextView tvH;
    TextView tvG;
    TextView tvM;
    TextView tvL;
    TextView tvS;
    TextView tvD;
    TextView tvQ;
    TextView tvJ;
    TextView tvY;
    TextView tvR;
    TextView tvW;
    TextView tvZ;

    TextView tvAlong;
    TextView tvElong;
    TextView tvIlong;
    TextView tvVlong;
    TextView tvUlong;
    TextView tvNlong;
    TextView tvBlong;
    TextView tvHlong;
    TextView tvGlong;
    TextView tvMlong;
    TextView tvLlong;
    TextView tvSlong;
    TextView tvDlong;
    TextView tvQlong;
    TextView tvJlong;
    TextView tvYlong;
    TextView tvRlong;
    TextView tvWlong;
    TextView tvZlong;

    TextView tvNamalaga;
    TextView tvCaseSuffix;
    TextView tvFvs1Top;
    TextView tvFvs1Bottom;
    TextView tvFvs2Top;
    TextView tvFvs2Bottom;
    TextView tvFvs3Top;
    TextView tvFvs3Bottom;
    TextView tvInput;
    TextView tvInputLong;
    TextView tvInputMongol;
    TextView tvInputMongolLong;

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
        //idToShortPunctuation.put(R.id.key_namalaga, MongolUnicodeRenderer.Uni.MVS);
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
        v.findViewById(R.id.key_namalaga).setOnClickListener(handleMvsClick);
        v.findViewById(R.id.key_case_suffix).setOnClickListener(handleCaseSuffixClick);
        v.findViewById(R.id.key_backspace).setOnTouchListener(handleBackspace);
        v.findViewById(R.id.key_input).setOnTouchListener(handleInputTouch);
        v.findViewById(R.id.key_space).setOnTouchListener(handleSpace);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvA = (TextView) getView().findViewById(R.id.tvMkeyA);
        tvE = (TextView) getView().findViewById(R.id.tvMkeyE);
        tvI = (TextView) getView().findViewById(R.id.tvMkeyI);
        tvV = (TextView) getView().findViewById(R.id.tvMkeyV);
        tvU = (TextView) getView().findViewById(R.id.tvMkeyU);
        tvN = (TextView) getView().findViewById(R.id.tvMkeyN);
        tvB = (TextView) getView().findViewById(R.id.tvMkeyB);
        tvH = (TextView) getView().findViewById(R.id.tvMkeyH);
        tvG = (TextView) getView().findViewById(R.id.tvMkeyG);
        tvM = (TextView) getView().findViewById(R.id.tvMkeyM);
        tvL = (TextView) getView().findViewById(R.id.tvMkeyL);
        tvS = (TextView) getView().findViewById(R.id.tvMkeyS);
        tvD = (TextView) getView().findViewById(R.id.tvMkeyD);
        tvQ = (TextView) getView().findViewById(R.id.tvMkeyQ);
        tvJ = (TextView) getView().findViewById(R.id.tvMkeyJ);
        tvY = (TextView) getView().findViewById(R.id.tvMkeyY);
        tvR = (TextView) getView().findViewById(R.id.tvMkeyR);
        tvW = (TextView) getView().findViewById(R.id.tvMkeyW);
        tvZ = (TextView) getView().findViewById(R.id.tvMkeyZ);

        tvAlong = (TextView) getView().findViewById(R.id.tvMkeyAlong);
        tvElong = (TextView) getView().findViewById(R.id.tvMkeyElong);
        tvIlong = (TextView) getView().findViewById(R.id.tvMkeyIlong);
        tvVlong = (TextView) getView().findViewById(R.id.tvMkeyVlong);
        tvUlong = (TextView) getView().findViewById(R.id.tvMkeyUlong);
        tvNlong = (TextView) getView().findViewById(R.id.tvMkeyNlong);
        tvBlong = (TextView) getView().findViewById(R.id.tvMkeyBlong);
        tvHlong = (TextView) getView().findViewById(R.id.tvMkeyHlong);
        tvGlong = (TextView) getView().findViewById(R.id.tvMkeyGlong);
        tvMlong = (TextView) getView().findViewById(R.id.tvMkeyMlong);
        tvLlong = (TextView) getView().findViewById(R.id.tvMkeyLlong);
        tvSlong = (TextView) getView().findViewById(R.id.tvMkeySlong);
        tvDlong = (TextView) getView().findViewById(R.id.tvMkeyDlong);
        tvQlong = (TextView) getView().findViewById(R.id.tvMkeyQlong);
        tvJlong = (TextView) getView().findViewById(R.id.tvMkeyJlong);
        tvYlong = (TextView) getView().findViewById(R.id.tvMkeyYlong);
        tvRlong = (TextView) getView().findViewById(R.id.tvMkeyRlong);
        tvWlong = (TextView) getView().findViewById(R.id.tvMkeyWlong);
        tvZlong = (TextView) getView().findViewById(R.id.tvMkeyZlong);

        tvNamalaga = (TextView) getView().findViewById(R.id.tvMkeyNamalaga);
        tvCaseSuffix = (TextView) getView().findViewById(R.id.tvMkeyCaseSuffix);
        tvFvs1Top = (TextView) getView().findViewById(R.id.tvFvs1Top);
        tvFvs1Bottom = (TextView) getView().findViewById(R.id.tvFvs1Bottom);
        tvFvs2Top = (TextView) getView().findViewById(R.id.tvFvs2Top);
        tvFvs2Bottom = (TextView) getView().findViewById(R.id.tvFvs2Bottom);
        tvFvs3Top = (TextView) getView().findViewById(R.id.tvFvs3Top);
        tvFvs3Bottom = (TextView) getView().findViewById(R.id.tvFvs3Bottom);
        tvInput = (TextView) getView().findViewById(R.id.tvMkeyInput);
        tvInputMongol = (TextView) getView().findViewById(R.id.tvMkeyInputMongol);
        tvInputLong = (TextView) getView().findViewById(R.id.tvMkeyInputLong);
        tvInputMongolLong = (TextView) getView().findViewById(R.id.tvMkeyInputMongolLong);

    }

    @Override
    public void onClick(View v) {

        char inputChar;

        if (punctuationOn) {
            inputChar = idToShortPunctuation.get(v.getId());
        } else {
            inputChar = idToShort.get(v.getId());
            updateFvsKeys(inputChar);
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
            updateFvsKeys(inputChar);
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

    private View.OnClickListener handleMvsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            clearFvsKeys();
            if (punctuationOn) return;

            mListener.keyMvs();
        }
    };

    private View.OnClickListener handleCaseSuffixClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearFvsKeys();
            if (punctuationOn) return;
            mListener.keySuffix();
        }
    };

    private View.OnTouchListener handleInputTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clearFvsKeys();
                    Log.i("TAG", "touched down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    switchPunctuation();
                    break;
            }





            // TODO if touch is shorter than threshold then switch punctuation mode

            //switchPunctuation();
            //punctuationOn = !punctuationOn;

            // TODO if longer then show keyboard chooser

            return true;
        }
    };

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

    public void switchPunctuation() {
        if (punctuationOn) {

            tvA.setText(getResources().getString(R.string.m_a));
            tvE.setText(getResources().getString(R.string.m_e));
            tvI.setText(getResources().getString(R.string.m_i));
            tvV.setText(getResources().getString(R.string.m_u));
            tvU.setText(getResources().getString(R.string.m_ue));
            tvN.setText(getResources().getString(R.string.m_na));
            tvB.setText(getResources().getString(R.string.m_ba));
            tvH.setText(getResources().getString(R.string.m_qa));
            tvG.setText(getResources().getString(R.string.m_ga));
            tvM.setText(getResources().getString(R.string.m_ma));
            tvL.setText(getResources().getString(R.string.m_la));
            tvS.setText(getResources().getString(R.string.m_sa));
            //tvT.setText(getResources().getString(R.string.m_ta));
            tvD.setText(getResources().getString(R.string.m_da));
            tvQ.setText(getResources().getString(R.string.m_cha));
            tvJ.setText(getResources().getString(R.string.m_ja));
            tvY.setText(getResources().getString(R.string.m_ya));
            tvR.setText(getResources().getString(R.string.m_ra));
            tvW.setText(getResources().getString(R.string.m_wa));
            tvZ.setText(getResources().getString(R.string.m_za));

            tvAlong.setText(getResources().getString(R.string.m_key_niguru));
            tvElong.setText(getResources().getString(R.string.m_ee));
            tvIlong.setText("");
            tvVlong.setText(getResources().getString(R.string.m_o));
            tvUlong.setText(getResources().getString(R.string.m_oe));
            tvNlong.setText(getResources().getString(R.string.m_ang));
            tvBlong.setText(getResources().getString(R.string.m_pa));
            tvHlong.setText(getResources().getString(R.string.m_haa));
            tvGlong.setText(getResources().getString(R.string.m_ka));
            tvMlong.setText("");
            tvLlong.setText(getResources().getString(R.string.m_lha));
            tvSlong.setText(getResources().getString(R.string.m_sha));
            tvDlong.setText(getResources().getString(R.string.m_ta));
            tvQlong.setText(getResources().getString(R.string.m_chi));
            tvJlong.setText(getResources().getString(R.string.m_zhi));
            tvYlong.setText("");
            tvRlong.setText(getResources().getString(R.string.m_zra));
            tvWlong.setText(getResources().getString(R.string.m_fa));
            tvZlong.setText(getResources().getString(R.string.m_tsa));

            //tvCaseSuffix.setText(getResources().getString(R.string.m_key_case_suffix));

            tvInput.setVisibility(View.VISIBLE);
            tvInputMongol.setVisibility(View.GONE);
            tvInputLong.setVisibility(View.VISIBLE);
            tvInputMongolLong.setVisibility(View.GONE);

        } else { // punctuation is not on. Turn it on now.

            tvA.setText(getResources().getString(R.string.m_key_p_top_paranthesis));
            tvE.setText(getResources().getString(R.string.m_key_p_bottom_paranthesis));
            tvI.setText(getResources().getString(R.string.m_key_p_top_double_quote));
            tvV.setText(getResources().getString(R.string.m_key_p_bottom_double_quote));
            tvU.setText(getResources().getString(R.string.m_key_p_dot));
            tvN.setText(getResources().getString(R.string.m_key_p_1));
            tvB.setText(getResources().getString(R.string.m_key_p_2));
            tvH.setText(getResources().getString(R.string.m_key_p_3));
            tvG.setText(getResources().getString(R.string.m_key_p_4));
            tvM.setText(getResources().getString(R.string.m_key_p_5));
            tvL.setText(getResources().getString(R.string.m_key_p_dash));
            tvS.setText(getResources().getString(R.string.m_key_p_6));
            tvD.setText(getResources().getString(R.string.m_key_p_7));
            tvQ.setText(getResources().getString(R.string.m_key_p_8));
            tvJ.setText(getResources().getString(R.string.m_key_p_9));
            tvY.setText(getResources().getString(R.string.m_key_p_0));
            tvR.setText(getResources().getString(R.string.m_key_p_full_stop));
            tvW.setText(getResources().getString(R.string.m_key_p_question_exclamation));
            tvZ.setText(getResources().getString(R.string.m_key_p_exclamation_exclamation));

            tvAlong.setText(getResources().getString(R.string.m_key_p_top_square_bracket));
            tvElong.setText(getResources().getString(R.string.m_key_p_bottom_square_bracket));
            tvIlong.setText(getResources().getString(R.string.m_key_p_top_single_quote));
            tvVlong.setText(getResources().getString(R.string.m_key_p_bottom_single_quote));
            tvUlong.setText(getResources().getString(R.string.m_key_p_ellipsis));
            tvNlong.setText(getResources().getString(R.string.m_key_p_mongol_1));
            tvBlong.setText(getResources().getString(R.string.m_key_p_mongol_2));
            tvHlong.setText(getResources().getString(R.string.m_key_p_mongol_3));
            tvGlong.setText(getResources().getString(R.string.m_key_p_mongol_4));
            tvMlong.setText(getResources().getString(R.string.m_key_p_mongol_5));
            tvLlong.setText(getResources().getString(R.string.m_key_p_birga));
            tvSlong.setText(getResources().getString(R.string.m_key_p_mongol_6));
            tvDlong.setText(getResources().getString(R.string.m_key_p_mongol_7));
            tvQlong.setText(getResources().getString(R.string.m_key_p_mongol_8));
            tvJlong.setText(getResources().getString(R.string.m_key_p_mongol_9));
            tvYlong.setText(getResources().getString(R.string.m_key_p_mongol_0));
            tvRlong.setText(getResources().getString(R.string.m_key_p_four_dots));
            tvWlong.setText(getResources().getString(R.string.m_key_p_colon));
            tvZlong.setText(getResources().getString(R.string.m_key_p_semicolon));

            tvFvs1Top.setText("");
            tvFvs1Bottom.setText("");
            tvFvs2Top.setText("");
            tvFvs2Bottom.setText("");
            tvFvs3Top.setText("");
            tvFvs3Bottom.setText("");

            tvInput.setVisibility(View.GONE);
            tvInputMongol.setVisibility(View.VISIBLE);
            tvInputLong.setVisibility(View.GONE);
            tvInputMongolLong.setVisibility(View.VISIBLE);

        }

        punctuationOn = !punctuationOn;

    }
}
