package com.firemaples.microsoft_translate_api_demo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.firemaples.language.Language;
import io.github.firemaples.translate.Translate;

public class MainActivity extends AppCompatActivity {

    private List<Language> fromLanguages = new ArrayList<>();
    private List<Language> toLanguages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Translate.setSubscriptionKey(getString(R.string.subscriptionKey));

        fromLanguages.addAll(Arrays.asList(Language.values()));
        toLanguages.addAll(Arrays.asList(Arrays.copyOfRange(Language.values(), 1, Language.values().length)));

        final Spinner sp_from = findViewById(R.id.sp_from);
        final Spinner sp_to = findViewById(R.id.sp_to);
        sp_from.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getLanguageNameList(fromLanguages)));
        sp_to.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getLanguageNameList(toLanguages)));

        findViewById(R.id.bt_translate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_FromText = findViewById(R.id.et_fromText);
                String fromText = et_FromText.getText().toString();

                if (fromText.length() == 0) {
                    return;
                }

                Language from = fromLanguages.get(sp_from.getSelectedItemPosition());
                Language to = toLanguages.get(sp_to.getSelectedItemPosition());

                new TranslateTask(fromText, from, to).execute();
            }
        });
    }

    private List<String> getLanguageNameList(List<Language> languages) {
        List<String> langNames = new ArrayList<>();
        for (Language language : languages) {
            langNames.add(language.name());
        }

        return langNames;
    }

    private class TranslateTask extends AsyncTask<Void, Void, Pair<String, Exception>> {
        private final String text;
        private final Language from;
        private final Language to;

        public TranslateTask(String text, Language from, Language to) {
            this.text = text;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Pair<String, Exception> doInBackground(Void... voids) {
            try {
                return new Pair<>(Translate.execute(text, from, to), null);
            } catch (Exception e) {
                e.printStackTrace();
                return new Pair<>(null, e);
            }
        }

        @Override
        protected void onPostExecute(Pair<String, Exception> result) {
            super.onPostExecute(result);

            EditText et_toText = findViewById(R.id.et_toText);

            if (result.second != null) {
                et_toText.setText(result.second.getMessage());
            } else {
                et_toText.setText(result.first);
            }
        }
    }
}
