package io.github.firemaples.models;

import java.util.ArrayList;
import java.util.List;

public class TranslationResult extends ArrayList<TranslationResult.Result> {

    public static class Result {
        public DetectedLanguage detectedLanguage;
        public List<Translation> translations = new ArrayList<>();
    }

    public static class Translation {
        public String text;
        public String to;
    }

    public static class DetectedLanguage {
        public String language;
        public float score;
    }
}
