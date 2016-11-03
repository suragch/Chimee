package net.studymongolian.chimee;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// This fragment holds the suggestion bar and a container for the keyboard
// They keyboards are loaded as subfragments
public class KeyboardController extends Fragment implements Keyboard.OnKeyboardListener, SuggestionsAdapter.ItemClickListener {

    private OnKeyboardControllerListener mListener;

    private RecyclerView rvSuggestions;
    private SuggestionsAdapter suggestionsAdapter;

    //SimpleCursorAdapter cursorAdapter; // adapter for db words
    MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
    //ListView lvSuggestions;
    boolean isFollowing = false; // db id of word whose followings are in lv
    boolean typingMongol = false;
    boolean isSuffix = false;
    String suggestionsParent = ""; // keep track of parent of following list
    //List<String> suggestionsUnicode = new ArrayList<String>(); // following

    protected static final int WORDS_LOADER_ID = 0;
    protected static final int MIN_DICTIONARY_WORD_LENGTH = 2;
    protected static final int MAX_FOLLOWING_WORDS = 10;
    protected static final int MAX_SUGGESTED_WORDS = 10;
    protected static final char SWITCH_TO_ENGLISH = 'α'; // arbitrary symbol
    protected static final char PUNCTUATION_KEY = 'γ'; // arbitrary symbol
    private static final int FVS_REQUEST = 10;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_keyboard_controller, container, false);
        rvSuggestions = (RecyclerView) layout.findViewById(R.id.rvSuggestions);
        suggestionsAdapter = new SuggestionsAdapter(getActivity(), new ArrayList<String>());
        suggestionsAdapter.setClickListener(this);
        rvSuggestions.setAdapter(suggestionsAdapter);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

//    public static List<String> getData() {
//        List<String> data = new ArrayList<>();
//        String[] words = {"this", "is", "a", "test", "for", "you", "to", "try"};
//        for (int i = 0; i < words.length; i++) {
//            data.add(words[i]);
//        }
//        return data;
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        Fragment childFragment = new KeyboardAeiou();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.keyboard_container_frame, childFragment).commit();

