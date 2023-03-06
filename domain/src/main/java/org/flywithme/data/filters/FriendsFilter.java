package org.flywithme.data.filters;


import lombok.Builder;
import lombok.NoArgsConstructor;
import org.flywithme.data.enums.Gender;
import org.flywithme.data.enums.Status;

import java.util.Optional;

@NoArgsConstructor
public class FriendsFilter {

    private String country;

    private Gender gender;

    private Status status;

    public FriendsFilter(String country, String gender, String status) {
        this.country = country;
        setGender(gender);
        setStatus(status);
    }

    public Optional<String> getCountry() {
        return Optional.ofNullable(this.country);
    }

    public Optional<Status> getStatus(){
        return Optional.ofNullable(this.status);
    }

    public Optional<Gender> getGender(){
        return Optional.ofNullable(this.gender);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setGender(String gender) {
        if (gender != null){
            this.gender = Gender.valueOf(gender);
        }
    }

    public void setStatus(String status) {
        if(status != null){
            this.status = Status.valueOf(status);
        }
    }
}
