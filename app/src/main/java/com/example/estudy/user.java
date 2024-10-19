package com.example.estudy;

public class user {
    private String name;
    private String phone;
    private String email;
    private String password;
    private String profilePictureUrl; // Property for profile picture URL
    private String coverPhotoUrl;     // Property for cover photo URL

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public user() {
    }

    public user(String name, String password, String phone, String email, String profilePictureUrl, String coverPhotoUrl) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
        this.coverPhotoUrl = coverPhotoUrl;
    }

    // Getters and setters for all properties
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl; // Getter for profile picture URL
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl; // Setter for profile picture URL
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl; // Getter for cover photo URL
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl; // Setter for cover photo URL
    }
}