//        Fragment childFragment = new KeyboardQwerty();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.keyboard_container_frame, childFragment).commit();

        Fragment childFragment = new KeyboardEnglish();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.keyboard_container_frame, childFragment).commit();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKeyboardControllerListener) {
            mListener = (OnKeyboardControllerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "onItemClick: " + position);

        // insert word into input window
        String unicodeString = suggestionsAdapter.getItem(position);
        mListener.replaceCurrentWordWith(unicodeString);

        // update suggestion bar with following words
        new QueryFollowingAndUpdateSuggestionBar().execute(unicodeString);

        // update frequency of clicked word and add it to the previous word's following
        saveWord();
        mListener.keyWasTapped(' ');

        // update frequency in suffix database
        if (unicodeString.charAt(0)==MongolUnicodeRenderer.Uni.NNBS) {
            new UpdateSuffixFrequency().execute(unicodeString);
        }

    }

    // Keyboard callback methods

    @Override
    public void keySuffix() {
        Log.i("TAG", "keySuffix: ");

        if (mListener.getCharBeforeCursor() == ' ') {
            mListener.keyBackspace();
        } else {
            saveWord();
        }
        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.NNBS);

        // update suggestion bar
        updateSuggestionBarWithSuffixList(String.valueOf(MongolUnicodeRenderer.Uni.NNBS));
    }

    @Override
    public void keyWasTapped(char character) {

        // FIXME delete testing
        // BEGIN TESTING

//        if (character == MongolUnicodeRenderer.Uni.CHI) {
//            // print all words in db
//            printAllWords();
//            return;
//        }

        // END TESTING




        if (character == ' ' || character == '\n' || character == '!' || character == '?' ||
                character == MongolUnicodeRenderer.Uni.MONGOLIAN_COMMA ||
                character == MongolUnicodeRenderer.Uni.MONGOLIAN_FULL_STOP) {
            saveWord();
        }

        Log.i("TAG", "keyWasTapped: ");
        mListener.keyWasTapped(character);

        // update suggestion bar
        String currentWord = mListener.oneMongolWordBeforeCursor();
        if (TextUtils.isEmpty(currentWord)) {
            suggestionsAdapter.clear();
            return;
        }
        if (currentWord.charAt(0) == MongolUnicodeRenderer.Uni.NNBS) {
            updateSuggestionBarWithSuffixList(currentWord);
        } else {
            new QueryPrefixAndUpdateSuggestionBar().execute(currentWord);
        }

    }

    @Override
    public void keyBackspace() {
        mListener.keyBackspace();
        Log.i("TAG", "keyBackspace: ");
        if (suggestionsAdapter.getItemCount()!=0) {
            suggestionsAdapter.clear();
        }
    }

    @Override
    public void keyMvs() {
        Log.i("TAG", "keyMvs: ");

        // input MVS
        keyWasTapped(MongolUnicodeRenderer.Uni.MVS);

        // Add A or E depending on word gender
        if (renderer.isMasculineWord(mListener.oneMongolWordBeforeCursor())) {
            keyWasTapped(MongolUnicodeRenderer.Uni.A);
        } else  {
            // Unknown gender words (I) are assumed to be feminine
            keyWasTapped(MongolUnicodeRenderer.Uni.E);
        }

        // Add a space automatically (this will also save the word
        keyWasTapped(' ');
    }

    @Override
    public void keyNewKeyboardChosen(KeyboardType type) {

    }

    @Override
    public char getCharBeforeCursor() {
        return mListener.getCharBeforeCursor();
    }


    // Interface for Activity communication

    public interface OnKeyboardControllerListener {
        void keyWasTapped(char character);
        //void keyNnbs();
        //void keyMvs();
        void keyBackspace();
        char getCharBeforeCursor();
        String oneMongolWordBeforeCursor();
        String secondMongolWordsBeforeCursor();
        void replaceCurrentWordWith(String replacementWord);
        //void replaceFromWordStartToCursor(String replacementWord);
    }

    // Suggestions bar

    public void updateSuggestionBarWithSuffixList(String suffixPrefix) {

        // get current suffix start and previous word
        //String suffixStart = mListener.oneMongolWordBeforeCursor();

        // error checking on string
        if (TextUtils.isEmpty(suffixPrefix)) {
            suggestionsAdapter.clear();
            return;
        }

        String previousWord = mListener.secondMongolWordsBeforeCursor();
        Suffix.WordEnding ending = getEndingOf(previousWord);
        Suffix.WordGender gender;
        if (ending == Suffix.WordEnding.Nil) {
            gender = getGenderOf(suffixPrefix);
        } else {
            gender = getGenderOf(previousWord);
        }

        // query db and update suggestion bar

        new QuerySuffixesAndUpdateSuggestionBar(suffixPrefix, gender, ending).execute();


//        let qualityOfServiceClass = QOS_CLASS_BACKGROUND
//        let backgroundQueue = dispatch_get_global_queue(qualityOfServiceClass, 0)
//        dispatch_async(backgroundQueue, {
//
//                // lookup words in suffix list that start with word before cursor
//                var suggestionList: [String] = []
//
//        do {
//            suggestionList = try SuffixListDataHelper.findSuffixesBeginningWith(suffixStart, withGender: gender, andEnding: ending)
//        } catch _ {
//            print("query for suggestions failed")
//        }
//
//        // update suggestion bar with those words
//        dispatch_async(dispatch_get_main_queue(), { () -> Void in
//
//                self.suggestedWords = suggestionList
//                self.suggestionBarTable?.reloadData()
//        })
//
//        })


    }

    private Suffix.WordGender getGenderOf(String word) {

        Suffix.WordGender gender = Suffix.WordGender.Neutral;

        if (renderer.isMasculineWord(word)) {
            gender = Suffix.WordGender.Masculine;
        } else if (renderer.isFeminineWord(word)) {
            gender = Suffix.WordGender.Feminine;
        }

        return gender;
    }

    private Suffix.WordEnding getEndingOf(String word) {

        Suffix.WordEnding ending = Suffix.WordEnding.Nil;

        if (TextUtils.isEmpty(word)) {
            return ending;
        }

        // determine ending character
        char endingChar = word.charAt(word.length() - 1);
        if (endingChar == MongolUnicodeRenderer.Uni.FVS1 ||
                endingChar == MongolUnicodeRenderer.Uni.FVS2 ||
                endingChar == MongolUnicodeRenderer.Uni.FVS3) {
            if (word.length() > 1) {
                endingChar = word.charAt(word.length() - 2);
            } else {
                return ending;
            }
        }

        // determine type
        if (renderer.isVowel(endingChar)) {
            ending = Suffix.WordEnding.Vowel;
        } else if (renderer.isConsonant(endingChar)) {
            if (endingChar == MongolUnicodeRenderer.Uni.NA) {
                ending = Suffix.WordEnding.N;
            } else if (renderer.isBGDRS(endingChar)) {
                ending = Suffix.WordEnding.BigDress;
            } else {
                ending = Suffix.WordEnding.OtherConsonant;
            }
        }

        return ending;
    }



    // Toast helper

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

    // AsyncTasks

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

            ContentResolver resolver;
            if (getActivity()!=null){
                resolver = getActivity().getContentResolver();
            }else{
                return null;
            }


            // check db for word

            String[] projection = new String[]{BaseColumns._ID, ChimeeUserDictionary.Words.WORD,
                    ChimeeUserDictionary.Words.FREQUENCY};
            String selection = ChimeeUserDictionary.Words.WORD + "=?";
            String[] selectionArgs = new String[]{word};

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
            selectionArgs = new String[] { previousWord };

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

    // call with: new QueryPrefixAndUpdateSuggestionBar().execute(unicodeString)
    private class QueryPrefixAndUpdateSuggestionBar extends AsyncTask<String, Void, List<String>> {

        private Context context = getActivity().getApplicationContext();
        String word;

        @Override
        protected List<String> doInBackground(String... params) {

            // android.os.Debug.waitForDebugger();

            // get the word
            word = params[0];

            // Query db to see if exists
            Cursor cursor = ChimeeUserDictionary.Words.queryPrefix(context, word);

            List<String> matches = new ArrayList<String>();
            int columnIndex = cursor.getColumnIndex(ChimeeUserDictionary.Words.WORD);
            int counter = 0;
            while (cursor.moveToNext() && counter <= MAX_SUGGESTED_WORDS) {
                matches.add(cursor.getString(columnIndex));
                counter++;
            }
            cursor.close();

            return matches;
        }

        @Override
        protected void onPostExecute(List<String> matches) {
            // update suggestion bar
            suggestionsAdapter.swap(matches);
        }
    }

    // call with: new QueryFollowingAndUpdateSuggestionBar().execute(unicodeString)
    private class QueryFollowingAndUpdateSuggestionBar extends AsyncTask<String, Void, String> {

        private Context context = getActivity().getApplicationContext();
        String word;

        @Override
        protected String doInBackground(String... params) {

            // android.os.Debug.waitForDebugger();

            // get the word
            word = params[0];

            // Query db to see if exists
            Cursor cursor = ChimeeUserDictionary.Words.queryWord(context, word);

            // If so then update then send results to UI and update suggestion bar
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

            List<String> list = Arrays.asList(result.split(","));

            // update suggestion bar
            suggestionsAdapter.swap(list);

            // TODO do I need to update following order?

        }

    }

    // call with: new QuerySuffixesAndUpdateSuggestionBar().execute(unicodeString)
    private class UpdateSuffixFrequency extends AsyncTask<String, Void, Void> {


        private Context context = getActivity().getApplicationContext();


        @Override
        protected Void doInBackground(String... params) {

            String suffix = params[0];

            // Query db to see if exists
            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(context);
            adapter.updateFrequencyForSuffix(suffix);

            return null;
        }

    }


    // call with: new QuerySuffixesAndUpdateSuggestionBar().execute(unicodeString)
    private class QuerySuffixesAndUpdateSuggestionBar extends AsyncTask<Void, Void, List<String>> {

        private String searchPrefix;
        private Suffix.WordGender gender;
        private Suffix.WordEnding ending;

        private Context context = getActivity().getApplicationContext();
        //String word;

        // constructor
        public QuerySuffixesAndUpdateSuggestionBar(String searchPrefix, Suffix.WordGender gender, Suffix.WordEnding ending) {
            this.searchPrefix = searchPrefix;
            this.gender = gender;
            this.ending = ending;
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            // Query db to see if exists
            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(context);
            List<String> suffixes = adapter.findSuffixesBeginningWith(searchPrefix, gender, ending);

            return suffixes;
        }


        @Override
        protected void onPostExecute(List<String> result) {
            // update suggestion bar
            suggestionsAdapter.swap(result);

        }

    }


    private class PrintAllWordsTask extends AsyncTask<Void, Void, Void> {

        private Context context = getActivity().getApplicationContext();


        @Override
        protected Void doInBackground(Void... params) {

            // Query db to see if exists
            String result = ChimeeUserDictionary.Words.getAllWords(context);
            Log.i("TAG", result);
            return null;

        }


    }
