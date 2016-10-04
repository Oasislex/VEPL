package by.scherbakov.vepl;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SBT-Sherbakov-AI on 13.05.2016.
 */
public class PlayerStatsGridViewAdapter extends ArrayAdapter<PlayerStats> {

    Context context;
    int resource;
    ArrayList<PlayerStats> data = new ArrayList<>();

    public PlayerStatsGridViewAdapter(Context context, int resource, ArrayList<PlayerStats> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        StatsHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new StatsHolder();
            holder.opponentView = (TextView) row.findViewById(R.id.grid_opponent_text);
            holder.mpView = (TextView) row.findViewById(R.id.grid_mp_text);
            holder.gsView = (TextView) row.findViewById(R.id.grid_gs_text);
            holder.aView = (TextView) row.findViewById(R.id.grid_a_text);
            holder.csView = (TextView) row.findViewById(R.id.grid_cs_text);
            holder.ogView = (TextView) row.findViewById(R.id.grid_og_text);
            row.setTag(holder);
        } else {
            holder = (StatsHolder) row.getTag();
        }

        PlayerStats item = data.get(position);
        holder.opponentView.setText(item.getOpponent());
        holder.mpView.setText(item.getMinutesPlayed());
        holder.gsView.setText(item.getGoalsScores());
        holder.aView.setText(item.getAssists());
        holder.csView.setText(item.getCleanSheets());
        holder.ogView.setText(item.getOwnGoals());
        if(position != 0){
            holder.opponentView.setTextSize(20);
            holder.mpView.setTextSize(20);
            holder.gsView.setTextSize(20);
            holder.aView.setTextSize(20);
            holder.csView.setTextSize(20);
            holder.ogView.setTextSize(20);
        }
        return row;

    }

    static class StatsHolder{
        TextView opponentView;
        TextView mpView;
        TextView gsView;
        TextView aView;
        TextView csView;
        TextView ogView;

    }
}
