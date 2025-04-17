package com.example.playlistapp.dto;

public class CategoryDTO {
    private String id;
    private String name;

    public CategoryDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
