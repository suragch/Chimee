package net.studymongolian.chimee;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

public class MyActionProvider extends ActionProvider {

    private Context mContext;

    public MyActionProvider(Context context) {
        super(context);

        mContext = context;
    }

    // for versions older than api 16
    @Override
    public View onCreateActionView() {
        // Inflate the action provider to be shown on the action bar.
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View providerView =
                layoutInflater.inflate(R.layout.my_action_provider, null);
        View myView =
                (View) providerView.findViewById(R.id.blackView);
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myTag", "black view was clicked");
            }
        });
        return providerView;
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        // Inflate the action provider to be shown on the action bar.
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View providerView =
                layoutInflater.inflate(R.layout.my_action_provider, null);
        View myView =
                (View) providerView.findViewById(R.id.blackView);
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myTag", "black view was clicked");
            }
        });
        return providerView;
    }


}
