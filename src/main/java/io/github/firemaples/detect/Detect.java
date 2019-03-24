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
package io.github.firemaples.detect;

import java.net.URL;

import io.github.firemaples.MicrosoftTranslatorAPI;
import io.github.firemaples.language.Language;
import io.github.firemaples.models.DetectResult;
import io.github.firemaples.models.TextArrayRequest;
import io.github.firemaples.utils.TypeReference;

/**
 * Detect
 * <p>
 * Provides an interface to the Microsoft Translator Detect service method
 * <p>
 * Uses the AJAX Interface V2 - see: http://msdn.microsoft.com/en-us/library/ff512396.aspx
 *
 * @author Jonathan Griggs [jonathan.griggs at gmail.com]
 * @author Firemaples (add new Azure framework support) [firemaples at gmail.com]
 */
public final class Detect extends MicrosoftTranslatorAPI<TextArrayRequest, DetectResult> {
    private static Detect instance = new Detect();
    private static final String SERVICE_URL = "api.cognitive.microsofttranslator.com/detect?api-version=3.0";
//    private static final String ARRAY_SERVICE_URL = "api.microsofttranslator.com/V2/Ajax.svc/DetectArray?";

    // prevent instantiation
    private Detect() {
    }

    /**
     * Detects the language of a supplied String.
     *
     * @param text The String to detect the language of.
     * @return A String containing the language
     * @throws Exception on error.
     */
    public static Language execute(final String text) throws Exception {
//        //Run the basic service validations first
//        validateServiceState(text);
//        final URL url = new URL(getProtocol() + SERVICE_URL
//                + (apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey, ENCODING) : "")
//                + PARAM_TEXT_SINGLE + URLEncoder.encode(text, ENCODING));
//
//        final String response = retrieveString(url);
//        return Language.fromString(response);
        DetectResult results = retrieveResult(text);
        if (results != null && !results.isEmpty()) {
            return Language.fromString(results.get(0).language);
        }
        throw new IllegalStateException("Parsing result failed");
    }

    /**
     * Detects the language of all supplied Strings in array.
     *
     * @return A String array containing the detected languages
     * @throws Exception on error.
     */
    public static String[] execute(final String[] texts) throws Exception {
//        //Run the basic service validations first
//        validateServiceState(texts);
//        final String textArr = buildStringArrayParam(texts);
//        final URL url = new URL(getProtocol() + ARRAY_SERVICE_URL
//                + (apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey, ENCODING) : "")
//                + PARAM_TEXT_ARRAY + URLEncoder.encode(textArr, ENCODING));
//        //noinspection UnnecessaryLocalVariable
//        final String[] response = retrieveStringArr(url);
//        return response;
        DetectResult results = retrieveResult(texts);
        if (results != null && results.size() > 0) {
            String[] resultArray = new String[results.size()];
            for (int i = 0; i < results.size(); i++) {
                resultArray[i] = results.get(i).language;
            }
            return resultArray;
        }
        throw new IllegalStateException("Parsing result failed");
    }

    public static DetectResult retrieveResult(String... texts) throws Exception {
        //Run the basic service validations first
        validateServiceState(texts);
        final URL url = new URL(PROTOCOL_HTTPS + SERVICE_URL);
        //noinspection UnnecessaryLocalVariable
        DetectResult result = instance.retrieveResponseV3(url, HTTP_POST, TextArrayRequest.build(texts), new TypeReference<DetectResult>() {
        });
        return result;
    }

//    private static void validateServiceState(final String text) throws Exception {
//        final int byteLength = text.getBytes(ENCODING).length;
//        if (byteLength > 10240) {
//            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (Detect) can handle up to 10,240 bytes per request");
//        }
//        validateServiceState();
//    }

    private static void validateServiceState(final String[] texts) throws Exception {
        if (texts.length > 100) {
            throw new RuntimeException("TEXT_COUNT_OVER_LIMIT - Microsoft Translator (Detect) can handle up to 100 texts per request");
        }
        for (String text : texts) {
            String json = instance.toJsonString(new TextArrayRequest.Text(text));
            if (json.length() > 10000) {
                throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (Detect) can handle up to 10,000 characters per array element");
            }
        }

        String json = instance.toJsonString(TextArrayRequest.build(texts));
        if (json.length() > 50000) {
            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (Detect) can handle up to 50,000 characters per request");
        }

        validateServiceState();
    }

}
