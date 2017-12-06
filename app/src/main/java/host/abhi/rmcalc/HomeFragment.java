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
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    public static final String TITLE = "Home";
    private EditText enteredWeight;
    private TextView textResult;
    private NumberPicker noReps;
    private TextView oneRMHeading;

    public static HomeFragment newInstance() {

        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupUI(getView());
        super.onViewCreated(view, savedInstanceState);
    }

    public void setupUI(View view) {
        noReps = (NumberPicker) view.findViewById(R.id.noReps);
        enteredWeight = (EditText) view.findViewById(R.id.enteredWeight);
        textResult =  (TextView) view.findViewById(R.id.oneRepMax);
        oneRMHeading = (TextView) view.findViewById(R.id.oneRepMaxHeading);
        setupEnteredWeight();
        setupRepsComponent();
    }


    public void setupEnteredWeight() {
        final Handler mHandler = new Handler();
        enteredWeight.addTextChangedListener(new TextWatcher() {
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
                    updateResult();
                }
            };
        });

    }

    public void setupRepsComponent() {
        noReps.setMinValue(1);
        noReps.setMaxValue(12);
        noReps.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateResult();
            }
        });
    }

    private double getLiftedWeight() {
        String weightStr = enteredWeight.getText().toString();
        double result;

        try {
            result =  Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            result = 0.0;
        }
        return result;
    }

    private double getEstimatedOneRM(Double liftedWeight, int noReps) {
        // Currently using Brzycki Formula. CanditoTraining FTW!
        // double max = liftedWeight/(1.0278 - (0.0278*noReps));

        // Logic from: https://www.bodybuilding.com/fun/other7.htm
        HashMap<Integer, Double> mFactor = new HashMap<Integer, Double>();
        mFactor.put(1, 1.0);
        mFactor.put(2, 1/0.95);
        mFactor.put(3, 1/0.93);
        mFactor.put(4, 1/0.90);
        mFactor.put(5, 1/0.87);
        mFactor.put(6, 1/0.85);
        mFactor.put(7, 1/0.83);
        mFactor.put(8, 1/0.80);
        mFactor.put(9, 1/0.77);
        mFactor.put(10, 1/0.75);
        mFactor.put(11, 1/0.73);
        mFactor.put(12, 1/0.70);

        double max = liftedWeight * mFactor.get(noReps);
        // some round-off (as per the website)
        max = Math.round(0.5 * Math.round(max * 2));

        return max;
    }

    private void updateTextResult(double liftedWeight, int noReps) {
        double oneRM = getEstimatedOneRM(liftedWeight, noReps);
        textResult.setText(String.format("%3.1f", oneRM));

        Double bodyWeight = getBodyWeight();
        oneRMHeading.setText(String.format("1RM: %3.2f x BW", oneRM/bodyWeight));
    }

    public void updateResult() {
        double weight = getLiftedWeight();
        if (weight != 0.0) {
            // off-by-1 as SeekBar starts from 0
            updateTextResult(weight, noReps.getValue());
        }
        // As it's hidden via XML
        oneRMHeading.setVisibility(View.VISIBLE);
        textResult.setVisibility(View.VISIBLE);
    }

    public Double getBodyWeight() {
        String weightStr = "";
        SharedPreferences prefs = getActivity().getSharedPreferences("rmCalcPrefs", 0);
        if (prefs.contains("body_weight")) {
            weightStr = prefs.getString("body_weight", "");
        }
        if (weightStr == "") {
            return 0.0;
        }
        else {
            return Double.parseDouble(weightStr);
        }
    }
}