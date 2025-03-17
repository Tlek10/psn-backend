package com.example.user_service.dto;

import com.example.user_service.model.FriendRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class FriendRequestsDTO {
    private Set<FriendRequest> incomingRequests;
    private Set<FriendRequest> outgoingRequests;

    public FriendRequestsDTO(Set<FriendRequest> incomingRequests, Set<FriendRequest> outgoingRequests) {
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
    }
}