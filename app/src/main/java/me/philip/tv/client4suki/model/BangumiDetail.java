package me.philip.tv.client4suki.model;

import java.util.List;

/**
 * Created by phili on 6/15/2017.
 */

public class BangumiDetail {
    private String id;
    private String bgm_id;
    private String name;
    private String name_cn;
    private String summary;
    private String image;
    private String cover;
    private String create_time;
    private String update_time;
    private String eps_no_offset;
    private String bangumi_moe;
    private String libyk_so;
    private String dmhy;
    private String type;
    private String status;
    private String air_date;
    private String air_weekday;
    private String delete_mark;
    private String acg_rip;
    private String rss;
    private String eps_regex;
    private String eps;
    private List<Episode> episodes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBgm_id() {
        return bgm_id;
    }

    public void setBgm_id(String bgm_id) {
        this.bgm_id = bgm_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getEps_no_offset() {
        return eps_no_offset;
    }

    public void setEps_no_offset(String eps_no_offset) {
        this.eps_no_offset = eps_no_offset;
    }

    public String getBangumi_moe() {
        return bangumi_moe;
    }

    public void setBangumi_moe(String bangumi_moe) {
        this.bangumi_moe = bangumi_moe;
    }

    public String getLibyk_so() {
        return libyk_so;
    }

    public void setLibyk_so(String libyk_so) {
        this.libyk_so = libyk_so;
    }

    public String getDmhy() {
        return dmhy;
    }

    public void setDmhy(String dmhy) {
        this.dmhy = dmhy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAir_date() {
        return air_date;
    }

    public void setAir_date(String air_date) {
        this.air_date = air_date;
    }

    public String getAir_weekday() {
        return air_weekday;
    }

    public void setAir_weekday(String air_weekday) {
        this.air_weekday = air_weekday;
    }

    public String getDelete_mark() {
        return delete_mark;
    }

    public void setDelete_mark(String delete_mark) {
        this.delete_mark = delete_mark;
    }

    public String getAcg_rip() {
        return acg_rip;
    }

    public void setAcg_rip(String acg_rip) {
        this.acg_rip = acg_rip;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getEps_regex() {
        return eps_regex;
    }

    public void setEps_regex(String eps_regex) {
        this.eps_regex = eps_regex;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
