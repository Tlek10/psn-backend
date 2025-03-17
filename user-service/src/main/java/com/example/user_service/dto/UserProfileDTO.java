package com.example.user_service.dto;

import com.example.user_service.model.Pet;
import com.example.user_service.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String profilePicture;
    private Set<Long> friendIds;
    private List<PetDTO> pets;

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.bio = user.getBio();
        this.profilePicture = user.getProfilePicture();
        this.friendIds = user.getFriends().stream().map(User::getId).collect(Collectors.toSet());
        this.pets = user.getPets().stream().map(PetDTO::new).collect(Collectors.toList());
    }
}