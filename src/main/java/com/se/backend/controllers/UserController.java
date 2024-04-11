/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.projection.UserDTO;
import com.se.backend.services.TokenService;
import com.se.backend.services.TourCollectionService;
import com.se.backend.services.UserService;
import com.se.backend.utils.AdminToken;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.se.backend.services.FileUtil.getFileExtension;
import static com.se.backend.services.FileUtil.saveFileToLocal;

/**
 * @eo.api-type http
 * @eo.groupName User
 * @eo.path /users
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {
    /**
     * userService
     */
    private final UserService userService;
    /**
     * tourCollectionService
     */
    private final TourCollectionService tourCollectionService;
    /**
     * tokenService
     */
    private final TokenService tokenService;

    @Autowired
    public UserController(UserService userService, TourCollectionService tourCollectionService, TokenService tokenService) {
        this.userService = userService;
        this.tourCollectionService = tourCollectionService;
        this.tokenService = tokenService;
    }


    /**
     * @eo.name getAllUsers
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     * @return ApiResponse
     */
    @AdminToken
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success("Get all users", UserDTO.toListDTO(userService.getAllUsers()));
    }


    /**
     * @eo.name addUser
     * @eo.url /signup
     * @eo.method post
     * @eo.request-type json
     * @param req
     * @return ApiResponse {@value {1}}
     */
    @IgnoreToken
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    ApiResponse<Void> addUser(@RequestBody ReqSignupForm req) {
        try {
            userService.getUserByEmail(req.email);
            return ApiResponse.error("User already exist");
        } catch (AuthException e) {
            if (e.getType().equals(AuthException.ErrorType.USER_NOT_FOUND)) {
                User newUser = new User();
                newUser.setNickname(req.nickname);
                newUser.setAvatar(User.DEFAULT_AVATAR);
                newUser.setEmail(req.email);
                newUser.setPassword(req.password);
                newUser.setRegisterTime(TimeUtil.getCurrentTimeString());
                newUser.setLatestLoginTime(TimeUtil.getCurrentTimeString());
                // 用户注册时创建一个默认的 Tour Collection
                tourCollectionService.createTourCollection(userService.createUser(newUser), new TourCollectionService.CreateTourCollectionForm("Default Collection", "Default Collection", "http://walcraft.wmzspace.space/static/tour/example/1.png", "Default Collection"));
                return ApiResponse.success("Signup succeed!");
            } else {
                return ApiResponse.error(e.getMessage());
            }
        }
    }

    /**
     * @eo.name isUserExist
     * @eo.url /exist
     * @eo.method get
     * @eo.request-type formdata
     * @param id
     * @param email
     * @return ApiResponse
     */
    @IgnoreToken
    @GetMapping(value = "/exist")
    public ApiResponse<Void> isUserExist(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        try {
            if (id != null) {
                userService.getUserById(id);
                return ApiResponse.success("GET user succeed with id");
            } else if (email != null) {
                userService.getUserByEmail(email);
                return ApiResponse.success("GET user succeed with email");
            }
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }

    /**
     * @eo.name getSingleUser
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
     * @param id
     * @param email
     * @return ApiResponse
     */
    @AdminToken
    @GetMapping
    public ApiResponse<UserDTO> getSingleUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        try {
            if (id != null) {
                return ApiResponse.success("GET user succeed with id", userService.getUserById(id).toDTO());
            } else if (email != null) {
                return ApiResponse.success("GET user succeed with email", userService.getUserByEmail(email).toDTO());
            }
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }

    /**
     * @eo.name updateUser
     * @eo.url /
     * @eo.method put
     * @eo.request-type json
     * @param user
     * @param updatedInfo
     * @return ApiResponse
     */
    @PutMapping
    ApiResponse<UserDTO> updateUser(@RequestAttribute("user") User user, @RequestBody UserService.ReqUpdateForm updatedInfo) {
        try {
            return ApiResponse.success("User information updated", userService.updateUser(user.getId(), updatedInfo).toDTO());
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @eo.name removeUser
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     * @param user
     * @return ApiResponse
     */
    @DeleteMapping
    ApiResponse<Void> removeUser(@RequestAttribute("user") User user) {
        try {
            userService.deleteUser(user.getId());
            return ApiResponse.success("User has been removed");
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @eo.name uploadFile
     * @eo.url /upload
     * @eo.method post
     * @eo.request-type formdata
     * @param file
     * @param uploadURL
     * @param filename
     * @return ApiResponse
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("uploadURL") String uploadURL,
                                            @RequestParam(value="filename",required = false) String filename) {
        if (file.isEmpty()) {
            return ApiResponse.error("File is empty");
        }


        try {
            // 生成一个随机的文件名或沿用参数
            String fileName = Objects.nonNull(filename)?
                filename :
                UUID.randomUUID() + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

            // 保存文件到本地
            saveFileToLocal(file.getInputStream(), uploadURL.concat("/").concat(fileName));

            // 如果需要保存到数据库或者其他操作，可以在这里进行处理

            return ApiResponse.success("File uploaded successfully", uploadURL.concat("/").concat(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("Failed to upload file");
        }
    }

    /**
     * @eo.name validateToken
     * @eo.url /token/{value}
     * @eo.method get
     * @eo.request-type formdata
     * @param token
     * @return ApiResponse
     */
    @GetMapping("/token/{value}")
    public ApiResponse<UserDTO> validateToken(@PathVariable("value") String token) {
        try {
            User user = tokenService.getUserByToken(token);
            return ApiResponse.success("Token is valid", user.toDTO());
        } catch (AuthException e) {
            return ApiResponse.error("Invalid token");
        }
    }

    // Signup request form from client
    @Getter
    public static class ReqSignupForm {
        /**
         * email
         */
        String email;
        /**
         * nickname
         */
        String nickname;
        /**
         * password
         */
        String password;
    }
}
