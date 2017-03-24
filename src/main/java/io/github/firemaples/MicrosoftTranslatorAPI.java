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
package io.github.firemaples;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MicrosoftAPI
 * <p>
 * Makes the generic Microsoft Translator API calls. Different service classes then
 * extend this to make the specific service calls.
 * <p>
 * Uses the AJAX Interface V2 - see: http://msdn.microsoft.com/en-us/library/ff512404.aspx
 *
 * @author Jonathan Griggs
 * @author Firemaples (add new Azure framework support) [firemaples at gmail.com]
 */
public abstract class MicrosoftTranslatorAPI {
    //Encoding type
    protected static final String ENCODING = "UTF-8";

    protected static String apiKey;
    @SuppressWarnings("FieldCanBeLocal")
    private static String DatamarketAccessUri = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
    @SuppressWarnings("FieldCanBeLocal")
    private static String OcpApimSubscriptionKeyHeader = "Ocp-Apim-Subscription-Key";
    private static String referrer;
    private static String subscriptionKey;
    private static String token;
    @SuppressWarnings("FieldCanBeLocal")
    private static long tokenCacheExpiration = 5 * 60 * 1000;
    private static long tokenExpiration = 0;
    private static String contentType = "text/plain";

    protected static final String PARAM_APP_ID = "appId=",
            PARAM_TO_LANG = "&to=",
            PARAM_FROM_LANG = "&from=",
            PARAM_TEXT_SINGLE = "&text=",
            PARAM_TEXT_ARRAY = "&texts=",
            PARAM_SPOKEN_LANGUAGE = "&language=",
            PARAM_SENTENCES_LANGUAGE = "&language=",
            PARAM_LOCALE = "&locale=",
            PARAM_LANGUAGE_CODES = "&languageCodes=";

    /**
     * Sets the API key.
     * <p>
     * Note: Should ONLY be used with API Keys generated prior to March 31, 2012. All new applications should obtain a ClientId and Client Secret by following
     * the guide at: http://msdn.microsoft.com/en-us/library/hh454950.aspx
     *
     * @param pKey The API key.
     */
    public static void setKey(final String pKey) {
        apiKey = pKey;
    }

    /**
     * Sets the API key.
     * <p>
     * Note: Should ONLY be used with API Keys generated prior to March 31, 2012. All new applications should obtain a ClientId and Client Secret by following
     * the guide at: http://msdn.microsoft.com/en-us/library/hh454950.aspx
     *
     * @param pKey The API key.
     */
    public static void setContentType(final String pKey) {
        contentType = pKey;
    }

    public static void setSubscriptionKey(String pSubscriptionKey) {
        subscriptionKey = pSubscriptionKey;
    }

    /**
     * Sets the Http Referrer.
     *
     * @param pReferrer The HTTP client referrer.
     */
    public static void setHttpReferrer(final String pReferrer) {
        referrer = pReferrer;
    }

    /**
     * Gets the OAuth access token.
     *
     * @param subscriptionKey The Subscription key
     */
    public static String getToken(final String subscriptionKey) throws Exception {
//       final String params = "grant_type=client_credentials&scope=http://api.microsofttranslator.com"
//               + "&client_id=" + URLEncoder.encode(clientId,ENCODING)
//               + "&client_secret=" + URLEncoder.encode(clientSecret,ENCODING) ;

        final URL url = new URL(DatamarketAccessUri);
        final HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        if (referrer != null) {
            uc.setRequestProperty("referer", referrer);
        }
        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + ENCODING);
        uc.setRequestProperty("Accept-Charset", ENCODING);
        uc.setRequestMethod("POST");
        uc.setDoOutput(true);
        uc.setRequestProperty(OcpApimSubscriptionKeyHeader, subscriptionKey);

        OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
        wr.write("");
        wr.flush();

