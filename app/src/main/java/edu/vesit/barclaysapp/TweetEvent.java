package edu.vesit.barclaysapp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TweetEvent {

    private String oauthVerifier;
    private String message;
}
