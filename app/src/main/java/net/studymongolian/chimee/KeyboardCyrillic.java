package net.studymongolian.chimee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class KeyboardCyrillic extends Keyboard {

    private static Map<Integer, Character> idToLowercase = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToUppercase = new HashMap<Integer, Character>();
    private static Map<Integer, Character> idToPunctuation = new HashMap<Integer, Character>();

    Keyboard.KeyMode currentKeyMode = Keyboard.KeyMode.Lowercase;

    TextView tvФ;
    TextView tvЦ;
    TextView tvУ;
    TextView tvЖ;
    TextView tvЭ;
    TextView tvН;
    TextView tvГ;
    TextView tvШ;
    TextView tvҮ;
    TextView tvЗ;
    TextView tvК;
    TextView tvЪ;
    TextView tvЙ;
    TextView tvЫ;
    TextView tvБ;
    TextView tvӨ;
    TextView tvА;
    TextView tvХ;
    TextView tvР;
    TextView tvО;
    TextView tvЛ;
    TextView tvД;
    TextView tvП;
    TextView tvЕ;
    TextView tvЯ;
    TextView tvЧ;
    TextView tvЁ;
    TextView tvС;
    TextView tvМ;
    TextView tvИ;
    TextView tvТ;
    TextView tvЬ;
    TextView tvВ;
    TextView tvЮ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // map the keys to their characters
        initMap();

        // inflate layout and add listeners to each key
        View layout = inflater.inflate(R.layout.fragment_keyboard_cyrillic, container, false);
        addListeners(layout);

        return layout;
    }

    @Override
    public void initMap() {

        // unicode characters for lowercase key taps
        idToLowercase.put(R.id.rl_c_key_Ф, 'ф');
        idToLowercase.put(R.id.rl_c_key_Ц, 'ц');
        idToLowercase.put(R.id.rl_c_key_У, 'у');
        idToLowercase.put(R.id.rl_c_key_Ж, 'ж');
        idToLowercase.put(R.id.rl_c_key_Э, 'э');
        idToLowercase.put(R.id.rl_c_key_Н, 'н');
        idToLowercase.put(R.id.rl_c_key_Г, 'г');
        idToLowercase.put(R.id.rl_c_key_Ш, 'ш');
        idToLowercase.put(R.id.rl_c_key_Ү, 'ү');
        idToLowercase.put(R.id.rl_c_key_З, 'з');
        idToLowercase.put(R.id.rl_c_key_К, 'к');
        idToLowercase.put(R.id.rl_c_key_Ъ, 'ъ');
        idToLowercase.put(R.id.rl_c_key_Й, 'й');
        idToLowercase.put(R.id.rl_c_key_Ы, 'ы');
        idToLowercase.put(R.id.rl_c_key_Б, 'б');
        idToLowercase.put(R.id.rl_c_key_Ө, 'ө');
        idToLowercase.put(R.id.rl_c_key_А, 'а');
        idToLowercase.put(R.id.rl_c_key_Х, 'х');
        idToLowercase.put(R.id.rl_c_key_Р, 'р');
        idToLowercase.put(R.id.rl_c_key_О, 'о');
        idToLowercase.put(R.id.rl_c_key_Л, 'л');
        idToLowercase.put(R.id.rl_c_key_Д, 'д');
        idToLowercase.put(R.id.rl_c_key_П, 'п');
        idToLowercase.put(R.id.rl_c_key_Е, 'е');
        idToLowercase.put(R.id.rl_c_key_Я, 'я');
        idToLowercase.put(R.id.rl_c_key_Ч, 'ч');
        idToLowercase.put(R.id.rl_c_key_Ё, 'ё');
        idToLowercase.put(R.id.rl_c_key_С, 'с');
        idToLowercase.put(R.id.rl_c_key_М, 'м');
        idToLowercase.put(R.id.rl_c_key_И, 'и');
        idToLowercase.put(R.id.rl_c_key_Т, 'т');
        idToLowercase.put(R.id.rl_c_key_Ь, 'ь');
        idToLowercase.put(R.id.rl_c_key_В, 'в');
        idToLowercase.put(R.id.rl_c_key_Ю, 'ю');
        idToLowercase.put(R.id.key_comma, ',');
        idToLowercase.put(R.id.key_question, '.');
        idToLowercase.put(R.id.key_return, NEW_LINE);

        // unicode characters for uppercase key taps
        idToUppercase.put(R.id.rl_c_key_Ф, 'Ф');
        idToUppercase.put(R.id.rl_c_key_Ц, 'Ц');
        idToUppercase.put(R.id.rl_c_key_У, 'У');
        idToUppercase.put(R.id.rl_c_key_Ж, 'Ж');
        idToUppercase.put(R.id.rl_c_key_Э, 'Э');
        idToUppercase.put(R.id.rl_c_key_Н, 'Н');
        idToUppercase.put(R.id.rl_c_key_Г, 'Г');
        idToUppercase.put(R.id.rl_c_key_Ш, 'Ш');
        idToUppercase.put(R.id.rl_c_key_Ү, 'Ү');
        idToUppercase.put(R.id.rl_c_key_З, 'З');
        idToUppercase.put(R.id.rl_c_key_К, 'К');
        idToUppercase.put(R.id.rl_c_key_Ъ, 'Ъ');
        idToUppercase.put(R.id.rl_c_key_Й, 'Й');
        idToUppercase.put(R.id.rl_c_key_Ы, 'Ы');
        idToUppercase.put(R.id.rl_c_key_Б, 'Б');
        idToUppercase.put(R.id.rl_c_key_Ө, 'Ө');
        idToUppercase.put(R.id.rl_c_key_А, 'А');
        idToUppercase.put(R.id.rl_c_key_Х, 'Х');
        idToUppercase.put(R.id.rl_c_key_Р, 'Р');
        idToUppercase.put(R.id.rl_c_key_О, 'О');
        idToUppercase.put(R.id.rl_c_key_Л, 'Л');
        idToUppercase.put(R.id.rl_c_key_Д, 'Д');
        idToUppercase.put(R.id.rl_c_key_П, 'П');
        idToUppercase.put(R.id.rl_c_key_Е, 'Е');
        idToUppercase.put(R.id.rl_c_key_Я, 'Я');
        idToUppercase.put(R.id.rl_c_key_Ч, 'Ч');
        idToUppercase.put(R.id.rl_c_key_Ё, 'Ё');
        idToUppercase.put(R.id.rl_c_key_С, 'С');
        idToUppercase.put(R.id.rl_c_key_М, 'М');
        idToUppercase.put(R.id.rl_c_key_И, 'И');
        idToUppercase.put(R.id.rl_c_key_Т, 'Т');
        idToUppercase.put(R.id.rl_c_key_Ь, 'Ь');
        idToUppercase.put(R.id.rl_c_key_В, 'В');
        idToUppercase.put(R.id.rl_c_key_Ю, 'Ю');
        idToUppercase.put(R.id.key_comma, ',');
        idToUppercase.put(R.id.key_question, '.');
        idToUppercase.put(R.id.key_return, NEW_LINE);

        // unicode characters for punctuation key taps
        idToPunctuation.put(R.id.rl_c_key_Ф, '1');
        idToPunctuation.put(R.id.rl_c_key_Ц, '2');
        idToPunctuation.put(R.id.rl_c_key_У, '3');
        idToPunctuation.put(R.id.rl_c_key_Ж, '4');
        idToPunctuation.put(R.id.rl_c_key_Э, '5');
        idToPunctuation.put(R.id.rl_c_key_Н, '6');
        idToPunctuation.put(R.id.rl_c_key_Г, '7');
        idToPunctuation.put(R.id.rl_c_key_Ш, '8');
        idToPunctuation.put(R.id.rl_c_key_Ү, '9');
        idToPunctuation.put(R.id.rl_c_key_З, '0');
        idToPunctuation.put(R.id.rl_c_key_К, '\\');
        idToPunctuation.put(R.id.rl_c_key_Ъ, '|');
        idToPunctuation.put(R.id.rl_c_key_Й, '@');
        idToPunctuation.put(R.id.rl_c_key_Ы, '#');
        idToPunctuation.put(R.id.rl_c_key_Б, '$');
        idToPunctuation.put(R.id.rl_c_key_Ө, '%');
        idToPunctuation.put(R.id.rl_c_key_А, '^');
        idToPunctuation.put(R.id.rl_c_key_Х, '&');
        idToPunctuation.put(R.id.rl_c_key_Р, '*');
        idToPunctuation.put(R.id.rl_c_key_О, '(');
        idToPunctuation.put(R.id.rl_c_key_Л, ')');
        idToPunctuation.put(R.id.rl_c_key_Д, '[');
        idToPunctuation.put(R.id.rl_c_key_П, ']');
        idToPunctuation.put(R.id.rl_c_key_Е, '_');
        idToPunctuation.put(R.id.rl_c_key_Я, ':');
        idToPunctuation.put(R.id.rl_c_key_Ч, '=');
        idToPunctuation.put(R.id.rl_c_key_Ё, '+');
        idToPunctuation.put(R.id.rl_c_key_С, '-');
        idToPunctuation.put(R.id.rl_c_key_М, '/');
        idToPunctuation.put(R.id.rl_c_key_И, '\'');
        idToPunctuation.put(R.id.rl_c_key_Т, '\"');
        idToPunctuation.put(R.id.rl_c_key_Ь, '{');
        idToPunctuation.put(R.id.rl_c_key_В, '}');
        idToPunctuation.put(R.id.rl_c_key_Ю, ';');
        idToPunctuation.put(R.id.key_comma, ',');
        idToPunctuation.put(R.id.key_question, '.');
        idToPunctuation.put(R.id.key_return, NEW_LINE);

    }



    private void addListeners(View v) {

        // *** Normal Keys ***

        // click
        v.findViewById(R.id.rl_c_key_Ф).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ц).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_У).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ж).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Э).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Н).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Г).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ш).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ү).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_З).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_К).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ъ).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Й).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ы).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Б).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ө).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_А).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Х).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Р).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_О).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Л).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Д).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_П).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Е).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Я).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ч).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ё).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_С).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_М).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_И).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Т).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ь).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_В).setOnClickListener(this);
        v.findViewById(R.id.rl_c_key_Ю).setOnClickListener(this);
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
        displayOrder.add(KeyboardType.English);
        displayOrder.add(KeyboardType.Qwerty);
        super.setOnTouchListenerForKeybordSwitcherView(v.findViewById(R.id.key_input), displayOrder);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvФ = (TextView) getView().findViewById(R.id.tvCkeyФ);
        tvЦ = (TextView) getView().findViewById(R.id.tvCkeyЦ);
        tvУ = (TextView) getView().findViewById(R.id.tvCkeyУ);
        tvЖ = (TextView) getView().findViewById(R.id.tvCkeyЖ);
        tvЭ = (TextView) getView().findViewById(R.id.tvCkeyЭ);
        tvН = (TextView) getView().findViewById(R.id.tvCkeyН);
        tvГ = (TextView) getView().findViewById(R.id.tvCkeyГ);
        tvШ = (TextView) getView().findViewById(R.id.tvCkeyШ);
        tvҮ = (TextView) getView().findViewById(R.id.tvCkeyҮ);
        tvЗ = (TextView) getView().findViewById(R.id.tvCkeyЗ);
        tvК = (TextView) getView().findViewById(R.id.tvCkeyК);
        tvЪ = (TextView) getView().findViewById(R.id.tvCkeyЪ);
        tvЙ = (TextView) getView().findViewById(R.id.tvCkeyЙ);
        tvЫ = (TextView) getView().findViewById(R.id.tvCkeyЫ);
        tvБ = (TextView) getView().findViewById(R.id.tvCkeyБ);
        tvӨ = (TextView) getView().findViewById(R.id.tvCkeyӨ);
        tvА = (TextView) getView().findViewById(R.id.tvCkeyА);
        tvХ = (TextView) getView().findViewById(R.id.tvCkeyХ);
        tvР = (TextView) getView().findViewById(R.id.tvCkeyР);
        tvО = (TextView) getView().findViewById(R.id.tvCkeyО);
        tvЛ = (TextView) getView().findViewById(R.id.tvCkeyЛ);
        tvД = (TextView) getView().findViewById(R.id.tvCkeyД);
        tvП = (TextView) getView().findViewById(R.id.tvCkeyП);
        tvЕ = (TextView) getView().findViewById(R.id.tvCkeyЕ);
        tvЯ = (TextView) getView().findViewById(R.id.tvCkeyЯ);
        tvЧ = (TextView) getView().findViewById(R.id.tvCkeyЧ);
        tvЁ = (TextView) getView().findViewById(R.id.tvCkeyЁ);
        tvС = (TextView) getView().findViewById(R.id.tvCkeyС);
        tvМ = (TextView) getView().findViewById(R.id.tvCkeyМ);
        tvИ = (TextView) getView().findViewById(R.id.tvCkeyИ);
        tvТ = (TextView) getView().findViewById(R.id.tvCkeyТ);
        tvЬ = (TextView) getView().findViewById(R.id.tvCkeyЬ);
        tvВ = (TextView) getView().findViewById(R.id.tvCkeyВ);
        tvЮ = (TextView) getView().findViewById(R.id.tvCkeyЮ);

    }

    @Override
    public void onClick(View v) {

        // get input char based on wether keys are
        // in uppercase, lowercase, or punctuation mode
        if (currentKeyMode == Keyboard.KeyMode.Lowercase) {
            mListener.keyWasTapped(idToLowercase.get(v.getId()));
        } else if (currentKeyMode == Keyboard.KeyMode.Uppercase) {
            mListener.keyWasTapped(idToUppercase.get(v.getId()));
            switchKeys(Keyboard.KeyMode.Lowercase);
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
            if (currentKeyMode == Keyboard.KeyMode.Lowercase) {
                switchKeys(Keyboard.KeyMode.Uppercase);
            } else if (currentKeyMode == Keyboard.KeyMode.Uppercase) {
                switchKeys(Keyboard.KeyMode.Lowercase);
            }
        }
    };

    public void switchKeys(Keyboard.KeyMode mode) {

        // swap between Uppercase, Lowercase, and Punctuation
        if (mode == currentKeyMode) {
            if (mode == Keyboard.KeyMode.Punctuation) {
                currentKeyMode = Keyboard.KeyMode.Lowercase;
            } else { // upper or lower case
                currentKeyMode = Keyboard.KeyMode.Punctuation;
            }
        } else {
            currentKeyMode = mode;
        }

        if (currentKeyMode == Keyboard.KeyMode.Lowercase) {

            tvФ.setText(getString(R.string.c_key_Ф));
            tvЦ.setText(getString(R.string.c_key_Ц));
            tvУ.setText(getString(R.string.c_key_У));
            tvЖ.setText(getString(R.string.c_key_Ж));
            tvЭ.setText(getString(R.string.c_key_Э));
            tvН.setText(getString(R.string.c_key_Н));
            tvГ.setText(getString(R.string.c_key_Г));
            tvШ.setText(getString(R.string.c_key_Ш));
            tvҮ.setText(getString(R.string.c_key_Ү));
            tvЗ.setText(getString(R.string.c_key_З));
            tvК.setText(getString(R.string.c_key_К));
            tvЪ.setText(getString(R.string.c_key_Ъ));
            tvЙ.setText(getString(R.string.c_key_Й));
            tvЫ.setText(getString(R.string.c_key_Ы));
            tvБ.setText(getString(R.string.c_key_Б));
            tvӨ.setText(getString(R.string.c_key_Ө));
            tvА.setText(getString(R.string.c_key_А));
            tvХ.setText(getString(R.string.c_key_Х));
            tvР.setText(getString(R.string.c_key_Р));
            tvО.setText(getString(R.string.c_key_О));
            tvЛ.setText(getString(R.string.c_key_Л));
            tvД.setText(getString(R.string.c_key_Д));
            tvП.setText(getString(R.string.c_key_П));
            tvЕ.setText(getString(R.string.c_key_Е));
            tvЯ.setText(getString(R.string.c_key_Я));
            tvЧ.setText(getString(R.string.c_key_Ч));
            tvЁ.setText(getString(R.string.c_key_Ё));
            tvС.setText(getString(R.string.c_key_С));
            tvМ.setText(getString(R.string.c_key_М));
            tvИ.setText(getString(R.string.c_key_И));
            tvТ.setText(getString(R.string.c_key_Т));
            tvЬ.setText(getString(R.string.c_key_Ь));
            tvВ.setText(getString(R.string.c_key_В));
            tvЮ.setText(getString(R.string.c_key_Ю));

        } else if (currentKeyMode == Keyboard.KeyMode.Uppercase) {

            tvФ.setText(getString(R.string.c_key_Ф_caps));
            tvЦ.setText(getString(R.string.c_key_Ц_caps));
            tvУ.setText(getString(R.string.c_key_У_caps));
            tvЖ.setText(getString(R.string.c_key_Ж_caps));
            tvЭ.setText(getString(R.string.c_key_Э_caps));
            tvН.setText(getString(R.string.c_key_Н_caps));
            tvГ.setText(getString(R.string.c_key_Г_caps));
            tvШ.setText(getString(R.string.c_key_Ш_caps));
            tvҮ.setText(getString(R.string.c_key_Ү_caps));
            tvЗ.setText(getString(R.string.c_key_З_caps));
            tvК.setText(getString(R.string.c_key_К_caps));
            tvЪ.setText(getString(R.string.c_key_Ъ_caps));
            tvЙ.setText(getString(R.string.c_key_Й_caps));
            tvЫ.setText(getString(R.string.c_key_Ы_caps));
            tvБ.setText(getString(R.string.c_key_Б_caps));
            tvӨ.setText(getString(R.string.c_key_Ө_caps));
            tvА.setText(getString(R.string.c_key_А_caps));
            tvХ.setText(getString(R.string.c_key_Х_caps));
            tvР.setText(getString(R.string.c_key_Р_caps));
            tvО.setText(getString(R.string.c_key_О_caps));
            tvЛ.setText(getString(R.string.c_key_Л_caps));
            tvД.setText(getString(R.string.c_key_Д_caps));
            tvП.setText(getString(R.string.c_key_П_caps));
            tvЕ.setText(getString(R.string.c_key_Е_caps));
            tvЯ.setText(getString(R.string.c_key_Я_caps));
            tvЧ.setText(getString(R.string.c_key_Ч_caps));
            tvЁ.setText(getString(R.string.c_key_Ё_caps));
            tvС.setText(getString(R.string.c_key_С_caps));
            tvМ.setText(getString(R.string.c_key_М_caps));
            tvИ.setText(getString(R.string.c_key_И_caps));
            tvТ.setText(getString(R.string.c_key_Т_caps));
            tvЬ.setText(getString(R.string.c_key_Ь_caps));
            tvВ.setText(getString(R.string.c_key_В_caps));
            tvЮ.setText(getString(R.string.c_key_Ю_caps));

        } else if (currentKeyMode == Keyboard.KeyMode.Punctuation) {

            tvФ.setText(getString(R.string.c_key_Ф_punctuation));
            tvЦ.setText(getString(R.string.c_key_Ц_punctuation));
            tvУ.setText(getString(R.string.c_key_У_punctuation));
            tvЖ.setText(getString(R.string.c_key_Ж_punctuation));
            tvЭ.setText(getString(R.string.c_key_Э_punctuation));
            tvН.setText(getString(R.string.c_key_Н_punctuation));
            tvГ.setText(getString(R.string.c_key_Г_punctuation));
            tvШ.setText(getString(R.string.c_key_Ш_punctuation));
            tvҮ.setText(getString(R.string.c_key_Ү_punctuation));
            tvЗ.setText(getString(R.string.c_key_З_punctuation));
            tvК.setText(getString(R.string.c_key_К_punctuation));
            tvЪ.setText(getString(R.string.c_key_Ъ_punctuation));
            tvЙ.setText(getString(R.string.c_key_Й_punctuation));
            tvЫ.setText(getString(R.string.c_key_Ы_punctuation));
            tvБ.setText(getString(R.string.c_key_Б_punctuation));
            tvӨ.setText(getString(R.string.c_key_Ө_punctuation));
            tvА.setText(getString(R.string.c_key_А_punctuation));
            tvХ.setText(getString(R.string.c_key_Х_punctuation));
            tvР.setText(getString(R.string.c_key_Р_punctuation));
            tvО.setText(getString(R.string.c_key_О_punctuation));
            tvЛ.setText(getString(R.string.c_key_Л_punctuation));
            tvД.setText(getString(R.string.c_key_Д_punctuation));
            tvП.setText(getString(R.string.c_key_П_punctuation));
            tvЕ.setText(getString(R.string.c_key_Е_punctuation));
            tvЯ.setText(getString(R.string.c_key_Я_punctuation));
            tvЧ.setText(getString(R.string.c_key_Ч_punctuation));
            tvЁ.setText(getString(R.string.c_key_Ё_punctuation));
            tvС.setText(getString(R.string.c_key_С_punctuation));
            tvМ.setText(getString(R.string.c_key_М_punctuation));
            tvИ.setText(getString(R.string.c_key_И_punctuation));
            tvТ.setText(getString(R.string.c_key_Т_punctuation));
            tvЬ.setText(getString(R.string.c_key_Ь_punctuation));
            tvВ.setText(getString(R.string.c_key_В_punctuation));
            tvЮ.setText(getString(R.string.c_key_Ю_punctuation));
        }
    }

}
