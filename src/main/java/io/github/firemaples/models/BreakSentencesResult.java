package io.github.firemaples.models;

import java.util.ArrayList;

public class BreakSentencesResult extends ArrayList<BreakSentencesResult.Result> {
    public static class Result {
        public Integer[] sentLen;
        public DetectedLanguage detectedLanguage;
    }

    public static class DetectedLanguage {
        public String language;
        public float score;
    }
}
