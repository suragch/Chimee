package net.studymongolian.chimee;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class InputWindowContextMenu extends Fragment implements OnClickListener {

	// Context Menu Item Codes
	public static final int COPY = 1;
	public static final int PASTE = 2;
	public static final int FAVORITES = 3;
	public static final int CLEAR = 4;
	
	ContextMenuCallback mCallback;
	
	// Container Activity must implement this interface
	public interface ContextMenuCallback {

		public void contextMenuItemClicked(int itemCode);

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (ContextMenuCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ContextMenuCallback");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.contextmenu_inputwindow, container, false);

		// Add listeners for all keys
		RelativeLayout rlCopy = (RelativeLayout) view.findViewById(R.id.rlContextMenuCopy);
		rlCopy.setOnClickListener(this);
		RelativeLayout rlPaste = (RelativeLayout) view.findViewById(R.id.rlContextMenuPaste);
		rlPaste.setOnClickListener(this);
		RelativeLayout rlFavorites = (RelativeLayout) view
				.findViewById(R.id.rlContextMenuFavorites);
		rlFavorites.setOnClickListener(this);
		RelativeLayout rlClear = (RelativeLayout) view.findViewById(R.id.rlContextMenuClear);
		rlClear.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rlContextMenuCopy:
			mCallback.contextMenuItemClicked(COPY);
			break;
		case R.id.rlContextMenuPaste:
			mCallback.contextMenuItemClicked(PASTE);
			break;
		case R.id.rlContextMenuFavorites:
			mCallback.contextMenuItemClicked(FAVORITES);
			break;
		case R.id.rlContextMenuClear:
			mCallback.contextMenuItemClicked(CLEAR);
			break;
		}
	}

}
