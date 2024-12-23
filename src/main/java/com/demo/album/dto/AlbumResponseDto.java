package com.demo.album.dto;

public class AlbumResponseDto {
    private Long albumId;
    private String title;
    private String albumColor;
    private boolean visibility;
    private int letterCount;
    private String stickerUrl; // 추가: Sticker URL
    private boolean isOwnAlbum;

    public AlbumResponseDto(Long albumId, String title, String albumColor, boolean visibility, int letterCount, String stickerUrl, boolean isOwnAlbum) {
        this.albumId = albumId;
        this.title = title;
        this.albumColor = albumColor;
        this.visibility = visibility;
        this.letterCount = letterCount;
        this.stickerUrl = stickerUrl;
        this.isOwnAlbum = isOwnAlbum;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumColor() {
        return albumColor;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public int getLetterCount() {
        return letterCount;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public boolean isOwnAlbum() {
        return isOwnAlbum;
    }
}
