package org.flywithme.entity;

import lombok.*;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;
import org.flywithme.data.user.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity(name = "user_tb")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String email;

    private String password;

    private boolean emailConfirmed;

    private boolean accountNonLocked = true;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    private String country;

    private String city;

    private String verificationCode;

    private String mainPhotoUrl;

    @OneToMany(targetEntity = UserGallery.class, mappedBy = "user_id", fetch = FetchType.EAGER)
    private List<UserGallery> userGalleries;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usersRoles", joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "requestFrom")
    private List<UserFriend> userFriends;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private Set<Trip> trips;

    @ManyToOne
    @JoinColumn(name = "joined_trip_id")
    private Trip currentTrip;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        for (Role r : getRoles()) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(r.getRoles().name());
            list.add(simpleGrantedAuthority);
        }
        return list;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.emailConfirmed;
    }

    public User update(UserDto userDto){
        if(!userDto.getGender().isEmpty()) {
            setGender(Gender.valueOf(userDto.getGender()));
        }
        if(!userDto.getStatus().isEmpty()) {
            setStatus(Status.valueOf(userDto.getStatus()));
        }
        if(userDto.getPassword() != null) {
            setPassword(userDto.getPassword());
        }
        setEmail(userDto.getEmail());
        setName(userDto.getName());
        setSurname(userDto.getSurname());
        setCountry(userDto.getCountry());
        setPhoneNumber(userDto.getPhoneNumber());
        return this;
    }
}
