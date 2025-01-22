package com.jikan.anime.entity;

public class JikanTrailer {
    public String youtube_id;
    public String url;
    public String embed_url;
    public JikanImage images;

    public static class JikanImage {
        public String image_url;
        public String small_image_url;
        public String medium_image_url;
        public String large_image_url;
        public String maximum_image_url;
    }
}
