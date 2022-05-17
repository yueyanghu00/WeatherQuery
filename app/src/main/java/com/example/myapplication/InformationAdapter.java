package com.example.myapplication;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class InformationAdapter
        extends RecyclerView.Adapter<InformationAdapter.ViewHolder>
        implements ItemTouchStaus{

    private List<String> mCityList;
    private List<Forecast> mShowList;
    private MainActivity mmainActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView data;

        public ViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.information_date);
            data = (TextView) view.findViewById(R.id.information_data);
        }
    }

    public InformationAdapter(List<Forecast> showList,List<String> cityList,MainActivity mainActivity) {
        mCityList = cityList;
        mShowList = showList;
        mmainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.information_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Forecast weather = mShowList.get(position);
        holder.date.setText(weather.getCity());
        holder.data.setText(weather.getWeatherShow());
        Log.d("onBindViewHolder","good");
    }

    private void showConfirm(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mmainActivity).setTitle("提示")
                .setMessage("是否确认删除")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mShowList.remove(position);
                        mCityList.remove(position);
                        notifyItemRemoved(position);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return mShowList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mShowList, fromPosition, toPosition);
        Collections.swap(mCityList,fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public boolean onItemRemove(int position) {
        showConfirm(position);
        return false;
    }
}