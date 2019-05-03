package com.ad.adsle.Information;

public class LocationDetails {

    String city, area, inside_area, formatted_address, country, latlng;

    public LocationDetails(String city, String area, String inside_area, String formatted_address, String country, String latlng) {
        this.city = city;
        this.area = area;
        this.inside_area = inside_area;
        this.formatted_address = formatted_address;
        this.country = country;
        this.latlng = latlng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInside_area() {
        return inside_area;
    }

    public void setInside_area(String inside_area) {
        this.inside_area = inside_area;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }
}
