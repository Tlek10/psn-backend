package com.example.user_service.service;

import com.example.user_service.dto.FriendRequestsDTO;
import com.example.user_service.exception.custom.DuplicateEntityException;
import com.example.user_service.exception.custom.InvalidOperationException;
import com.example.user_service.model.FriendRequest;
import com.example.user_service.model.RequestStatus;
import com.example.user_service.model.User;
import com.example.user_service.repository.FriendRequestRepository;
import com.example.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public Set<User> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getFriends();
    }

    public FriendRequestsDTO getFriendRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new FriendRequestsDTO(user.getReceivedRequests(), user.getSentRequests());
    }

    @Transactional
    public void cancelFriendRequest(String username, Long requestId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (!request.getSender().getId().equals(user.getId())) {
            throw new InvalidOperationException("You cannot cancel this request");
        }

        friendRequestRepository.delete(request);
    }

    @Transactional
    public void removeFriend(String username, Long friendId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        if (!user.getFriends().contains(friend)) {
            throw new InvalidOperationException("User is not your friend");
        }

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        userRepository.save(user);
        userRepository.save(friend);
    }

    public String sendFriendRequest(String senderUsername, String receiverIdentifier) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        User receiver = userRepository.findByUsername(receiverIdentifier)
                .orElse(userRepository.findByEmail(receiverIdentifier)
                        .orElseThrow(() -> new EntityNotFoundException("Receiver not found")));

        if (sender.getId().equals(receiver.getId())) {
            throw new InvalidOperationException("Cannot send request to yourself");
        }

        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest.isPresent()) {
            throw new DuplicateEntityException("Request already sent");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(RequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);

        return "Friend request sent!";
    }

    @Transactional
    public String acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidOperationException("Request already processed");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        request.getSender().getFriends().add(request.getReceiver());
        request.getReceiver().getFriends().add(request.getSender());

        friendRequestRepository.save(request);
        return "Request accepted!";
    }

    @Transactional
    public String declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new InvalidOperationException("Request already processed");
        }

        request.setStatus(RequestStatus.DECLINED);
        friendRequestRepository.save(request);
        return "Request declined!";
    }
}