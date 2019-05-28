package com.ad.adsle.Information;

public class User {

    String id, name, email, number, gender, religion, tag, referralCode, referralLink, msgId, deviceId, created_date;
    long bonus_data;
    int age;

    public User() {

    }

    public User(String id, int age, String gender, String religion) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.religion = religion;
    }

    public User(String id, String name, String email, String number, int age, String gender, String religion, String tag, long bonus_data, String referralCode, String referralLink, String msgId, String deviceId, String created_date) {
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
        this.created_date = created_date;
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

    public long getBonus_data() {
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

    public void setBonus_data(long bonus_data) {
        this.bonus_data = bonus_data;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
