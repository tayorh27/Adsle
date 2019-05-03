package com.ad.adsle.Information;

public class User {

    String id, name, email, number, age, gender, tag, bonus_data, referralCode, referralLink, loc_address;

    public User() {

    }

    public User(String id, String name, String email, String number, String age, String gender, String tag, String bonus_data, String referralCode, String referralLink, String loc_address) {
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
        this.loc_address = loc_address;
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

    public String getAge() {
        return age;
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

    public String getLoc_address() {
        return loc_address;
    }


}
