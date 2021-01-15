package src;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeSync {

    private String url;

    public TimeSync(String url){
        this.url = url;
    }

    public long calculateDelay(LocalDateTime systemtime) throws Exception {
        LocalDateTime servertime = getServertime();
        return ChronoUnit.NANOS.between(systemtime, servertime) / 1000000;
    }

    /*
     *  Usage:
     *  Sind wir noch vor der Deadline?
     *
     *  boolean inTime = getServerTime().before(DeadLine);
     *
     *  Dabei muss Deadline eine Instanz von LocalDateTime sein!
     */
    public LocalDateTime getServertime(){
        try {
            JSONObject servertimeJSON = readJsonFromUrl(this.url);
            ZonedDateTime servertimeUTC = ZonedDateTime.parse(servertimeJSON.getString("time")).plusNanos(servertimeJSON.getLong("milliseconds") * 1000000);
            LocalDateTime servertime = LocalDateTime.ofInstant(servertimeUTC.toInstant(), ZoneOffset.systemDefault());

            return servertime;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
