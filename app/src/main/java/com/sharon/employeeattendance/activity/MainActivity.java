package com.sharon.employeeattendance.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.sharon.employeeattendance.utils.Api;
import com.sharon.employeeattendance.R;
import com.sharon.employeeattendance.utils.RetrofitClient;
import com.sharon.employeeattendance.model.Employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Spinner spinner, spinMonth, spinYear;
    private Button buttonViewAtt;
    private RelativeLayout layout;
    private ArrayList<String> nameList = new ArrayList<>();
    private List<Employees> employlist = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private String empID, fromMonth, toYear, empName, monthName, year;
    private String firstDate, lastDate;


    public void initViews() {
        layout = findViewById(R.id.mainActivityRootLayout);
        spinner = findViewById(R.id.spinner);
        spinMonth = findViewById(R.id.spinnerMonth);
        spinYear = findViewById(R.id.spinnerYear);
        buttonViewAtt = findViewById(R.id.buttonViewAttReport);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        spinner.setOnItemSelectedListener(this);
        spinMonth.setOnItemSelectedListener(this);
        spinYear.setOnItemSelectedListener(this);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getMonth();
        getYear();
        getEmpName();

        buttonViewAtt.setOnClickListener(view -> {
            if (validate()) {
                firstDate = toYear + "-" + fromMonth + "-" + 1;
                lastDate = getLastDay(toYear, fromMonth);
                Intent i = new Intent(getApplicationContext(), AttReportActivity.class);
                i.putExtra("emp_name", empName);
                i.putExtra("emp_id", empID);
                i.putExtra("firstDate", firstDate);
                i.putExtra("lastDate", lastDate);
                i.putExtra("monthName", monthName);
                i.putExtra("year", year);
                startActivity(i);
                finish();
            }

        });

    }

    public boolean validate() {

        if (empID == null) {
            Snackbar.make(layout, "Select Employee Name", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (monthName == null) {
            Snackbar.make(layout, "Select Month", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (year == null) {
            Snackbar.make(layout, "Select Year", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getEmpName() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<List<Employees>> cal1 = api.getEmployee();
        cal1.enqueue(new Callback<List<Employees>>() {
            @Override
            public void onResponse(Call<List<Employees>> call, Response<List<Employees>> response) {
                employlist = response.body();
                assert employlist != null;
                nameList.add("Select Employee");
                for (Employees em : employlist) {
                    Log.d(TAG, "Emp ID: " + em.getEmp_id());
                    Log.d(TAG, "Emp Name: " + em.getName());
                    nameList.add(em.getName());
                    idList.add(em.getEmp_id());
                }

                getEmploye();
                //spinner.setPrompt("PROMPT");
            }

            @Override
            public void onFailure(Call<List<Employees>> call, Throwable t) {
                Snackbar.make(layout, Objects.requireNonNull(t.getMessage()), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static String getLastDay(String year, String month) {

        GregorianCalendar calendar = new GregorianCalendar();
        int yearInt = Integer.parseInt(year);
        int monthInt = Integer.parseInt(month) - 1;
        calendar.set(yearInt, monthInt, 1);
        String dayInt = String.valueOf(calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        Log.d(TAG, "LAST DAY: " + dayInt);
        return (year + "-" + month + "-" + dayInt);
    }

    private void getEmploye() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nameList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void getYear() {
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1995; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(adapter);
    }

    private void getMonth() {
        ArrayAdapter<CharSequence> monthadapter = ArrayAdapter.createFromResource(this, R.array.item_month, android.R.layout.simple_spinner_item);
        monthadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinMonth.setAdapter(monthadapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        if (adapterView.getId() == R.id.spinner) {

            if (i == 0) {
                //Snackbar.make(layout, "Select Employee Name", Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "Position=: " + i);
            } else {
                Employees employees = employlist.get(i-1);
                String id = employees.getEmp_id();
                empName = employees.getName();
                empID = id;
                Log.d(TAG, "Emp id=: " + id);
            }


        }
        if (adapterView.getId() == R.id.spinnerMonth) {
            monthName = spinMonth.getSelectedItem().toString();
            fromMonth = String.valueOf(spinMonth.getSelectedItemPosition() + 1);
            Log.d(TAG, "Month=: " + monthName);
            Log.d(TAG, "Month=: " + fromMonth);
        }
        if (adapterView.getId() == R.id.spinnerYear) {
            year = spinYear.getSelectedItem().toString();
            toYear = String.valueOf(Integer.parseInt(year));
            Log.d(TAG, "Year=: " + year);
            Log.d(TAG, "Year=: " + toYear);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Snackbar.make(layout, "Select Details", Snackbar.LENGTH_SHORT).show();
    }

}
