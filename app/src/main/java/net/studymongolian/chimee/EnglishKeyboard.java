package net.studymongolian.chimee;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EnglishKeyboard extends Fragment implements OnClickListener, OnLongClickListener {

	protected static final char BACKSPACE = '\u232b';
	protected static final char SPACE = ' ';
	protected static final char NEW_LINE = '\n';
	protected static final char SWITCH_TO_MONGOLIAN = 'β'; // arbitrary symbol

	// Input states
	protected static final int ENGLISH = 0;
	protected static final int CYRILLIC = 1;
	protected static final int PUNCTUATION = 2;
	int inputState = ENGLISH;

	private static Map<Integer, Character> idToEnglish = new HashMap<Integer, Character>();
	private static Map<Integer, Character> idToEnglishCaps = new HashMap<Integer, Character>();
	private static Map<Integer, Character> idToPunctuation = new HashMap<Integer, Character>();
	private static Map<Integer, Character> idToCyrillic = new HashMap<Integer, Character>();
	private static Map<Integer, Character> idToCyrillicCaps = new HashMap<Integer, Character>();


	OnKeyTouchListener mCallback;
	//Boolean punctuationOn = false;
	Boolean capsOn = false;
	ImageView ivShift;
	TextView tv1;
	TextView tv2;
	TextView tv3;
	TextView tv4;
	TextView tv5;
	TextView tv6;
	TextView tv7;
	TextView tv8;
	RelativeLayout rlKey1;
	RelativeLayout rlKey2;
	RelativeLayout rlKey3;
	RelativeLayout rlKey4;
	RelativeLayout rlKey5;
	RelativeLayout rlKey6;
	RelativeLayout rlKey7;
	RelativeLayout rlKey8;
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
	TextView tv41;
	TextView tv42;
	TextView tv44;


	// Container Activity must implement this interface
	public interface OnKeyTouchListener {
		public void onKeyTouched(char keyChar);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.english_keyboard, container, false);
		initMap();

        if (savedInstanceState != null) {
            inputState = savedInstanceState.getInt("keyboard");
        }

		// Add listeners for all keys
		rlKey1 = (RelativeLayout) v.findViewById(R.id.ekey_1);
		rlKey1.setOnClickListener(this);
		rlKey2 = (RelativeLayout) v.findViewById(R.id.ekey_2);
		rlKey2.setOnClickListener(this);
		rlKey3 = (RelativeLayout) v.findViewById(R.id.ekey_3);
		rlKey3.setOnClickListener(this);
		rlKey4 = (RelativeLayout) v.findViewById(R.id.ekey_4);
		rlKey4.setOnClickListener(this);
		rlKey5 = (RelativeLayout) v.findViewById(R.id.ekey_5);
		rlKey5.setOnClickListener(this);
		rlKey6 = (RelativeLayout) v.findViewById(R.id.ekey_6);
		rlKey6.setOnClickListener(this);
		rlKey7 = (RelativeLayout) v.findViewById(R.id.ekey_7);
		rlKey7.setOnClickListener(this);
		rlKey8 = (RelativeLayout) v.findViewById(R.id.ekey_8);
		rlKey8.setOnClickListener(this);
		RelativeLayout rlKeyQ = (RelativeLayout) v.findViewById(R.id.ekey_q);
		rlKeyQ.setOnClickListener(this);
		RelativeLayout rlKeyW = (RelativeLayout) v.findViewById(R.id.ekey_w);
		rlKeyW.setOnClickListener(this);
		RelativeLayout rlKeyE = (RelativeLayout) v.findViewById(R.id.ekey_e);
		rlKeyE.setOnClickListener(this);
		RelativeLayout rlKeyR = (RelativeLayout) v.findViewById(R.id.ekey_r);
		rlKeyR.setOnClickListener(this);
		RelativeLayout rlKeyT = (RelativeLayout) v.findViewById(R.id.ekey_t);
		rlKeyT.setOnClickListener(this);
		RelativeLayout rlKeyY = (RelativeLayout) v.findViewById(R.id.ekey_y);
		rlKeyY.setOnClickListener(this);
		RelativeLayout rlKeyU = (RelativeLayout) v.findViewById(R.id.ekey_u);
		rlKeyU.setOnClickListener(this);
		RelativeLayout rlKeyI = (RelativeLayout) v.findViewById(R.id.ekey_i);
		rlKeyI.setOnClickListener(this);
		RelativeLayout rlKeyO = (RelativeLayout) v.findViewById(R.id.ekey_o);
		rlKeyO.setOnClickListener(this);
		RelativeLayout rlKeyP = (RelativeLayout) v.findViewById(R.id.ekey_p);
		rlKeyP.setOnClickListener(this);
		RelativeLayout rlKeyA = (RelativeLayout) v.findViewById(R.id.ekey_a);
		rlKeyA.setOnClickListener(this);
		RelativeLayout rlKeyS = (RelativeLayout) v.findViewById(R.id.ekey_s);
		rlKeyS.setOnClickListener(this);
		RelativeLayout rlKeyD = (RelativeLayout) v.findViewById(R.id.ekey_d);
		rlKeyD.setOnClickListener(this);
		RelativeLayout rlKeyF = (RelativeLayout) v.findViewById(R.id.ekey_f);
		rlKeyF.setOnClickListener(this);
		RelativeLayout rlKeyG = (RelativeLayout) v.findViewById(R.id.ekey_g);
		rlKeyG.setOnClickListener(this);
		RelativeLayout rlKeyH = (RelativeLayout) v.findViewById(R.id.ekey_h);
		rlKeyH.setOnClickListener(this);
		RelativeLayout rlKeyJ = (RelativeLayout) v.findViewById(R.id.ekey_j);
		rlKeyJ.setOnClickListener(this);
		RelativeLayout rlKeyK = (RelativeLayout) v.findViewById(R.id.ekey_k);
		rlKeyK.setOnClickListener(this);
		RelativeLayout rlKeyL = (RelativeLayout) v.findViewById(R.id.ekey_l);
		rlKeyL.setOnClickListener(this);
		RelativeLayout rlKeyShift = (RelativeLayout) v.findViewById(R.id.ekey_shift);
		rlKeyShift.setOnClickListener(this);
		RelativeLayout rlKeyZ = (RelativeLayout) v.findViewById(R.id.ekey_z);
		rlKeyZ.setOnClickListener(this);
		RelativeLayout rlKeyX = (RelativeLayout) v.findViewById(R.id.ekey_x);
		rlKeyX.setOnClickListener(this);
		RelativeLayout rlKeyC = (RelativeLayout) v.findViewById(R.id.ekey_c);
		rlKeyC.setOnClickListener(this);
		RelativeLayout rlKeyV = (RelativeLayout) v.findViewById(R.id.ekey_v);
		rlKeyV.setOnClickListener(this);
		RelativeLayout rlKeyB = (RelativeLayout) v.findViewById(R.id.ekey_b);
		rlKeyB.setOnClickListener(this);
		RelativeLayout rlKeyN = (RelativeLayout) v.findViewById(R.id.ekey_n);
		rlKeyN.setOnClickListener(this);
		RelativeLayout rlKeyM = (RelativeLayout) v.findViewById(R.id.ekey_m);
		rlKeyM.setOnClickListener(this);
		RelativeLayout rlKeyBackspace = (RelativeLayout) v.findViewById(R.id.ekey_backspace);
		rlKeyBackspace.setOnTouchListener(handleTouch);
		RelativeLayout rlKey41 = (RelativeLayout) v.findViewById(R.id.ekey_41);
		rlKey41.setOnClickListener(this);
		rlKey41.setOnLongClickListener(this);
		RelativeLayout rlKey42 = (RelativeLayout) v.findViewById(R.id.ekey_42);
		rlKey42.setOnClickListener(this);
		rlKey42.setOnLongClickListener(this);
		RelativeLayout rlKeySpace = (RelativeLayout) v.findViewById(R.id.ekey_space);
		rlKeySpace.setOnTouchListener(handleTouch);
		RelativeLayout rlKey44 = (RelativeLayout) v.findViewById(R.id.ekey_44);
		rlKey44.setOnClickListener(this);
		rlKey44.setOnLongClickListener(this);
		RelativeLayout rlKey45 = (RelativeLayout) v.findViewById(R.id.ekey_45);
		rlKey45.setOnClickListener(this);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ivShift = (ImageView) getView().findViewById(R.id.ivEkeyShift);
		tv1 = (TextView) getView().findViewById(R.id.tvEkey1);
		tv2 = (TextView) getView().findViewById(R.id.tvEkey2);
		tv3 = (TextView) getView().findViewById(R.id.tvEkey3);
		tv4 = (TextView) getView().findViewById(R.id.tvEkey4);
		tv5 = (TextView) getView().findViewById(R.id.tvEkey5);
		tv6 = (TextView) getView().findViewById(R.id.tvEkey6);
		tv7 = (TextView) getView().findViewById(R.id.tvEkey7);
		tv8 = (TextView) getView().findViewById(R.id.tvEkey8);
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
		tv41 = (TextView) getView().findViewById(R.id.tvEkey41);
		tv42 = (TextView) getView().findViewById(R.id.tvEkey42);
		tv44 = (TextView) getView().findViewById(R.id.tvEkey44);

        capsOn = false;
		// TODO: remove this. it does nothing. Make the caps only stay one keypress.
        ivShift.setImageResource(R.drawable.ic_keyboard_capslock_white_24dp);
        if (inputState == ENGLISH) {
            cyrillicSetVisibility(View.GONE);
        } else if (inputState == CYRILLIC) {
            cyrillicSetVisibility(View.VISIBLE);
        } else { // PUNCTUATION
            cyrillicSetVisibility(View.GONE);
        }
        switchKeyLabels();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnKeyTouchListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnKeyTouchListener");
		}
	}

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save text in case of screen rotation
        savedInstanceState.putInt("keyboard", inputState);
        super.onSaveInstanceState(savedInstanceState);
    }

	@Override
	public void onClick(View view) {

		// Send the event to the host activity
		switch (view.getId()) {
		case R.id.ekey_shift:
			// SHIFT: Turn caps on/off
			if (inputState== PUNCTUATION) return;
			capsOn = !capsOn;
			if (capsOn){
				ivShift.setImageResource(R.drawable.ic_keyboard_capslock_white_24dp); // TODO
			}else{
				ivShift.setImageResource(R.drawable.ic_keyboard_capslock_white_24dp); // TODO
			}
			if (inputState == ENGLISH) {
				setEnglishKeyLabels();
			} else if (inputState == CYRILLIC) {
				setCyrillicKeyLabels();
			}
			return;
		case R.id.ekey_41:
			capsOn = false;
			ivShift.setImageResource(R.drawable.ic_keyboard_capslock_white_24dp); // TODO
			// switch between English, Cyrillic, and puctuation
			if (inputState == ENGLISH) {
				inputState = CYRILLIC;
				cyrillicSetVisibility(View.VISIBLE);
			} else if (inputState == CYRILLIC) {
				inputState = PUNCTUATION;
				cyrillicSetVisibility(View.GONE);
			} else { // PUNCTUATION
				inputState = ENGLISH;
				cyrillicSetVisibility(View.GONE);
			}
			switchKeyLabels();
			return;
		case R.id.ekey_42:
			mCallback.onKeyTouched(',');
			return;
		case R.id.ekey_44:
			mCallback.onKeyTouched('.');
			return;
		case R.id.ekey_45:
			mCallback.onKeyTouched(NEW_LINE);
			return;
		}

		if (inputState == ENGLISH) {

			if (capsOn) {
				mCallback.onKeyTouched(idToEnglishCaps.get(view.getId()));
			} else {
				mCallback.onKeyTouched(idToEnglish.get(view.getId()));
			}

		} else if (inputState == CYRILLIC) {

			if (capsOn) {
				mCallback.onKeyTouched(idToCyrillicCaps.get(view.getId()));
			} else {
				mCallback.onKeyTouched(idToCyrillic.get(view.getId()));
			}

		} else { // PUNCTUATION

			mCallback.onKeyTouched(idToPunctuation.get(view.getId()));
		}

	}

	private void cyrillicSetVisibility(int visible) {
		
		//visible = View.VISIBLE;
		
		rlKey1.setVisibility(visible);
		rlKey2.setVisibility(visible);
		rlKey3.setVisibility(visible);
		rlKey4.setVisibility(visible);
		rlKey5.setVisibility(visible);
		rlKey6.setVisibility(visible);
		rlKey7.setVisibility(visible);
		rlKey8.setVisibility(visible);

	}

	@Override
	public boolean onLongClick(View v) {

		// Send the event to the host activity
		switch (v.getId()) {
		case R.id.ekey_41:
			// Switch to Mongolian keyboard
			mCallback.onKeyTouched(SWITCH_TO_MONGOLIAN);
			return true;
		case R.id.ekey_42:
			mCallback.onKeyTouched('!');
			return true;
		case R.id.ekey_44:
			mCallback.onKeyTouched('?');
			return true;
		default:
			return false;
		}
	}
	
	// Handles continuous space and backspace presses
	private OnTouchListener handleTouch = new OnTouchListener() {

		private Handler handler;
		final int INITIAL_DELAY = 500;
		final int REPEAT_DELAY = 50;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (view.getId() == R.id.ekey_backspace) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
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
			} else if (view.getId() == R.id.ekey_space) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
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
			}

			return true;
		}
		
		private void doBackspace(){
			mCallback.onKeyTouched(BACKSPACE);
		}
		
		private void doSpace(){
			mCallback.onKeyTouched(SPACE);
		}
		

		Runnable actionBackspace = new Runnable() {
			@Override
			public void run() {
				doBackspace();
				handler.postDelayed(this, REPEAT_DELAY);
			}
		};
		
		Runnable actionSpace = new Runnable() {
			@Override
			public void run() {
				doSpace();
				handler.postDelayed(this, REPEAT_DELAY);
			}
		};

	};

	private void switchKeyLabels() {

		if (inputState == ENGLISH) {

			tv41.setText(getResources().getString(R.string.e_key_41_cyrillic));
			cyrillicSetVisibility(View.GONE);
			setEnglishKeyLabels();

		} else if (inputState == CYRILLIC) {

			tv41.setText(getResources().getString(R.string.e_key_41_punctuation));
			cyrillicSetVisibility(View.VISIBLE);
			setCyrillicKeyLabels();

		} else { // PUNCTUATION

			tv41.setText(getResources().getString(R.string.e_key_41_english));
			cyrillicSetVisibility(View.GONE);
			setPunctuationKeyLabels();
		}

	}

	private void setEnglishKeyLabels() {
		if (capsOn) {

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

		} else { // caps is not on
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

		}

	}

	private void setCyrillicKeyLabels() {

		if (capsOn) {

			tv1.setText(getResources().getString(R.string.e_key_1_cyrillic_caps));
			tv2.setText(getResources().getString(R.string.e_key_2_cyrillic_caps));
			tv3.setText(getResources().getString(R.string.e_key_3_cyrillic_caps));
			tv4.setText(getResources().getString(R.string.e_key_4_cyrillic_caps));
			tv5.setText(getResources().getString(R.string.e_key_5_cyrillic_caps));
			tv6.setText(getResources().getString(R.string.e_key_6_cyrillic_caps));
			tv7.setText(getResources().getString(R.string.e_key_7_cyrillic_caps));
			tv8.setText(getResources().getString(R.string.e_key_8_cyrillic_caps));
			tvQ.setText(getResources().getString(R.string.e_key_q_cyrillic_caps));
			tvW.setText(getResources().getString(R.string.e_key_w_cyrillic_caps));
			tvE.setText(getResources().getString(R.string.e_key_e_cyrillic_caps));
			tvR.setText(getResources().getString(R.string.e_key_r_cyrillic_caps));
			tvT.setText(getResources().getString(R.string.e_key_t_cyrillic_caps));
			tvY.setText(getResources().getString(R.string.e_key_y_cyrillic_caps));
			tvU.setText(getResources().getString(R.string.e_key_u_cyrillic_caps));
			tvI.setText(getResources().getString(R.string.e_key_i_cyrillic_caps));
			tvO.setText(getResources().getString(R.string.e_key_o_cyrillic_caps));
			tvP.setText(getResources().getString(R.string.e_key_p_cyrillic_caps));
			tvA.setText(getResources().getString(R.string.e_key_a_cyrillic_caps));
			tvS.setText(getResources().getString(R.string.e_key_s_cyrillic_caps));
			tvD.setText(getResources().getString(R.string.e_key_d_cyrillic_caps));
			tvF.setText(getResources().getString(R.string.e_key_f_cyrillic_caps));
			tvG.setText(getResources().getString(R.string.e_key_g_cyrillic_caps));
			tvH.setText(getResources().getString(R.string.e_key_h_cyrillic_caps));
			tvJ.setText(getResources().getString(R.string.e_key_j_cyrillic_caps));
			tvK.setText(getResources().getString(R.string.e_key_k_cyrillic_caps));
			tvL.setText(getResources().getString(R.string.e_key_l_cyrillic_caps));
			tvZ.setText(getResources().getString(R.string.e_key_z_cyrillic_caps));
			tvX.setText(getResources().getString(R.string.e_key_x_cyrillic_caps));
			tvC.setText(getResources().getString(R.string.e_key_c_cyrillic_caps));
			tvV.setText(getResources().getString(R.string.e_key_v_cyrillic_caps));
			tvB.setText(getResources().getString(R.string.e_key_b_cyrillic_caps));
			tvN.setText(getResources().getString(R.string.e_key_n_cyrillic_caps));
			tvM.setText(getResources().getString(R.string.e_key_m_cyrillic_caps));

		} else { // caps is not on
			tv1.setText(getResources().getString(R.string.e_key_1_cyrillic));
			tv2.setText(getResources().getString(R.string.e_key_2_cyrillic));
			tv3.setText(getResources().getString(R.string.e_key_3_cyrillic));
			tv4.setText(getResources().getString(R.string.e_key_4_cyrillic));
			tv5.setText(getResources().getString(R.string.e_key_5_cyrillic));
			tv6.setText(getResources().getString(R.string.e_key_6_cyrillic));
			tv7.setText(getResources().getString(R.string.e_key_7_cyrillic));
			tv8.setText(getResources().getString(R.string.e_key_8_cyrillic));
			tvQ.setText(getResources().getString(R.string.e_key_q_cyrillic));
			tvW.setText(getResources().getString(R.string.e_key_w_cyrillic));
			tvE.setText(getResources().getString(R.string.e_key_e_cyrillic));
			tvR.setText(getResources().getString(R.string.e_key_r_cyrillic));
			tvT.setText(getResources().getString(R.string.e_key_t_cyrillic));
			tvY.setText(getResources().getString(R.string.e_key_y_cyrillic));
			tvU.setText(getResources().getString(R.string.e_key_u_cyrillic));
			tvI.setText(getResources().getString(R.string.e_key_i_cyrillic));
			tvO.setText(getResources().getString(R.string.e_key_o_cyrillic));
			tvP.setText(getResources().getString(R.string.e_key_p_cyrillic));
			tvA.setText(getResources().getString(R.string.e_key_a_cyrillic));
			tvS.setText(getResources().getString(R.string.e_key_s_cyrillic));
			tvD.setText(getResources().getString(R.string.e_key_d_cyrillic));
			tvF.setText(getResources().getString(R.string.e_key_f_cyrillic));
			tvG.setText(getResources().getString(R.string.e_key_g_cyrillic));
			tvH.setText(getResources().getString(R.string.e_key_h_cyrillic));
			tvJ.setText(getResources().getString(R.string.e_key_j_cyrillic));
			tvK.setText(getResources().getString(R.string.e_key_k_cyrillic));
			tvL.setText(getResources().getString(R.string.e_key_l_cyrillic));
			tvZ.setText(getResources().getString(R.string.e_key_z_cyrillic));
			tvX.setText(getResources().getString(R.string.e_key_x_cyrillic));
			tvC.setText(getResources().getString(R.string.e_key_c_cyrillic));
			tvV.setText(getResources().getString(R.string.e_key_v_cyrillic));
			tvB.setText(getResources().getString(R.string.e_key_b_cyrillic));
			tvN.setText(getResources().getString(R.string.e_key_n_cyrillic));
			tvM.setText(getResources().getString(R.string.e_key_m_cyrillic));

		}

	}

	private void setPunctuationKeyLabels() {
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

	private void initMap() {

		idToEnglish.put(R.id.ekey_q, getResources().getString(R.string.e_key_q).charAt(0));
		idToEnglish.put(R.id.ekey_w, getResources().getString(R.string.e_key_w).charAt(0));
		idToEnglish.put(R.id.ekey_e, getResources().getString(R.string.e_key_e).charAt(0));
		idToEnglish.put(R.id.ekey_r, getResources().getString(R.string.e_key_r).charAt(0));
		idToEnglish.put(R.id.ekey_t, getResources().getString(R.string.e_key_t).charAt(0));
		idToEnglish.put(R.id.ekey_y, getResources().getString(R.string.e_key_y).charAt(0));
		idToEnglish.put(R.id.ekey_u, getResources().getString(R.string.e_key_u).charAt(0));
		idToEnglish.put(R.id.ekey_i, getResources().getString(R.string.e_key_i).charAt(0));
		idToEnglish.put(R.id.ekey_o, getResources().getString(R.string.e_key_o).charAt(0));
		idToEnglish.put(R.id.ekey_p, getResources().getString(R.string.e_key_p).charAt(0));
		idToEnglish.put(R.id.ekey_a, getResources().getString(R.string.e_key_a).charAt(0));
		idToEnglish.put(R.id.ekey_s, getResources().getString(R.string.e_key_s).charAt(0));
		idToEnglish.put(R.id.ekey_d, getResources().getString(R.string.e_key_d).charAt(0));
		idToEnglish.put(R.id.ekey_f, getResources().getString(R.string.e_key_f).charAt(0));
		idToEnglish.put(R.id.ekey_g, getResources().getString(R.string.e_key_g).charAt(0));
		idToEnglish.put(R.id.ekey_h, getResources().getString(R.string.e_key_h).charAt(0));
		idToEnglish.put(R.id.ekey_j, getResources().getString(R.string.e_key_j).charAt(0));
		idToEnglish.put(R.id.ekey_k, getResources().getString(R.string.e_key_k).charAt(0));
		idToEnglish.put(R.id.ekey_l, getResources().getString(R.string.e_key_l).charAt(0));
		idToEnglish.put(R.id.ekey_z, getResources().getString(R.string.e_key_z).charAt(0));
		idToEnglish.put(R.id.ekey_x, getResources().getString(R.string.e_key_x).charAt(0));
		idToEnglish.put(R.id.ekey_c, getResources().getString(R.string.e_key_c).charAt(0));
		idToEnglish.put(R.id.ekey_v, getResources().getString(R.string.e_key_v).charAt(0));
		idToEnglish.put(R.id.ekey_b, getResources().getString(R.string.e_key_b).charAt(0));
		idToEnglish.put(R.id.ekey_n, getResources().getString(R.string.e_key_n).charAt(0));
		idToEnglish.put(R.id.ekey_m, getResources().getString(R.string.e_key_m).charAt(0));

		idToEnglishCaps
				.put(R.id.ekey_q, getResources().getString(R.string.e_key_q_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_w, getResources().getString(R.string.e_key_w_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_e, getResources().getString(R.string.e_key_e_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_r, getResources().getString(R.string.e_key_r_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_t, getResources().getString(R.string.e_key_t_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_y, getResources().getString(R.string.e_key_y_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_u, getResources().getString(R.string.e_key_u_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_i, getResources().getString(R.string.e_key_i_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_o, getResources().getString(R.string.e_key_o_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_p, getResources().getString(R.string.e_key_p_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_a, getResources().getString(R.string.e_key_a_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_s, getResources().getString(R.string.e_key_s_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_d, getResources().getString(R.string.e_key_d_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_f, getResources().getString(R.string.e_key_f_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_g, getResources().getString(R.string.e_key_g_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_h, getResources().getString(R.string.e_key_h_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_j, getResources().getString(R.string.e_key_j_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_k, getResources().getString(R.string.e_key_k_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_l, getResources().getString(R.string.e_key_l_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_z, getResources().getString(R.string.e_key_z_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_x, getResources().getString(R.string.e_key_x_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_c, getResources().getString(R.string.e_key_c_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_v, getResources().getString(R.string.e_key_v_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_b, getResources().getString(R.string.e_key_b_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_n, getResources().getString(R.string.e_key_n_caps).charAt(0));
		idToEnglishCaps
				.put(R.id.ekey_m, getResources().getString(R.string.e_key_m_caps).charAt(0));

		idToPunctuation.put(R.id.ekey_q, getResources().getString(R.string.e_key_q_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_w, getResources().getString(R.string.e_key_w_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_e, getResources().getString(R.string.e_key_e_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_r, getResources().getString(R.string.e_key_r_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_t, getResources().getString(R.string.e_key_t_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_y, getResources().getString(R.string.e_key_y_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_u, getResources().getString(R.string.e_key_u_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_i, getResources().getString(R.string.e_key_i_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_o, getResources().getString(R.string.e_key_o_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_p, getResources().getString(R.string.e_key_p_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_a, getResources().getString(R.string.e_key_a_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_s, getResources().getString(R.string.e_key_s_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_d, getResources().getString(R.string.e_key_d_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_f, getResources().getString(R.string.e_key_f_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_g, getResources().getString(R.string.e_key_g_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_h, getResources().getString(R.string.e_key_h_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_j, getResources().getString(R.string.e_key_j_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_k, getResources().getString(R.string.e_key_k_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_l, getResources().getString(R.string.e_key_l_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_z, getResources().getString(R.string.e_key_z_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_x, getResources().getString(R.string.e_key_x_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_c, getResources().getString(R.string.e_key_c_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_v, getResources().getString(R.string.e_key_v_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_b, getResources().getString(R.string.e_key_b_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_n, getResources().getString(R.string.e_key_n_punctuation)
				.charAt(0));
		idToPunctuation.put(R.id.ekey_m, getResources().getString(R.string.e_key_m_punctuation)
				.charAt(0));

		idToCyrillic.put(R.id.ekey_1, getResources().getString(R.string.e_key_1_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_2, getResources().getString(R.string.e_key_2_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_3, getResources().getString(R.string.e_key_3_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_4, getResources().getString(R.string.e_key_4_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_5, getResources().getString(R.string.e_key_5_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_6, getResources().getString(R.string.e_key_6_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_7, getResources().getString(R.string.e_key_7_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_8, getResources().getString(R.string.e_key_8_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_q, getResources().getString(R.string.e_key_q_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_w, getResources().getString(R.string.e_key_w_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_e, getResources().getString(R.string.e_key_e_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_r, getResources().getString(R.string.e_key_r_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_t, getResources().getString(R.string.e_key_t_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_y, getResources().getString(R.string.e_key_y_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_u, getResources().getString(R.string.e_key_u_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_i, getResources().getString(R.string.e_key_i_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_o, getResources().getString(R.string.e_key_o_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_p, getResources().getString(R.string.e_key_p_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_a, getResources().getString(R.string.e_key_a_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_s, getResources().getString(R.string.e_key_s_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_d, getResources().getString(R.string.e_key_d_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_f, getResources().getString(R.string.e_key_f_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_g, getResources().getString(R.string.e_key_g_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_h, getResources().getString(R.string.e_key_h_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_j, getResources().getString(R.string.e_key_j_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_k, getResources().getString(R.string.e_key_k_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_l, getResources().getString(R.string.e_key_l_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_z, getResources().getString(R.string.e_key_z_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_x, getResources().getString(R.string.e_key_x_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_c, getResources().getString(R.string.e_key_c_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_v, getResources().getString(R.string.e_key_v_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_b, getResources().getString(R.string.e_key_b_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_n, getResources().getString(R.string.e_key_n_cyrillic)
				.charAt(0));
		idToCyrillic.put(R.id.ekey_m, getResources().getString(R.string.e_key_m_cyrillic)
				.charAt(0));

		idToCyrillicCaps.put(R.id.ekey_1, getResources().getString(R.string.e_key_1_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_2, getResources().getString(R.string.e_key_2_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_3, getResources().getString(R.string.e_key_3_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_4, getResources().getString(R.string.e_key_4_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_5, getResources().getString(R.string.e_key_5_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_6, getResources().getString(R.string.e_key_6_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_7, getResources().getString(R.string.e_key_7_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_8, getResources().getString(R.string.e_key_8_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_q, getResources().getString(R.string.e_key_q_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_w, getResources().getString(R.string.e_key_w_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_e, getResources().getString(R.string.e_key_e_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_r, getResources().getString(R.string.e_key_r_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_t, getResources().getString(R.string.e_key_t_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_y, getResources().getString(R.string.e_key_y_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_u, getResources().getString(R.string.e_key_u_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_i, getResources().getString(R.string.e_key_i_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_o, getResources().getString(R.string.e_key_o_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_p, getResources().getString(R.string.e_key_p_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_a, getResources().getString(R.string.e_key_a_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_s, getResources().getString(R.string.e_key_s_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_d, getResources().getString(R.string.e_key_d_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_f, getResources().getString(R.string.e_key_f_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_g, getResources().getString(R.string.e_key_g_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_h, getResources().getString(R.string.e_key_h_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_j, getResources().getString(R.string.e_key_j_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_k, getResources().getString(R.string.e_key_k_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_l, getResources().getString(R.string.e_key_l_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_z, getResources().getString(R.string.e_key_z_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_x, getResources().getString(R.string.e_key_x_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_c, getResources().getString(R.string.e_key_c_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_v, getResources().getString(R.string.e_key_v_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_b, getResources().getString(R.string.e_key_b_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_n, getResources().getString(R.string.e_key_n_cyrillic_caps)
				.charAt(0));
		idToCyrillicCaps.put(R.id.ekey_m, getResources().getString(R.string.e_key_m_cyrillic_caps)
				.charAt(0));

	}

}
