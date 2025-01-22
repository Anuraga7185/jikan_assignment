package com.jikan.anime.entity;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class AnimeDetail implements Serializable {
    public long mal_id;
    public String url;
    public JikanImages images;
    public JikanTrailer trailer;
    public boolean approved;
    //    public ArrayList<JikanTitles> titles;
    public String title;
    public String title_english;
    public String title_japanese;
    //    public ArrayList<JikanTitleSynonyms> title_synonyms;
    public String type;
    public String source;
    public int episodes;
    public String status;
    public boolean airing;
    //    public JikanAired aired;
    public String duration;
    public String ratingl;
    public double score;
    public long scored_by;
    public int rank;
    public int popularity;
    public long members;
    public long favourites;
    public String synopsis;
    public String background;
    public String season;
    public String year;
    public ArrayList<JikanGenres> genres;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof String) {
            return title.toLowerCase().toLowerCase().contains(String.valueOf(obj).toLowerCase());
        }
        return super.equals(obj);
    }
}
