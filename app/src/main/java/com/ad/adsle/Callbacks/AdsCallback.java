package com.ad.adsle.Callbacks;

public interface AdsCallback {
    void setCurrentAd(String AdId, String adImage, String AdTitle, String AdType, String AdLink);
    void onImageAdClicked(String AdId, String AdType, String AdLink);
}
