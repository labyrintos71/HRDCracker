package Cracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class HRDCracker {

    public void loginQuery(String id,String pw) {
        parseID(
                postQuery("http://app.hrd.go.kr/qrattend/loginProc.jsp?id="+encryptionString(id)+"&pw="+encryptionString(pw)));
    }

    public void getClassQuery(String trpid) {
        parseClass(
                postQuery("http://app.hrd.go.kr/qrattend/retriveTrList.jsp?trp_id="+encryptionString(trpid)));
    }

    public void checkLocation(String trpid,String latitude,String longitude) {
        if(
                parseCheck(
                        postQuery("http://app.hrd.go.kr/qrattend/retriveTrList.jsp?trp_id="+encryptionString(trpid)+"&"+"latitude="+encryptionString(latitude)+"&"+"longitude="+encryptionString(longitude))))
            System.out.println("OK");
        else
            System.out.println("ERROR");

    }

    public void postAttendQuery(String ID,String time, String attend) {
        postQuery(generateURL(ID,time,attend,"37.0000000","126.0000000"));
        AttendCheck(ID);
    }

    public String postQuery(String query) {
        URL url=null;
        URLConnection urlconn=null;
        StringBuffer result=new StringBuffer();
        String str;
        try {
            url=new URL(query);
            urlconn=url.openConnection();
            //((HttpURLConnection)urlconn).setRequestMethod("POST");
            urlconn.setDoOutput(true);
            urlconn.setRequestProperty("Connection", "Close");
            urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlconn.setRequestProperty("DeviceModel", "SM-G955N");
            urlconn.setRequestProperty("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.0; SM-G955N Build/NRD90M)");
            urlconn.setRequestProperty("OsVerison", "24");
            urlconn.setRequestProperty("Host", "app.hrd.go.kr");
            urlconn.setRequestProperty("Timezone", "Asia/Seoul");
            urlconn.setRequestProperty("Accept-Encoding", "gzip");
            urlconn.connect();

            BufferedReader in=new BufferedReader(new InputStreamReader(urlconn.getInputStream(),"UTF-8"));
            while((str=in.readLine())!=null)
            {
                result.append(str);
            }
            System.out.println("SERVER RETURN : "+result.toString());
            return result.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        return "ERROR";
    }

    public String generateURL(String ID,String time,String att,String latitude, String longitude) {
        String query;
        query="http://app.hrd.go.kr/qrattend/sendAtdInfo.jsp?"+
                "trpr_id="+encryptionString("ABA20173000474502")+"&"+
                "trpr_degr=" + encryptionString("1")+"&"+
                "trpr_gbn_cd=" + encryptionString("C0041")+"&"+
                "trp_id=" + encryptionString(ID)+"&"+
                "latitude=" + encryptionString(latitude)+"&"+
                "longitude=" + encryptionString(longitude)+"&"+
                "qr_string=" + encryptionString("TRACSE_ID=ABA20173000474502,TRACSE_TME=1,CRSE_TRACSE_SE=C0041,NW_INO=201200926")+"&"+
                "imei=" + encryptionString(encryptSHA1("357156080458366"))+"&"+
                "tag_dtm=" + encryptionString(time)+"&"+
                "retry_yn=" + encryptionString("N")+"&"+
                "in_retir_gbn="+encryptionString(att);

        return query;
    }

    public boolean parseCheck(JSONObject jsonObj) {
        if(jsonObj.get("result_cd").equals("1000")) return true;
        else return false;
    }

    public boolean parseCheck(String data) {
        JSONObject jsonObj = new JSONObject(data);
        if(jsonObj.get("result_cd").equals("1000")) return true;
        else return false;
    }

    public void getResult(String data) {
        JSONObject jsonObj= new JSONObject(data);
        jsonObj.get("result_msg");
        jsonObj.get("result_cd");
    }
    public void parseID(String data) {
        JSONObject jsonObj = new JSONObject(data);
        if(parseCheck(jsonObj)) {
            jsonObj.get("tpr_nm");
            jsonObj.get("tpr_id");
        }else {
            jsonObj.get("result_msg");
            jsonObj.get("result_cd");
        }
    }

    public void parseClass(String data) {
        JSONObject jsonObj = new JSONObject(data);
        if(parseCheck(jsonObj)) {
            JSONArray infoArray = (JSONArray) jsonObj.get("list");
            JSONObject tempObj=(JSONObject)infoArray.get(0);
            tempObj.get("trpr_id");
            tempObj.get("trpr_stdt");
            tempObj.get("trpr_endt");
            tempObj.get("trpr_gbn_nm");
            tempObj.get("trpr_gbn_cd");
            tempObj.get("trpr_nm");
            tempObj.get("tra_nm");
            tempObj.get("trpr_degr");
            tempObj.get("atd_first_tm");
            tempObj.get("atd_last_tm");
            tempObj.get("atd_cnt");
            tempObj.get("ino");

        }else {
            jsonObj.get("result_msg");
            jsonObj.get("result_cd");
        }
    }

    public void AttendCheck(String ID) {
        String data=postQuery("http://app.hrd.go.kr/qrattend/retriveTrList.jsp?trp_id="+encryptionString(ID));
        JSONObject jsonObj = new JSONObject(data);
        if(parseCheck(jsonObj)) {
            JSONArray infoArray = (JSONArray) jsonObj.get("list");
            JSONObject tempObj=(JSONObject)infoArray.get(0);
            String first=tempObj.get("atd_first_tm").toString();
            String last=tempObj.get("atd_last_tm").toString();
            String count=tempObj.get("atd_cnt").toString();
            if(!first.equals("null"))
                first=first.substring(0, 2)+"시"+first.substring(2, 4)+"분"+first.substring(4, 6)+"초";
            if(!last.equals("null"))
                last=last.substring(0, 2)+"시"+first.substring(2, 4)+"분"+first.substring(4, 6)+"초";
        }else {
            jsonObj.get("result_msg");
            jsonObj.get("result_cd");
        }

    }

    public String encryptionString(String paramString){
        try
        {
            // "QRApp^.^" 키로 DES 디코딩  ->  base 64 디코딩
            Object localObject = new DESKeySpec("QRApp^.^".getBytes("UTF8"));
            localObject = SecretKeyFactory.getInstance("DES").generateSecret((KeySpec)localObject);
            SecureRandom localSecureRandom = new SecureRandom();
            Cipher localCipher = Cipher.getInstance("DES");
            localCipher.init(1, (Key)localObject, localSecureRandom);
            paramString =Base64.getEncoder().encodeToString(localCipher.doFinal(paramString.getBytes()));
            paramString=URLEncoder.encode(paramString,StandardCharsets.UTF_8.toString());
            return paramString;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public String encryptSHA1(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String decrypt(String input) throws Exception {
        input=URLDecoder.decode(input,StandardCharsets.UTF_8.toString());
        Cipher cipher = Cipher.getInstance( "DES" );
        cipher.init( Cipher.DECRYPT_MODE,SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec("QRApp^.^".getBytes("UTF8") )));
        byte [] outputBytes = cipher.doFinal(Base64.getDecoder().decode(input));
        return new String( outputBytes, "UTF8" );
    }
}
