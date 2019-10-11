package com.sharon.employeeattendance.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.sharon.employeeattendance.model.EmployeeAttDetails;
import com.sharon.employeeattendance.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class EmployeReportAdapter extends RecyclerView.Adapter<EmployeReportAdapter.MyViewHolder> {

    private List<EmployeeAttDetails> empAttList;
    private ArrayList<String> hours = new ArrayList<>();
    private TextView hoursLogged, present, absent;

    public EmployeReportAdapter(List<EmployeeAttDetails> empAttList, TextView hoursLogged, TextView present, TextView absent) {
        this.empAttList = empAttList;
        this.hoursLogged = hoursLogged;
        this.present = present;
        this.absent = absent;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employe_report_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        EmployeeAttDetails employeeAttDetails = empAttList.get(position);
        holder.textViewMonth.setText(getDateNum(employeeAttDetails.getEntry_at()));
        holder.textViewHoursLogged.setText(cal(employeeAttDetails.entry_at, employeeAttDetails.exit_at));
        hours.add(cal(employeeAttDetails.entry_at, employeeAttDetails.exit_at));


    }

    @Override
    public int getItemCount() {
        return empAttList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewMonth, textViewHoursLogged;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMonth = itemView.findViewById(R.id.textViewMonthDate);
            textViewHoursLogged = itemView.findViewById(R.id.textViewHoursLogged);
            hoursLogged.setText(findSum(hours));
            present.setText(Integer.toString(empAttList.size()));
//            absent.setText("2");
            findSum(hours);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getDateNum(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newDateStr = null;
        try {
            newDateStr = simpleDateFormat.format(Objects.requireNonNull(simpleDateFormat.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert newDateStr != null;
        String[] str = newDateStr.split("-");
        return str[2];
    }


    private String cal(String dateStart, String dateStop) {
        //HH converts hour in 24 hours format (0-23), day calculation
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        String min = null;
        String total = null;

        try {
            d1 = format.parse(dateStart);
            Date d2 = format.parse(dateStop);

            //in milliseconds
            assert d2 != null;
            assert d1 != null;
            long diff = d2.getTime() - d1.getTime();

            final long diffSeconds = diff / 1000 % 60;
            final long diffMinutes = diff / (60 * 1000) % 60;
            final long diffHours = diff / (60 * 60 * 1000) % 24;
            final long diffDays = diff / (24 * 60 * 60 * 1000);
            String hour = Long.toString(diffHours);
            min = Long.toString(diffMinutes);
            total = hour.concat("." + min);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public String findSum(ArrayList<String> hours) {
        ArrayList<Float> floatList = new ArrayList<>();

        for (int i = 0; i < hours.size(); i++) {
            floatList.add(Float.parseFloat(hours.get(i)));
        }
        float sum = 0;
        for (int i = 0; i < floatList.size(); i++) {
            sum += floatList.get(i);
        }

        DecimalFormat df = new DecimalFormat("#.00000");
        df.format(sum);

        return df.format(sum);
    }


}
