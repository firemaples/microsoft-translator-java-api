package io.github.firemaples.models;

import java.util.ArrayList;

public class TextArrayRequest extends ArrayList<TextArrayRequest.Text> {
    private TextArrayRequest() {
    }

    public static TextArrayRequest build(String... texts) {
        TextArrayRequest request = new TextArrayRequest();
        for (String text : texts) {
            request.add(new Text(text));
        }
        return request;
    }

    public static class Text {
        private String Text;

        public Text(String text) {
            Text = text;
        }
    }
}


