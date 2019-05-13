package com.ad.adsle.Information;

public class User {

    String id, name, email, number, gender, religion, tag, bonus_data, referralCode, referralLink, msgId, deviceId;
    int age;

    public User() {

    }

    public User(String id, String name, String email, String number, int age, String gender, String religion, String tag, String bonus_data, String referralCode, String referralLink, String msgId, String deviceId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.age = age;
        this.gender = gender;
        this.religion = religion;
        this.tag = tag;
        this.bonus_data = bonus_data;
        this.referralCode = referralCode;
        this.referralLink = referralLink;
        this.msgId = msgId;
        this.deviceId = deviceId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getTag() {
        return tag;
    }

    public String getBonus_data() {
        return bonus_data;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getReferralLink() {
        return referralLink;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public int getAge() {
        return age;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public void setReferralLink(String referralLink) {
        this.referralLink = referralLink;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBonus_data(String bonus_data) {
        this.bonus_data = bonus_data;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


}
