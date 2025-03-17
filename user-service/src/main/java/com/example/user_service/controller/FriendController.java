package com.example.user_service.controller;

import com.example.user_service.dto.FriendRequestsDTO;
import com.example.user_service.model.User;
import com.example.user_service.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<Set<User>> getFriends(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(friendService.getFriends(username));
    }

    @GetMapping("/requests")
    public ResponseEntity<FriendRequestsDTO> getFriendRequests(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(friendService.getFriendRequests(username));
    }

    @PostMapping("/send-request")
    public ResponseEntity<String> sendFriendRequest(@RequestParam String identifier, Authentication authentication) {
        String senderUsername = authentication.getName();
        return ResponseEntity.ok(friendService.sendFriendRequest(senderUsername, identifier));
    }

    @PostMapping("/accept-request/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.acceptFriendRequest(requestId));
    }

    @PostMapping("/decline-request/{requestId}")
    public ResponseEntity<String> declineFriendRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(friendService.declineFriendRequest(requestId));
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<String> removeFriend(@PathVariable Long friendId, Authentication authentication) {
        String username = authentication.getName();
        friendService.removeFriend(username, friendId);
        return ResponseEntity.ok("Friend removed!");
    }

    @DeleteMapping("/cancel-request/{requestId}")
    public ResponseEntity<String> cancelFriendRequest(@PathVariable Long requestId, Authentication authentication) {
        String username = authentication.getName();
        friendService.cancelFriendRequest(username, requestId);
        return ResponseEntity.ok("Friend request canceled!");
    }
}