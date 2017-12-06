package host.abhi.rmcalc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SettingsFragment extends Fragment {

    public static final String TITLE = "Settings";
    private EditText bodyWeight;
    private SharedPreferences myPrefs;
    private static final String PREFS_NAME = "rmCalcPrefs";

    public static SettingsFragment newInstance() {

        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bodyWeight = (EditText) view.findViewById(R.id.bodyWeight);

        bodyWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        final Handler mHandler = new Handler();
        bodyWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(userStoppedTyping, 500); // 500ms
            }

            Runnable userStoppedTyping = new Runnable() {

                @Override
                public void run() {
                    myPrefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString("body_weight", bodyWeight.getText().toString());
                    editor.commit();
                }
            };
        });



        // Fetch from preferences on load
        String weight = getBodyWeight();
        bodyWeight.setText(weight);

        super.onViewCreated(view, savedInstanceState);
    }

    public String getBodyWeight() {
        String weight = "";
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (prefs.contains("body_weight")) {
            weight = prefs.getString("body_weight", "");
        }
        return weight;
    }

}