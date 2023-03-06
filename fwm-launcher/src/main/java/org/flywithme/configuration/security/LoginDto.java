package org.flywithme.configuration.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.flywithme.entity.Roles;

import java.util.Set;

@Builder
public class LoginDto {

    @JsonProperty("jwt")
    private String jwt;

    @JsonProperty("roleSet")
    private Set<Roles> rolesSet;

}
