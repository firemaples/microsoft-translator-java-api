/*
 * microsoft-translator-java-api
 *
 * Copyright 2012 Jonathan Griggs [jonathan.griggs at gmail.com].
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.firemaples.sentence;

import java.net.URL;
import java.net.URLEncoder;

import io.github.firemaples.MicrosoftTranslatorAPI;
import io.github.firemaples.language.Language;
import io.github.firemaples.models.BreakSentencesResult;
import io.github.firemaples.models.DetectResult;
import io.github.firemaples.models.TextArrayRequest;
import io.github.firemaples.utils.TypeReference;

/**
 * BreakSentences
 * <p>
 * Provides an interface to the Microsoft Translator BreakSentences service
 * <p>
 * This service is basically a utility for determining how Microsoft Translator is
 * interpreting sentence breaks within a given string of text
 * <p>
 * Uses the AJAX Interface V2 - see: http://msdn.microsoft.com/en-us/library/ff512395.aspx
 *
 * @author Jonathan Griggs [jonathan.griggs at gmail.com]
 * @author Firemaples (add new Azure framework support) [firemaples at gmail.com]
 */
public final class BreakSentences extends MicrosoftTranslatorAPI<TextArrayRequest, BreakSentencesResult> {
    private static BreakSentences instance = new BreakSentences();

    private static final String SERVICE_URL = "api.cognitive.microsofttranslator.com/breaksentence?api-version=3.0";

    // prevent instantiation
    private BreakSentences() {
    }

    /**
     * Reports the number of sentences detected and the length of those sentences
     *
     * @param text     The String to break into sentences
     * @param fromLang The Language of origin
     * @return an array of integers representing the size of each detected sentence
     * @throws Exception on error.
     */
    public static Integer[] execute(final String text, final Language fromLang) throws Exception {
//        //Run the basic service validations first
//        validateServiceState(text, fromLang);
//        final URL url = new URL(getProtocol() + SERVICE_URL
//                + (apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey, ENCODING) : "")
//                + PARAM_SENTENCES_LANGUAGE + URLEncoder.encode(fromLang.toString(), ENCODING)
//                + PARAM_TEXT_SINGLE + URLEncoder.encode(text, ENCODING));
//
//        //noinspection UnnecessaryLocalVariable
//        final Integer[] response = retrieveIntArray(url);
//        return response;
        BreakSentencesResult results = retrieveResult(fromLang, text);
        if (results != null && !results.isEmpty()) {
            return results.get(0).sentLen;
        }
        throw new IllegalStateException("Parsing result failed");
    }

    public static BreakSentencesResult retrieveResult(Language fromLang, String... texts) throws Exception {
        //Run the basic service validations first
        validateServiceState(texts);
        final URL url = new URL(PROTOCOL_HTTPS + SERVICE_URL
                + PARAM_SENTENCES_LANGUAGE + URLEncoder.encode(fromLang.toString(), ENCODING));
        //noinspection UnnecessaryLocalVariable
        BreakSentencesResult result = instance.retrieveResponseV3(url, TextArrayRequest.build(texts), new TypeReference<BreakSentencesResult>() {
        });
        return result;
    }

    private static void validateServiceState(final String[] texts) throws Exception {
        if (texts.length > 100) {
            throw new RuntimeException("TEXT_COUNT_OVER_LIMIT - Microsoft Translator (BreakSentences) can handle up to 100 texts per request");
        }
        for (String text : texts) {
            String json = instance.toJsonString(new TextArrayRequest.Text(text));
            if (json.length() > 10000) {
                throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (BreakSentences) can handle up to 10,000 characters per array element");
            }
        }

        String json = instance.toJsonString(TextArrayRequest.build(texts));
        if (json.length() > 50000) {
            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (BreakSentences) can handle up to 50,000 characters per request");
        }

        validateServiceState();
    }

//    private static void validateServiceState(final String text, final Language fromLang) throws Exception {
//        final int byteLength = text.getBytes(ENCODING).length;
//        if (byteLength > 10240) {
//            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (BreakSentences) can handle up to 10,240 bytes per request");
//        }
//        if (Language.AUTO_DETECT.equals(fromLang)) {
//            throw new RuntimeException("BreakSentences does not support AUTO_DETECT Langauge. Please specify the origin language");
//        }
//        validateServiceState();
//    }
}
