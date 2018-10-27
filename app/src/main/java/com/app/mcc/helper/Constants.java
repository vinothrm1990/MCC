package com.app.mcc.helper;

import android.content.SharedPreferences;

public class Constants {

    //sharedPreference
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    //base URL
    public static String MEMBER_URL = "http://mycinemachance.com/jsons/member/";
    public static String DIRECTOR_URL = "http://mycinemachance.com/jsons/director/";
    public static String GUEST_URL = "http://mycinemachance.com/jsons/guest/";
    public static String IMAGE_URL = "http://mycinemachance.com/upload/";

    //page URL
    public static String LOGIN = "login.php?";
    public static String REGISTER = "register.php?";
    public static String GET_OTP = "get_otp.php?";
    public static String VALIDATE_OTP = "validate_otp.php?";
    public static String FORGOT_PASSWORD = "forgot_password.php?";
    public static String CHANGE_PASSWORD = "change_password.php?";
    public static String HOME_DATA = "home_data.php?";
    public static String ADD_REMOVE_WISHLIST = "add_remove_wishlist.php?";
    public static String GET_WISHLIST = "get_wishlist.php?";
    public static String GET_CATEGORY = "get_category.php?";
    public static String GET_MEMBER = "get_member.php?";
    public static String SEND_FEEDBACK = "send_feedback.php?";
    public static String ADD_POST = "add_post.php?";
    public static String POST_UPLOAD = "post_upload.php?";
    public static String PROFILE_UPLOAD = "profile_upload.php?";
    public static String UPDATE_PROFILE = "update_profile.php?";
}
