package com.example.test.bean;

import java.util.List;

/**
 * @authore WinterFellSo 2017/3/24
 * @purpose Retrofit
 */
public class GankResultBean {

    private boolean error;

    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error){
        this.error=error;
    }

    public List<ResultsBean> getResults(){
        return results;
    }

    public void setResults(List<ResultsBean> results){
        this.results=results;
    }

    public class ResultsBean {

        private String _id;

        private String createdAt;

        private String desc;

        private String publishedAt;

        private String source;

        private String type;

        private String url;

        private String used;

        private String who;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsed() {
            return used;
        }

        public void setUsed(String used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        @Override
        public String toString() {
            return "ResultsBean{" +
                    "_id='" + _id + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", desc='" + desc + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", source='" + source + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", used='" + used + '\'' +
                    ", who='" + who + '\'' +
                    '}';
        }
    }
}
