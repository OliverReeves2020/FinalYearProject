package com.example.finalyearproject.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalyearproject.R;
import com.example.finalyearproject.databinding.FragmentMainBinding;


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

        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);



        // Create a listener for preference changes
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                // Handle preference changes here
                Toast.makeText(requireContext(), "change detected", Toast.LENGTH_SHORT).show();
                ProgressBar progressBar = requireView().findViewById(R.id.progressBar2);
                int progress = sharedPreferences.getInt("dailyAmount", 0);
                progressBar.setProgress(progress);

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


        updatebar(view);







    }

    private void updatebar(@NonNull View view) {
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        ProgressBar progressBar;

        TextView textView;

        //set main goal
        int totalDays=sharedPreferences.getInt("totalDays", 0);
        TextView txt=view.findViewById(R.id.CurrentProgressText);
        if(txt.getText().toString().equals(totalDays + " Days")||txt.getText().equals("")) {
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
        setbar(view,progressBar,"dailyAmount");

        //set streak bar
        progressBar = view.findViewById(R.id.streakProgressBar);
        setbarmax(view,progressBar,"highestStreak","currentStreak");
        setbar(view,progressBar,"currentStreak");

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

            if (key.equals("UserStats")) {
                //SharedPreferences UserStats = context.getSharedPreferences("UserStats", Context.MODE_PRIVATE);
                // Update the progress bar with the new value
                updatebar(requireView());


            }
            //if current streak is greater than highest streak or equal but not 0
            if(key.equals("currentStreak")){
                if((sharedPreferences.getInt("highestStreak",0)<=sharedPreferences.getInt("currentStreak",0))
                        &&sharedPreferences.getInt("currentStreak",0)>0){
                    requireView().findViewById(R.id.streakicon).setVisibility(View.VISIBLE);

                    AnimationDrawable animationDrawable = (AnimationDrawable) requireView().findViewById(R.id.streakicon).getBackground();
                    animationDrawable.setEnterFadeDuration(2500);
                    animationDrawable.setExitFadeDuration(5000);
                    animationDrawable.start();
                }
            }

    }


    private void setbar(View view,ProgressBar progressBar, String key){

        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(key, 0);
        progressBar.setProgress(progress);
    }
    private void setbarmax(View view,ProgressBar progressBar, String key,String backup){

        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(key, 0);
        if (progress==0){
            progressBar.setMax(sharedPreferences.getInt(backup, 1));
        }
        else{
            progressBar.setMax(progress);
        }

    }
    private void setTxt(View view,TextView textView, String key){
        SharedPreferences sharedPreferences= requireContext().getSharedPreferences("UserStats", Context.MODE_PRIVATE);
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