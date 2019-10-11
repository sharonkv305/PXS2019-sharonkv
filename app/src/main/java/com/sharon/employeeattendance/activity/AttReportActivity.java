package com.sharon.employeeattendance.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sharon.employeeattendance.utils.Api;
import com.sharon.employeeattendance.adapter.EmployeReportAdapter;
import com.sharon.employeeattendance.model.EmployeeAttDetails;
import com.sharon.employeeattendance.R;
import com.sharon.employeeattendance.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttReportActivity extends AppCompatActivity {
    private static final String TAG = "AttReportActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout tableHeader, hoursLoggedTableLayout;
    private TextView textViewEmpName, textViewNorecords;
    public TextView textViewHoursLogged, textViewDaysPresent, textViewDaysAbsent;

    private EmployeReportAdapter employeReportAdapter;
    private List<EmployeeAttDetails> empAttList = new ArrayList<>();

    private String empId, empName, firstDate, lastDate, monthName, year;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initView() {

        recyclerView = findViewById(R.id.employeReportRecyclerView);
        progressBar = findViewById(R.id.employeReportProgressBar);
        tableHeader = findViewById(R.id.empReportTableHeader);
        textViewEmpName = findViewById(R.id.textViewEmployeName);
        textViewNorecords = findViewById(R.id.textViewNoreports);
        hoursLoggedTableLayout = findViewById(R.id.hoursLoggedtableLayout);

        textViewHoursLogged = findViewById(R.id.textViewHoursLogged);
        textViewDaysPresent = findViewById(R.id.textViewDaysPresent);
        textViewDaysAbsent = findViewById(R.id.textViewDaysAbsent);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("History");
        textViewEmpName.setText(empName + "’s Attendance Report for " + monthName + " " + year);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att_report);
        Intent i = getIntent();
        empId = i.getExtras().getString("emp_id");
        empName = i.getExtras().getString("emp_name");
        firstDate = i.getExtras().getString("firstDate");
        lastDate = i.getExtras().getString("lastDate");
        monthName = i.getExtras().getString("monthName");
        year = i.getExtras().getString("year");
        Log.d(TAG, "empId: " + empId);
        Log.d(TAG, "empName: " + empName);
        Log.d(TAG, "firstDate: " + firstDate);
        Log.d(TAG, "lastDate: " + lastDate);
        initView();
        getAttDetails();


    }

    private void getAttDetails() {
        progressBar.setVisibility(View.VISIBLE);
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<List<EmployeeAttDetails>> call = api.getAttDet(empId, firstDate, lastDate);
        call.enqueue(new Callback<List<EmployeeAttDetails>>() {
            @Override
            public void onResponse(Call<List<EmployeeAttDetails>> call, Response<List<EmployeeAttDetails>> response) {
                progressBar.setVisibility(View.GONE);
                empAttList = response.body();
                assert empAttList != null;
                if (!empAttList.isEmpty()) {
                    tableHeader.setVisibility(View.VISIBLE);
                    hoursLoggedTableLayout.setVisibility(View.VISIBLE);
                } else {
                    textViewNorecords.setVisibility(View.VISIBLE);

                }
                //employeReportAdapter.notifyDataSetChanged();
                for (EmployeeAttDetails em : empAttList) {
                    Log.d(TAG, "EMp ID: " + em.getEmp_id());
                    Log.d(TAG, "EMp Enrty: " + em.getEntry_at());
                    Log.d(TAG, "EMp Exit: " + em.getExit_at());
                }
                employeReportAdapter = new EmployeReportAdapter(empAttList, textViewHoursLogged, textViewDaysPresent, textViewDaysAbsent);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(employeReportAdapter);
            }

            @Override
            public void onFailure(Call<List<EmployeeAttDetails>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());

                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
