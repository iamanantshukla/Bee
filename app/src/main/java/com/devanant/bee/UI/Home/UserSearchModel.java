package com.devanant.bee.UI.Home;

public class UserSearchModel {
    String UserName;
    String UserID;
    String ProfileImage;

    public UserSearchModel(String userName, String userID, String profileImage) {
        UserName = userName;
        UserID = userID;
        ProfileImage = profileImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }
}
