package com.example.finalyearproject;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalyearproject.databinding.FragmentMainBinding;
import com.example.finalyearproject.functions.Alarmsandnotifcation;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class Settings extends Fragment{
        private static final String ARG_SECTION_NUMBER = "section_number";

        private SettingsViewModel settingsViewModel;
        private SharedPreferences notifcationPrefrences;
        private FragmentMainBinding binding;


        public static Settings newInstance(int index) {
            Settings fragment = new Settings();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
            int index = 1;
            if (getArguments() != null) {
                index = getArguments().getInt(ARG_SECTION_NUMBER);
            }
           // settingsViewModel.setIndex(index);






        }

        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_settings, container, false);

        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load notification preferences
        notifcationPrefrences= requireContext().getSharedPreferences("notifactionPrefrences", Context.MODE_PRIVATE);

        //set selected days
        daytextsetter(view,notifcationPrefrences.getStringSet("selectedDays",null));
        //set calendar
        Button btnDayPicker =view.findViewById(R.id.dayPickerButton);
        btnDayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayPick(view,notifcationPrefrences);
            }
        });

        //set selected time
        String s=notifcationPrefrences.getInt("timeHour",12)+":"+notifcationPrefrences.getInt("timeMin",30);
        ((TextView) view.findViewById(R.id.timePickerTextView)).setText(s);
        //set on click listener for time picker
        Button btnOpenTimePicker = view.findViewById(R.id.timePickerButton);

        btnOpenTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepick(view,notifcationPrefrences);
            }
        });

        //set the switches of this activity aswell as their handlers
        setswitches(view,notifcationPrefrences);









    }

    @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }

    private void setswitches(View view, SharedPreferences notifcationPrefrences){

        //set all switch boxes to desired starting state then create listners to change prefrences
        for (int id : new int[] { R.id.checkBoxVibration }) {
            //get switch of id
            SwitchMaterial checkbox = view.findViewById(id);

            //set each switch to correct starting state
            switch(id){
                case R.id.checkBoxVibration:
                    checkbox.setChecked(notifcationPrefrences.getBoolean("Silence",false));
                    break;
            }

            //for each id create listner
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch(id){
                        case R.id.checkBoxVibration:
                            notifcationPrefrences.edit().putBoolean("Silence",isChecked).apply();
                            break;



                    }

                }
            });
        }


    }

    //controls time picker
    public void timepick(View view, SharedPreferences notifcationPrefrences) {


        //create time picker with time options from shared prefrences
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(notifcationPrefrences.getInt("timeHour", 12))
                .setMinute(notifcationPrefrences.getInt("timeMin", 30))
                .setTitleText("Select Time for notifications to start")
                .build();

        // Set an OnPositiveButtonClickListener
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();

                // display it in a TextView
                TextView SelectedTime = view.findViewById(R.id.timePickerTextView);
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                SelectedTime.setText(formattedTime);
                // edit prefrences
                notifcationPrefrences.edit().putInt("timeHour",selectedHour).putInt("timeMin",selectedMinute).apply();

                //call function to delete and create new alarm== update alarm
                new Alarmsandnotifcation().alarmcreator(requireContext(),selectedHour,selectedMinute);

            }
        });


        // Show the time picker
        timePicker.show(getChildFragmentManager(), "timePicker");

    }



    //controls the materialdaypicker dialog
    public void dayPick(View view, SharedPreferences notifcationPrefrences){



        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_daypicker, null);

        // Create the dialog
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(dialogView);


        // Get the MaterialDayPicker view
        MaterialDayPicker materialDayPicker = dialogView.findViewById(R.id.day_picker);



        Set<String> SelectedDays = new HashSet<String>();
        SelectedDays.add("Monday");
        SelectedDays.add("Tuesday");
        SelectedDays.add("Wednesday");
        SelectedDays.add("Thursday");
        SelectedDays.add("Friday");
        SelectedDays.add("Saturday");
        SelectedDays.add("Sunday");
        SelectedDays=notifcationPrefrences.getStringSet("selectedDays",SelectedDays);



        HashMap<Object, MaterialDayPicker.Weekday> dayObjects = new HashMap<>();
        dayObjects.put("Monday", MaterialDayPicker.Weekday.MONDAY);
        dayObjects.put("Tuesday", MaterialDayPicker.Weekday.TUESDAY);
        dayObjects.put("Wednesday", MaterialDayPicker.Weekday.WEDNESDAY);
        dayObjects.put("Thursday", MaterialDayPicker.Weekday.THURSDAY);
        dayObjects.put("Friday", MaterialDayPicker.Weekday.FRIDAY);
        dayObjects.put("Saturday", MaterialDayPicker.Weekday.SATURDAY);
        dayObjects.put("Sunday", MaterialDayPicker.Weekday.SUNDAY);

        List<MaterialDayPicker.Weekday> selectedWeekdays = new ArrayList<>();

        for (String day : SelectedDays) {
            MaterialDayPicker.Weekday weekday = dayObjects.get(day);
            if (weekday != null) {
                selectedWeekdays.add(weekday);
            }
        }

// Now the `selectedWeekdays` list contains the weekday objects
// corresponding to the days selected in the `SelectedDays` set


        //notifcationPrefrences.getStringSet("days",SelectedDays);

        materialDayPicker.setSelectedDays(selectedWeekdays);
        // Set up the MaterialDayPicker as needed

        // Get the OK button and set its click listener
        Button okButton = dialogView.findViewById(R.id.daybutton_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected days and add them to prefrences
                List<MaterialDayPicker.Weekday> newSelection = materialDayPicker.getSelectedDays();
                // Iterate through the newSelection list and get the corresponding keys from dayObjects
                if (newSelection.size()!=0){
                Set<String> selectedDays = new LinkedHashSet<>();
                for (MaterialDayPicker.Weekday weekday : newSelection) {
                    String day = getKey(dayObjects, weekday).toString();
                    System.out.println(day);
                    selectedDays.add(day);
                }
                System.out.println("->"+selectedDays);
                notifcationPrefrences.edit().putStringSet("selectedDays",selectedDays).apply();


                //set text view to correct days
                daytextsetter(view,selectedDays);

                // Handle the OK button click as needed
                dialog.dismiss();}

            }
        });

        // Get the cancel button and set its click listener
        Button cancelButton = dialogView.findViewById(R.id.daybutton_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();






    }

    // helper method to get the key from a HashMap based on a value
    private static <T, E> T getKey(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void daytextsetter(View view,Set<String> selectedDays){

            String newString = "";
            if(selectedDays==null){newString="All Days";}
            else{
                List<String> weekdays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
                List<String> weekends= Arrays.asList("Saturday", "Sunday");
                List<String> alldays= Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday","Sunday");

                if (selectedDays.containsAll(alldays)&& selectedDays.size() == alldays.size()) {
                    newString = "All Days";
                } else if (selectedDays.containsAll(weekdays)&& selectedDays.size() == weekdays.size()) {
                    newString = "Weekdays";
                } else if (selectedDays.containsAll(weekends)&& selectedDays.size() == weekends.size()) {
                    newString = "Weekends";
                } else {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String day : selectedDays) {
                        stringBuilder.append(day.substring(0, 3)).append(", ");
                    }

            newString = stringBuilder.substring(0, stringBuilder.length() - 2);
        }}

            TextView textView= view.findViewById(R.id.daytextView);
            textView.setText(newString);

        }


}