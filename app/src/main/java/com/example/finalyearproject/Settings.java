package com.example.finalyearproject;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalyearproject.databinding.FragmentMainBinding;


public class Settings extends Fragment{
        private static final String ARG_SECTION_NUMBER = "section_number";

        private SettingsViewModel settingsViewModel;
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
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }


}