//
//    // call with: new incrementWordFrequencyTask(rowId, frequency).execute();
//    private class IncrementWordFrequencyTask extends AsyncTask<Void, Void, Void> {
//
//        long rowId;
//        int frequency;
//        private Context context = getActivity().getApplicationContext();
//
//        public IncrementWordFrequencyTask(long rowId, int frequency) {
//            this.rowId = rowId;
//            this.frequency = frequency;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            // android.os.Debug.waitForDebugger();
//
//            // Increment frequency
//            if (context!=null){
//                ChimeeUserDictionary.Words.updateFrequency(context, rowId, frequency + 1);
//            }
//
//            return null;
//        }
//
//    }
//
//    // call with: new UpdateFollowingWordTask().execute(word, followingWordToAdd);
//    private class UpdateFollowingWordTask extends AsyncTask<String, Void, Void> {
//
//        private Context context = getActivity().getApplicationContext();
//
//        @Override
//        protected Void doInBackground(String... params) {
//
//            // android.os.Debug.waitForDebugger();
//
//            // get the word
//            String word = params[0];
//            String followingWordList = params[1];
//
//            // Update following
//            if (context!=null){
//                ChimeeUserDictionary.Words.addFollowing(context, word, followingWordList);
//            }
//
//            return null;
//        }
//
//    }
//
//    // call with: new DeleteWordTask().execute(word);
//    private class DeleteWordByIdTask extends AsyncTask<Long, Void, Integer> {
//
//        private Context context = getActivity().getApplicationContext();
//
//        @Override
//        protected Integer doInBackground(Long... params) {
//
//            // android.os.Debug.waitForDebugger();
//
//            // get the word
//            long wordId = params[0];
//
//            // Delete word
//            int count = 0;
//            if (context!=null){
//                count = ChimeeUserDictionary.Words.deleteWord(context, wordId);
//            }
//
//            return count;
//        }
//
//        @Override
//        protected void onPostExecute(Integer count) {
//
//            // This is the result from doInBackground
//
//            if (count > 0) {
//                // Notify the user that the word was deleted
//                showToast(context, getResources().getString(R.string.word_deleted),
//                        Toast.LENGTH_SHORT);
//            }
//        }
//    }
//
//    // call with: new DeleteFollowingWordTask().execute(word, following);
//    private class DeleteFollowingWordTask extends AsyncTask<String, Void, Integer> {
//
//        private Context context = getActivity().getApplicationContext();
//
//        @Override
//        protected Integer doInBackground(String... params) {
//
//            // android.os.Debug.waitForDebugger();
//
//            // get the words
//            String word = params[0];
//            String following = params[1];
//
//            // Udpate word
//            int count = 0;
//            if (context!=null){
//                count = ChimeeUserDictionary.Words.updateFollowing(context, word, following);
//            }
//
//            return count;
//        }
//
//        @Override
//        protected void onPostExecute(Integer count) {
//
//            // This is the result from doInBackground
//
//            if (count > 0) {
//                // Notify the user that the word was deleted
//                showToast(context, getResources().getString(R.string.word_deleted),
//                        Toast.LENGTH_SHORT);
//            }
//        }
//    }



    private void saveWord() {

        // This method saves the word before the cursor and also
        // adds it to the following list of the previous word

        String thisWord = mListener.oneMongolWordBeforeCursor();
        String previousWord = mListener.secondMongolWordsBeforeCursor();
        if (!TextUtils.isEmpty(previousWord) && previousWord.length() < MIN_DICTIONARY_WORD_LENGTH) {
            previousWord = "";
        }

        // only save words that are at least the minimum length
        if (thisWord != null && thisWord.length() >= MIN_DICTIONARY_WORD_LENGTH) {
            new AddOrUpdateDictionaryWordsTask().execute(thisWord, previousWord);
        }
    }

    // Testing
    private void printAllWords() {
        new PrintAllWordsTask().execute();
    }
}