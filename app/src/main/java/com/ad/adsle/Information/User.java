package com.ad.adsle.Information;

public class User {

    String id, name, email, number, age, gender, tag, bonus_data, referralCode, referralLink, msgId;

    public User() {

    }

    public User(String id, String name, String email, String number, String age, String gender, String tag, String bonus_data, String referralCode, String referralLink, String msgId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.age = age;
        this.gender = gender;
        this.tag = tag;
        this.bonus_data = bonus_data;
        this.referralCode = referralCode;
        this.referralLink = referralLink;
        this.msgId = msgId;
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

    public String getAge() {
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

    public void setAge(String age) {
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
