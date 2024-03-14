/**
 * User Controller Class
 */
package com.se.backend.controllers;

import com.se.backend.exceptions.AuthException;
import com.se.backend.models.User;
import com.se.backend.projection.UserDTO;
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

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final TourCollectionService tourCollectionService;

    @Autowired
    public UserController(UserService userService, TourCollectionService tourCollectionService) {
        this.userService = userService;
        this.tourCollectionService = tourCollectionService;
    }

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    @AdminToken
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return ApiResponse.success("Get all users", UserDTO.toListDTO(userService.getAllUsers()));
    }

    /**
     * 添加用户
     *
     * @param req 新用户信息
     * @return access token
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
                tourCollectionService.createTourCollection(userService.createUser(newUser), new TourCollectionService.CreateTourCollectionForm("Hiking Collection", "Hike a hidden gem in Southwest Germany – Palatinate High Route", "http://walcraft.wmzspace.space/static/tour/example/1.png", "For those who love cycling, adventure and, more generally, the outdoors, the idea of conquering epic mountains is certainly a strong driving force."));
                return ApiResponse.success("Signup succeed!");
            } else {
                return ApiResponse.error(e.getMessage());
            }
        }
    }

    /**
     * 根据ID或者邮箱验证对应用户是否存在
     *
     * @param id,email 用户ID, 邮箱
     * @return ApiResponse<boolean>
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
     * 根据ID或者邮箱获取用户信息
     *
     * @param id,email 用户ID, 邮箱
     * @return ApiResponse<对应ID的用户信息>
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
     * 更新用户信息
     *
     * @param user        用户
     * @param updatedInfo 更新后的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping
    ApiResponse<UserDTO> updateUser(@RequestAttribute("user") User user, @RequestBody UserService.ReqUpdateForm updatedInfo) {
        try {
            return ApiResponse.success("User information updated", userService.updateUser(user.getId(), updatedInfo).toDTO());
        } catch (AuthException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
//    public ApiResponse<User> getSingleUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
//        if (Objects.equals(user.getId(), id) || Objects.equals(user.getEmail(), email)) {
//            return ApiResponse.success("GET user succeed", user);
//        }
//        return ApiResponse.error("Error when getting user");
//    }

    /**
     * 删除用户信息
     *
     * @param user 用户ID
     * @return ApiResponse<Void>
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

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("uploadURL") String uploadURL) {
        if (file.isEmpty()) {
            return ApiResponse.error("File is empty");
        }


        try {
            // 生成一个随机的文件名
            String fileName = UUID.randomUUID() + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

            // 保存文件到本地
            saveFileToLocal(file.getInputStream(), uploadURL.concat("/").concat(fileName));

            // 如果需要保存到数据库或者其他操作，可以在这里进行处理

            return ApiResponse.success("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("Failed to upload file");
        }
    }


    // Signup request form from client
    @Getter
    public static class ReqSignupForm {
        String email;
        String nickname;
        String password;
    }
}
