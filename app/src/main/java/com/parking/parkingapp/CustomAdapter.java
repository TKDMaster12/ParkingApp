package com.parking.parkingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtStatus;
        TextView txtTotalAmount;
        TextView txtAmountLeft;
    }

    CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.parking_lot_item, data);
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.parking_lot_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.name);
            viewHolder.txtStatus = convertView.findViewById(R.id.status);
            viewHolder.txtAmountLeft = convertView.findViewById(R.id.amount_left);
            viewHolder.txtTotalAmount = convertView.findViewById(R.id.total_amount);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtStatus.setText(dataModel.getStatus());
        viewHolder.txtAmountLeft.setText(dataModel.getAmountLeft());
        viewHolder.txtTotalAmount.setText(dataModel.getTotalAmount());
        // Return the completed view to render on screen
        return convertView;
    }
}