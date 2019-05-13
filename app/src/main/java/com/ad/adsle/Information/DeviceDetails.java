package com.ad.adsle.Information;

public class DeviceDetails {

    public DeviceDetails() {

    }

    String release_build_version, build_version_code_name, manufacturer, model, product, display_version, os_version, sdk_version;

    public DeviceDetails(String release_build_version, String build_version_code_name, String manufacturer, String model, String product, String display_version, String os_version, String sdk_version) {
        this.release_build_version = release_build_version;
        this.build_version_code_name = build_version_code_name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.product = product;
        this.display_version = display_version;
        this.os_version = os_version;
        this.sdk_version = sdk_version;
    }

    public String getRelease_build_version() {
        return release_build_version;
    }

    public void setRelease_build_version(String release_build_version) {
        this.release_build_version = release_build_version;
    }

    public String getBuild_version_code_name() {
        return build_version_code_name;
    }

    public void setBuild_version_code_name(String build_version_code_name) {
        this.build_version_code_name = build_version_code_name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDisplay_version() {
        return display_version;
    }

    public void setDisplay_version(String display_version) {
        this.display_version = display_version;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getSdk_version() {
        return sdk_version;
    }

    public void setSdk_version(String sdk_version) {
        this.sdk_version = sdk_version;
    }
}
