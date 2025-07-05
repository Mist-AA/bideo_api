package com.video_streaming.project_video.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.video_streaming.project_video.DTOMapper.UserDTOMapper;
import com.video_streaming.project_video.DTOs.UserDTO;
import com.video_streaming.project_video.Entity.User;
import com.video_streaming.project_video.Repository.UserRepository;
import com.video_streaming.project_video.Service.UserService;

import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;

import static com.video_streaming.project_video.Configurations.SupportVariablesConfig.thumbnailURLDefault;

@Service
public class UserServiceImpl implements UserService {
    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";
    private static final String USER_ROLE_USER = "USER";


    private final FirebaseAuth firebaseAuth;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void create(String emailId, String password, String user_name, String thumbnail_url) throws Exception {
        CreateRequest request = new CreateRequest();
        request.setEmail(emailId);
        request.setPassword(password);
        request.setDisplayName(user_name);
        request.setEmailVerified(Boolean.TRUE);

        try {
            UserRecord userRecord = firebaseAuth.createUser(request);
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userRecord.getUid());
            userDTO.setUser_name(userRecord.getDisplayName());
            userDTO.setUser_email(userRecord.getEmail());
            userDTO.setThumbnail_url(thumbnail_url!=null?thumbnail_url:thumbnailURLDefault);
            updateUser(userDTO);
        } catch (FirebaseAuthException exception) {
            if (exception.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new Exception("Account with given email-id already exists");
            }
            throw exception;
        }
    }

    @Transactional
    public void updateUser(UserDTO userDTO) {
        UserDTOMapper userDTOMapper = new UserDTOMapper();
        User user = userDTOMapper.convertDTOToEntity(userDTO);
        user.setUser_role(USER_ROLE_USER);
        userRepository.save(user);

        ResponseEntity.ok("User updated successfully.");
    }

}
