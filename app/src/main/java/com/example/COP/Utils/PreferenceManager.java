package com.example.COP.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/** PreferenceManager Class
 * 앱 내부에 저장할 데이터를 자료형에 따라 함수로 만들어져있음.
 * 키값과 데이터를 저장한다.
 * PreferenceManager 통해 저장된 값은, 코드로 지우거나, 앱을 지우지 않는이상 없어지지 않는다.
 * 승,하차 내역 및 유저 정보를 저장하는데 사용하는중이다.
 *
 * beaconData라는 키값으로 통칭하는 방 하나가 있고,
 * 그 방 안에 여러개의 키값으로 데이터를 저장중.
 *
 * 여러개의 키 종류
 *  - payLog : 승하차 정보를 처리하는 키값
 *
 */
public class PreferenceManager {
    public static final String PREFERENCES_NAME = "beaconData";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;
    private static final int DEFAULT_VALUE_INT = -1;
    private static final long DEFAULT_VALUE_LONG = -1L;
    private static final float DEFAULT_VALUE_FLOAT = -1F;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * setJsonArray (): 승하차에 대한 정보(Json Object)를 리스트로 저장
     * @param context : 호출하는 Context
     * @param key     : 저장할 key 값
     * @param object  : Json Object ( ex object : {
     *                                      'state' : '승차',
     *                                      'beaconName' : 'MiniBeacon_123456',
     *                                      'date' : '20-02-11 14:00'
     *                                  })
     *
     *  추가설명 : 오브젝트 단위의 배열을 앱 내부에 저장하기에 가장 좋은방법은 Json을 이용하는것입니다.
     *                다만, 앱내부에 저장할때 json클래스를 지원하지 않기때문에,
     *                json의 내용을 String으로 풀어서 저장한다음, ( setJsonArray )
     *                꺼내 쓸때는 String 형의 데이터를 json화 시켜서 다시 사용합니다. ( getJsonArray )
     */
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


    /**
     * getJsonArray (): 승,하차에 대한 정보(Json Array)를 리턴한다.
     */
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