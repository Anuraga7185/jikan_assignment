package com.jikan.anime.entity;

import java.util.Map;

public class JikanPagination {
    public int last_visible_page;
    public boolean has_next_page;
    public int current_page;
    public Map<String,Integer> items;
}
