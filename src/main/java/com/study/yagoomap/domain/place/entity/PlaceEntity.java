package com.study.yagoomap.domain.place.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
public class PlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kakaoPlaceId;
    private String name;
    private String team;
    private long teamId;
    private String district;
    private String address;
    private String roadAddress;
    private double latitude;
    private double longitude;
    private String category;
    private String categoryName;
    private String categoryGroupCode;
    private String phone;
    private String instagramUrl;
    private String naverMapUrl;
    private String kakaoPlaceUrl;
    private String representativeImageUrl;
    private String note;
    private String status;
    private double rating;
    private int reviewCount;
    private int distanceMeters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ElementCollection
    private List<String> photos = new ArrayList<>();

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    public PlaceEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getKakaoPlaceId() {
        return kakaoPlaceId;
    }

    public void setKakaoPlaceId(String kakaoPlaceId) {
        this.kakaoPlaceId = kakaoPlaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    public void setCategoryGroupCode(String categoryGroupCode) {
        this.categoryGroupCode = categoryGroupCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getNaverMapUrl() {
        return naverMapUrl;
    }

    public void setNaverMapUrl(String naverMapUrl) {
        this.naverMapUrl = naverMapUrl;
    }

    public String getKakaoPlaceUrl() {
        return kakaoPlaceUrl;
    }

    public void setKakaoPlaceUrl(String kakaoPlaceUrl) {
        this.kakaoPlaceUrl = kakaoPlaceUrl;
    }

    public String getRepresentativeImageUrl() {
        return representativeImageUrl;
    }

    public void setRepresentativeImageUrl(String representativeImageUrl) {
        this.representativeImageUrl = representativeImageUrl;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos == null ? new ArrayList<>() : new ArrayList<>(photos);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
