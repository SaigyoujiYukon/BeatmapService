package com.edlplan.beatmapservice.site.sayo;

import com.edlplan.beatmapservice.Util;
import com.edlplan.beatmapservice.site.BeatmapInfo;
import com.edlplan.beatmapservice.site.BeatmapInfoV2;
import com.edlplan.beatmapservice.site.IBeatmapDetailSite;
import com.edlplan.beatmapservice.site.IBeatmapSetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SayoBeatmapDetailSite implements IBeatmapDetailSite {

    @Override
    public List<BeatmapInfo> getBeatmapInfo(IBeatmapSetInfo setInfo) {
        try {
            URL url = new URL("https://api.sayobot.cn/v2/beatmapinfo?0=" + setInfo.getBeatmapSetID());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Util.modifyUserAgent(connection);
            JSONObject obj = new JSONObject(Util.readFullString(connection.getInputStream()));
            //if (BuildConfig.DEBUG) {
            //    System.out.println(obj.toString(2));
            //}
            if (obj.getInt("status") != 0) {
                return null;
            }
            //System.out.println(obj.toString(2));
            obj = obj.getJSONObject("data");
            double bpm = obj.getDouble("bpm");
            JSONArray array = obj.getJSONArray("bid_data");
            List<BeatmapInfo> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject bd = array.getJSONObject(i);
                BeatmapInfo info = new BeatmapInfo();
                info.setBpm(bpm);
                info.setBid(bd.getInt("bid"));
                info.setMode(bd.getInt("mode"));
                info.setVersion(bd.getString("version"));
                info.setLength(bd.getInt("length"));
                info.setCircleSize(bd.getDouble("CS"));
                info.setApproachRate(bd.getDouble("AR"));
                info.setOverallDifficulty(bd.getDouble("OD"));
                info.setHP(bd.getDouble("HP"));
                info.setStar(bd.getDouble("star"));
                info.setAim(bd.getDouble("aim"));
                info.setSpeed(bd.getDouble("speed"));
                info.setPP(bd.getDouble("pp"));
                info.setCircleCount(bd.getInt("circles"));
                info.setSliderCount(bd.getInt("sliders"));
                info.setSpinnerCount(bd.getInt("spinners"));
                info.setMaxCombo(bd.getInt("maxcombo"));
                info.setPlaycount(bd.getInt("playcount"));
                info.setPasscount(bd.getInt("passcount"));
                if (bd.getString("bg").trim().length() != 0) {
                    info.setBackgroundUrl(String.format(
                            Locale.getDefault(),
                            "https://txy1.sayobot.cn/beatmaps/files/%d/%s",
                            setInfo.getBeatmapSetID(), bd.getString("bg")));
                }
                info.setStrainAim(divideToStrain(bd.getString("strain_aim")));
                info.setStrainSpeed(divideToStrain(bd.getString("strain_speed")));
                list.add(info);
            }
            return list;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BeatmapInfoV2 getBeatmapInfoV2(IBeatmapSetInfo setInfo) {
        try {
            URL url = new URL("https://api.sayobot.cn/v2/beatmapinfo?0=" + setInfo.getBeatmapSetID());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            JSONObject obj = new JSONObject(Util.readFullString(connection.getInputStream()));
            if (obj.getInt("status") != 0) {
                return null;
            }

            BeatmapInfoV2 info = new BeatmapInfoV2();
            obj = obj.getJSONObject("data");
            JSONArray array = obj.getJSONArray("bid_data");
            List<BeatmapInfoV2.BidData> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject bd = array.getJSONObject(i);
                BeatmapInfoV2.BidData data = new BeatmapInfoV2.BidData();
                if (bd.getString("bg").trim().length() != 0) {
                    data.setBackgroundUrl(String.format(
                            Locale.getDefault(),
                            "https://dl.sayobot.cn/beatmaps/files/%d/%s",
                            setInfo.getBeatmapSetID(), bd.getString("bg")));
                }
                if (bd.getString("audio").trim().length() != 0) {
                    data.setAudioUrl(String.format(
                            Locale.getDefault(),
                            "https://dl.sayobot.cn/beatmaps/files/%d/%s",
                            setInfo.getBeatmapSetID(), bd.getString("audio")));
                }
                list.add(data);
            }
            info.setBidData(list);
            return info;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static int[] divideToStrain(CharSequence s) {
        int[] l = new int[s.length()];
        for (int i = 0; i < l.length; i++) {
            l[i] = s.charAt(i) - '0';
        }
        return l;
    }

}
