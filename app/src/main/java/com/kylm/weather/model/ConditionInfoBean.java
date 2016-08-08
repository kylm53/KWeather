package com.kylm.weather.model;

import io.realm.RealmObject;

public class ConditionInfoBean extends RealmObject {
        private String code;
        private String txt;
        private String txt_en;
        private String icon;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public String getTxt_en() {
            return txt_en;
        }

        public void setTxt_en(String txt_en) {
            this.txt_en = txt_en;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }