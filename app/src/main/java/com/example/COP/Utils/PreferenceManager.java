package com.example.COP.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/** PreferenceManager Class
 * 앱 내부에 저장할 데이터를 자료형에 따라 함수로 만들어져있음.
 * 키값과 데이터를 저장한다.
 *
 */
public class PreferenceManager {
    public static final String PREFERENCES_NAME = "smart_sitting01";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void setJsonArray(Context context, String key, JSONObject object){
        JSONArray jsonArray;
        try{
            String logList = getString(context,key);    // 키값과 Context로 메모리에 저장된 데이터를 가져온다
            if(logList.equals("")){                       // 만약 저장된 정보가 없다면
                jsonArray = new JSONArray();                // Array에 object를 바로 넣는다.
                jsonArray.put(object);
            }
            else{                                         // 만약 저장된 정보가 있다면
                jsonArray = new JSONArray(logList);         // 저장된 정보를 먼저 가져와서
                jsonArray.put(object);                      // object를 기존의 array에 추가한다
            }
            setString(context, key, jsonArray.toString());  // 변경된 값을 String형으로 바꿔 저장한다.
        }catch (Exception e){
            Log.i("seo","PreferenceManager.java setJsonArray() -> Error to Parshing Json : " + e);
        }
    }

    public static JSONArray getJsonArray(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        String logList = prefs.getString(key, DEFAULT_VALUE_STRING);
        JSONArray jsonArray = null;
        try{
            jsonArray = new JSONArray(logList);
        }catch (Exception e){
            Log.i("seo","PreferenceManager.java getJsonArray() -> Error to Parshing Json : " + e);

        }
        return jsonArray;
    }



    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setInt(Context context, String key, int value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */

    public static void setLong(Context context, String key, long value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setFloat(Context context, String key, float value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        String value = prefs.getString(key, DEFAULT_VALUE_STRING);
        return value;
    }
    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }
    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        int value = prefs.getInt(key, DEFAULT_VALUE_INT);
        return value;
    }
    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        long value = prefs.getLong(key, DEFAULT_VALUE_LONG);
        return value;
    }
    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        float value = prefs.getFloat(key, DEFAULT_VALUE_FLOAT);
        return value;
    }
    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }
    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

}