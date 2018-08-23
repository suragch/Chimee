package net.studymongolian.chimee;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import net.studymongolian.mongollibrary.Key;
import net.studymongolian.mongollibrary.KeyBackspace;
import net.studymongolian.mongollibrary.KeyImage;
import net.studymongolian.mongollibrary.KeyText;
import net.studymongolian.mongollibrary.Keyboard;
import net.studymongolian.mongollibrary.PopupKeyCandidate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardEmoji extends Keyboard {

    // name to use in the keyboard popup chooser
    private static final String DEFAULT_DISPLAY_NAME = "ᠢᠮᠤᠵᠢ"; // smile emoji

    private static final String EMOJI_LIST = "\uD83D\uDE00 \uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE11 \uD83D\uDE2E \uD83D\uDE2F \uD83D\uDE34 \uD83D\uDE1B \uD83D\uDE15 \uD83D\uDE1F \uD83D\uDE26 \uD83D\uDE27 \uD83D\uDE2C \uD83D\uDE42 \uD83D\uDE41 \uD83D\uDE01 \uD83D\uDE02 \uD83D\uDE03 \uD83D\uDE04 \uD83D\uDE05 \uD83D\uDE06 \uD83D\uDE09 \uD83D\uDE0A \uD83D\uDE0B \uD83D\uDE0E \uD83D\uDE0D \uD83D\uDE18 \uD83D\uDE1A \uD83D\uDE10 \uD83D\uDE36 \uD83D\uDE0F \uD83D\uDE23 \uD83D\uDE25 \uD83D\uDE2A \uD83D\uDE2B \uD83D\uDE0C \uD83D\uDE1C \uD83D\uDE1D \uD83D\uDE12 \uD83D\uDE13 \uD83D\uDE14 \uD83D\uDE32 \uD83D\uDE16 \uD83D\uDE1E \uD83D\uDE24 \uD83D\uDE22 \uD83D\uDE2D \uD83D\uDE28 \uD83D\uDE29 \uD83D\uDE30 \uD83D\uDE31 \uD83D\uDE33 \uD83D\uDE35 \uD83D\uDE21 \uD83D\uDE20 \uD83D\uDE37 \uD83D\uDE00 \uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE11 \uD83D\uDE2E \uD83D\uDE2F \uD83D\uDE34 \uD83D\uDE1B \uD83D\uDE15 \uD83D\uDE1F \uD83D\uDE26 \uD83D\uDE27 \uD83D\uDE2C ☺️ ☹️ \uD83D\uDE07 \uD83D\uDE08 \uD83D\uDC7F \uD83D\uDC79 \uD83D\uDC7A \uD83D\uDC80 \uD83D\uDC7B \uD83D\uDC7D \uD83D\uDC7E \uD83D\uDCA9 \uD83D\uDE3A \uD83D\uDE38 \uD83D\uDE39 \uD83D\uDE3B \uD83D\uDE3C \uD83D\uDE3D \uD83D\uDE40 \uD83D\uDE3F \uD83D\uDE3E \uD83D\uDE48 \uD83D\uDE49 \uD83D\uDE4A \uD83D\uDC76 \uD83D\uDC66 \uD83D\uDC67 \uD83D\uDC71 \uD83D\uDC68 \uD83D\uDC69 \uD83D\uDC74 \uD83D\uDC75 \uD83D\uDC6E \uD83D\uDC82 \uD83D\uDC77 \uD83D\uDC78 \uD83D\uDC73 \uD83D\uDC72 \uD83D\uDC70 \uD83D\uDC7C \uD83C\uDF85 \uD83D\uDE4D \uD83D\uDE4E \uD83D\uDE45 \uD83D\uDE46 \uD83D\uDC81 \uD83D\uDE4B \uD83D\uDE47 \uD83D\uDC86 \uD83D\uDC87 \uD83D\uDEB6 \uD83C\uDFC3 \uD83D\uDC83 \uD83D\uDC6F \uD83D\uDEC0 \uD83D\uDC64 \uD83D\uDC65 \uD83C\uDFC7 \uD83C\uDFC2 \uD83C\uDFC4 \uD83D\uDEA3 \uD83C\uDFCA \uD83D\uDEB4 \uD83D\uDEB5 \uD83D\uDD75️ \uD83D\uDECC \uD83D\uDD74️ \uD83D\uDDE3️ \uD83C\uDFCC️ \uD83C\uDFCB️ \uD83C\uDFCE️ \uD83C\uDFCD️ \uD83D\uDD90️ \uD83D\uDC41️ \uD83D\uDDE8️ \uD83D\uDDEF️ \uD83D\uDD73️ \uD83D\uDD76️ \uD83D\uDECD️ \uD83D\uDC3F️ \uD83D\uDD4A️ \uD83D\uDD77️ \uD83D\uDD78️ \uD83C\uDFF5️ \uD83C\uDF36️ \uD83C\uDF7D️ \uD83D\uDDFA️ \uD83C\uDFD4️ \uD83C\uDFD5️ \uD83C\uDFD6️ \uD83C\uDFDC️ \uD83C\uDFDD️ \uD83C\uDFDE️ \uD83C\uDFDF️ \uD83C\uDFDB️ \uD83C\uDFD7️ \uD83C\uDFD8️ \uD83C\uDFDA️ \uD83C\uDFD9️ \uD83D\uDEE3️ \uD83D\uDEE4️ \uD83D\uDEE2️ \uD83D\uDEF3️ \uD83D\uDEE5️ \uD83D\uDEE9️ \uD83D\uDEEB \uD83D\uDEEC \uD83D\uDEF0️ \uD83D\uDECE️ \uD83D\uDD70️ \uD83C\uDF21️ \uD83C\uDF24️ \uD83C\uDF25️ \uD83C\uDF26️ \uD83C\uDF27️ \uD83C\uDF28️ \uD83C\uDF29️ \uD83C\uDF2A️ \uD83C\uDF2B️ \uD83C\uDF2C️ \uD83C\uDF97️ \uD83C\uDF9F️ \uD83C\uDF96️ \uD83C\uDFC5 \uD83D\uDD79️ \uD83D\uDDBC️ \uD83C\uDF99️ \uD83C\uDF9A️ \uD83C\uDF9B️ \uD83D\uDDA5️ \uD83D\uDDA8️ \uD83D\uDDB1️ \uD83D\uDDB2️ \uD83C\uDF9E️ \uD83D\uDCFD️ \uD83D\uDCF8 \uD83D\uDD6F️ \uD83D\uDDDE️ \uD83C\uDFF7️ \uD83D\uDDF3️ \uD83D\uDD8B️ \uD83D\uDD8A️ \uD83D\uDD8C️ \uD83D\uDD8D️ \uD83D\uDDC2️ \uD83D\uDDD2️ \uD83D\uDDD3️ \uD83D\uDD87️ \uD83D\uDDC3️ \uD83D\uDDD1️ \uD83D\uDDDD️ \uD83D\uDEE0️ \uD83D\uDDE1️ \uD83D\uDEE1️ \uD83D\uDDDC️ \uD83D\uDECF️ \uD83D\uDECB️ \uD83C\uDFF4 \uD83C\uDFF3️ ⚽ ⚾ \uD83D\uDCAA \uD83D\uDC48 \uD83D\uDC49 \uD83D\uDC46 \uD83D\uDC47 ✋ \uD83D\uDC4C \uD83D\uDC4D \uD83D\uDC4E ✊ \uD83D\uDC4A \uD83D\uDC4B \uD83D\uDC4F \uD83D\uDC50 \uD83D\uDE4C \uD83D\uDE4F \uD83D\uDC42 \uD83D\uDC43 \uD83D\uDC63 \uD83D\uDC40 \uD83D\uDC45 \uD83D\uDC44 \uD83D\uDC8B \uD83D\uDC98 \uD83D\uDC9D \uD83D\uDC96 \uD83D\uDC97 \uD83D\uDC93 \uD83D\uDC9E \uD83D\uDC95 \uD83D\uDC8C \uD83D\uDC94 \uD83D\uDC9B \uD83D\uDC9A \uD83D\uDC99 \uD83D\uDC9C \uD83D\uDC9F \uD83D\uDCA3 \uD83D\uDCA5 \uD83D\uDCA6 \uD83D\uDCA8 \uD83D\uDCAB \uD83D\uDC53 \uD83D\uDC54 \uD83D\uDC55 \uD83D\uDC56 \uD83D\uDC57 \uD83D\uDC58 \uD83D\uDC5A \uD83D\uDC5B \uD83D\uDC5C \uD83D\uDC5D \uD83C\uDF92 \uD83D\uDC5E \uD83D\uDC5F \uD83D\uDC60 \uD83D\uDC61 \uD83D\uDC62 \uD83D\uDC51 \uD83D\uDC52 \uD83C\uDFA9 \uD83C\uDF93 \uD83D\uDC84 \uD83D\uDC8D \uD83D\uDC8E \uD83D\uDC35 \uD83D\uDC12 \uD83D\uDC36 \uD83D\uDC15 \uD83D\uDC29 \uD83D\uDC3A \uD83D\uDC31 \uD83D\uDC08 \uD83D\uDC2F \uD83D\uDC05 \uD83D\uDC06 \uD83D\uDC34 \uD83D\uDC0E \uD83D\uDC2E \uD83D\uDC02 \uD83D\uDC03 \uD83D\uDC04 \uD83D\uDC37 \uD83D\uDC16 \uD83D\uDC17 \uD83D\uDC3D \uD83D\uDC0F \uD83D\uDC11 \uD83D\uDC10 \uD83D\uDC2A \uD83D\uDC2B \uD83D\uDC18 \uD83D\uDC2D \uD83D\uDC01 \uD83D\uDC00 \uD83D\uDC39 \uD83D\uDC30 \uD83D\uDC07 \uD83D\uDC3B \uD83D\uDC28 \uD83D\uDC3C \uD83D\uDC3E \uD83D\uDC14 \uD83D\uDC13 \uD83D\uDC23 \uD83D\uDC24 \uD83D\uDC25 \uD83D\uDC26 \uD83D\uDC27 \uD83D\uDC38 \uD83D\uDC0A \uD83D\uDC22 \uD83D\uDC0D \uD83D\uDC32 \uD83D\uDC09 \uD83D\uDC33 \uD83D\uDC0B \uD83D\uDC2C \uD83D\uDC1F \uD83D\uDC20 \uD83D\uDC21 \uD83D\uDC19 \uD83D\uDC1A \uD83D\uDC0C \uD83D\uDC1B \uD83D\uDC1C \uD83D\uDC1D \uD83D\uDC1E \uD83D\uDC90 \uD83C\uDF38 \uD83D\uDCAE \uD83C\uDF39 \uD83C\uDF3A \uD83C\uDF3B \uD83C\uDF3C \uD83C\uDF37 \uD83C\uDF31 \uD83C\uDF32 \uD83C\uDF33 \uD83C\uDF34 \uD83C\uDF35 \uD83C\uDF3E \uD83C\uDF3F \uD83C\uDF40 \uD83C\uDF41 \uD83C\uDF42 \uD83C\uDF43 \uD83C\uDF47 \uD83C\uDF48 \uD83C\uDF49 \uD83C\uDF4A \uD83C\uDF4B \uD83C\uDF4C \uD83C\uDF4D \uD83C\uDF4E \uD83C\uDF4F \uD83C\uDF50 \uD83C\uDF51 \uD83C\uDF52 \uD83C\uDF53 \uD83C\uDF45 \uD83C\uDF46 \uD83C\uDF3D \uD83C\uDF44 \uD83C\uDF30 \uD83C\uDF5E \uD83C\uDF56 \uD83C\uDF57 \uD83C\uDF54 \uD83C\uDF5F \uD83C\uDF55 \uD83C\uDF73 \uD83C\uDF72 \uD83C\uDF71 \uD83C\uDF58 \uD83C\uDF59 \uD83C\uDF5A \uD83C\uDF5B \uD83C\uDF5C \uD83C\uDF5D \uD83C\uDF60 \uD83C\uDF62 \uD83C\uDF63 \uD83C\uDF64 \uD83C\uDF65 \uD83C\uDF61 \uD83C\uDF66 \uD83C\uDF67 \uD83C\uDF68 \uD83C\uDF69 \uD83C\uDF6A \uD83C\uDF82 \uD83C\uDF70 \uD83C\uDF6B \uD83C\uDF6C \uD83C\uDF6D \uD83C\uDF6E \uD83C\uDF6F \uD83C\uDF7C \uD83C\uDF75 \uD83C\uDF76 \uD83C\uDF77 \uD83C\uDF78 \uD83C\uDF79 \uD83C\uDF7A \uD83C\uDF7B \uD83C\uDF74 \uD83D\uDD2A \uD83C\uDF0D \uD83C\uDF0E \uD83C\uDF0F \uD83C\uDF10 \uD83C\uDF0B \uD83D\uDDFB \uD83C\uDFE0 \uD83C\uDFE1 \uD83C\uDFE2 \uD83C\uDFE3 \uD83C\uDFE4 \uD83C\uDFE5 \uD83C\uDFE8 \uD83C\uDFE9 \uD83C\uDFEA \uD83C\uDFEB \uD83C\uDFEC \uD83C\uDFED \uD83C\uDF03 \uD83C\uDF04 \uD83C\uDF05 \uD83C\uDF06 \uD83C\uDF07 \uD83C\uDF09 \uD83C\uDF0C \uD83C\uDFA0 \uD83C\uDFA1 \uD83C\uDFA2 \uD83D\uDC88 \uD83C\uDFAA \uD83D\uDE82 \uD83D\uDE83 \uD83D\uDE84 \uD83D\uDE85 \uD83D\uDE86 \uD83D\uDE87 \uD83D\uDE88 \uD83D\uDE89 \uD83D\uDE8A \uD83D\uDE9D \uD83D\uDE9E \uD83D\uDE8B \uD83D\uDE8C \uD83D\uDE8D \uD83D\uDE8E \uD83D\uDE90 \uD83D\uDE91 \uD83D\uDE92 \uD83D\uDE93 \uD83D\uDE94 \uD83D\uDE95 \uD83D\uDE96 \uD83D\uDE97 \uD83D\uDE98 \uD83D\uDE99 \uD83D\uDE9A \uD83D\uDE9B \uD83D\uDE9C \uD83D\uDEB2 \uD83D\uDE8F \uD83D\uDEA8 \uD83D\uDEA5 \uD83D\uDEA6 \uD83D\uDEA7 \uD83D\uDEA4 \uD83D\uDEA2 \uD83D\uDCBA \uD83D\uDE81 \uD83D\uDE9F \uD83D\uDEA0 \uD83D\uDEA1 \uD83D\uDE80 ⏳ ⏰ ⏱️ ⏲️ \uD83D\uDD5B \uD83D\uDD67 \uD83D\uDD50 \uD83D\uDD5C \uD83D\uDD51 \uD83D\uDD5D \uD83D\uDD52 \uD83D\uDD5E \uD83D\uDD53 \uD83D\uDD5F \uD83D\uDD54 \uD83D\uDD60 \uD83D\uDD55 \uD83D\uDD61 \uD83D\uDD56 \uD83D\uDD62 \uD83D\uDD57 \uD83D\uDD63 \uD83D\uDD58 \uD83D\uDD64 \uD83D\uDD59 \uD83D\uDD65 \uD83D\uDD5A \uD83D\uDD66 \uD83C\uDF11 \uD83C\uDF12 \uD83C\uDF13 \uD83C\uDF14 \uD83C\uDF15 \uD83C\uDF16 \uD83C\uDF17 \uD83C\uDF18 \uD83C\uDF19 \uD83C\uDF1A \uD83C\uDF1B \uD83C\uDF1C \uD83C\uDF1D \uD83C\uDF1E \uD83C\uDF1F \uD83C\uDF20 \uD83C\uDF00 \uD83C\uDF08 \uD83C\uDF02 \uD83D\uDD25 \uD83D\uDCA7 \uD83C\uDF0A \uD83C\uDF83 \uD83C\uDF84 \uD83C\uDF86 \uD83C\uDF87 ✨ \uD83C\uDF88 \uD83C\uDF89 \uD83C\uDF8A \uD83C\uDF8B \uD83C\uDF8D \uD83C\uDF8E \uD83C\uDF8F \uD83C\uDF90 \uD83C\uDF91 \uD83C\uDF80 \uD83C\uDF81 \uD83C\uDFAB \uD83C\uDFC6 \uD83C\uDFC0 \uD83C\uDFC8 \uD83C\uDFC9 \uD83C\uDFBE \uD83C\uDFB3 \uD83C\uDFA3 \uD83C\uDFBD \uD83C\uDFBF \uD83C\uDFAF \uD83C\uDFB1 \uD83D\uDD2E \uD83C\uDFAE \uD83C\uDFB0 \uD83C\uDFB2 \uD83C\uDFAD \uD83C\uDFA8 \uD83D\uDCE2 \uD83D\uDCE3 \uD83D\uDCEF \uD83D\uDD14 \uD83D\uDD15 \uD83C\uDFBC \uD83C\uDFB5 \uD83C\uDFB6 \uD83C\uDFA4 \uD83C\uDFA7 \uD83D\uDCFB \uD83C\uDFB7 \uD83C\uDFB8 \uD83C\uDFB9 \uD83C\uDFBA \uD83C\uDFBB \uD83D\uDCF1 \uD83D\uDCF2 \uD83D\uDCDE \uD83D\uDCDF \uD83D\uDCE0 \uD83D\uDD0B \uD83D\uDD0C \uD83D\uDCBB \uD83D\uDCBD \uD83D\uDCBE \uD83D\uDCBF \uD83C\uDFA5 \uD83C\uDFAC \uD83D\uDCFA \uD83D\uDCF7 \uD83D\uDCF9 \uD83D\uDCFC \uD83D\uDD0D \uD83D\uDD0E \uD83D\uDCA1 \uD83D\uDD26 \uD83C\uDFEE \uD83D\uDCD4 \uD83D\uDCD5 \uD83D\uDCD6 \uD83D\uDCD7 \uD83D\uDCD8 \uD83D\uDCD9 \uD83D\uDCDA \uD83D\uDCD3 \uD83D\uDCD2 \uD83D\uDCC3 \uD83D\uDCDC \uD83D\uDCC4 \uD83D\uDCF0 \uD83D\uDCD1 \uD83D\uDD16 \uD83D\uDCB0 \uD83D\uDCB4 \uD83D\uDCB5 \uD83D\uDCB6 \uD83D\uDCB7 \uD83D\uDCB8 \uD83D\uDCB3 \uD83D\uDCE6 \uD83D\uDCEB \uD83D\uDCEA \uD83D\uDCEC \uD83D\uDCED \uD83D\uDCEE \uD83D\uDCDD \uD83D\uDCBC \uD83D\uDCC1 \uD83D\uDCC2 \uD83D\uDCC5 \uD83D\uDCC6 \uD83D\uDCC7 \uD83D\uDCC8 \uD83D\uDCC9 \uD83D\uDCCA \uD83D\uDCCB \uD83D\uDCCC \uD83D\uDCCD \uD83D\uDCCE \uD83D\uDCCF \uD83D\uDCD0 \uD83D\uDD12 \uD83D\uDD13 \uD83D\uDD0F \uD83D\uDD10 \uD83D\uDD11 \uD83D\uDD28 \uD83D\uDD2B \uD83D\uDD27 \uD83D\uDD29 \uD83D\uDD17 \uD83D\uDD2C \uD83D\uDD2D \uD83D\uDCE1 \uD83D\uDC89 \uD83D\uDC8A \uD83D\uDEAA \uD83D\uDEBD \uD83D\uDEBF \uD83D\uDEC1 \uD83D\uDEAC \uD83D\uDDFF \uD83C\uDFE7 \uD83D\uDEAE \uD83D\uDEB0 \uD83D\uDEB9 \uD83D\uDEBA \uD83D\uDEBB \uD83D\uDEBC \uD83D\uDEBE \uD83D\uDEC2 \uD83D\uDEC3 \uD83D\uDEC4 \uD83D\uDEC5 \uD83D\uDEB8 \uD83D\uDEAB \uD83D\uDEB3 \uD83D\uDEAD \uD83D\uDEAF \uD83D\uDEB1 \uD83D\uDEB7 \uD83D\uDCF5 \uD83D\uDD05 \uD83D\uDD06 \uD83D\uDCAF \uD83C\uDFC1";
    private static final int DESIRED_COLUMN_WIDTH_DP = 50;
    private static final String FINISHED = "finished";

    protected KeyText mKeySpace;
    protected KeyBackspace mKeyBackspace;
    protected KeyImage mKeyFinished;
    protected GridView mEmojiGridView;
    private int mColumnWidth;

    public KeyboardEmoji(Context context) {
        super(context);
        init(context);
    }

    public KeyboardEmoji(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardEmoji(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public KeyboardEmoji(Context context, StyleBuilder style) {
        super(context, style);
        init(context);
    }

    protected void init(Context context) {

        // | del |                     |    Row 1
        // |space|     Emoji grid      |    Row 2
        // | kb  |                     |    Row 3

        // actual layout work is done by Keyboard superclass's onLayout
        mNumberOfKeysInRow = new int[]{1, 1, 1};
        mKeyWeights = new float[]{
                1 / 5f,            // row 0
                1 / 5f,            // row 1
                1 / 5f};           // row 2


        instantiateKeys(context);
        setKeyImages();
        setKeyValues();
        setListeners();
        addKeysToKeyboard();
        applyThemeToKeys();
        initGridView(context);
    }

    private void instantiateKeys(Context context) {
        mKeyBackspace = new KeyBackspace(context);
        mKeySpace = new KeyText(context);
        mKeyFinished = new KeyImage(context);
    }

    private void setKeyImages() {
        mKeyBackspace.setImage(getBackspaceImage(), getPrimaryTextColor());
        mKeyFinished.setImage(getBackImage(), getPrimaryTextColor());
    }

    private void setKeyValues() {
        mKeySpace.setText(" ");
        mKeyFinished.setText(FINISHED);
    }

    private void setListeners() {
        mKeyBackspace.setKeyListener(this);
        mKeySpace.setKeyListener(this);
        mKeyFinished.setKeyListener(this);
    }

    private void addKeysToKeyboard() {
        addView(mKeyBackspace);
        addView(mKeySpace);
        addView(mKeyFinished);
    }

    private void initGridView(Context context) {
        mEmojiGridView = new GridView(context);
        mEmojiGridView.setAdapter(new EmojiGridAdapter(getContext(), getEmojis()));
        addView(mEmojiGridView);
    }

    private List<String> getEmojis() {
        String[] splitStr = EMOJI_LIST.split(" ");
        return new ArrayList<>(Arrays.asList(splitStr));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        layoutGridView();
    }

    private void layoutGridView() {

        final int totalWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int totalHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        float keyWidth = totalWidth * mKeyWeights[0];
        float gridWidth = totalWidth - keyWidth;
        float x = getPaddingLeft() + keyWidth;
        float y = getPaddingTop();
        mColumnWidth = getIdealColumnWidth(gridWidth);
        mEmojiGridView.setColumnWidth(mColumnWidth);
        mEmojiGridView.setNumColumns(GridView.AUTO_FIT);
        mEmojiGridView.setStretchMode(GridView.NO_STRETCH);
        mEmojiGridView.measure(MeasureSpec.makeMeasureSpec((int) gridWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        mEmojiGridView.layout((int) x, (int) y, (int) (x + gridWidth), (int) (y + totalHeight));
    }

    private int getIdealColumnWidth(float gridWidth) {
        float desiredColWidthPx = DESIRED_COLUMN_WIDTH_DP * getResources().getDisplayMetrics().density;
        int numberOfColumns = (int) (gridWidth / desiredColWidthPx);
        return (int) (gridWidth / numberOfColumns);
    }

    @Override
    public void onKeyInput(String text) {
        if (text.equals(FINISHED)) {
            finishKeyboard();
            return;
        }
        super.onKeyInput(text);
    }

    @Override
    public String getDisplayName() {
        return DEFAULT_DISPLAY_NAME;
    }

    @Override
    public void onKeyboardKeyClick() {}

    @Override
    public List<PopupKeyCandidate> getPopupCandidates(Key key) {
        return null;
    }

    class EmojiGridAdapter extends BaseAdapter implements Key.KeyListener {

        private Context mContext;
        private List<String> mEmojiList;

        EmojiGridAdapter(Context c, List<String> emojis) {
            this.mContext = c;
            this.mEmojiList = emojis;
        }

        public int getCount() {
            return mEmojiList.size();
        }

        public String getItem(int position) {
            return mEmojiList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        class GridItemViewHolder {
            KeyText emojiKey;
            GridItemViewHolder(View v) {
                emojiKey = (KeyText) v;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridItem = convertView;
            GridItemViewHolder holder;

            if (gridItem == null) {
                gridItem = getEmojiKey();
                holder = new GridItemViewHolder(gridItem);
                gridItem.setTag(holder);
            } else { // recycling
                holder = (GridItemViewHolder) gridItem.getTag();
            }

            String emoji = getItem(position);
            holder.emojiKey.setText(emoji);

            return gridItem;
        }

        private KeyText getEmojiKey() {
            KeyText key = new KeyText(mContext);
            key.setLayoutParams(new GridView.LayoutParams(mColumnWidth, mColumnWidth));
            key.setTextSize(getPrimaryTextSize());
            key.setKeyColor(getKeyColor());
            key.setPressedColor(getKeyPressedColor());
            key.setBorderColor(getBorderColor());
            key.setBorderWidth(getBorderWidth());
            key.setBorderRadius(getBorderRadius());
            key.setIsRotatedPrimaryText(false);
            int spacing = getKeySpacing();
            key.setPadding(spacing, spacing, spacing, spacing);
            key.setKeyListener(this);
            return key;
        }

        // KeyListener methods

        @Override
        public void onKeyInput(String text) {
            KeyboardEmoji.this.onKeyInput(text);
        }

        @Override
        public void onBackspace() {}
        @Override
        public boolean getIsShowingPopup() {
            return false;
        }
        @Override
        public void showPopup(Key key, int xPosition) {}
        @Override
        public void updatePopup(int xPosition) {}
        @Override
        public void finishPopup(int xPosition) {}
        @Override
        public void onKeyboardKeyClick() {}
        @Override
        public void onNewKeyboardChosen(int xPositionOnPopup) {}
        @Override
        public void onShiftChanged(boolean isShiftOn) {}
    }
}
