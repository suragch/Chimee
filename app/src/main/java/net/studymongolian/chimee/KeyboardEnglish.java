package net.studymongolian.chimee;


import android.content.SharedPreferences;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KeyboardEnglish extends Keyboard {

    private static Map<Integer, Character> idToLowercase = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToUppercase = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToPunctuation = new HashMap<Integer, Character>();

    KeyMode currentKeyMode = KeyMode.Lowercase;
    //Boolean punctuationOn = false;

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
    TextView tvZ;
    TextView tvX;
    TextView tvC;
    TextView tvV;
    TextView tvB;
    TextView tvN;
    TextView tvM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // map the keys to their characters
        initMap();

        // inflate layout and add listeners to each key
        View layout = inflater.inflate(R.layout.fragment_keyboard_english, container, false);
        addListeners(layout);

        return layout;
    }

    @Override
    public void initMap() {

        // unicode characters for lowercase key taps
        idToLowercase.put(R.id.key_q, 'q');
        idToLowercase.put(R.id.key_w, 'w');
        idToLowercase.put(R.id.key_e, 'e');
        idToLowercase.put(R.id.key_r, 'r');
        idToLowercase.put(R.id.key_t, 't');
        idToLowercase.put(R.id.key_y, 'y');
        idToLowercase.put(R.id.key_u, 'u');
        idToLowercase.put(R.id.key_i, 'i');
        idToLowercase.put(R.id.key_o, 'o');
        idToLowercase.put(R.id.key_p, 'p');
        idToLowercase.put(R.id.key_a, 'a');
        idToLowercase.put(R.id.key_s, 's');
        idToLowercase.put(R.id.key_d, 'd');
        idToLowercase.put(R.id.key_f, 'f');
        idToLowercase.put(R.id.key_g, 'g');
        idToLowercase.put(R.id.key_h, 'h');
        idToLowercase.put(R.id.key_j, 'j');
        idToLowercase.put(R.id.key_k, 'k');
        idToLowercase.put(R.id.key_l, 'l');
        idToLowercase.put(R.id.key_z, 'z');
        idToLowercase.put(R.id.key_x, 'x');
        idToLowercase.put(R.id.key_c, 'c');
        idToLowercase.put(R.id.key_v, 'v');
        idToLowercase.put(R.id.key_b, 'b');
        idToLowercase.put(R.id.key_n, 'n');
        idToLowercase.put(R.id.key_m, 'm');
        idToLowercase.put(R.id.key_comma, ',');
        idToLowercase.put(R.id.key_question, '.');
        idToLowercase.put(R.id.key_return, NEW_LINE);

        // unicode characters for uppercase key taps
        idToUppercase.put(R.id.key_q, 'Q');
        idToUppercase.put(R.id.key_w, 'W');
        idToUppercase.put(R.id.key_e, 'E');
        idToUppercase.put(R.id.key_r, 'R');
        idToUppercase.put(R.id.key_t, 'T');
        idToUppercase.put(R.id.key_y, 'Y');
        idToUppercase.put(R.id.key_u, 'U');
        idToUppercase.put(R.id.key_i, 'I');
        idToUppercase.put(R.id.key_o, 'O');
        idToUppercase.put(R.id.key_p, 'P');
        idToUppercase.put(R.id.key_a, 'A');
        idToUppercase.put(R.id.key_s, 'S');
        idToUppercase.put(R.id.key_d, 'D');
        idToUppercase.put(R.id.key_f, 'F');
        idToUppercase.put(R.id.key_g, 'G');
        idToUppercase.put(R.id.key_h, 'H');
        idToUppercase.put(R.id.key_j, 'J');
        idToUppercase.put(R.id.key_k, 'K');
        idToUppercase.put(R.id.key_l, 'L');
        idToUppercase.put(R.id.key_z, 'Z');
        idToUppercase.put(R.id.key_x, 'X');
        idToUppercase.put(R.id.key_c, 'C');
        idToUppercase.put(R.id.key_v, 'V');
        idToUppercase.put(R.id.key_b, 'B');
        idToUppercase.put(R.id.key_n, 'N');
        idToUppercase.put(R.id.key_m, 'M');
        idToUppercase.put(R.id.key_comma, ',');
        idToUppercase.put(R.id.key_question, '.');
        idToUppercase.put(R.id.key_return, NEW_LINE);

        // unicode characters for punctuation key taps
        idToPunctuation.put(R.id.key_q, '1');
        idToPunctuation.put(R.id.key_w, '2');
        idToPunctuation.put(R.id.key_e, '3');
        idToPunctuation.put(R.id.key_r, '4');
        idToPunctuation.put(R.id.key_t, '5');
        idToPunctuation.put(R.id.key_y, '6');
        idToPunctuation.put(R.id.key_u, '7');
        idToPunctuation.put(R.id.key_i, '8');
        idToPunctuation.put(R.id.key_o, '9');
        idToPunctuation.put(R.id.key_p, '0');
        idToPunctuation.put(R.id.key_a, '(');
        idToPunctuation.put(R.id.key_s, ')');
        idToPunctuation.put(R.id.key_d, '[');
        idToPunctuation.put(R.id.key_f, ']');
        idToPunctuation.put(R.id.key_g, '{');
        idToPunctuation.put(R.id.key_h, '}');
        idToPunctuation.put(R.id.key_j, '+');
        idToPunctuation.put(R.id.key_k, '-');
        idToPunctuation.put(R.id.key_l, '%');
        idToPunctuation.put(R.id.key_z, ':');
        idToPunctuation.put(R.id.key_x, ';');
        idToPunctuation.put(R.id.key_c, '?');
        idToPunctuation.put(R.id.key_v, '!');
        idToPunctuation.put(R.id.key_b, '_');
        idToPunctuation.put(R.id.key_n, '\'');
        idToPunctuation.put(R.id.key_m, '"');
        idToPunctuation.put(R.id.key_comma, ',');
        idToPunctuation.put(R.id.key_question, '.');
        idToPunctuation.put(R.id.key_return, NEW_LINE);

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
        v.findViewById(R.id.key_comma).setOnLongClickListener(this);
        v.findViewById(R.id.key_question).setOnLongClickListener(this);


        // *** Special Keys ***

        v.findViewById(R.id.key_shift).setOnClickListener(handleShift);
        v.findViewById(R.id.key_backspace).setOnTouchListener(handleBackspace);
        v.findViewById(R.id.key_space).setOnTouchListener(handleSpace);

        // input key
        ArrayList<KeyboardType> displayOrder = new ArrayList<>();
        displayOrder.add(KeyboardType.Aeiou);
        displayOrder.add(KeyboardType.Cyrillic);
        displayOrder.add(KeyboardType.Qwerty);
        super.setOnTouchListenerForKeybordSwitcherView(v.findViewById(R.id.key_input), displayOrder);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvQ = (TextView) getView().findViewById(R.id.tvEkeyQ);
        tvW = (TextView) getView().findViewById(R.id.tvEkeyW);
        tvE = (TextView) getView().findViewById(R.id.tvEkeyE);
        tvR = (TextView) getView().findViewById(R.id.tvEkeyR);
        tvT = (TextView) getView().findViewById(R.id.tvEkeyT);
        tvY = (TextView) getView().findViewById(R.id.tvEkeyY);
        tvU = (TextView) getView().findViewById(R.id.tvEkeyU);
        tvI = (TextView) getView().findViewById(R.id.tvEkeyI);
        tvO = (TextView) getView().findViewById(R.id.tvEkeyO);
        tvP = (TextView) getView().findViewById(R.id.tvEkeyP);
        tvA = (TextView) getView().findViewById(R.id.tvEkeyA);
        tvS = (TextView) getView().findViewById(R.id.tvEkeyS);
        tvD = (TextView) getView().findViewById(R.id.tvEkeyD);
        tvF = (TextView) getView().findViewById(R.id.tvEkeyF);
        tvG = (TextView) getView().findViewById(R.id.tvEkeyG);
        tvH = (TextView) getView().findViewById(R.id.tvEkeyH);
        tvJ = (TextView) getView().findViewById(R.id.tvEkeyJ);
        tvK = (TextView) getView().findViewById(R.id.tvEkeyK);
        tvL = (TextView) getView().findViewById(R.id.tvEkeyL);
        tvZ = (TextView) getView().findViewById(R.id.tvEkeyZ);
        tvX = (TextView) getView().findViewById(R.id.tvEkeyX);
        tvC = (TextView) getView().findViewById(R.id.tvEkeyC);
        tvV = (TextView) getView().findViewById(R.id.tvEkeyV);
        tvB = (TextView) getView().findViewById(R.id.tvEkeyB);
        tvN = (TextView) getView().findViewById(R.id.tvEkeyN);
        tvM = (TextView) getView().findViewById(R.id.tvEkeyM);

    }

    @Override
    public void onClick(View v) {

        // get input char based on wether keys are
        // in uppercase, lowercase, or punctuation mode
        if (currentKeyMode == KeyMode.Lowercase) {
            mListener.keyWasTapped(idToLowercase.get(v.getId()));
        } else if (currentKeyMode == KeyMode.Uppercase) {
            mListener.keyWasTapped(idToUppercase.get(v.getId()));
            switchKeys(KeyMode.Lowercase);
        } else { // punctuation
            mListener.keyWasTapped(idToPunctuation.get(v.getId()));
        }
    }

    @Override
    public boolean onLongClick(View v) {

        if (v.getId()==R.id.key_comma) {
            mListener.keyWasTapped('!');
        } else if (v.getId()==R.id.key_question) {
            mListener.keyWasTapped('?');
        }
        return true;
    }

    private View.OnClickListener handleShift = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentKeyMode == KeyMode.Lowercase) {
                switchKeys(KeyMode.Uppercase);
            } else if (currentKeyMode == KeyMode.Uppercase) {
                switchKeys(KeyMode.Lowercase);
            }
        }
    };

    public void switchKeys(KeyMode mode) {

        // swap between Uppercase, Lowercase, and Punctuation
        if (mode == currentKeyMode) {
            if (mode == KeyMode.Punctuation) {
                currentKeyMode = KeyMode.Lowercase;
            } else { // upper or lower case
                currentKeyMode = KeyMode.Punctuation;
            }
        } else {
            currentKeyMode = mode;
        }

        if (currentKeyMode == KeyMode.Lowercase) {

            tvQ.setText(getResources().getString(R.string.e_key_q));
            tvW.setText(getResources().getString(R.string.e_key_w));
            tvE.setText(getResources().getString(R.string.e_key_e));
            tvR.setText(getResources().getString(R.string.e_key_r));
            tvT.setText(getResources().getString(R.string.e_key_t));
            tvY.setText(getResources().getString(R.string.e_key_y));
            tvU.setText(getResources().getString(R.string.e_key_u));
            tvI.setText(getResources().getString(R.string.e_key_i));
            tvO.setText(getResources().getString(R.string.e_key_o));
            tvP.setText(getResources().getString(R.string.e_key_p));
            tvA.setText(getResources().getString(R.string.e_key_a));
            tvS.setText(getResources().getString(R.string.e_key_s));
            tvD.setText(getResources().getString(R.string.e_key_d));
            tvF.setText(getResources().getString(R.string.e_key_f));
            tvG.setText(getResources().getString(R.string.e_key_g));
            tvH.setText(getResources().getString(R.string.e_key_h));
            tvJ.setText(getResources().getString(R.string.e_key_j));
            tvK.setText(getResources().getString(R.string.e_key_k));
            tvL.setText(getResources().getString(R.string.e_key_l));
            tvZ.setText(getResources().getString(R.string.e_key_z));
            tvX.setText(getResources().getString(R.string.e_key_x));
            tvC.setText(getResources().getString(R.string.e_key_c));
            tvV.setText(getResources().getString(R.string.e_key_v));
            tvB.setText(getResources().getString(R.string.e_key_b));
            tvN.setText(getResources().getString(R.string.e_key_n));
            tvM.setText(getResources().getString(R.string.e_key_m));

        } else if (currentKeyMode == KeyMode.Uppercase) {

            tvQ.setText(getResources().getString(R.string.e_key_q_caps));
            tvW.setText(getResources().getString(R.string.e_key_w_caps));
            tvE.setText(getResources().getString(R.string.e_key_e_caps));
            tvR.setText(getResources().getString(R.string.e_key_r_caps));
            tvT.setText(getResources().getString(R.string.e_key_t_caps));
            tvY.setText(getResources().getString(R.string.e_key_y_caps));
            tvU.setText(getResources().getString(R.string.e_key_u_caps));
            tvI.setText(getResources().getString(R.string.e_key_i_caps));
            tvO.setText(getResources().getString(R.string.e_key_o_caps));
            tvP.setText(getResources().getString(R.string.e_key_p_caps));
            tvA.setText(getResources().getString(R.string.e_key_a_caps));
            tvS.setText(getResources().getString(R.string.e_key_s_caps));
            tvD.setText(getResources().getString(R.string.e_key_d_caps));
            tvF.setText(getResources().getString(R.string.e_key_f_caps));
            tvG.setText(getResources().getString(R.string.e_key_g_caps));
            tvH.setText(getResources().getString(R.string.e_key_h_caps));
            tvJ.setText(getResources().getString(R.string.e_key_j_caps));
            tvK.setText(getResources().getString(R.string.e_key_k_caps));
            tvL.setText(getResources().getString(R.string.e_key_l_caps));
            tvZ.setText(getResources().getString(R.string.e_key_z_caps));
            tvX.setText(getResources().getString(R.string.e_key_x_caps));
            tvC.setText(getResources().getString(R.string.e_key_c_caps));
            tvV.setText(getResources().getString(R.string.e_key_v_caps));
            tvB.setText(getResources().getString(R.string.e_key_b_caps));
            tvN.setText(getResources().getString(R.string.e_key_n_caps));
            tvM.setText(getResources().getString(R.string.e_key_m_caps));

        } else if (currentKeyMode == KeyMode.Punctuation) {

            tvQ.setText(getResources().getString(R.string.e_key_q_punctuation));
            tvW.setText(getResources().getString(R.string.e_key_w_punctuation));
            tvE.setText(getResources().getString(R.string.e_key_e_punctuation));
            tvR.setText(getResources().getString(R.string.e_key_r_punctuation));
            tvT.setText(getResources().getString(R.string.e_key_t_punctuation));
            tvY.setText(getResources().getString(R.string.e_key_y_punctuation));
            tvU.setText(getResources().getString(R.string.e_key_u_punctuation));
            tvI.setText(getResources().getString(R.string.e_key_i_punctuation));
            tvO.setText(getResources().getString(R.string.e_key_o_punctuation));
            tvP.setText(getResources().getString(R.string.e_key_p_punctuation));
            tvA.setText(getResources().getString(R.string.e_key_a_punctuation));
            tvS.setText(getResources().getString(R.string.e_key_s_punctuation));
            tvD.setText(getResources().getString(R.string.e_key_d_punctuation));
            tvF.setText(getResources().getString(R.string.e_key_f_punctuation));
            tvG.setText(getResources().getString(R.string.e_key_g_punctuation));
            tvH.setText(getResources().getString(R.string.e_key_h_punctuation));
            tvJ.setText(getResources().getString(R.string.e_key_j_punctuation));
            tvK.setText(getResources().getString(R.string.e_key_k_punctuation));
            tvL.setText(getResources().getString(R.string.e_key_l_punctuation));
            tvZ.setText(getResources().getString(R.string.e_key_z_punctuation));
            tvX.setText(getResources().getString(R.string.e_key_x_punctuation));
            tvC.setText(getResources().getString(R.string.e_key_c_punctuation));
            tvV.setText(getResources().getString(R.string.e_key_v_punctuation));
            tvB.setText(getResources().getString(R.string.e_key_b_punctuation));
            tvN.setText(getResources().getString(R.string.e_key_n_punctuation));
            tvM.setText(getResources().getString(R.string.e_key_m_punctuation));
        }
    }


}
