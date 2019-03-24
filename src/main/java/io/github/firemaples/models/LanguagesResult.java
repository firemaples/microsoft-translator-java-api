package io.github.firemaples.models;

import java.util.HashMap;

public class LanguagesResult {
    public HashMap<String, TranslationLanguage> translation;

    public static class TranslationLanguage {
        public String name;
        public String nativeName;
        public String dir;
    }
}
