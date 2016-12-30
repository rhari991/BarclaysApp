package edu.vesit.barclaysapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String uid;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarPath;
    private String twitterToken;
}