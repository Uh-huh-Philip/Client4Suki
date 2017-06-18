package me.philip.tv.client4suki.model;

import java.io.Serializable;

/**
 * Created by phili on 6/16/2017.
 */

public class VideoFiles implements Serializable {
    private String id;
    private int status;
    private String torrent_id;
    private String url;
    private String file_path;
    private String file_name;
    private int resolution_w;
    private String download_url;
    private String episode_id;
    private int resolution_h;
    private String bangumi_id;
    private int duration;
    private String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTorrent_id() {
        return torrent_id;
    }

    public void setTorrent_id(String torrent_id) {
        this.torrent_id = torrent_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getResolution_w() {
        return resolution_w;
    }

    public void setResolution_w(int resolution_w) {
        this.resolution_w = resolution_w;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getEpisode_id() {
        return episode_id;
    }

    public void setEpisode_id(String episode_id) {
        this.episode_id = episode_id;
    }

    public int getResolution_h() {
        return resolution_h;
    }

    public void setResolution_h(int resolution_h) {
        this.resolution_h = resolution_h;
    }

    public String getBangumi_id() {
        return bangumi_id;
    }

    public void setBangumi_id(String bangumi_id) {
        this.bangumi_id = bangumi_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
