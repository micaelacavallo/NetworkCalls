package com.example.micaelacavallo.worldcupapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by micaela.cavallo on 10/02/2015.
 */
public class MatchAdapter extends ArrayAdapter<Match> {

    List<Match> mMatches;

    public class ViewHolder {
        public final TextView mTextViewTeams;
        public final TextView mTextViewLocation;

        public ViewHolder(View view) {
            mTextViewTeams = (TextView) view.findViewById(R.id.text_view_teams);
            mTextViewLocation = (TextView) view.findViewById(R.id.text_view_location);
        }
    }


    public MatchAdapter (Context context, List<Match> matches) {
        super (context, R.layout.list_item_matches, matches);
        mMatches = matches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = reuseOrGenerateRowView(convertView, parent);
        displayRepoInRow(position, rowView);
        return super.getView(position, convertView, parent);
    }

    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if (convertView != null)
        {
            rowView = convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_matches, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }
        return rowView;
    }

    private void displayRepoInRow(int position, View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        viewHolder.mTextViewTeams.setText(mMatches.get(position).getmTeams());
        viewHolder.mTextViewLocation.setText(mMatches.get(position).getmLocation());
    }
}
