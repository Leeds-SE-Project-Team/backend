/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.exceptions.ResourceException;
import com.se.backend.models.User;
import com.se.backend.projection.UserDTO;
import com.se.backend.services.TokenService;
import com.se.backend.services.TourCollectionService;
import com.se.backend.services.UserService;
import com.se.backend.utils.ApiResponse;
import com.se.backend.utils.IgnoreToken;
import com.se.backend.utils.TimeUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.se.backend.utils.FileUtil.getFileExtension;
import static com.se.backend.utils.FileUtil.saveFileToLocal;

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
     * @return ApiResponse
     * @eo.name getAllUsers
     * @eo.url /all
     * @eo.method get
     * @eo.request-type formdata
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success("Get all users", UserDTO.toListDTO(userService.getAllUsers()));
    }


    /**
     * @param req
     * @return ApiResponse {@value {1}}
     * @eo.name addUser
     * @eo.url /signup
     * @eo.method post
     * @eo.request-type json
     */
    @IgnoreToken
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    ApiResponse<Void> addUser(@RequestBody ReqSignupForm req) {
        try {
            userService.getUserByEmail(req.email);
            return ApiResponse.error("User already exist");
        } catch (ResourceException e) {
            if (e.getType().equals(ResourceException.ErrorType.USER_NOT_FOUND)) {
                User newUser = new User();
                newUser.setNickname(req.nickname);
                newUser.setAvatar(User.DEFAULT_AVATAR);
                newUser.setEmail(req.email);
                newUser.setPassword(req.password);

                newUser.setRegisterTime(TimeUtil.getCurrentTimeString());
                newUser.setLatestLoginTime(TimeUtil.getCurrentTimeString());
                newUser.setType(User.UserType.COMMON);
                newUser.setVipExpireTime(null);
                // 用户注册时创建一个默认的 Tour Collection
                tourCollectionService.createTourCollection(userService.createUser(newUser), new TourCollectionService.CreateTourCollectionForm("Default Collection", "Default Collection", "http://walcraft.wmzspace.space/static/tour/example/1.png", "Default Collection"));
                return ApiResponse.success("Signup succeed!");
            } else {
                return ApiResponse.error(e.getMessage());
            }
        }
    }

    /**
     * @param id
     * @param email
     * @return ApiResponse
     * @eo.name isUserExist
     * @eo.url /exist
     * @eo.method get
     * @eo.request-type formdata
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
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }

    /**
     * @param id
     * @param email
     * @return ApiResponse
     * @eo.name getSingleUser
     * @eo.url /
     * @eo.method get
     * @eo.request-type formdata
     */
//    @AdminToken
    @GetMapping
    public ApiResponse<UserDTO> getSingleUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        try {
            if (id != null) {
                return ApiResponse.success("GET user succeed with id", userService.getUserById(id).toDTO());
            } else if (email != null) {
                return ApiResponse.success("GET user succeed with email", userService.getUserByEmail(email).toDTO());
            }
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
        return ApiResponse.error("Both id and email cannot be null");
    }

    /**
     * @param user
     * @param updatedInfo
     * @return ApiResponse
     * @eo.name updateUser
     * @eo.url
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping
    ApiResponse<UserDTO> updateUser(@RequestAttribute("user") User user, @RequestBody UserService.ReqUpdateForm updatedInfo) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("User information updated", userService.updateUser(eagerredUser.getId(), updatedInfo).toDTO());
        } catch (AuthException | ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param updatedInfo
     * @return ApiResponse
     * @eo.name updateUserType
     * @eo.url /type
     * @eo.method put
     * @eo.request-type json
     */
    @PutMapping(value = "/type")
    ApiResponse<UserDTO> updateUserType(@RequestAttribute("user") User user, @RequestBody UserService.UpdateUserTypeForm updatedInfo) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("User Type updated", userService.updateUserType(eagerredUser.getId(), updatedInfo).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @param vipPackage
     * @return ApiResponse
     * @eo.name updateVipExpireTime
     * @eo.url /buy_vip
     * @eo.method put
     * @eo.request-type formdata
     */
    @PutMapping(value = "/buy_vip")
    ApiResponse<UserDTO> updateVipExpireTime(@RequestAttribute("user") User user, @RequestParam Integer vipPackage) {
        try {
            for (var p : User.VipPackage.values()) {
                if (p.ordinal() == vipPackage) {
                    User eagerredUser = userService.getUserById(user.getId());
                    return ApiResponse.success("Vip opened successfully", userService.buyVip(eagerredUser, p).toDTO());
                }
            }

            return ApiResponse.error("Invalid VIP package");

        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @eo.name cancelVip
     * @eo.url /cancel_vip
     * @eo.method put
     * @eo.request-type formdata
     * @param user
     * @return ApiResponse
     */
    @PutMapping(value = "/cancel_vip")
    ApiResponse<UserDTO> cancelVip(@RequestAttribute("user") User user) {
        try {
            User eagerredUser = userService.getUserById(user.getId());
            return ApiResponse.success("User vip is expired", userService.cancelVip(eagerredUser.getId()).toDTO());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @return ApiResponse
     * @eo.name predictWeeklyRevenue
     * @eo.url /predict_weekly_revenue
     * @eo.method get
     * @eo.request-type formdata
     */
    @GetMapping("/predict_weekly_revenue")
    ApiResponse<List<Map.Entry<LocalDate, Double>>> predictWeeklyRevenue() {
        try {
            List<Map.Entry<LocalDate, Double>> weeklyRevenuePredictions = userService.predictWeeklyRevenue();
            return ApiResponse.success("Weekly revenue predictions retrieved successfully", weeklyRevenuePredictions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param user
     * @return ApiResponse
     * @eo.name removeUser
     * @eo.url /
     * @eo.method delete
     * @eo.request-type formdata
     */
    @DeleteMapping
    ApiResponse<Void> removeUser(@RequestAttribute("user") User user) {
        try {
            userService.deleteUser(user.getId());
            return ApiResponse.success("User has been removed");
        } catch (ResourceException | AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }


    /**
     * @param inviteId
     * @param groupId
     * @return ApiResponse
     * @eo.name addUserToGroup
     * @eo.url /addUserToGroup
     * @eo.method post
     * @eo.request-type formdata
     */
    @PostMapping("/addUserToGroup")
    public ApiResponse<Void> addUserToGroup(@RequestParam Long inviteId, Long groupId) {
        try {
            userService.addUserToGroup(inviteId, groupId);
            return ApiResponse.success("User added to group successfully");
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        } catch (ResourceException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * @param file
     * @param uploadURL
     * @param filename
     * @return ApiResponse
     * @eo.name uploadFile
     * @eo.url /upload
     * @eo.method post
     * @eo.request-type formdata
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("uploadURL") String uploadURL, @RequestParam(value = "filename", required = false) String filename) {
        if (file.isEmpty()) {
            return ApiResponse.error("File is empty");
        }
        try {
            // 生成一个随机的文件名或沿用参数
            String fileName = Objects.nonNull(filename) ? filename : UUID.randomUUID() + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
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
     * @param token
     * @return ApiResponse
     * @eo.name validateToken
     * @eo.url /token/{value}
     * @eo.method get
     * @eo.request-type formdata
     */
    @IgnoreToken
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
