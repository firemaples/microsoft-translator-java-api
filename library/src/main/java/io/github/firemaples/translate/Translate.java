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
package io.github.firemaples.translate;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import io.github.firemaples.MicrosoftTranslatorAPI;
import io.github.firemaples.language.Language;
import io.github.firemaples.models.TextArrayRequest;
import io.github.firemaples.models.TranslationResult;
import io.github.firemaples.utils.TypeReference;

/**
 * Translate
 * <p>
 * Makes calls to the Microsoft Translator API /Translate service
 * <p>
 * Uses the AJAX Interface V2 - see: http://msdn.microsoft.com/en-us/library/ff512406.aspx
 *
 * @author Jonathan Griggs [jonathan.griggs at gmail.com]
 * @author Firemaples (add new Azure framework support) [firemaples at gmail.com]
 */
public final class Translate extends MicrosoftTranslatorAPI<TextArrayRequest, TranslationResult> {
    private static Translate instance = new Translate();

    private static final String SERVICE_URL = "api.cognitive.microsofttranslator.com/translate?api-version=3.0";
//    private static final String ARRAY_SERVICE_URL = "api.microsofttranslator.com/V2/Ajax.svc/TranslateArray?";
//    private static final String ARRAY_JSON_OBJECT_PROPERTY = "TranslatedText";

    //prevent instantiation
    private Translate() {
    }

    /**
     * Translates text from a given Language to another given Language using Microsoft Translator.
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to   The language code to translate to.
     * @return The translated String.
     * @throws Exception on error.
     */
    public static String execute(final String text, final Language from, final Language to) throws Exception {
//        //Run the basic service validations first
//        validateServiceState(text);
//        final String params =
//                (apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey, ENCODING) : "")
//                        + PARAM_FROM_LANG + URLEncoder.encode(from.toString(), ENCODING)
//                        + PARAM_TO_LANG + URLEncoder.encode(to.toString(), ENCODING)
//                        + PARAM_TEXT_SINGLE + URLEncoder.encode(text, ENCODING);
//
//        final URL url = new URL(getProtocol() + SERVICE_URL + params);
//        //noinspection UnnecessaryLocalVariable
//        final String response = retrieveString(url);
//        return response;

        TranslationResult result = retrieveResult(from, to, text);
        if (result != null && result.size() > 0) {
            List<TranslationResult.Translation> translations = result.get(0).translations;
            if (translations != null && translations.size() > 0) {
                return translations.get(0).text;
            }
        }
        throw new IllegalStateException("Parsing result failed");
    }

    /**
     * Translates text from a given Language to another given Language using Microsoft Translator.
     * <p>
     * Default the from to AUTO_DETECT
     *
     * @param text The String to translate.
     * @param to   The language code to translate to.
     * @return The translated String.
     * @throws Exception on error.
     */
    public static String execute(final String text, final Language to) throws Exception {
        return execute(text, Language.AUTO_DETECT, to);
    }

    /**
     * Translates an array of texts from a given Language to another given Language using Microsoft Translator's TranslateArray
     * service
     * <p>
     * Note that the Microsoft Translator expects all source texts to be of the SAME language.
     *
     * @param texts The Strings Array to translate.
     * @param from  The language code to translate from.
     * @param to    The language code to translate to.
     * @return The translated Strings Array[].
     * @throws Exception on error.
     */
    public static String[] execute(final String[] texts, final Language from, final Language to) throws Exception {
        //Run the basic service validations first
//        validateServiceState(texts);
//        final String params =
//                (apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey, ENCODING) : "")
//                        + PARAM_FROM_LANG + URLEncoder.encode(from.toString(), ENCODING)
//                        + PARAM_TO_LANG + URLEncoder.encode(to.toString(), ENCODING)
//                        + PARAM_TEXT_ARRAY + URLEncoder.encode(buildStringArrayParam(texts), ENCODING);
//
//        final URL url = new URL(getProtocol() + ARRAY_SERVICE_URL + params);
//        //noinspection UnnecessaryLocalVariable
//        final String[] response = retrieveStringArr(url, ARRAY_JSON_OBJECT_PROPERTY);
//        return response;
        TranslationResult result = retrieveResult(from, to, texts);

        if (result != null && result.size() > 0) {
            String[] resultArr = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                List<TranslationResult.Translation> translations = result.get(i).translations;
                if (translations != null && translations.size() > 0) {
                    resultArr[i] = translations.get(0).text;
                } else {
                    resultArr[i] = "";
                }
            }
            return resultArr;
        }
        throw new IllegalStateException("Parsing result failed");
    }

    /**
     * Translates an array of texts from an Automatically detected language to another given Language using Microsoft Translator's TranslateArray
     * service
     * <p>
     * Note that the Microsoft Translator expects all source texts to be of the SAME language.
     * <p>
     * This is an overloaded convenience method that passes Language.AUTO_DETECT as fromLang to
     * execute(texts[],fromLang,toLang)
     *
     * @param texts The Strings Array to translate.
     * @param to    The language code to translate to.
     * @return The translated Strings Array[].
     * @throws Exception on error.
     */
    public static String[] execute(final String[] texts, final Language to) throws Exception {
        return execute(texts, Language.AUTO_DETECT, to);
    }

    /**
     * Translates an array of texts from a given Language to another given Language using Microsoft Translator's TranslateArray
     * service
     * <p>
     * Note that the Microsoft Translator expects all source texts to be of the SAME language.
     *
     * @param from  The language code to translate from.
     * @param to    The language code to translate to.
     * @param texts The Strings Array to translate.
     * @return The translated Strings Array[].
     * @throws Exception on error.
     */
    public static TranslationResult retrieveResult(final Language from, final Language to, String... texts) throws Exception {
        //Run the basic service validations first
        validateServiceState(texts);
        final String params =
                PARAM_FROM_LANG + URLEncoder.encode(from.toString(), ENCODING)
                        + PARAM_TO_LANG + URLEncoder.encode(to.toString(), ENCODING);

        final URL url = new URL(PROTOCOL_HTTPS + SERVICE_URL + params);
        //noinspection UnnecessaryLocalVariable
        TranslationResult result = instance.retrieveResponseV3(url, HTTP_POST, TextArrayRequest.build(texts), new TypeReference<TranslationResult>() {
        });
        return result;
    }

    private static void validateServiceState(final String[] texts) throws Exception {
        if (texts.length > 100) {
            throw new RuntimeException("TEXT_COUNT_OVER_LIMIT - Microsoft Translator (Translate) can handle up to 100 texts per request");
        }
        String json = instance.toJsonString(TextArrayRequest.build(texts));
        if (json.length() > 5000) {
            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (Translate) can handle up to 5,000 characters per request");
        }
        validateServiceState();
    }


//    private static void validateServiceState(final String text) throws Exception {
//        String json = instance.toJsonString(TextArrayRequest.build(text));
//        if (json.length() > 5000) {
//            throw new RuntimeException("TEXT_TOO_LARGE - Microsoft Translator (Translate) can handle up to 5,000 characters per request");
//        }
//        validateServiceState();
//    }


}
