package com.shikhar.inventory_mgt;

public class Inventory {

    private String mItemName;
    private int mItemPrice;
    private int mItemQuantity;
    private String mSupplierName;
    private String mSupplierPhone;
    private String mSupplierEMail;
    private String mImage;

    public Inventory(String mItemName, int mItemPrice, int mItemQuantity, String mSupplierName, String mSupplierPhone, String mSupplierEMail, String mImage) {
        this.mItemName = mItemName;
        this.mItemPrice = mItemPrice;
        this.mItemQuantity = mItemQuantity;
        this.mSupplierName = mSupplierName;
        this.mSupplierPhone = mSupplierPhone;
        this.mSupplierEMail = mSupplierEMail;
        this.mImage = mImage;
    }

    public String getmItemName() {
        return mItemName;
    }

    public void setmItemName(String mItemName) {
        this.mItemName = mItemName;
    }

    public int getmItemPrice() {
        return mItemPrice;
    }

    public void setmItemPrice(int mItemPrice) {
        this.mItemPrice = mItemPrice;
    }

    public int getmItemQuantity() {
        return mItemQuantity;
    }

    public void setmItemQuantity(int mItemQuantity) {
        this.mItemQuantity = mItemQuantity;
    }

    public String getmSupplierName() {
        return mSupplierName;
    }

    public void setmSupplierName(String mSupplierName) {
        this.mSupplierName = mSupplierName;
    }

    public String getmSupplierPhone() {
        return mSupplierPhone;
    }

    public void setmSupplierPhone(String mSupplierPhone) {
        this.mSupplierPhone = mSupplierPhone;
    }

    public String getmSupplierEMail() {
        return mSupplierEMail;
    }

    public void setmSupplierEMail(String mSupplierEMail) {
        this.mSupplierEMail = mSupplierEMail;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }
}
