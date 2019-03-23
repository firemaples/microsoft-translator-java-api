package io.github.firemaples.translate.models;

import java.util.ArrayList;

public class TranslationRequest extends ArrayList<TranslationRequest.Text> {
    private TranslationRequest() {
    }

    public static TranslationRequest build(String... texts) {
        TranslationRequest request = new TranslationRequest();
        for (String text : texts) {
            request.add(new Text(text));
        }
        return request;
    }

    static class Text {
        private String Text;

        private Text(String text) {
            Text = text;
        }
    }
}


