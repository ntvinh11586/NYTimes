package com.coderschool.vinh.nytimes.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.models.Filter;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FilterDialog extends DialogFragment {
    @BindView(R.id.edit_begin_date)
    DatePicker dpBeginDate;
    @BindView(R.id.spinner_sort_order)
    Spinner spSortOrder;
    @BindView(R.id.checkbox_arts)
    CheckBox cbArts;
    @BindView(R.id.checkbox_fashion_style)
    CheckBox cbFashionStyle;
    @BindView(R.id.checkbox_sports)
    CheckBox cbSports;
    @BindView(R.id.btnOK)
    Button btnOK;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    private Unbinder unbinder;
    private Calendar currentDate;

    public interface FilterDialogListener {
        void onFinishFilterDialog(Filter filter);
    }

    public FilterDialog() {

    }

    public static FilterDialog newInstance() {
        return new FilterDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filter, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnOK.setOnClickListener(v -> {

            int day = dpBeginDate.getDayOfMonth();
            int month = dpBeginDate.getMonth() + 1;
            int year = dpBeginDate.getYear();

            String sortOrder = spSortOrder.getSelectedItem().toString();

            int isArts = cbArts.isChecked() ? 1 : 0;
            int isFashionStyle = cbFashionStyle.isChecked() ? 1 : 0;
            int isSport = cbSports.isChecked() ? 1 : 0;




//            Filter filter = new Filter(
//                    Calendar.getInstance(),
//                    sortOrder,
//
//            );


            Filter filter = new Filter(day, month, year, sortOrder, isArts, isFashionStyle, isSport);

            FilterDialogListener listener = (FilterDialogListener) getActivity();
            listener.onFinishFilterDialog(filter);

            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}



















