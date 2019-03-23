package io.github.firemaples.translate.models;

import java.util.ArrayList;
import java.util.List;

public class TranslationResult extends ArrayList<TranslationResult.Item> {

    public static class Item {
        public DetectedLanguage detectedLanguage;
        public List<Result> translations = new ArrayList<>();
    }

    public static class Result {
        public String text;
        public String to;
    }

    public static class DetectedLanguage {
        public String language;
        public float score;
    }
}
