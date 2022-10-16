package com.statefarm.codingcompetition.simpledatatool.model;

public class Policy {
    private int id;
    private int customerId;
    private String policyType;
    private String startDate;
    private double premiumPerMonth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public double getPremiumPerMonth() {
        return premiumPerMonth;
    }

    public void setPremiumPerMonth(float premiumPerMonth) {
        this.premiumPerMonth = premiumPerMonth;
    }

}
