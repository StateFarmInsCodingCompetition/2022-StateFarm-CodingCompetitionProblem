package com.statefarm.codingcompetition.simpledatatool.model;

public class Claim {
    private int id;
    private int policyId;
    private boolean isClaimOpen;
    private String claimType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public boolean getIsClaimOpen() {
        return isClaimOpen;
    }

    public void setIsClaimOpen(boolean isClaimOpen) {
        this.isClaimOpen = isClaimOpen;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }
}
