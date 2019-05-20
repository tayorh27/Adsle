package com.ad.adsle.Information;

public class Settings {

    long signup_data, invite_bonus_data, reach_data, click_data, app_install_data, withdrawal_data_check, total_users;
    int amount_per_reach, amount_per_click, amount_per_app_install;

    public Settings() {

    }

    public Settings(long signup_data, long invite_bonus_data, long reach_data, long click_data, long app_install_data, long withdrawal_data_check, long total_users, int amount_per_reach, int amount_per_click, int amount_per_app_install) {
        this.signup_data = signup_data;
        this.invite_bonus_data = invite_bonus_data;
        this.reach_data = reach_data;
        this.click_data = click_data;
        this.app_install_data = app_install_data;
        this.withdrawal_data_check = withdrawal_data_check;
        this.total_users = total_users;
        this.amount_per_reach = amount_per_reach;
        this.amount_per_click = amount_per_click;
        this.amount_per_app_install = amount_per_app_install;
    }

    public long getSignup_data() {
        return signup_data;
    }

    public void setSignup_data(long signup_data) {
        this.signup_data = signup_data;
    }

    public long getInvite_bonus_data() {
        return invite_bonus_data;
    }

    public void setInvite_bonus_data(long invite_bonus_data) {
        this.invite_bonus_data = invite_bonus_data;
    }

    public long getReach_data() {
        return reach_data;
    }

    public void setReach_data(long reach_data) {
        this.reach_data = reach_data;
    }

    public long getClick_data() {
        return click_data;
    }

    public void setClick_data(long click_data) {
        this.click_data = click_data;
    }

    public long getApp_install_data() {
        return app_install_data;
    }

    public void setApp_install_data(long app_install_data) {
        this.app_install_data = app_install_data;
    }

    public long getWithdrawal_data_check() {
        return withdrawal_data_check;
    }

    public void setWithdrawal_data_check(long withdrawal_data_check) {
        this.withdrawal_data_check = withdrawal_data_check;
    }

    public long getTotal_users() {
        return total_users;
    }

    public void setTotal_users(long total_users) {
        this.total_users = total_users;
    }

    public int getAmount_per_reach() {
        return amount_per_reach;
    }

    public void setAmount_per_reach(int amount_per_reach) {
        this.amount_per_reach = amount_per_reach;
    }

    public int getAmount_per_click() {
        return amount_per_click;
    }

    public void setAmount_per_click(int amount_per_click) {
        this.amount_per_click = amount_per_click;
    }

    public int getAmount_per_app_install() {
        return amount_per_app_install;
    }

    public void setAmount_per_app_install(int amount_per_app_install) {
        this.amount_per_app_install = amount_per_app_install;
    }
}
