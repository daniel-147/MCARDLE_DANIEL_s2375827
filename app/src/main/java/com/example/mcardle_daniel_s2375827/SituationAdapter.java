package com.example.mcardle_daniel_s2375827;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.ArrayList;

public class SituationAdapter extends BaseAdapter {
    Context context;
    ArrayList<Situation> situations;
    LayoutInflater inflter;

    public SituationAdapter(Context applicationContext, ArrayList<Situation> situationList) {
        this.context = applicationContext;
        this.situations = situationList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return situations.size();
    }

    @Override
    public Object getItem(int i) {
        return situations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SituationAdapter.WidgetsHolder holder = null;
        if(view == null) { //first time, create view and bound to widgets
            view = inflter.inflate(R.layout.layout_situation, null);
            holder = new SituationAdapter.WidgetsHolder();
            holder.text = (TextView) view.findViewById(R.id.situationText);
            holder.icon1 = (ImageView) view.findViewById(R.id.icon);
            holder.icon2 = (ImageView) view.findViewById(R.id.icon2);
            view.setTag(holder); //store bound objects into the view
        }
        else{ //already existing, just reuse bound objects
            holder = (SituationAdapter.WidgetsHolder)view.getTag();
        }

        Situation s = situations.get(i);
        String listText = "Incident type:" + s.getType() + "\nIncident description: " + s.getComment();
        holder.text.setText(listText);

        // Colour the row based on the delay
        String[] impact = s.getImpact();
        if (impact != null && impact.length >= 2) {
            Log.e("colour debug", "IN COLOUR LOOP");
            if (impact[0].equalsIgnoreCase("delays")){
                Log.e("colour debug", "IN DELAYS LOOP");
                if (impact[1].equalsIgnoreCase("delaysOfUncertainDuration")){
                    view.setBackgroundColor(context.getResources().getColor(R.color.delaysOfUncertainDuration));
                } else if (impact[1].equalsIgnoreCase("delays")){
                    view.setBackgroundColor(context.getResources().getColor(R.color.delays));
                } else if (impact[1].equalsIgnoreCase("longDelays")){
                    view.setBackgroundColor(context.getResources().getColor(R.color.longDelays));
                } else if (impact[1].equalsIgnoreCase("veryLongDelays")) {
                    view.setBackgroundColor(context.getResources().getColor(R.color.veryLongDelays));
                } else {
                    view.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
            } else {
                view.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        } else{
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        String url = s.getSourceURL();
        String type = s.getType();
        if (url.contains("UnplannedEvents")){
            holder.icon1.setImageResource(R.drawable.unplanned);
        } else {
            holder.icon1.setImageResource(R.drawable.roadworks);
        }

        if (type.equalsIgnoreCase("MaintenanceWorks")){
            holder.icon2.setImageResource(R.drawable.maintenance);
        } else if (type.equalsIgnoreCase("RoadOrCarriagewayOrLaneManagement")){
            holder.icon2.setImageResource(R.drawable.road_or_carriageway);
        } else if (type.equalsIgnoreCase("GeneralObstruction")){
            holder.icon2.setImageResource(R.drawable.general_obstruction);
        } else if (type.equalsIgnoreCase("Accident")){
            holder.icon2.setImageResource(R.drawable.accident);
        } else {
            holder.icon2.setImageIcon(null);
        }

        return view;
    }
    static class WidgetsHolder { //preserve objects bound to widgets
        TextView text;
        ImageView icon1;
        ImageView icon2;
    }
}