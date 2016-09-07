package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FvsChooserDialog extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fvs_chooser);

        LinearLayout llFvs1 = (LinearLayout) findViewById(R.id.key_fvs1);
        LinearLayout llFvs2 = (LinearLayout) findViewById(R.id.key_fvs2);
        LinearLayout llFvs3 = (LinearLayout) findViewById(R.id.key_fvs3);

        String topFvs1 = "";
        String topFvs2 = "";
        String topFvs3 = "";
        String bottomFvs1 = "";
        String bottomFvs2 = "";
        String bottomFvs3 = "";

        // Get FVS char combos from button
        //Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            topFvs1 = extras.getString("topFvs1");
            topFvs2 = extras.getString("topFvs2");
            topFvs3 = extras.getString("topFvs3");
            bottomFvs1 = extras.getString("bottomFvs1");
            bottomFvs2 = extras.getString("bottomFvs2");
            bottomFvs3 = extras.getString("bottomFvs3");
        } else {
            finish();
            return;
        }

        // Initialize textviews
        TextView tvFvs1Top = (TextView) findViewById(R.id.tvFvs1Top);
        TextView tvFvs2Top = (TextView) findViewById(R.id.tvFvs2Top);
        TextView tvFvs3Top = (TextView) findViewById(R.id.tvFvs3Top);
        TextView tvFvs1Bottom = (TextView) findViewById(R.id.tvFvs1Bottom);
        TextView tvFvs2Bottom = (TextView) findViewById(R.id.tvFvs2Bottom);
        TextView tvFvs3Bottom = (TextView) findViewById(R.id.tvFvs3Bottom);

        // set TextView values
        tvFvs1Top.setText(topFvs1);
        tvFvs2Top.setText(topFvs2);
        tvFvs3Top.setText(topFvs3);
        tvFvs1Bottom.setText(bottomFvs1);
        tvFvs2Bottom.setText(bottomFvs2);
        tvFvs3Bottom.setText(bottomFvs3);

        // set button listeners
        llFvs1.setOnClickListener(this);
        llFvs2.setOnClickListener(this);

        // hide third button if blank
        if (topFvs3.equals("") && bottomFvs3.equals("")) {
            llFvs3.setVisibility(View.GONE);
        } else {
            llFvs3.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();

        switch (v.getId()) {
            case R.id.key_fvs1:

                returnIntent.putExtra("result", MongolUnicodeRenderer.Uni.FVS1);
                setResult(RESULT_OK, returnIntent);
                finish();

                break;
            case R.id.key_fvs2:

                returnIntent.putExtra("result", MongolUnicodeRenderer.Uni.FVS2);
                setResult(RESULT_OK, returnIntent);
                finish();

                break;
            case R.id.key_fvs3:

                returnIntent.putExtra("result", MongolUnicodeRenderer.Uni.FVS3);
                setResult(RESULT_OK, returnIntent);
                finish();

                break;
            default:

                break;
        }

    }

}
