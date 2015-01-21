package net.studymongolian.chimee;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class FavoriteActivityContextMenu extends Fragment implements OnClickListener {

	// Context Menu Item Codes
	public static final int MOVE_TO_FRONT = 1;
	public static final int DELETE = 2;

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
			throw new ClassCastException(activity.toString()
					+ " must implement ContextMenuCallback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.contextmenu_favorite, container, false);

		// Add listeners for all keys
		RelativeLayout rlMoveToFront = (RelativeLayout) view
				.findViewById(R.id.rlContextMenuMoveToFront);
		rlMoveToFront.setOnClickListener(this);
		RelativeLayout rlDelete = (RelativeLayout) view.findViewById(R.id.rlContextMenuDelete);
		rlDelete.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rlContextMenuMoveToFront:
			mCallback.contextMenuItemClicked(MOVE_TO_FRONT);
			break;
		case R.id.rlContextMenuDelete:
			mCallback.contextMenuItemClicked(DELETE);
			break;
		}
	}

}
