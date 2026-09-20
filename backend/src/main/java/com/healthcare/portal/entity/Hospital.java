package com.healthcare.portal.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="hospitals")
public class Hospital {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=160) private String name;
 @Column(length=255) private String address;
 @Column(length=80) private String city;
 @Column(length=30) private String phone;
 @Column(unique=true,length=120) private String email;
 @Column(nullable=false) private boolean active=true;
 private LocalDateTime createdAt;
 @PrePersist void create(){createdAt=LocalDateTime.now();}
 public Hospital(){} public Hospital(String name,String address,String city,String phone,String email){this.name=name;this.address=address;this.city=city;this.phone=phone;this.email=email;}
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public String getCity(){return city;} public void setCity(String v){city=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
