package com.healthcare.portal.dto;
public class RecoveryRequests {
 public static class EmailRequest { public String email; public String getEmail(){return email;} public void setEmail(String v){email=v;} }
 public static class ResetRequest { public String token; public String password; public String getToken(){return token;} public void setToken(String v){token=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;} }
 public static class OtpRequest { public String email; public String otp; public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getOtp(){return otp;} public void setOtp(String v){otp=v;} }
}
