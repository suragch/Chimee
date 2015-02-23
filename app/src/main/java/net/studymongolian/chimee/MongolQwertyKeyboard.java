package net.studymongolian.chimee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MongolQwertyKeyboard extends Fragment implements OnClickListener, OnLongClickListener,
		OnItemClickListener, OnItemLongClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	// static variables at end for readibility

	protected static final int MIN_DICTIONARY_WORD_LENGTH = 3;
	protected static final int MAX_FOLLOWING_WORDS = 10;
	protected static final char SWITCH_TO_ENGLISH = 'α'; // arbitrary symbol
	protected static final char PUNCTUATION_KEY = 'γ'; // arbitrary symbol
	private static final int FVS_REQUEST = 10;

	Communicator mCallback;
	Boolean punctuationOn = false;
	MongolUnicodeRenderer converter = new MongolUnicodeRenderer();
	ListView lvSuggestions;
	boolean isFollowing = false; // db id of word whose followings are in lv
	boolean typingMongol = false;
	boolean isSuffix = false;

	SimpleCursorAdapter cursorAdapter; // adapter for db words
	String suggestionsParent = ""; // keep track of parent of following list
	List<String> suggestionsUnicode = new ArrayList<String>(); // following
	protected static final int WORDS_LOADER_ID = 0;

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
	TextView tvInput;
	TextView tvInputMongol;


	// Container Activity must implement this interface
	public interface Communicator {

		/**
		 * This is the primary interface. Other methods may be deleted if functionality is not
		 * needed.
		 * 
		 * @param keyChar
		 *            Provides Unicode value for the key that was pressed.
		 */
		public void onKeyTouched(char keyChar);

		/**
		 * @return Return the character directly before the cursor
		 */
		public char getCharBeforeCursor();

		/**
		 * @return Return the word before the cursor
		 */
		public String getWordBeforeCursor();

		/**
		 * @return Return the word before the current word that the cursor is at
		 */
		public String getPreviousWord();

		/**
		 * @param cursorOffset
		 *            Relative to the cursor. Make zero if you want to use the current cursor
		 *            location.
		 * 
		 * @return Return the location of the character with relation to the cursor (ie, ISOLATE,
		 *         INITIAL, MEDIAL, FINAL)
		 */
		public MongolUnicodeRenderer.Location getLocationOfCharInMongolWord(int cursorOffset);

		public void replaceFromWordStartToCursor(String replacementString);


	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize suffix variables
		YIN = getResources().getString(R.string.suffix_yin);
		ON = getResources().getString(R.string.suffix_on);
		UN = getResources().getString(R.string.suffix_un);
		O = getResources().getString(R.string.suffix_o);
		U = getResources().getString(R.string.suffix_u);
		I = getResources().getString(R.string.suffix_i);
		YI = getResources().getString(R.string.suffix_yi);
		DO = getResources().getString(R.string.suffix_do);
		DU = getResources().getString(R.string.suffix_du);
		TO = getResources().getString(R.string.suffix_to);
		TU = getResources().getString(R.string.suffix_tu);
		ACHA = getResources().getString(R.string.suffix_acha);
		ECHE = getResources().getString(R.string.suffix_eche);
		BAR = getResources().getString(R.string.suffix_bar);
		BER = getResources().getString(R.string.suffix_ber);
		IYAR = getResources().getString(R.string.suffix_iyar);
		IYER = getResources().getString(R.string.suffix_iyer);
		TAI = getResources().getString(R.string.suffix_tai);
		TEI = getResources().getString(R.string.suffix_tei);
		IYAN = getResources().getString(R.string.suffix_iyan);
		IYEN = getResources().getString(R.string.suffix_iyen);
		BAN = getResources().getString(R.string.suffix_ban);
		BEN = getResources().getString(R.string.suffix_ben);
		OO = getResources().getString(R.string.suffix_oo);
		UU = getResources().getString(R.string.suffix_uu);
		YOGAN = getResources().getString(R.string.suffix_yogan);
		YUGEN = getResources().getString(R.string.suffix_yugen);
		DAGAN = getResources().getString(R.string.suffix_dagan);
		DEGEN = getResources().getString(R.string.suffix_degen);
		TAGAN = getResources().getString(R.string.suffix_tagan);
		TEGEN = getResources().getString(R.string.suffix_tegen);
		ACHAGAN = getResources().getString(R.string.suffix_achagan);
		ECHEGEN = getResources().getString(R.string.suffix_echegen);
		TAIGAN = getResources().getString(R.string.suffix_taigan);
		TEIGEN = getResources().getString(R.string.suffix_teigen);
		OD = getResources().getString(R.string.suffix_od);
		UD = getResources().getString(R.string.suffix_ud);
		NOGOD = getResources().getString(R.string.suffix_nogod);
		NUGUD = getResources().getString(R.string.suffix_nugud);
		NAR = getResources().getString(R.string.suffix_nar);
		NER = getResources().getString(R.string.suffix_ner);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.mongol_qwerty_keyboard, container, false);

		String[] fromColumns = { ChimeeUserDictionary.Words.WORD };
		int[] toViews = { R.id.tvMongolListViewItem };

		// set up the adapter for the list view
		cursorAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),
				R.layout.mongol_suggestions_listview, null, fromColumns, toViews, 0) {
			// Format the unicode from the db for the font
			@Override
			public void setViewText(TextView v, String text) {
				String renderedText = converter.unicodeToGlyphs(text);
				super.setViewText(v, renderedText);
			}
		};

		lvSuggestions = (ListView) v.findViewById(R.id.lvSuggestions);
		lvSuggestions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvSuggestions.setOnItemClickListener(this);
		lvSuggestions.setOnItemLongClickListener(this);
		lvSuggestions.setAdapter(cursorAdapter);

		// Add listeners for all keys
		RelativeLayout rlKeyQ = (RelativeLayout) v.findViewById(R.id.key_q);
		rlKeyQ.setOnClickListener(this);
		rlKeyQ.setOnLongClickListener(this);
		RelativeLayout rlKeyW = (RelativeLayout) v.findViewById(R.id.key_w);
		rlKeyW.setOnClickListener(this);
		rlKeyW.setOnLongClickListener(this);
		RelativeLayout rlKeyE = (RelativeLayout) v.findViewById(R.id.key_e);
		rlKeyE.setOnClickListener(this);
		rlKeyE.setOnLongClickListener(this);
		RelativeLayout rlKeyR = (RelativeLayout) v.findViewById(R.id.key_r);
		rlKeyR.setOnClickListener(this);
		rlKeyR.setOnLongClickListener(this);
		RelativeLayout rlKeyT = (RelativeLayout) v.findViewById(R.id.key_t);
		rlKeyT.setOnClickListener(this);
		rlKeyT.setOnLongClickListener(this);
		RelativeLayout rlKeyY = (RelativeLayout) v.findViewById(R.id.key_y);
		rlKeyY.setOnClickListener(this);
		rlKeyY.setOnLongClickListener(this);
		RelativeLayout rlKeyU = (RelativeLayout) v.findViewById(R.id.key_u);
		rlKeyU.setOnClickListener(this);
		rlKeyU.setOnLongClickListener(this);
		RelativeLayout rlKeyI = (RelativeLayout) v.findViewById(R.id.key_i);
		rlKeyI.setOnClickListener(this);
		rlKeyI.setOnLongClickListener(this);
		RelativeLayout rlKeyO = (RelativeLayout) v.findViewById(R.id.key_o);
		rlKeyO.setOnClickListener(this);
		rlKeyO.setOnLongClickListener(this);
		RelativeLayout rlKeyP = (RelativeLayout) v.findViewById(R.id.key_p);
		rlKeyP.setOnClickListener(this);
		rlKeyP.setOnLongClickListener(this);
		RelativeLayout rlKeyA = (RelativeLayout) v.findViewById(R.id.key_a);
		rlKeyA.setOnClickListener(this);
		RelativeLayout rlKeyS = (RelativeLayout) v.findViewById(R.id.key_s);
		rlKeyS.setOnClickListener(this);
		RelativeLayout rlKeyD = (RelativeLayout) v.findViewById(R.id.key_d);
		rlKeyD.setOnClickListener(this);
		RelativeLayout rlKeyF = (RelativeLayout) v.findViewById(R.id.key_f);
		rlKeyF.setOnClickListener(this);
		RelativeLayout rlKeyG = (RelativeLayout) v.findViewById(R.id.key_g);
		rlKeyG.setOnClickListener(this);
		RelativeLayout rlKeyH = (RelativeLayout) v.findViewById(R.id.key_h);
		rlKeyH.setOnClickListener(this);
		rlKeyH.setOnLongClickListener(this);
		RelativeLayout rlKeyJ = (RelativeLayout) v.findViewById(R.id.key_j);
		rlKeyJ.setOnClickListener(this);
		rlKeyJ.setOnLongClickListener(this);
		RelativeLayout rlKeyK = (RelativeLayout) v.findViewById(R.id.key_k);
		rlKeyK.setOnClickListener(this);
		RelativeLayout rlKeyL = (RelativeLayout) v.findViewById(R.id.key_l);
		rlKeyL.setOnClickListener(this);
		rlKeyL.setOnLongClickListener(this);
		RelativeLayout rlKeyNg = (RelativeLayout) v.findViewById(R.id.key_ng);
		rlKeyNg.setOnClickListener(this);
		RelativeLayout rlKeyZ = (RelativeLayout) v.findViewById(R.id.key_z);
		rlKeyZ.setOnClickListener(this);
		rlKeyZ.setOnLongClickListener(this);
		RelativeLayout rlKeyX = (RelativeLayout) v.findViewById(R.id.key_x);
		rlKeyX.setOnClickListener(this);
		RelativeLayout rlKeyC = (RelativeLayout) v.findViewById(R.id.key_c);
		rlKeyC.setOnClickListener(this);
		RelativeLayout rlKeyV = (RelativeLayout) v.findViewById(R.id.key_v);
		rlKeyV.setOnClickListener(this);
		RelativeLayout rlKeyB = (RelativeLayout) v.findViewById(R.id.key_b);
		rlKeyB.setOnClickListener(this);
		RelativeLayout rlKeyN = (RelativeLayout) v.findViewById(R.id.key_n);
		rlKeyN.setOnClickListener(this);
		RelativeLayout rlKeyM = (RelativeLayout) v.findViewById(R.id.key_m);
		rlKeyM.setOnClickListener(this);

		RelativeLayout rlKeyNamalaga = (RelativeLayout) v.findViewById(R.id.key_namalaga);
		rlKeyNamalaga.setOnClickListener(this);
		rlKeyNamalaga.setOnLongClickListener(this);
		RelativeLayout rlKeyCaseSuffix = (RelativeLayout) v.findViewById(R.id.key_case_suffix);
		rlKeyCaseSuffix.setOnClickListener(this);
		RelativeLayout rlKeyFvs = (RelativeLayout) v.findViewById(R.id.key_fvs);
		rlKeyFvs.setOnClickListener(this);
		RelativeLayout rlKeyBackspace = (RelativeLayout) v.findViewById(R.id.key_backspace);
		rlKeyBackspace.setOnTouchListener(handleTouch);
		RelativeLayout rlKeyInput = (RelativeLayout) v.findViewById(R.id.key_input);
		rlKeyInput.setOnClickListener(this);
		rlKeyInput.setOnLongClickListener(this);
		RelativeLayout rlKeyComma = (RelativeLayout) v.findViewById(R.id.key_comma);
		rlKeyComma.setOnClickListener(this);
		rlKeyComma.setOnLongClickListener(this);
		RelativeLayout rlKeySpace = (RelativeLayout) v.findViewById(R.id.key_space);
		rlKeySpace.setOnTouchListener(handleTouch);
		RelativeLayout rlKeyPeriod = (RelativeLayout) v.findViewById(R.id.key_question);
		rlKeyPeriod.setOnClickListener(this);
		rlKeyPeriod.setOnLongClickListener(this);
		RelativeLayout rlKeyReturn = (RelativeLayout) v.findViewById(R.id.key_return);
		rlKeyReturn.setOnClickListener(this);

		return v;
	}

	class CustomListViewAdapter extends ArrayAdapter<String> {

		Context context;
		List<String> wordsArray;

		CustomListViewAdapter(Context c, List<String> words) {
			super(c, R.layout.mongol_suggestions_listview, R.id.tvMongolListViewItem, words);
			this.context = c;
			this.wordsArray = words;
		}

		class MyViewHolder {
			// This is an optimization to avoid expensive findViewById calls
			TextView tvWords;

			MyViewHolder(View v) {
				tvWords = (TextView) v.findViewById(R.id.tvMongolListViewItem);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			MyViewHolder holder = null;

			if (row == null) {
				// Expensive LayoutInflator calls only done once
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.mongol_suggestions_listview, parent, false);
				holder = new MyViewHolder(row);
				row.setTag(holder);
			} else { // recycling
				holder = (MyViewHolder) row.getTag();
			}

			holder.tvWords.setText(wordsArray.get(position));

			return row;
		}
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
		tvInput = (TextView) getView().findViewById(R.id.tvMkeyInput);
		tvInputMongol = (TextView) getView().findViewById(R.id.tvMkeyInputMongol);

        // TODO this is a strange hack fix for Xiaomi pad with Android 4.4 (letters didn't display)
        tvFvs1Top.setText("  ");
        tvFvs1Bottom.setText("  ");
        tvFvs2Top.setText("  ");
        tvFvs2Bottom.setText("  ");
        tvFvs3Top.setText("  ");
        tvFvs3Bottom.setText("  ");


	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (Communicator) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement Communicator");
		}
	}

	// Handles continuous space and backspace presses
	private OnTouchListener handleTouch = new OnTouchListener() {

		private Handler handler;
		final int INITIAL_DELAY = 500;
		final int REPEAT_DELAY = 50;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (view.getId() == R.id.key_backspace) {

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
			} else if (view.getId() == R.id.key_space) {
				
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
			doPostKeyPressActivities(BACKSPACE);
		}
		
		private void doSpace(){
			mCallback.onKeyTouched(SPACE);
			doPostKeyPressActivities(SPACE);
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


	@Override
	public void onClick(View v) {
		
		// TODO can this code be cleaned by making Maps? idToLetter and idToPunc

		char key = NULL_CHAR;

		// Send the event to the host activity
		switch (v.getId()) {
		case R.id.key_q:
			if (punctuationOn) {
				key = '1';
			} else {
				key = MongolUnicodeRenderer.UNI_CHA;
			}
			break;
		case R.id.key_w:
			if (punctuationOn) {
				key = '2';
			} else {
				key = MongolUnicodeRenderer.UNI_WA;
			}
			break;
		case R.id.key_e:
			if (punctuationOn) {
				key = '3';
			} else {
				key = MongolUnicodeRenderer.UNI_E;
			}
			break;
		case R.id.key_r:
			if (punctuationOn) {
				key = '4';
			} else {
				key = MongolUnicodeRenderer.UNI_RA;
			}
			break;
		case R.id.key_t:
			if (punctuationOn) {
				key = '5';
			} else {
				key = MongolUnicodeRenderer.UNI_TA;
			}
			break;
		case R.id.key_y:
			if (punctuationOn) {
				key = '6';
			} else {
				key = MongolUnicodeRenderer.UNI_YA;
			}
			break;
		case R.id.key_u:
			if (punctuationOn) {
				key = '7';
			} else {
				key = MongolUnicodeRenderer.UNI_UE;
			}
			break;
		case R.id.key_i:
			if (punctuationOn) {
				key = '8';
			} else {
				key = MongolUnicodeRenderer.UNI_I;
			}
			break;
		case R.id.key_o:
			if (punctuationOn) {
				key = '9';
			} else {
				key = MongolUnicodeRenderer.UNI_OE;
			}
			break;
		case R.id.key_p:
			if (punctuationOn) {
				key = '0';
			} else {
				key = MongolUnicodeRenderer.UNI_PA;
			}
			break;
		case R.id.key_a:
			if (punctuationOn) {
				key = '(';
			} else {
				key = MongolUnicodeRenderer.UNI_A;
			}
			break;
		case R.id.key_s:
			if (punctuationOn) {
				key = ')';
			} else {
				key = MongolUnicodeRenderer.UNI_SA;
			}
			break;
		case R.id.key_d:
			if (punctuationOn) {
				key = '<';
			} else {
				key = MongolUnicodeRenderer.UNI_DA;
			}
			break;
		case R.id.key_f:
			if (punctuationOn) {
				key = '>';
			} else {
				key = MongolUnicodeRenderer.UNI_FA;
			}
			break;
		case R.id.key_g:
			if (punctuationOn) {
				key = PUNCTUATION_DOUBLEQUOTE_TOP;
			} else {
				key = MongolUnicodeRenderer.UNI_GA;
			}
			break;
		case R.id.key_h:
			if (punctuationOn) {
				key = PUNCTUATION_DOUBLEQUOTE_BOTTOM;
			} else {
				key = MongolUnicodeRenderer.UNI_QA;
			}
			break;
		case R.id.key_j:
			if (punctuationOn) {
				key = PUNCTUATION_QUESTION_EXCLAMATION;
			} else {
				key = MongolUnicodeRenderer.UNI_JA;
			}
			break;
		case R.id.key_k:
			if (punctuationOn) {
				key = PUNCTUATION_EXCLAMATION_QUESTION;
			} else {
				key = MongolUnicodeRenderer.UNI_KA;
			}
			break;
		case R.id.key_l:
			if (punctuationOn) {
				key = PUNCTUATION_EXCLAMATION_EXCLAMATION;
			} else {
				key = MongolUnicodeRenderer.UNI_LA;
			}
			break;
		case R.id.key_ng:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_COLON;
			} else {
				key = MongolUnicodeRenderer.UNI_ANG;
			}
			break;
		case R.id.key_z:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_ELLIPSIS;
			} else {
				key = MongolUnicodeRenderer.UNI_ZA;
			}
			break;
		case R.id.key_x:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_FOUR_DOTS;
			} else {
				key = MongolUnicodeRenderer.UNI_SHA;
			}
			break;
		case R.id.key_c:
			if (punctuationOn) {
				key = MONGOLIAN_DOT;
			} else {
				key = MongolUnicodeRenderer.UNI_O;
			}
			break;
		case R.id.key_v:
			if (punctuationOn) {
				key = '.';
			} else {
				key = MongolUnicodeRenderer.UNI_U;
			}
			break;
		case R.id.key_b:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_NIRUGU;
			} else {
				key = MongolUnicodeRenderer.UNI_BA;
			}
			break;
		case R.id.key_n:
			if (punctuationOn) {
				key = MONGOLIAN_DASH;
			} else {
				key = MongolUnicodeRenderer.UNI_NA;
			}
			break;
		case R.id.key_m:
			if (punctuationOn) {
				key = ';';
			} else {
				key = MongolUnicodeRenderer.UNI_MA;
			}
			break;
		case R.id.key_namalaga:
			if (punctuationOn) {
				// key = '$';
			} else {
				key = MVS;
			}
			break;
		case R.id.key_case_suffix:
			if (punctuationOn) {
				// key = '*';
			} else {
				key = NNBS;
			}
			break;
		case R.id.key_fvs:
			if (!punctuationOn) {

				startFvsDialog();
				key = NULL_CHAR;

			}
			break;
		case R.id.key_backspace:
			// Unreachable.
			// This is handled by handleTouch now
			key = BACKSPACE;
			break;
		case R.id.key_input:
			// Switch to number/punctuation keyboard
			key = PUNCTUATION_KEY;
			switchPunctuation();
			punctuationOn = !punctuationOn;
			break;
		case R.id.key_comma:
			key = MongolUnicodeRenderer.MONGOLIAN_COMMA;
			break;
		case R.id.key_space:
			// Unreachable.
			// This is handled by handleTouch now
			key = SPACE;
			break;
		case R.id.key_question:
			key = '?';
			break;
		case R.id.key_return:
			key = NEW_LINE;
			break;
		default:
			// Log.e("Chimee", "Mongol key error");
		}

		if (key != PUNCTUATION_KEY) {

			mCallback.onKeyTouched(key);
			doPostKeyPressActivities(key);
		}

	}

	private void startFvsDialog() {

		// only FVS1 is available
		if (tvFvs2Top.getText().equals("") && tvFvs2Bottom.getText().equals("")
				&& tvFvs3Top.getText().equals("") && tvFvs3Bottom.getText().equals("")) {
			if (!tvFvs1Top.getText().equals("") || !tvFvs1Bottom.getText().equals("")) {
				// as long as at least one is not blank
				mCallback.onKeyTouched(FVS1);
				doPostKeyPressActivities(FVS1);
			}

		} else {
			// Bring up a dialog box with FVS choices
			Intent intent = new Intent(getActivity().getApplicationContext(),
					FvsChooserDialog.class);
			intent.putExtra("topFvs1", tvFvs1Top.getText());
			intent.putExtra("topFvs2", tvFvs2Top.getText());
			intent.putExtra("topFvs3", tvFvs3Top.getText());
			intent.putExtra("bottomFvs1", tvFvs1Bottom.getText());
			intent.putExtra("bottomFvs2", tvFvs2Bottom.getText());
			intent.putExtra("bottomFvs3", tvFvs3Bottom.getText());
			this.startActivityForResult(intent, FVS_REQUEST);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO this only gets called if Activity calls
		// TODO super.onActivityResult(requestCode, resultCode, data);
		// Check which request we're responding to
		if (requestCode == FVS_REQUEST) {
			// Make sure the request was successful
			if (resultCode == Activity.RESULT_OK) {
				char fvsValue = data.getCharExtra("result", NULL_CHAR);
				mCallback.onKeyTouched(fvsValue);
				doPostKeyPressActivities(fvsValue);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// fvsValue = NULL_CHAR;
			}

		}
	}

	@Override
	public boolean onLongClick(View v) {

		char key = NULL_CHAR;

		// Send the event to the host activity
		switch (v.getId()) {
		/*
		 * case R.id.key_12: key = MONGOLIAN_NIRUGU; break;
		 */
		case R.id.key_q:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_ONE;
			} else {
				key = MongolUnicodeRenderer.UNI_CHI;
			}
			break;
		case R.id.key_w:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_TWO;
			} else {
				key = MongolUnicodeRenderer.UNI_WA;
			}
			break;
		case R.id.key_e:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_THREE;
			} else {
				key = MongolUnicodeRenderer.UNI_EE;
			}
			break;
		case R.id.key_r:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_FOUR;
			} else {
				key = MongolUnicodeRenderer.UNI_ZRA;
			}
			break;
		case R.id.key_t:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_FIVE;
			} else {
				key = MongolUnicodeRenderer.UNI_TA;
			}
			break;
		case R.id.key_y:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_SIX;
			} else {
				key = MongolUnicodeRenderer.UNI_YA;
			}
			break;
		case R.id.key_u:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_SEVEN;
			} else {
				key = MongolUnicodeRenderer.UNI_UE;
			}
			break;
		case R.id.key_i:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_EIGHT;
			} else {
				key = MongolUnicodeRenderer.UNI_I;
			}
			break;
		case R.id.key_o:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_NINE;
			} else {
				key = MongolUnicodeRenderer.UNI_OE;
			}
			break;
		case R.id.key_p:
			if (punctuationOn) {
				key = MongolUnicodeRenderer.MONGOLIAN_DIGIT_ZERO;
			} else {
				key = MongolUnicodeRenderer.UNI_PA;
			}
			break;
		case R.id.key_h:
			if (!punctuationOn) {
				key = MongolUnicodeRenderer.UNI_HAA;
			}
			break;
		case R.id.key_l:
			if (!punctuationOn) {
				key = MongolUnicodeRenderer.UNI_LHA;
			}
			break;
		case R.id.key_z:
			if (!punctuationOn) {
				key = MongolUnicodeRenderer.UNI_TSA;
			}
			break;
		case R.id.key_j:
			if (!punctuationOn) {
				key = MongolUnicodeRenderer.UNI_ZHI;
			}
			break;
		case R.id.key_input:
			key = SWITCH_TO_ENGLISH;
			break;
		case R.id.key_namalaga:
			if (!punctuationOn) {
				key = ZWJ;
			}
			break;
		case R.id.key_comma:
			key = MongolUnicodeRenderer.MONGOLIAN_FULL_STOP;
			break;
		case R.id.key_question:
			key = '!';
			break;
		default:
			return false;
		}

		mCallback.onKeyTouched(key);
		doPostKeyPressActivities(key);

		return true;
	}

	private void doPostKeyPressActivities(char key) {

		// error checking
		if (key == NULL_CHAR) {
			return;
		}

		if (punctuationOn) {
			// do nothing
		} else if (key == BACKSPACE) {
			tvFvs1Top.setText("");
			tvFvs1Bottom.setText("");
			tvFvs2Top.setText("");
			tvFvs2Bottom.setText("");
			tvFvs3Top.setText("");
			tvFvs3Bottom.setText("");
			if (lvSuggestions.getAdapter() != null) {
				lvSuggestions.setAdapter(null);
			}
		} else {// Mongol keyboard

			// MainActivity.onKeyTouched adds vowel and space after MVS

			updateFvsKeys();

			isFollowing = false;

			if (MongolUnicodeRenderer.isMongolian(key) || key == NNBS) {
				if (lvSuggestions.getAdapter() != cursorAdapter) {
					lvSuggestions.setAdapter(cursorAdapter);
				}
				typingMongol = true;

				if (key == NNBS) {
					saveWord();
					isSuffix = true;
					updateLvSuffix(mCallback.getPreviousWord(), Character.toString(NNBS));
				} else if (isSuffix) {
					updateLvSuffix(mCallback.getPreviousWord(), mCallback.getWordBeforeCursor());
				} else {
					updateLvSuggestions();
				}

			} else {
				if (lvSuggestions.getAdapter() != null) {
					lvSuggestions.setAdapter(null);
				}
				if (typingMongol) {

					typingMongol = false;
					isSuffix = false;

					// I've been typing in Mongolian but now the current
					// character is not Mongolian
					// So I need to save the current word to the db

					saveWord();

					// Now the listview needs to be cleared
					getLoaderManager().destroyLoader(WORDS_LOADER_ID);

				}
			}
		}
	}

	private void saveWord() {

		// This method saves the word before the cursor and also
		// adds it to the following list of the previous word

		String thisWord = mCallback.getWordBeforeCursor();
		String previousWord = mCallback.getPreviousWord();
		if (!TextUtils.isEmpty(previousWord) && previousWord.length() < MIN_DICTIONARY_WORD_LENGTH) {

			previousWord = "";
		}

		// only save words that are at least the minimum length
		if (thisWord != null && thisWord.length() >= MIN_DICTIONARY_WORD_LENGTH) {

			new AddOrUpdateDictionaryWordsTask().execute(thisWord, previousWord);
		}
	}

	private List<String> getSuffixList(String currentInputWord) {

		final int ENDING_NULL = -1;
		final int ENDING_N = 0;
		final int ENDING_VOWEL = 1;
		final int ENDING_CONSONANT = 2;
		final int ENDING_BIG_DRESS = 3;

		final int NEUTRAL = 0;
		final int MASCULINE = 1;
		final int FEMININE = 2;

		List<String> initialList = new ArrayList<String>();
		List<String> returnList = new ArrayList<String>();

		if (TextUtils.isEmpty(currentInputWord)) {
			return null;
		}
		// take off the NNBS for now
		if (currentInputWord.startsWith(Character.toString(NNBS))) {
			if (currentInputWord.equals(Character.toString(NNBS))) {
				currentInputWord = "";
			} else {
				currentInputWord = currentInputWord.substring(1);
			}

		}

		String previousWord;
		if (currentInputWord.equals("")) {
			previousWord = mCallback.getWordBeforeCursor();
		} else {
			previousWord = mCallback.getPreviousWord();
		}

		// Determine ending type
		int ending = ENDING_NULL;
		int length = previousWord.length();
		char endingChar = NULL_CHAR;
		if (length > 0) {
			endingChar = previousWord.charAt(previousWord.length() - 1);
			if (endingChar == FVS1 || endingChar == FVS2 || endingChar == FVS3) {
				if (length > 1) {
					endingChar = previousWord.charAt(previousWord.length() - 2);
				} else {
					endingChar = NULL_CHAR;
				}
			}
		}
		if (MongolUnicodeRenderer.isVowel(endingChar)) {
			ending = ENDING_VOWEL;
		} else if (converter.isConsonant(endingChar)) {
			if (endingChar == MongolUnicodeRenderer.UNI_NA) {
				ending = ENDING_N;
			} else if (converter.isBGDRS(endingChar)) {
				ending = ENDING_BIG_DRESS;
			} else {
				ending = ENDING_CONSONANT;
			}
		} else {
			ending = ENDING_NULL;
		}

		// Determine gender
		int gender = NEUTRAL;
		if (converter.isMasculineWord(previousWord)) {
			gender = MASCULINE;
		} else if (converter.isFeminineWord(previousWord)) {
			gender = FEMININE;
		} else {
			gender = NEUTRAL;
		}

		switch (ending) {
		case ENDING_NULL:

			List<String> listAll = Arrays.asList(YIN, ON, O, I, YI, DO, TO, ACHA, BAR, IYAR, TAI,
					IYAN, BAN, OO, YOGAN, DAGAN, TAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR, UN, U, DU,
					TU, ECHE, BER, IYER, TEI, IYEN, BEN, UU, YUGEN, DEGEN, TEGEN, ECHEGEN, TEIGEN,
					UD, NUGUD, NER);

			initialList = listAll;

			break;
		case ENDING_VOWEL:

			if (gender == MASCULINE) {
				List<String> listMV = Arrays.asList(YIN, YI, DO, ACHA, BAR, TAI, BAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR);

				initialList = listMV;

			} else if (gender == FEMININE) {
				List<String> listFV = Arrays.asList(YIN, YI, DU, ECHE, BER, TEI, BEN, UU, YUGEN,
						DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listFV;

			} else { // Gender NEUTRAL
				List<String> listNV = Arrays.asList(YIN, YI, DO, ACHA, BAR, TAI, BAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR, DU, ECHE, BER, TEI, BEN, UU, YUGEN,
						DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listNV;

			}
			break;
		case ENDING_N:

			if (gender == MASCULINE) {
				List<String> listMN = Arrays.asList(O, I, DO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR);

				initialList = listMN;

			} else if (gender == FEMININE) {
				List<String> listFN = Arrays.asList(U, I, DU, ECHE, IYER, TEI, IYEN, UU, YUGEN,
						DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listFN;

			} else { // Gender NEUTRAL
				List<String> listNN = Arrays.asList(O, I, DO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR, U, DU, ECHE, IYER, TEI, IYEN, UU,
						YUGEN, DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listNN;

			}
			break;

		case ENDING_BIG_DRESS:

			if (gender == MASCULINE) {
				List<String> listMB = Arrays.asList(ON, I, TO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						TAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR);

				initialList = listMB;

			} else if (gender == FEMININE) {
				List<String> listFB = Arrays.asList(UN, I, TU, ECHE, IYER, TEI, IYEN, UU, YUGEN,
						TEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listFB;

			} else { // Gender NEUTRAL

				List<String> listNB = Arrays.asList(ON, I, TO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						TAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR, UN, TU, ECHE, IYER, TEI, IYEN, UU,
						YUGEN, TEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listNB;

			}
			break;
		case ENDING_CONSONANT: // besides N and BGDRS

			if (gender == MASCULINE) {
				List<String> listMC = Arrays.asList(ON, I, DO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR);

				initialList = listMC;

			} else if (gender == FEMININE) {
				List<String> listFC = Arrays.asList(UN, I, DU, ECHE, IYER, TEI, IYEN, UU, YUGEN,
						DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listFC;

			} else { // Gender NEUTRAL
				List<String> listNC = Arrays.asList(ON, I, DO, ACHA, IYAR, TAI, IYAN, OO, YOGAN,
						DAGAN, ACHAGAN, TAIGAN, OD, NOGOD, NAR, UN, DU, ECHE, IYER, TEI, IYEN, UU,
						YUGEN, DEGEN, ECHEGEN, TEIGEN, UD, NUGUD, NER);

				initialList = listNC;

			}
			break;
		default:
		}

		for (String item : initialList) {
			if (item.startsWith(currentInputWord)) {
				returnList.add(NNBS + item);
			}
		}

		return returnList;
	}

	private void updateLvSuggestions() {

		// The purpose of this method is to do a new query of the db
		// to see which words start with the string before the cursor.
		// Then update the listview with those words

		// Get current word/input string
		String currentInputWord = mCallback.getWordBeforeCursor();

		if (!TextUtils.isEmpty(currentInputWord)) {

			if (lvSuggestions.getAdapter() == null) {

				lvSuggestions.setAdapter(cursorAdapter);
			}

			Bundle bundle = new Bundle();
			bundle.putString("query", currentInputWord);
			getLoaderManager().restartLoader(WORDS_LOADER_ID, bundle, this);
		}

	}

	private void updateLvFollowing(String parentWord, String followingWordListString) {

		// The purpose of this method is to fill the listview with the list of
		// following words.

		// error checking on string
		if (TextUtils.isEmpty(followingWordListString)) {
			lvSuggestions.setAdapter(null);
			return;
		}

		// Now the listview needs to be cleared
		// getLoaderManager().destroyLoader(WORDS_LOADER_ID);

		// This is the one place I should update this variable
		// (Also have to do it in QueryFollowingAndUpdateLv)
		suggestionsParent = parentWord;

		// Clear any previous words in list
		suggestionsUnicode.clear();

		// wordlist string to list array
		// Convert mongolToFont = new Convert();
		String[] followingSplit = followingWordListString.split(",");
		List<String> followingArray = new ArrayList<String>();
		for (String item : followingSplit) {
			suggestionsUnicode.add(item); // separate unicode list
			followingArray.add(converter.unicodeToGlyphs(item));
		}

		// set the listview adapter and bind to listview
		Context appContext = getActivity().getApplicationContext();
		CustomListViewAdapter lvAdapter = new CustomListViewAdapter(appContext, followingArray);
		lvSuggestions.setAdapter(lvAdapter);

	}

	private void updateLvSuffix(String parentWord, String suffixStart) {

		// The purpose of this method is to fill the listview with the list of
		// NNBS suffix words.

		// error checking on string
		if (TextUtils.isEmpty(suffixStart)) {
			lvSuggestions.setAdapter(null);
			return;
		}

		// This is the one place I should update this variable
		// (Also have to do it in QueryFollowingAndUpdateLv)
		suggestionsParent = parentWord;

		// Clear any previous words in list
		suggestionsUnicode.clear();

		// wordlist string to list array
		// Convert mongolToFont = new Convert();
		List<String> mongolSuffixArray = new ArrayList<String>();
		suggestionsUnicode = getSuffixList(suffixStart);
		for (String item : suggestionsUnicode) {
			mongolSuffixArray.add(converter.unicodeToGlyphs(item));
		}

		// set the listview adapter and bind to listview
		Context appContext = getActivity().getApplicationContext();
		CustomListViewAdapter lvAdapter = new CustomListViewAdapter(appContext, mongolSuffixArray);
		lvSuggestions.setAdapter(lvAdapter);

	}

	private void updateFvsKeys() {

		tvFvs1Top.setText("");
		tvFvs1Bottom.setText("");
		tvFvs2Top.setText("");
		tvFvs2Bottom.setText("");
		tvFvs3Top.setText("");
		tvFvs3Bottom.setText("");

		// get last few chars before cursor
		char previousChar = mCallback.getCharBeforeCursor();

		// error checking
		if (previousChar == NULL_CHAR) {
			return;
		}
		if (!MongolUnicodeRenderer.isMongolianAlphabet(previousChar)) {
			return;
		}

		// Get position
		MongolUnicodeRenderer.Location position = mCallback.getLocationOfCharInMongolWord(-1);

		// According to position do lookup
		// Update keys
		if (position == MongolUnicodeRenderer.Location.ISOLATE) {
			tvFvs1Top.setText(converter.getInitial("" + previousChar + FVS1));
			tvFvs2Top.setText(converter.getInitial("" + previousChar + FVS2));
			tvFvs3Top.setText(converter.getInitial("" + previousChar + FVS3));
			tvFvs1Bottom.setText(converter.getIsolate("" + previousChar + FVS1));
			tvFvs2Bottom.setText(converter.getIsolate("" + previousChar + FVS2));
			tvFvs3Bottom.setText(converter.getIsolate("" + previousChar + FVS3));
		} else if (position == MongolUnicodeRenderer.Location.INITIAL) {
			// TODO never gets here?
		} else if (position == MongolUnicodeRenderer.Location.MEDIAL) {
			// TODO never gets here?
		} else if (position == MongolUnicodeRenderer.Location.FINAL) {
			tvFvs1Top.setText(converter.getMedial("" + previousChar + FVS1));
			tvFvs2Top.setText(converter.getMedial("" + previousChar + FVS2));
			tvFvs3Top.setText(converter.getMedial("" + previousChar + FVS3));
			tvFvs1Bottom.setText(converter.getFinal("" + previousChar + FVS1));
			tvFvs2Bottom.setText(converter.getFinal("" + previousChar + FVS2));
			tvFvs3Bottom.setText(converter.getFinal("" + previousChar + FVS3));
		}

	}

	private void switchPunctuation() {
		if (punctuationOn) {
			tvQ.setText(getResources().getString(R.string.m_cha));
			tvQlong.setText(getResources().getString(R.string.m_chi));
			tvW.setText(getResources().getString(R.string.m_wa));
			tvWlong.setText("");
			tvE.setText(getResources().getString(R.string.m_e));
			tvElong.setText(getResources().getString(R.string.m_ee));
			tvR.setText(getResources().getString(R.string.m_ra));
			tvRlong.setText(getResources().getString(R.string.m_zra));
			tvT.setText(getResources().getString(R.string.m_ta));
			tvTlong.setText("");
			tvY.setText(getResources().getString(R.string.m_ya));
			tvYlong.setText("");
			tvU.setText(getResources().getString(R.string.m_ue));
			tvUlong.setText("");
			tvI.setText(getResources().getString(R.string.m_i));
			tvIlong.setText("");
			tvO.setText(getResources().getString(R.string.m_oe));
			tvOlong.setText("");
			tvP.setText(getResources().getString(R.string.m_pa));
			tvPlong.setText("");
			tvA.setText(getResources().getString(R.string.m_a));
			tvS.setText(getResources().getString(R.string.m_sa));
			tvD.setText(getResources().getString(R.string.m_da));
			tvF.setText(getResources().getString(R.string.m_fa));
			tvG.setText(getResources().getString(R.string.m_ga));
			tvH.setText(getResources().getString(R.string.m_qa));
			tvJ.setText(getResources().getString(R.string.m_ja));
			tvK.setText(getResources().getString(R.string.m_ka));
			tvL.setText(getResources().getString(R.string.m_la));
			tvNg.setText(getResources().getString(R.string.m_ang));
			tvZ.setText(getResources().getString(R.string.m_za));
			tvX.setText(getResources().getString(R.string.m_sha));
			tvC.setText(getResources().getString(R.string.m_o));
			tvV.setText(getResources().getString(R.string.m_u));
			tvB.setText(getResources().getString(R.string.m_ba));
			tvN.setText(getResources().getString(R.string.m_na));
			tvM.setText(getResources().getString(R.string.m_ma));

			// show long touch views
			tvHlong.setVisibility(View.VISIBLE);
			tvJlong.setVisibility(View.VISIBLE);
			tvLlong.setVisibility(View.VISIBLE);
			tvZlong.setVisibility(View.VISIBLE);

			tvCaseSuffix.setText(getResources().getString(R.string.m_key_case_suffix));

			tvInput.setVisibility(View.VISIBLE);
			tvInputMongol.setVisibility(View.GONE);

		} else { // punctuation is not on
			tvQ.setText(getResources().getString(R.string.m_key_p_1));
			tvQlong.setText(getResources().getString(R.string.m_key_p_mongol_1));
			tvW.setText(getResources().getString(R.string.m_key_p_2));
			tvWlong.setText(getResources().getString(R.string.m_key_p_mongol_2));
			tvE.setText(getResources().getString(R.string.m_key_p_3));
			tvElong.setText(getResources().getString(R.string.m_key_p_mongol_3));
			tvR.setText(getResources().getString(R.string.m_key_p_4));
			tvRlong.setText(getResources().getString(R.string.m_key_p_mongol_4));
			tvT.setText(getResources().getString(R.string.m_key_p_5));
			tvTlong.setText(getResources().getString(R.string.m_key_p_mongol_5));
			tvY.setText(getResources().getString(R.string.m_key_p_6));
			tvYlong.setText(getResources().getString(R.string.m_key_p_mongol_6));
			tvU.setText(getResources().getString(R.string.m_key_p_7));
			tvUlong.setText(getResources().getString(R.string.m_key_p_mongol_7));
			tvI.setText(getResources().getString(R.string.m_key_p_8));
			tvIlong.setText(getResources().getString(R.string.m_key_p_mongol_8));
			tvO.setText(getResources().getString(R.string.m_key_p_9));
			tvOlong.setText(getResources().getString(R.string.m_key_p_mongol_9));
			tvP.setText(getResources().getString(R.string.m_key_p_0));
			tvPlong.setText(getResources().getString(R.string.m_key_p_mongol_0));
			tvA.setText(getResources().getString(R.string.m_key_p_top_paranthesis));
			tvS.setText(getResources().getString(R.string.m_key_p_bottom_paranthesis));
			tvD.setText(getResources().getString(R.string.m_key_p_top_single_quote));
			tvF.setText(getResources().getString(R.string.m_key_p_bottom_single_quote));
			tvG.setText(getResources().getString(R.string.m_key_p_top_double_quote));
			tvH.setText(getResources().getString(R.string.m_key_p_bottom_double_quote));
			tvJ.setText(getResources().getString(R.string.m_key_p_question_exclamation));
			tvK.setText(getResources().getString(R.string.m_key_p_exclamation_question));
			tvL.setText(getResources().getString(R.string.m_key_p_exclamation_exclamation));
			tvNg.setText(getResources().getString(R.string.m_key_p_colon));
			tvZ.setText(getResources().getString(R.string.m_key_p_ellipsis));
			tvX.setText(getResources().getString(R.string.m_key_p_four_dots));
			tvC.setText(getResources().getString(R.string.m_key_p_dot));
			tvV.setText(getResources().getString(R.string.m_key_p_full_stop));
			tvB.setText(getResources().getString(R.string.m_key_niguru));
			tvN.setText(getResources().getString(R.string.m_key_p_dash));
			tvM.setText(getResources().getString(R.string.m_key_p_semicolon));

			// hide long touch views
			tvHlong.setVisibility(View.INVISIBLE);
			tvJlong.setVisibility(View.INVISIBLE);
			tvLlong.setVisibility(View.INVISIBLE);
			tvZlong.setVisibility(View.INVISIBLE);

			tvFvs1Top.setText("");
			tvFvs1Bottom.setText("");
			tvFvs2Top.setText("");
			tvFvs2Bottom.setText("");
			tvFvs3Top.setText("");
			tvFvs3Bottom.setText("");

			tvInput.setVisibility(View.GONE);
			tvInputMongol.setVisibility(View.VISIBLE);

		}

	}

	// call: new AddOrUpdateDictionaryWordsTask().execute(word, previousWord);
	private class AddOrUpdateDictionaryWordsTask extends AsyncTask<String, Void, Void> {

		// AsyncTask<Params, Progress, Result>.
		// Params – the input. what you pass to the AsyncTask
		// Progress – if you have any updates, passed to onProgressUpdate()
		// Result – the output. what returns doInBackground()

		Context context = getActivity().getApplicationContext();

		@Override
		protected Void doInBackground(String... params) {

			// android.os.Debug.waitForDebugger();

			// get the word
			String word = params[0];
			String previousWord = params[1];

			// check db for word
            ContentResolver resolver;
            if (getActivity()!=null){
                resolver = getActivity().getContentResolver();
            }else{
                return null;
            }
			String[] projection = new String[] { BaseColumns._ID, ChimeeUserDictionary.Words.WORD,
					ChimeeUserDictionary.Words.FREQUENCY };
			String selection = ChimeeUserDictionary.Words.WORD + "=?";
			String[] selectionArgs = { word };

			Cursor cursor = null;
			try {

				cursor = resolver.query(ChimeeUserDictionary.Words.CONTENT_URI, projection,
						selection, selectionArgs, null);

				// if exists then increment frequency,
				if (cursor.moveToNext()) {

					// Get word id from cursor

					long id = cursor.getLong(cursor.getColumnIndex(ChimeeUserDictionary.Words._ID));
					int frequency = cursor.getInt(cursor
							.getColumnIndex(ChimeeUserDictionary.Words.FREQUENCY));
					frequency++;

					// Update word
					ChimeeUserDictionary.Words.updateWord(context, id, frequency, null);

				} else {
					// add word

					ChimeeUserDictionary.Words.addWord(context, word, 1, null);

				}

			} catch (Exception e) {
				// Log.e("Chimee AsyncTask", e.toString());
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			// Change following of previous word

			if (TextUtils.isEmpty(previousWord)) {
				return null;
			}

			projection = new String[] { BaseColumns._ID, ChimeeUserDictionary.Words.WORD,
					ChimeeUserDictionary.Words.FOLLOWING };
			selectionArgs[0] = previousWord;

			// get previous word
			Cursor anotherCursor = null;
			try {

				anotherCursor = resolver.query(ChimeeUserDictionary.Words.CONTENT_URI, projection,
						selection, selectionArgs, null);

				// if exists then update following,
				if (anotherCursor.moveToNext()) {

					// Get word id from cursor

					long id = anotherCursor.getLong(anotherCursor
							.getColumnIndex(ChimeeUserDictionary.Words._ID));
					String following = anotherCursor.getString(anotherCursor
							.getColumnIndex(ChimeeUserDictionary.Words.FOLLOWING));

					// stick thisWord into following
					following = reorderFollowing(word, following);

					// Update word
					ChimeeUserDictionary.Words.updateWord(context, id, -1, following);

				}

			} catch (Exception e) {
				// Log.e("Chimee AsyncTask Following", e.toString());
			} finally {
				if (anotherCursor != null) {
					anotherCursor.close();
				}
			}

			return null;
		}

		private String reorderFollowing(String wordToAdd, String following) {

			if (TextUtils.isEmpty(following)) {
				return wordToAdd;
			} else {
				String[] followingSplit = following.split(",");
				StringBuilder builder = new StringBuilder();
				builder.append(wordToAdd);
				int counter = 0;
				for (String item : followingSplit) {
					if (!item.equals(wordToAdd)) {
						builder.append(",").append(item);
					}
					counter++;
					if (counter >= MAX_FOLLOWING_WORDS)
						break;
				}
				return builder.toString();
			}
		}

	}

	// call with: new incrementWordFrequencyTask(rowId, frequency).execute();
	private class IncrementWordFrequencyTask extends AsyncTask<Void, Void, Void> {

		long rowId;
		int frequency;
		private Context context = getActivity().getApplicationContext();

		public IncrementWordFrequencyTask(long rowId, int frequency) {
			this.rowId = rowId;
			this.frequency = frequency;
		}

		@Override
		protected Void doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			// Increment frequency
            if (context!=null){
                ChimeeUserDictionary.Words.updateFrequency(context, rowId, frequency + 1);
            }

			return null;
		}

	}

	// call with: new UpdateFollowingWordTask().execute(word,
	// followingWordToAdd);
	private class UpdateFollowingWordTask extends AsyncTask<String, Void, Void> {

		private Context context = getActivity().getApplicationContext();

		@Override
		protected Void doInBackground(String... params) {

			// android.os.Debug.waitForDebugger();

			// get the word
			String word = params[0];
			String followingWordList = params[1];

			// Update following
            if (context!=null){
                ChimeeUserDictionary.Words.addFollowing(context, word, followingWordList);
            }

			return null;
		}

	}

	// call with: new DeleteWordTask().execute(word);
	private class DeleteWordByIdTask extends AsyncTask<Long, Void, Integer> {

		private Context context = getActivity().getApplicationContext();

		@Override
		protected Integer doInBackground(Long... params) {

			// android.os.Debug.waitForDebugger();

			// get the word
			long wordId = params[0];

			// Delete word
            int count = 0;
            if (context!=null){
                count = ChimeeUserDictionary.Words.deleteWord(context, wordId);
            }

			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {

			// This is the result from doInBackground

			if (count > 0) {
				// Notify the user that the word was deleted
				showToast(context, getResources().getString(R.string.word_deleted),
						Toast.LENGTH_SHORT);
			}
		}
	}

	// call with: new DeleteFollowingWordTask().execute(word, following);
	private class DeleteFollowingWordTask extends AsyncTask<String, Void, Integer> {

		private Context context = getActivity().getApplicationContext();

		@Override
		protected Integer doInBackground(String... params) {

			// android.os.Debug.waitForDebugger();

			// get the words
			String word = params[0];
			String following = params[1];

			// Udpate word
            int count = 0;
            if (context!=null){
                count = ChimeeUserDictionary.Words.updateFollowing(context, word, following);
            }

			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {

			// This is the result from doInBackground

			if (count > 0) {
				// Notify the user that the word was deleted
				showToast(context, getResources().getString(R.string.word_deleted),
						Toast.LENGTH_SHORT);
			}
		}
	}

	// call with: new QueryFollowingAndUpdateLV().execute(unicodeString)
	private class QueryFollowingAndUpdateLV extends AsyncTask<String, Void, String> {

		private Context context = getActivity().getApplicationContext();
		String word;

		@Override
		protected String doInBackground(String... params) {

			// android.os.Debug.waitForDebugger();

			// get the word
			word = params[0];

			// Query db to see if exists
			Cursor cursor = ChimeeUserDictionary.Words.queryWord(context, word);

			// If so then update then send results to UI and update LV
			String following = "";
			if (cursor.moveToNext()) {
				following = cursor.getString(cursor
						.getColumnIndex(ChimeeUserDictionary.Words.FOLLOWING));
			}
			cursor.close();

			return following;
		}

		@Override
		protected void onPostExecute(String result) {

			// This is the result from doInBackground

			// Check if it is not too late to update LV
			// How do I know?
			// isFollowing = true
			// parent word is still the same
			if (isFollowing || isSuffix) {
				if (TextUtils.isEmpty(result)) {
					lvSuggestions.setAdapter(null);
				} else {
					updateLvFollowing(word, result);
				}
				isSuffix = false;
				isFollowing = true;
			} else {
				// do nothing. You just wasted your time.
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {

		// when the listview is clicked the word should be added to the input
		// window

		if (isFollowing) {

			String unicodeString = suggestionsUnicode.get(position);

			// Add the following string to the input window
			mCallback.replaceFromWordStartToCursor(unicodeString);
			// mCallback.addString(unicodeString + " ");

			// reorder list if not first choice
			if (position > 0) {
				new UpdateFollowingWordTask().execute(suggestionsParent, unicodeString);
			}

			// check if there are any following words for this one
			new QueryFollowingAndUpdateLV().execute(unicodeString);

		} else if (isSuffix) {

			String unicodeString = suggestionsUnicode.get(position);

			// Add the following string to the input window
			mCallback.replaceFromWordStartToCursor(unicodeString);
			// isSuffix=false;

			// add this word to dictionary and to following of previous
			// word
			new AddOrUpdateDictionaryWordsTask()
					.execute(unicodeString, mCallback.getPreviousWord());

			// check if there are any following words for this one
			new QueryFollowingAndUpdateLV().execute(unicodeString);

		} else {

			// Get info from the cursor
			Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
			cursor.moveToPosition(position);
			String unicodeString = cursor.getString(cursor
					.getColumnIndex(ChimeeUserDictionary.Words.WORD));
			int frequency = cursor.getInt(cursor
					.getColumnIndex(ChimeeUserDictionary.Words.FREQUENCY));
			String following = cursor.getString(cursor
					.getColumnIndex(ChimeeUserDictionary.Words.FOLLOWING));

			mCallback.replaceFromWordStartToCursor(unicodeString);

			// Clear the listview
			getLoaderManager().destroyLoader(WORDS_LOADER_ID);

			// if not first on list then increment word frequency
			if (position > 0) {
				new IncrementWordFrequencyTask(rowId, frequency).execute();
			}

			// Add chosen word to following of previous word
			String previousWord = mCallback.getPreviousWord();
			if (!TextUtils.isEmpty(previousWord)) {
				new UpdateFollowingWordTask().execute(previousWord, unicodeString);
			}

			// Show following word suggestions for this word
			updateLvFollowing(unicodeString, following);
			isFollowing = true;

		}

	}

	// Delete word
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {

		// When a listview item is long clicked then it should be deleted

		if (isFollowing) { // Remove word from following list

			// String followingWord = suggestionsUnicode.get(position);
			suggestionsUnicode.remove(position);
			StringBuilder followingConcat = new StringBuilder();
			if (suggestionsUnicode.size() > 0) {
				followingConcat.append(suggestionsUnicode.get(0));
				for (int i = 1; i < suggestionsUnicode.size(); i++) {
					followingConcat.append(",").append(suggestionsUnicode.get(i));
				}
			} else {
				followingConcat.append("");
			}

			// Delete word
			new DeleteFollowingWordTask().execute(suggestionsParent, followingConcat.toString());

			// Update LV
			updateLvFollowing(suggestionsParent, followingConcat.toString());

		} else if (isSuffix) {

			// Suffixes cannot be deleted because the come from hard coded array
			showToast(getActivity().getApplicationContext(),
					getResources().getString(R.string.cant_delete_suffix), Toast.LENGTH_LONG);

		} else { // remove word from db

			// Deleate word
			new DeleteWordByIdTask().execute(rowId);

		}

		return true;
	}

	private void showToast(Context context, String text, int toastLength) {

		// TextView
		final float scale = getResources().getDisplayMetrics().density;
		int padding_8dp = (int) (8 * scale + 0.5f);
		MongolTextView tvMongolToastMessage = new MongolTextView(context);
		tvMongolToastMessage.setText(text);
		tvMongolToastMessage.setPadding(padding_8dp, padding_8dp, padding_8dp, padding_8dp);
		tvMongolToastMessage.setTextColor(getResources().getColor(R.color.white));

		// Layout
		LinearLayout toastLayout = new LinearLayout(context);
		toastLayout.setBackgroundResource(R.color.black_c);
		toastLayout.addView(tvMongolToastMessage);

		// Toast
		Toast mongolToast = new Toast(context);
		mongolToast.setView(toastLayout);
		mongolToast.setDuration(toastLength);
		mongolToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		mongolToast.show();

	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {

		// The loader's job is to query the database for the words that start
		// with the correct string

		String[] projection;
		String selection;
		String[] selectionArgs = { "" };
		String orderBy;

		switch (loaderID) {
		case WORDS_LOADER_ID:

			projection = new String[] { ChimeeUserDictionary.Words._ID,
					ChimeeUserDictionary.Words.WORD, ChimeeUserDictionary.Words.FREQUENCY,
					ChimeeUserDictionary.Words.FOLLOWING };
			selection = ChimeeUserDictionary.Words.WORD + " LIKE ?";
			selectionArgs[0] = bundle.getString("query") + "%";
			orderBy = ChimeeUserDictionary.Words.DEFAULT_SORT_ORDER;

			return new CursorLoader(getActivity().getApplicationContext(),
					ChimeeUserDictionary.Words.CONTENT_URI, projection, selection, selectionArgs,
					orderBy);

		default:
			// Log.e("Chimee", "An invalid id was passed in");
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor newCursor) {
		cursorAdapter.swapCursor(newCursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		cursorAdapter.swapCursor(null);
	}

	// static variables here for readability
	protected static final char NULL_CHAR = '\u0000';
	protected static final char BACKSPACE = '\u232b';
	protected static final char SPACE = ' ';
	protected static final char NEW_LINE = '\n';
	protected static final char ZWJ = MongolUnicodeRenderer.ZWJ;// ZeroWidthJoiner
	protected static final char NNBS = MongolUnicodeRenderer.NNBS;// NarrowNonBreakingSpace
	protected static final char FVS1 = MongolUnicodeRenderer.FVS1;// FreeVariationSelector
	protected static final char FVS2 = MongolUnicodeRenderer.FVS2;
	protected static final char FVS3 = MongolUnicodeRenderer.FVS3;
	protected static final char MVS = MongolUnicodeRenderer.MVS;// VOWEL SEPARATOR
	protected static final char MONGOLIAN_DOT = '\u00b7';
	protected static final char MONGOLIAN_DASH = '\ufe31';
	protected static final char PUNCTUATION_QUESTION_EXCLAMATION = '\u2048';
	protected static final char PUNCTUATION_EXCLAMATION_QUESTION = '\u2049';
	protected static final char PUNCTUATION_EXCLAMATION_EXCLAMATION = '\u203c';
	protected static final char PUNCTUATION_DOUBLEQUOTE_TOP = '\u00ab';
	protected static final char PUNCTUATION_DOUBLEQUOTE_BOTTOM = '\u00bb';
	protected static final char PUNCTUATION_MONGOL_PAREN_TOP = '\u005b';
	protected static final char PUNCTUATION_MONGOL_PAREN_BOTTOM = '\u005d';

	// These are the suffixes
	protected static String YIN;
	protected static String ON;
	protected static String UN;
	protected static String O;
	protected static String U;
	protected static String I;
	protected static String YI;
	protected static String DO;
	protected static String DU;
	protected static String TO;
	protected static String TU;
	protected static String ACHA;
	protected static String ECHE;
	protected static String BAR;
	protected static String BER;
	protected static String IYAR;
	protected static String IYER;
	protected static String TAI;
	protected static String TEI;
	protected static String IYAN;
	protected static String IYEN;
	protected static String BAN;
	protected static String BEN;
	protected static String OO;
	protected static String UU;
	protected static String YOGAN;
	protected static String YUGEN;
	protected static String DAGAN;
	protected static String DEGEN;
	protected static String TAGAN;
	protected static String TEGEN;
	protected static String ACHAGAN;
	protected static String ECHEGEN;
	protected static String TAIGAN;
	protected static String TEIGEN;
	protected static String OD;
	protected static String UD;
	protected static String NOGOD;
	protected static String NUGUD;
	protected static String NAR;
	protected static String NER;
}
