package com.jueda.ndian.utils;

import java.util.HashMap;

/**
 * 等级制度
 * Created by Administrator on 2016/5/26.
 */
public class LvUtil {
    private HashMap<String,String> hashMap;
    public HashMap<String,String> LvUtil(float Devotion){
        hashMap=new HashMap<>();
        if(Devotion<5){
            hashMap.put("lv", "Lv1");
            hashMap.put("progress", (int) ((Devotion / 5)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/5");
        }else if(5<=Devotion&&Devotion<10) {
            hashMap.put("lv", "Lv2");
            hashMap.put("progress", (int) ((Devotion-5)/(10-5)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/10");
        }else if(10<=Devotion&&Devotion<15) {
            hashMap.put("lv", "Lv3");
            hashMap.put("progress", (int) ((Devotion-10)/(15-10)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/15");
        }else if(15<=Devotion&&Devotion<20) {
            hashMap.put("lv", "Lv4");
            hashMap.put("progress", (int) ((Devotion-15)/(20-15)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/20");
        }else if(20<=Devotion&&Devotion<30) {
            hashMap.put("lv", "Lv5");
            hashMap.put("progress", (int) ((Devotion-20)/(30-20)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/30");
        }else if(30<=Devotion&&Devotion<40) {
            hashMap.put("lv", "Lv6");
            hashMap.put("progress", (int) ((Devotion-30)/(40-30)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/40");
        }else if(40<=Devotion&&Devotion<50) {
            hashMap.put("lv", "Lv7");
            hashMap.put("progress", (int) ((Devotion-40)/(50-40)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/50");
        }else if(50<=Devotion&&Devotion<60) {
            hashMap.put("lv", "Lv8");
            hashMap.put("progress", (int) ((Devotion-50)/(60-50)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/60");
        }else if(60<=Devotion&&Devotion<75) {
            hashMap.put("lv", "Lv9");
            hashMap.put("progress", (int) ((Devotion-60)/(75-60)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/75");
        }else if(75<=Devotion&&Devotion<90) {
            hashMap.put("lv", "Lv10");
            hashMap.put("progress", (int) ((Devotion-75)/(90-75)*100)+ "");
            hashMap.put("ExperienceValue", (int) Devotion + "/90");
        }else if(90<=Devotion&&Devotion<lv(11)) {
            hashMap.put("lv", "Lv11");
            hashMap.put("progress", (int) ((Devotion-90)/(lv(11)-90)*100) + "");
            hashMap.put("ExperienceValue", (int) Devotion + "/"+(int)lv(11));
        }else{
            if(Devotion>=lv(255)){
                hashMap.put("lv", "Lv"+(255));
                hashMap.put("progress", (int) 100 + "");
                hashMap.put("ExperienceValue", (int) Devotion + "/"+(int)lv(255));
            }else {
                for (int i = 0; i < 243; i++) {
                    if (lv(i + 11) <= Devotion && Devotion < lv(i + 12)) {
                        hashMap.put("lv", "Lv" + (i + 12));
                        hashMap.put("progress", (int) ((Devotion - lv(i + 11)) / (lv(i + 12) - lv(i + 11)) * 100) + "");
                        hashMap.put("ExperienceValue", (int) Devotion + "/" + (int) lv(i + 12));
                        break;
                    }
                }
            }
        }
        return hashMap;
    }
    private float lv(float lv){
        return ((lv-7)*(lv-7)*10);
    }
}
