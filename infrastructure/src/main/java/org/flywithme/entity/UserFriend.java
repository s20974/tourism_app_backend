package org.flywithme.entity;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;

@Entity(name = "user_friend_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User requestFrom;

    @ManyToOne
    @JoinColumn(name = "friendId")
    private User requestTo;

    private boolean isRequestAccepted = false;

}
