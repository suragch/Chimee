package net.studymongolian.chimee;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

// This fragment holds the suggestion bar and a container for the keyboard
// They keyboards are loaded as subfragments
public class KeyboardController extends Fragment implements Keyboard.OnKeyboardListener {

    private OnKeyboardControllerListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keyboard_controller, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new KeyboardAeiou();
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
    public void keySuffix() {
        Log.i("TAG", "keySuffix: ");
        mListener.keyWasTapped(MongolUnicodeRenderer.Uni.NNBS);
    }

    @Override
    public void keyWasTapped(char character) {

        Log.i("TAG", "keyWasTapped: ");
        mListener.keyWasTapped(character);
    }

    @Override
    public void keyBackspace() {
        mListener.keyBackspace();
        Log.i("TAG", "keyBackspace: ");
    }

    @Override
    public void keyNewKeyboardChosen(KeyboardType type) {

    }

    public interface OnKeyboardControllerListener {
        void keyWasTapped(char character);
        void keyBackspace();
        String oneMongolWordBeforeCursor();
        String[] twoMongolWordsBeforeCursor();
        void replaceCurrentWordWith(String replacementWord);
    }
}