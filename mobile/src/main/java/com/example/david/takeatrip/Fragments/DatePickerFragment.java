package com.example.david.takeatrip.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.david.takeatrip.Utilities.Constants;
import com.example.david.takeatrip.Utilities.DatesUtils;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar cal;

        Bundle args = getArguments();
        String currentDate = args.getString(Constants.CURRENT_DATE_ID);
        String dateFormat = args.getString(Constants.DATE_FORMAT_ID);

        if (currentDate != null && dateFormat != null) {
            cal = DatesUtils.getDateFromString(currentDate, dateFormat);
        } else {
            Log.i("TEST", "null bundle, setting default date to current");
            cal = Calendar.getInstance();
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);


        // Calling Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }


}
