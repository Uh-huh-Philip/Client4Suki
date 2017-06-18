package me.philip.tv.client4suki.model;

import java.io.Serializable;

/**
 * Created by phili on 6/15/2017.
 */

public class EpisodeDetail implements Serializable {
    private String id;
    private String bangumi_id;
    private String bgm_eps_id;
    private String name;
    private String name_cn;
    private String thumbnail;
    private int status;
    private int episode_no;
    private String create_time;
    private String update_time;
    private String airdate;
    private String delete_mark;
    private String duration;
    private AttachedBangumiInfo bangumi;
    private VideoFiles[] video_files;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBangumi_id() {
        return bangumi_id;
    }

    public void setBangumi_id(String bangumi_id) {
        this.bangumi_id = bangumi_id;
    }

    public String getBgm_eps_id() {
        return bgm_eps_id;
    }

    public void setBgm_eps_id(String bgm_eps_id) {
        this.bgm_eps_id = bgm_eps_id;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEpisode_no() {
        return episode_no;
    }

    public void setEpisode_no(int episode_no) {
        this.episode_no = episode_no;
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

    public String getAirdate() {
        return airdate;
    }

    public void setAirdate(String airdate) {
        this.airdate = airdate;
    }

    public String getDelete_mark() {
        return delete_mark;
    }

    public void setDelete_mark(String delete_mark) {
        this.delete_mark = delete_mark;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public AttachedBangumiInfo getBangumi() {
        return bangumi;
    }

    public void setBangumi(AttachedBangumiInfo bangumi) {
        this.bangumi = bangumi;
    }

    public VideoFiles[] getVideo_files() {
        return video_files;
    }

    public void setVideo_files(VideoFiles[] video_files) {
        this.video_files = video_files;
    }
}
