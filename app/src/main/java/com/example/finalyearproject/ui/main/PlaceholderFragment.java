package com.example.finalyearproject.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalyearproject.R;
import com.example.finalyearproject.databinding.FragmentMainBinding;
import com.example.finalyearproject.datatypes.AchievementData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);


        // Get a reference to the shared preferences

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);


        // Create a listener for preference changes
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // Handle preference changes here
                Toast.makeText(requireContext(), "change detected", Toast.LENGTH_SHORT).show();
                ProgressBar progressBar = requireView().findViewById(R.id.progressBar2);
                int progress = sharedPreferences.getInt("dailyAmount", 0);
                progressBar.setProgress(progress);

                progressBar = requireView().findViewById(R.id.progressBar4);
                progressBar.setProgress(sharedPreferences.getInt("CurrentAchieves", 0));
                progressBar.setMax(sharedPreferences.getInt("MaxAchieves", 0));

                setStreakIconandText();
            }
        };

        // Register the listener with the shared preferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);


    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        //    view.findViewWithTag("blur").setRenderEffect(RenderEffect.createBlurEffect(20,20, Shader.TileMode.DECAL));
        //    view.findViewWithTag("blur").setOutlineAmbientShadowColor(Color.argb(40,00,00,00));
        //}
        setStreakIconandText();
        updatebar(view);

        //set achievements up
        achievementSetup(view);


    }

    private void achievementSetup(@NonNull View view) {

        //creates a table full of achievement buttons based on read date from a csv file
        TableLayout buttonsTableLayout = view.findViewById(R.id.buttonsTableLayout);

        List<AchievementData> achievementDataList = loadButtonDataFromCsv(requireContext(),"achievements.csv");

        int rowCount = 3; // Number of rows in the table
        int colCount = (int) Math.ceil((double) achievementDataList.size() / rowCount); // Number of columns in the table
        int badgecount=0;
        for (int i = 0; i < rowCount; i++) {
            TableRow row = new TableRow(requireContext());

            for (int j = 0; j < colCount; j++) {
                int index = i * colCount + j;
                if (index >= achievementDataList.size()) {
                    break;
                }
                badgecount+=1;

                Button button = new Button(requireContext());
                button.setBackground(requireContext().getDrawable(R.drawable.badge));
                button.setWidth(50);
                button.setHeight(50);
                button.setGravity(1);
                Drawable background = button.getBackground();


                AchievementData achievementData = achievementDataList.get(index);
                try{
                background.setTint(Color.parseColor(achievementData.getIconUrl()));} catch (
                        Exception e) {
                    background.setTint(Color.BLUE);
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(achievementData.getDescription(),achievementData.getText());
                    }
                });
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();

                button.setLayoutParams(layoutParams);
                row.addView(button);

            }
            TableLayout.LayoutParams lp =new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            if(i==1){
            lp.setMargins(80,10,10,10);}
            else{
                lp.setMargins(10,10,10,10);
            }
            row.setLayoutParams(lp);
            buttonsTableLayout.addView(row,lp);
        }

        requireContext().getSharedPreferences("UserStats",Context.MODE_PRIVATE).edit().putInt("CurrentAchieves",badgecount).commit();

    }

    private void showPopup(String popupText,String popupTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(popupTitle);
        builder.setMessage(popupText);
        builder.show();
    }


    public List<AchievementData> loadButtonDataFromCsv(Context context, String filename) {
        List<AchievementData> achievementDataList = new ArrayList<>();

        try {

            FileInputStream inputStream = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String achieveText = data[0];
                String achieveIconUrl = data[1];
                String achieveDescription = data[2];

                AchievementData achievementData = new AchievementData(achieveText, achieveIconUrl,achieveDescription);
                achievementDataList.add(achievementData);
            }

            reader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return achievementDataList;
    }


    private void setStreakIconandText() {
        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        if ((sharedPreferences1.getInt("highestStreak", 0) <= sharedPreferences1.getInt("currentStreak", 0))
                && sharedPreferences1.getInt("currentStreak", 0) > 0) {
            requireView().findViewById(R.id.streakicon).setVisibility(View.VISIBLE);

        } else {
            requireView().findViewById(R.id.streakicon).setVisibility(View.INVISIBLE);
        }
    }

    private void updatebar(@NonNull View view) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        ProgressBar progressBar;

        TextView textView;

        //set main goal
        int totalDays = sharedPreferences.getInt("totalDays", 0);
        TextView txt = view.findViewById(R.id.CurrentProgressText);
        if (txt.getText().toString().equals(totalDays + " Days") || txt.getText().equals("")) {
            int maxDays = getNextMultipleOfSeven(totalDays);
            //get current main goal progress bar
            progressBar = view.findViewById(R.id.MainGoal);
            //set the max to next 7 multiple based on total days
            progressBar.setMax(maxDays);
            //set the current bar to total days
            setbar(view, progressBar, "totalDays");
            //set current text to total days
            textView = view.findViewById(R.id.CurrentProgressText);
            String s = totalDays + " Days";
            textView.setText(s);
            //set the aim text to next 7th
            textView = view.findViewById(R.id.GoalProgressText);
            s = maxDays + " Days";
            textView.setText(s);
        }


        //set progress bar
        progressBar = view.findViewById(R.id.progressBar2);
        setbar(view, progressBar, "dailyAmount");

        //set streak bar
        progressBar = view.findViewById(R.id.streakProgressBar);
        setbarmax(view, progressBar, "highestStreak", "currentStreak");
        setbar(view, progressBar, "currentStreak");

        progressBar = requireView().findViewById(R.id.progressBar4);
        progressBar.setProgress(sharedPreferences.getInt("CurrentAchieves", 0));
        progressBar.setMax(sharedPreferences.getInt("MaxAchieves", 0));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the listener with the shared preferences when the fragment is destroyed
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        binding = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("xxxxx" + key);
        if (key.equals("UserStats")) {
            //SharedPreferences UserStats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
            // Update the progress bar with the new value
            System.out.println("xxxxx");
            updatebar(requireView());
            SharedPreferences sharedPref = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);

            System.out.println("-->--" + sharedPref.getInt("highestStreak", 0) + sharedPreferences.getInt("currentStreak", 0));
            if ((sharedPreferences.getInt("highestStreak", 0) <= sharedPreferences.getInt("currentStreak", 0))
                    && sharedPreferences.getInt("currentStreak", 0) > 0) {
                System.out.println("-->--");
                requireView().findViewById(R.id.streakicon).setVisibility(View.VISIBLE);

            }

        }

    }


    private void setbar(View view, ProgressBar progressBar, String key) {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(key, 0);
        progressBar.setProgress(progress);
    }

    private void setbarmax(View view, ProgressBar progressBar, String key, String backup) {

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(key, 0);
        if (progress == 0) {
            progressBar.setMax(sharedPreferences.getInt(backup, 1));
        } else {
            progressBar.setMax(progress);
        }

    }

    private void setTxt(View view, TextView textView, String key) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        String txt = String.valueOf(sharedPreferences.getInt(key, 0));
        textView.setText(txt);
    }


    //return multiple of 7 greater than x if 1 day return 7 days if 8 days return 14 days
    public static int getNextMultipleOfSeven(int x) {
        if (x <= 0) {
            return 7;
        }
        int remainder = x % 7;
        if (remainder == 0) {
            return x;
        } else {
            return x + 7 - remainder;
        }
    }


}