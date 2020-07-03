package com.example.necoo.reklamyonetimsistemi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    static ListPreference pref = null;
    static Boolean flag = true;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceClickListener api_onclick = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {

            System.out.println("dfgfdg");
            if (preference instanceof EditTextPreference) {
                EditTextPreference textPreference = (EditTextPreference) preference;


            }
            return false;
        }
    };
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                pref = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                FileOps fileOps = new FileOps();

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                fileOps.write(preference.getContext(), "filter.txt", listPreference.getEntries()[index].toString());
                //  System.out.println("settings "+listPreference.getEntries()[index]);

            }
            if (preference instanceof EditTextPreference) {
                ApplicationActivity.companyName = value.toString();
            }


            return true;
        }

    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        preference.setOnPreferenceClickListener(api_onclick);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof EditTextPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } else if (preference instanceof ListPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setupActionBar();


    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName) || PasswordPreferenceFragment.class.getName().equals(fragmentName);

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            ListPreference listPreference = (ListPreference) findPreference("example_list");

            List<String> categories = new ArrayList<>();
            try {
                NodeAPI nodeAPI = new NodeAPI(new URL(getString(R.string.api_url)));
                String jsonResponse = nodeAPI.requestCategories();
                List<JSONObject> jsonObjects = NodeAPI.getJsonObjects(jsonResponse);
                for (JSONObject jsonObject : jsonObjects) {
                    categories.add(jsonObject.getString("Kategori"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            List<CharSequence[]> sequencesList = getTypes(categories);
            listPreference.setEntries(sequencesList.get(0));
            listPreference.setEntryValues(sequencesList.get(1));

            if (flag) {
                listPreference.setValueIndex(0);
                flag = false;
            }


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_list"));
            bindPreferenceSummaryToValue(findPreference("edit_text_preference"));
            System.out.println("VALUE" + listPreference.getEntry());

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PasswordPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_password);
            setHasOptionsMenu(true);
            startActivity(new Intent(getContext(),PasswordChangeActivity.class));
            getActivity().onBackPressed();
        }
    }
    private static List<CharSequence[]> getTypes(List<String> categories) {
        List<CharSequence[]> sequencesList = new ArrayList<>();
        CharSequence[] entries = null;
        CharSequence[] values = null;

        try {
            entries = new CharSequence[categories.size() + 1];
            values = new CharSequence[categories.size() + 1];
            entries[0] = "Hepsi";
            values[0] = "0";
            for (int i = 1; i < categories.size() + 1; i++) {
                entries[i] = categories.get(i - 1);
                values[i] = i + "";
            }
            sequencesList.add(entries);
            sequencesList.add(values);
            return sequencesList;

        } catch (Exception e) {
            e.printStackTrace();
            entries = new CharSequence[1];
            values = new CharSequence[1];
            entries[0] = "Hepsi";
            values[0] = "0";
            sequencesList.add(entries);
            sequencesList.add(values);
            return sequencesList;
        }
    }

    private Context getContext() {
        return getApplicationContext();
    }


}
