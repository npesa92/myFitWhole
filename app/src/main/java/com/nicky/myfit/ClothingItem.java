package com.nicky.myfit;

/**
 * Created by nicholas on 12/31/14.
 */
public class ClothingItem {

    private String clothingTag;
    private String clothingText;

    public ClothingItem(String clothingTag, String clothingText) {
        this.clothingTag = clothingTag;
        this.clothingText = clothingText;
    }

    public String getClothingTag() {
        return clothingTag;
    }

    public void setClothingTag(String clothingTag) {
        this.clothingTag = clothingTag;
    }

    public String getClothingText() {
        return clothingText;
    }

    public void setClothingText(String clothingText) {
        this.clothingText = clothingText;
    }
}
