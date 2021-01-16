package src;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TimeSync {

    private String url;

    public TimeSync(String url){
        this.url = url;
    }

    public long calculateDelay(LocalDateTime systemtime) {
        return ChronoUnit.NANOS.between(systemtime, getServertime()) / 1000000;
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

            return LocalDateTime.ofInstant(servertimeUTC.toInstant(), ZoneOffset.systemDefault());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
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
