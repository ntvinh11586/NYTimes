package com.coderschool.vinh.nytimes.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coderschool.vinh.nytimes.fragments.DatePickerFragment;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.R;

import org.parceler.Parcels;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.text_begin_date)
    TextView tvBeginDate;
    @BindView(R.id.spinner_sort_order)
    Spinner spSortOrder;
    @BindView(R.id.checkbox_arts)
    CheckBox cbArts;
    @BindView(R.id.checkbox_fashion_style)
    CheckBox cbFashionStyle;
    @BindView(R.id.checkbox_sports)
    CheckBox cbSports;

    Calendar currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("Article Filtering");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cbArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbArts.isChecked()) {
                    cbFashionStyle.setChecked(false);
                    cbSports.setChecked(false);
                }
            }
        });

        cbFashionStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbFashionStyle.isChecked()) {
                    cbArts.setChecked(false);
                    cbSports.setChecked(false);
                }
            }
        });

        cbSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSports.isChecked()) {
                    cbFashionStyle.setChecked(false);
                    cbArts.setChecked(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_filter, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:

                if (currentDate != null) {

                    int day = currentDate.get(Calendar.DAY_OF_MONTH);
                    int month = currentDate.get(Calendar.MONTH) + 1;
                    int year = currentDate.get(Calendar.YEAR);

                    String sortOrder = spSortOrder.getSelectedItem().toString();

                    int isArts = cbArts.isChecked() ? 1 : 0;
                    int isFashionStyle = cbFashionStyle.isChecked() ? 1 : 0;
                    int isSport = cbSports.isChecked() ? 1 : 0;

                    Filter filter = new Filter(day, month, year, sortOrder, isArts, isFashionStyle, isSport);

                    Intent data = new Intent();
                    data.putExtra("filter", Parcels.wrap(filter));
                    setResult(RESULT_OK, data);

                    finish();
                } else {
                    Toast.makeText(SearchFilterActivity.this, "Please choose begin date", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDate = Calendar.getInstance();
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, monthOfYear);
        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDay = currentDate.get(Calendar.DAY_OF_MONTH) >= 10 ?
                String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH)) :
                "0" + currentDate.get(Calendar.DAY_OF_MONTH);
        String currentMonth = currentDate.get(Calendar.MONTH) + 1 >= 10 ?
                String.valueOf(currentDate.get(Calendar.MONTH) + 1) :
                "0" + (currentDate.get(Calendar.MONTH) + 1);
        String currentYear = String.valueOf(currentDate.get(Calendar.YEAR));

        tvBeginDate.setText(currentMonth + "/" + currentDay + "/" + currentYear);
    }

    public void showCalendar(View view) {
        showDatePickerDialog(view);
    }
}
