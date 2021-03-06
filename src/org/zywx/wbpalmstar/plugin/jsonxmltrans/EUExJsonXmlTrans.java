package org.zywx.wbpalmstar.plugin.jsonxmltrans;

import android.content.Context;
import android.util.Log;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class EUExJsonXmlTrans extends EUExBase {
    public static final String TAG = "EUExJsonXmlTrans";
    public static final String FUN_ON_CALLBACK = "javascript:uexJsonXmlTrans.cbTransFinished";
    private static final String CAN_NOT_GET_CONTENT = "未读取到文件内容，请检查文件路径";
    private static final String NO_PARAM = "请传入参数";
    private static final String JSON_PARSE_ERROR = "JSON 解析出错";
    private static final String XML_PARSE_ERROR = "XML 解析出错";



    private Context context;

    public EUExJsonXmlTrans(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
        this.context = context;
    }
    public void json2xml(String [] params) {
        String result;
        if (params.length == 0) {
            result = NO_PARAM;
            onCallback(FUN_ON_CALLBACK + "('" + result +"')");
            return;
        }
        String data = getText(params[0]);
        if (CAN_NOT_GET_CONTENT.equals(data)) {
            onCallback(FUN_ON_CALLBACK + "('" + data +"')");
            return;
        }
        try {
            JSONObject obj = new JSONObject (data);
            result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + XML.toString(obj);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            result = JSON_PARSE_ERROR;
        }
        onCallback(FUN_ON_CALLBACK + "('" + result + "')");
    }


    public void xml2json(String [] params) {
        String result;
        if (params.length == 0) {
            result = NO_PARAM;
            onCallback(FUN_ON_CALLBACK + "('" + result +"')");
            return;
        }
        String data = getText(params[0]);
        if (CAN_NOT_GET_CONTENT.equals(data)) {
            onCallback(FUN_ON_CALLBACK + "('" + data +"')");
            return;
        }
        try {
            result = XML.toJSONObject(data).toString();
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            result = XML_PARSE_ERROR;
        }
        onCallback(FUN_ON_CALLBACK + "('" + result + "')");
    }

    public String getText(String param) {
        String data = null;
        if (param.startsWith(BUtility.F_Widget_RES_SCHEMA) || param.startsWith(BUtility.F_APP_SCHEMA)
                || param.startsWith(BUtility.F_WIDGET_SCHEMA) || param.startsWith(BUtility.F_SDCARD_PATH)) {
            String realPath = BUtility.makeRealPath(
                    BUtility.makeUrl(mBrwView.getCurrentUrl(), param),
                    mBrwView.getCurrentWidget().m_widgetPath,
                    mBrwView.getCurrentWidget().m_wgtType);
            if (param.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
                data =  getFromAssets(realPath);
            } else {
                data = readDataFromFile (realPath);
            }
            if (data.equals("")) {
                data = CAN_NOT_GET_CONTENT ;
            }
        } else {
            data = param;
        }
        return data;
    }
    public String readDataFromFile(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        String str = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String strTemp = null;
            while ((strTemp = reader.readLine()) != null) {
                str = str + strTemp;
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }
        return str;
    }

    public String getFromAssets(String fileName){
        String result = "";
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[]  buffer = new byte[length];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf8");
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }
        return result;
    }
    @Override
    protected boolean clean() {
        return false;
    }
}
