package net.studymongolian.chimee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageHistoryListAdapter extends ArrayAdapter<Message> {

	MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
	
	// View lookup cache
	private static class ViewHolder {
		TextView tvDate;
		TextView tvMessage;
	}

	public MessageHistoryListAdapter(Context context, ArrayList<Message> messages) {
		super(context, R.layout.message_history_listview_item, messages);
		//converter = new MongolUnicodeRenderer();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//android.os.Debug.waitForDebugger();
		
		// Get the data item for this position
		Message message = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.message_history_listview_item, parent,
					false);
			viewHolder.tvDate = (TextView) convertView
					.findViewById(R.id.tvHistoryListDateItem);
			viewHolder.tvMessage = (TextView) convertView
					.findViewById(R.id.tvHistoryListMessageItem);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String formattedDate = convertDate(message.getDate()); // TODO
		
		// Populate the data into the template view using the data object
		viewHolder.tvDate.setText(formattedDate);
		viewHolder.tvMessage.setText(renderer.unicodeToGlyphs(message.getMessage()));
		
		// Return the completed view to render on screen
		return convertView;
	}
	
	private String convertDate(long unixMilliseconds) {
		
		//long unixSeconds = 1372339860;
		Date date = new Date(unixMilliseconds); 
		SimpleDateFormat sdf = new SimpleDateFormat("yy.M.d H:mm:ss");
		return sdf.format(date);
	}
}