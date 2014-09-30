package com.example.tabswipeexample;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.timroes.android.listview.EnhancedListView;

public class GamesFragment extends Fragment {

	private EnhancedListAdapter mAdapter;
	private EnhancedListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_games, container, false);

		mListView = (EnhancedListView)rootView.findViewById(R.id.list);

		mAdapter = new EnhancedListAdapter();
		mAdapter.resetItems();

		mListView.setAdapter(mAdapter);
		mListView.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
			/**
			 * This method will be called when the user swiped a way or deleted it via
			 * {@link de.timroes.android.listview.EnhancedListView#delete(int)}.
			 *
			 * @param listView The {@link EnhancedListView} the item has been deleted from.
			 * @param position The position of the item to delete from your adapter.
			 * @return An {@link de.timroes.android.listview.EnhancedListView.Undoable}, if you want
			 *      to give the user the possibility to undo the deletion.
			 */
			@Override
			public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {

				final String item = (String) mAdapter.getItem(position);
				mAdapter.remove(position);
				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						mAdapter.insert(position, item);
					}
				};
			}
		});

		// Show toast message on click and long click on list items.
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getActivity(), "Clicked on item " + mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
			}
		});
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getActivity(), "Long clicked on item " + mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
				return true;
			}
		});

		mListView.setSwipingLayout(R.id.swiping_layout);

		applySettings();

		return rootView;
	}
	private void applySettings() {

		EnhancedListView.UndoStyle style = EnhancedListView.UndoStyle.SINGLE_POPUP;;
		mListView.setUndoStyle(style);
		mListView.enableSwipeToDismiss();
		EnhancedListView.SwipeDirection direction = EnhancedListView.SwipeDirection.BOTH;
		mListView.setSwipeDirection(direction);

		// Enable or disable swiping layout feature
		mListView.setSwipingLayout(R.id.swiping_layout);
	}

	@Override
	public void onStop() {
		if(mListView != null) {
			mListView.discardUndo();
		}
		super.onStop();
	}

	public void resetItems(View view) {
		mListView.discardUndo();
		mAdapter.resetItems();
	}

	private class EnhancedListAdapter extends BaseAdapter {

		private List<String> mItems = new ArrayList<String>();

		void resetItems() {
			mItems.clear();
			for(int i = 1; i <= 40; i++) {
				mItems.add("Item " + i);
			}
			notifyDataSetChanged();
		}

		public void remove(int position) {
			mItems.remove(position);
			notifyDataSetChanged();
		}

		public void insert(int position, String item) {
			mItems.add(position, item);
			notifyDataSetChanged();
		}

		/**
		 * How many items are in the data set represented by this Adapter.
		 *
		 * @return Count of items.
		 */
		@Override
		public int getCount() {
			return mItems.size();
		}

		/**
		 * Get the data item associated with the specified position in the data set.
		 *
		 * @param position Position of the item whose data we want within the adapter's
		 *                 data set.
		 * @return The data at the specified position.
		 */
		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		/**
		 * Get the row id associated with the specified position in the list.
		 *
		 * @param position The position of the item within the adapter's data set whose row id we want.
		 * @return The id of the item at the specified position.
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Get a View that displays the data at the specified position in the data set. You can either
		 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
		 * parent View (GridView, ListView...) will apply default layout parameters unless you use
		 * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
		 * to specify a root view and to prevent attachment to the root.
		 *
		 * @param position    The position of the item within the adapter's data set of the item whose view
		 *                    we want.
		 * @param convertView The old view to reuse, if possible. Note: You should check that this view
		 *                    is non-null and of an appropriate type before using. If it is not possible to convert
		 *                    this view to display the correct data, this method can create a new view.
		 *                    Heterogeneous lists can specify their number of view types, so that this View is
		 *                    always of the right type (see {@link #getViewTypeCount()} and
		 *                    {@link #getItemViewType(int)}).
		 * @param parent      The parent that this view will eventually be attached to
		 * @return A View corresponding to the data at the specified position.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if(convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, parent, false);
				// Clicking the delete icon, will read the position of the item stored in
				// the tag and delete it from the list. So we don't need to generate a new
				// onClickListener every time the content of this view changes.
				final View origView = convertView;
				convertView.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mListView.delete(((ViewHolder)origView.getTag()).position);
					}
				});

				holder = new ViewHolder();
				assert convertView != null;
				holder.mTextView = (TextView) convertView.findViewById(R.id.text);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.position = position;
			holder.mTextView.setText(mItems.get(position));

			return convertView;
		}

		private class ViewHolder {
			TextView mTextView;
			int position;
		}

	}
}
