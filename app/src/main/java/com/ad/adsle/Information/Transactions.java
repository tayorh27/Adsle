package com.ad.adsle.Information;

/**
 *
 */

public class Transactions {

    String id;
    String campaign_id, amount, date, reference;

    public Transactions() {

    }

    public Transactions(String id, String campaign_id, String amount, String date, String reference) {
        this.id = id;
        this.campaign_id = campaign_id;
        this.amount = amount;
        this.date = date;
        this.reference = reference;
    }

    public String getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(String campaign_id) {
        this.campaign_id = campaign_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