        try {
            final int responseCode = uc.getResponseCode();
            final String result = inputStreamToString(uc.getInputStream());
            if (responseCode != 200) {
                throw new Exception("Error retrieving token from Microsoft Translator API (" + responseCode + "): " + result);
            }
            return result;
        } finally {
            uc.disconnect();
        }
    }

    public static void resetToken() {
        token = null;
        tokenExpiration = 0;
    }

    /**
     * Forms an HTTP request, sends it using GET method and returns the result of the request as a String.
     *
     * @param url The URL to query for a String response.
     * @return The translated String.
     * @throws Exception on error.
     */
    private static String retrieveResponse(final URL url) throws Exception {
        if (subscriptionKey != null && System.currentTimeMillis() > tokenExpiration) {
            String tokenKey = getToken(subscriptionKey);
            tokenExpiration = System.currentTimeMillis() + tokenCacheExpiration;
            token = "Bearer " + tokenKey;
        }
        final HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        if (referrer != null) {
            uc.setRequestProperty("referer", referrer);
        }
        uc.setRequestProperty("Content-Type", contentType + "; charset=" + ENCODING);
        uc.setRequestProperty("Accept-Charset", ENCODING);
        if (token != null) {
            uc.setRequestProperty("Authorization", token);
        }
        uc.setRequestMethod("GET");
        uc.setDoOutput(true);

        try {
            final int responseCode = uc.getResponseCode();
            final String result = inputStreamToString(uc.getInputStream());
            if (responseCode != 200) {
                throw new Exception("Error retrieving translation from Microsoft Translator API (" + responseCode + "): " + result);
            }
            return result;
        } finally {
            uc.disconnect();
        }
    }

    /**
     * Fetches the JSON response, parses the JSON Response, returns the result of the request as a String.
     *
     * @param url The URL to query for a String response.
     * @return The translated String.
     * @throws Exception on error.
     */
    protected static String retrieveString(final URL url) throws Exception {
        try {
            final String response = retrieveResponse(url);
            return jsonToString(response);
        } catch (Exception ex) {
            throw new Exception("[microsoft-translator-api] Error retrieving translation: " + ex.getMessage(), ex);
        }
    }

    /**
     * Fetches the JSON response, parses the JSON Response as an Array of JSONObjects,
     * retrieves the String value of the specified JSON Property, and returns the result of
     * the request as a String Array.
     *
     * @param url The URL to query for a String response.
     * @return The translated String[].
     * @throws Exception on error.
     */
    protected static String[] retrieveStringArr(final URL url, final String jsonProperty) throws Exception {
        try {
            final String response = retrieveResponse(url);
            return jsonToStringArr(response, jsonProperty);
        } catch (Exception ex) {
            throw new Exception("[microsoft-translator-api] Error retrieving translation: " + ex.getMessage(), ex);
        }
    }

    /**
     * Fetches the JSON response, parses the JSON Response as an array of Strings
     * and returns the result of the request as a String Array.
     * <p>
     * Overloaded to pass null as the JSON Property (assume only Strings instead of JSONObjects)
     *
     * @param url The URL to query for a String response.
     * @return The translated String[].
     * @throws Exception on error.
     */
    protected static String[] retrieveStringArr(final URL url) throws Exception {
        return retrieveStringArr(url, null);
    }

    /**
     * Fetches the JSON response, parses the JSON Response, returns the result of the request as an array of integers.
     *
     * @param url The URL to query for a String response.
     * @return The translated String.
     * @throws Exception on error.
     */
    protected static Integer[] retrieveIntArray(final URL url) throws Exception {
        try {
            final String response = retrieveResponse(url);
            return jsonToIntArr(response);
        } catch (Exception ex) {
            throw new Exception("[microsoft-translator-api] Error retrieving translation : " + ex.getMessage(), ex);
        }
    }

    private static Integer[] jsonToIntArr(final String inputString) throws Exception {
        final JSONArray jsonArr = (JSONArray) JSONValue.parse(inputString);
        Integer[] intArr = new Integer[jsonArr.size()];
        int i = 0;
        for (Object obj : jsonArr) {
            intArr[i] = ((Long) obj).intValue();
            i++;
        }
        return intArr;
    }

    private static String jsonToString(final String inputString) throws Exception {
        //noinspection UnnecessaryLocalVariable
        String json = (String) JSONValue.parse(inputString);
        return json;
    }

    // Helper method to parse a JSONArray. Reads an array of JSONObjects and returns a String Array
    // containing the toString() of the desired property. If propertyName is null, just return the String value.
    private static String[] jsonToStringArr(final String inputString, final String propertyName) throws Exception {
        final JSONArray jsonArr = (JSONArray) JSONValue.parse(inputString);
        String[] values = new String[jsonArr.size()];

        int i = 0;
        for (Object obj : jsonArr) {
            if (propertyName != null && propertyName.length() != 0) {
                final JSONObject json = (JSONObject) obj;
                if (json.containsKey(propertyName)) {
                    values[i] = json.get(propertyName).toString();
                }
            } else {
                values[i] = obj.toString();
            }
            i++;
        }
        return values;
    }

    /**
     * Reads an InputStream and returns its contents as a String.
     * Also effects rate control.
     *
     * @param inputStream The InputStream to read from.
     * @return The contents of the InputStream as a String.
     * @throws Exception on error.
     */
    private static String inputStreamToString(final InputStream inputStream) throws Exception {
        final StringBuilder outputBuilder = new StringBuilder();

        try {
            String string;
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
                while (null != (string = reader.readLine())) {
                    // Need to strip the Unicode Zero-width Non-breaking Space. For some reason, the Microsoft AJAX
                    // services prepend this to every response
                    outputBuilder.append(string.replaceAll("\uFEFF", ""));
                }
            }
        } catch (Exception ex) {
            throw new Exception("[microsoft-translator-api] Error reading translation stream: " + ex.getMessage(), ex);
        }

        return outputBuilder.toString();
    }

    //Check if ready to make request, if not, throw a RuntimeException
    protected static void validateServiceState() throws Exception {
        if (subscriptionKey == null) {
            throw new RuntimeException("Must provide a Windows Azure Marketplace SubscriptionKey - Please see https://www.microsoft.com/cognitive-services/en-us/translator-api/documentation/TranslatorInfo/overview for further documentation");
        }
    }

    protected static String buildStringArrayParam(Object[] values) {
        JSONArray list = new JSONArray();
        String value;
        for (Object obj : values) {
            if (obj != null) {
                value = obj.toString();
                if (value.length() != 0) {
                    //noinspection unchecked
                    list.add(value);
                }
            }
        }
        return list.toJSONString();
    }

}
