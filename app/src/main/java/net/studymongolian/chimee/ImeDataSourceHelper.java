package net.studymongolian.chimee;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.inputmethod.InputConnection;

import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolCode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImeDataSourceHelper implements ImeContainer.DataSource {

    private DataSourceHelperListener mListener;

    public interface DataSourceHelperListener {
        CustomImeContainer getImeContainer();
        Context getContext();
    }


    ImeDataSourceHelper(DataSourceHelperListener listener) {
        this.mListener = listener;
    }

    private Context getContext() {
        if (mListener != null)
            return mListener.getContext();
        return null;
    }

    private ImeContainer getImeContainer() {
        if (mListener != null)
            return mListener.getImeContainer();
        return null;
    }

    private InputConnection getInputConnection() {
        if (mListener == null) return null;
        ImeContainer container = mListener.getImeContainer();
        if (container == null) return null;
        return container.getInputConnection();
    }

    @Override
    public void onRequestWordsStartingWith(String text) {
        if (text.startsWith(String.valueOf(MongolCode.Uni.NNBS))) {
            new GetSuffixesStartingWith(this).execute(text);
        } else {
            new GetWordsStartingWith(this).execute(text);
        }
    }

    @Override
    public void onWordFinished(String word, String previousWord) {
        new AddOrUpdateDictionaryWordsTask(this).execute(word, previousWord);
    }

    @Override
    public void onCandidateClick(int position, String word, String previousWordInEditor) {
        addSpace();
        new RespondToCandidateClick(this).execute(word, previousWordInEditor);
    }

    private void addSpace() {
        InputConnection ic = getInputConnection();
        if (ic == null) return;
        ic.commitText(" ", 1);
    }

    @Override
    public void onCandidateLongClick(int position, String word, String previousWordInEditor) {
        new DeleteWord(this, position).execute(word, previousWordInEditor);
    }


    private static class GetSuffixesStartingWith extends AsyncTask<String, Integer, List<String>> {


        private WeakReference<ImeDataSourceHelper> classReference;

        GetSuffixesStartingWith(ImeDataSourceHelper context) {
            classReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String suffixPrefix = params[0];

            List<String> words = new ArrayList<>();

            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return words;
            ImeContainer imeContainer = helper.getImeContainer();
            if (imeContainer == null) return words;

            List<String> previousWords = imeContainer.getPreviousMongolWords(2, false);
            String wordBeforePreviousWord = previousWords.get(1);

            Suffix.WordEnding ending = getEndingOf(wordBeforePreviousWord);
            Suffix.WordGender gender;
            if (ending == Suffix.WordEnding.Nil) {
                gender = getWordGender(suffixPrefix);
            } else {
                gender = getWordGender(wordBeforePreviousWord);
            }

            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(helper.getContext());
            return adapter.findSuffixesBeginningWith(suffixPrefix, gender, ending);
        }

        private Suffix.WordEnding getEndingOf(String word) {

            Suffix.WordEnding ending = Suffix.WordEnding.Nil;

            if (TextUtils.isEmpty(word)) {
                return ending;
            }

            // determine ending character
            char endingChar = word.charAt(word.length() - 1);
            if (MongolCode.isFVS(endingChar)) {
                if (word.length() > 1) {
                    endingChar = word.charAt(word.length() - 2);
                } else {
                    return ending;
                }
            }

            // determine type
            if (MongolCode.isVowel(endingChar)) {
                ending = Suffix.WordEnding.Vowel;
            } else if (MongolCode.isConsonant(endingChar)) {
                if (endingChar == MongolCode.Uni.NA) {
                    ending = Suffix.WordEnding.N;
                } else if (isBGDRS(endingChar)) {
                    ending = Suffix.WordEnding.BigDress;
                } else {
                    ending = Suffix.WordEnding.OtherConsonant;
                }
            }

            return ending;
        }

        private Suffix.WordGender getWordGender(String word) {
            MongolCode.Gender gender = MongolCode.getWordGender(word);
            if (gender == null)
                return Suffix.WordGender.Neutral;
            switch (gender) {
                case MASCULINE:
                    return Suffix.WordGender.Masculine;
                case FEMININE:
                    return Suffix.WordGender.Feminine;
                default:
                    return Suffix.WordGender.Neutral;
            }
        }

        private boolean isBGDRS(char character) {
            return (character == MongolCode.Uni.BA ||
                    character == MongolCode.Uni.GA ||
                    character == MongolCode.Uni.DA ||
                    character == MongolCode.Uni.RA ||
                    character == MongolCode.Uni.SA);
        }
        @Override
        protected void onPostExecute(List<String> result) {
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return;
            ImeContainer imeContainer = helper.getImeContainer();
            if (imeContainer == null) return;

            if (result.size() > 0)
                imeContainer.setCandidates(result);
            else
                imeContainer.clearCandidates();
        }

    }

    private static class GetWordsStartingWith extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<ImeDataSourceHelper> classReference;

        GetWordsStartingWith(ImeDataSourceHelper context) {
            classReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String prefix = params[0];

            List<String> words = new ArrayList<>();

            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return words;
            Context context = helper.getContext();
            if (context == null) return words;

            Cursor cursor = UserDictionary.Words.queryPrefix(context, prefix);
            if (cursor == null) return words;
            int indexWord = cursor.getColumnIndex(UserDictionary.Words.WORD);
            while (cursor.moveToNext()) {
                words.add(cursor.getString(indexWord));
            }
            cursor.close();
            return words;

        }

        @Override
        protected void onPostExecute(List<String> result) {
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return;
            ImeContainer imeContainer = helper.getImeContainer();
            if (imeContainer == null) return;

            if (result.size() > 0)
                imeContainer.setCandidates(result);
            else
                imeContainer.clearCandidates();
        }
    }

    private static class AddOrUpdateDictionaryWordsTask extends AsyncTask<String, Integer, Void> {

        private WeakReference<ImeDataSourceHelper> classReference;

        AddOrUpdateDictionaryWordsTask(ImeDataSourceHelper helper) {
            classReference = new WeakReference<>(helper);
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return null;
            Context context = helper.getContext();
            if (context == null) return null;
            insertUpdateWord(context, word);

            UserDictionary.Words.addFollowing(context, previousWord, word);
            return null;
        }

    }

    private static void insertUpdateWord(Context context, String word) {
        if (context == null) return;

        int id = UserDictionary.Words.incrementFrequency(context, word);
        if (id < 0) {
            UserDictionary.Words.addWord(context, word);
        }

    }


    private static class RespondToCandidateClick extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<ImeDataSourceHelper> classReference;

        RespondToCandidateClick(ImeDataSourceHelper helper) {
            classReference = new WeakReference<>(helper);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return new ArrayList<>();
            Context context = helper.getContext();
            if (context == null) return new ArrayList<>();

            int id = UserDictionary.Words.incrementFrequency(context, word);
            if (word.charAt(0) == MongolCode.Uni.NNBS) {
                if (id < 0) {
                    // it should already be in the suffix database, but adding it
                    // to the user dictionary will make it so that there is no error
                    // when incrementing the frequency in the user dictionary later
                    UserDictionary.Words.addWord(context, word);
                }
                incrementSuffixFrequency(context, word);

            }
            UserDictionary.Words.addFollowing(context, previousWord, word);
            return UserDictionary.Words.getFollowing(context, word);
        }

        private void incrementSuffixFrequency(Context context, String suffix) {
            SuffixDatabaseAdapter adapter = new SuffixDatabaseAdapter(context);
            adapter.updateFrequencyForSuffix(suffix);
        }

        @Override
        protected void onPostExecute(List<String> followingWords) {
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return;
            ImeContainer imeContainer = helper.getImeContainer();
            if (imeContainer == null) return;

            if (followingWords.size() == 0) {
                imeContainer.clearCandidates();
            } else {
                imeContainer.setCandidates(followingWords);
            }
        }
    }

    private static class DeleteWord extends AsyncTask<String, Integer, Void> {

        private WeakReference<ImeDataSourceHelper> classReference;
        private int index;

        DeleteWord(ImeDataSourceHelper helper, int index) {
            classReference = new WeakReference<>(helper);
            this.index = index;
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];

            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return null;
            Context context = helper.getContext();
            if (context == null) return null;

            UserDictionary.Words.deleteWord(context, word);
            UserDictionary.Words.deleteFollowingWord(context, previousWord, word);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            ImeDataSourceHelper helper = classReference.get();
            if (helper == null) return;
            ImeContainer imeContainer = helper.getImeContainer();
            if (imeContainer == null) return;

            imeContainer.removeCandidate(index);
        }
    }
}
