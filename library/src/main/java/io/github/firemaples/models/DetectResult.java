package io.github.firemaples.models;

import java.util.ArrayList;
import java.util.List;

public class DetectResult extends ArrayList<DetectResult.Result> {
    public static class Result extends Alternative {
        public List<Alternative> alternatives;
    }

    public static class Alternative {
        public String language;
        public float score;
        public boolean isTranslationSupported;
        public boolean isTransliterationSupported;
    }
}
