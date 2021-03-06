package org.pikIt.studentInit.services;

import lombok.Data;
import org.pikIt.studentInit.model.Role;
import org.pikIt.studentInit.model.User;
import org.pikIt.studentInit.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void adminInit(){
        if(userRepository.findByEmail("Admin@admin") == null){
            User user = new User();
            user.setName("Admin");
            user.setSurname("Admin");
            user.setEmail("Admin@admin");
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.SUPER_USER));
            user.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
    }

    public String addUserInModel(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "home/registration";
    }

    public String adduser(User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);

            return "home/registration";
        } else {
            if (userRepository.findByEmail(user.getEmail()) != null) {
                model.addAttribute("message", "???????????????????????? ????????????????????");
                return "home/registration";
            }
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return "redirect:/home";
        }
    }

    public void replaceUsers(Model model) {
        model.addAttribute("users", userRepository.findActiveUsers());
    }

    public String userEdit(Model model, User user) {
        model.addAttribute("user2", user);
        model.addAttribute("userRoles", user.getRoles());
        model.addAttribute("roles", Role.values());
        return "users/userEdit";
    }

    public String userSave(Map<String, String> userRolesMap, User user) {
        Set<String> roleSet = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : userRolesMap.keySet()) {
            if (roleSet.contains(key))
                user.getRoles().add(Role.valueOf(key));
        }
        userRepository.save(user);
        return "redirect:/users/superUserPage";
    }

    public String searchUsers(String filterName, String filterSurname,
                              Model model) {
        if (filterName != null && !filterName.isBlank() || filterSurname != null && !filterSurname.isBlank()) {
            model.addAttribute("users", userRepository.findByNameAndSurnameContains(filterName, filterSurname));
            model.addAttribute("message", "?????????????????? ????????????????????????");
        } else {
            model.addAttribute("message", "???????????????????????? ???? ????????????");
            model.addAttribute("users", userRepository.findAll());
        }
        return "users/superUserPage";
    }

    public void findInactiveUsers(Model model) {
        if (!userRepository.findInactiveUsers().isEmpty()) {
            model.addAttribute("users", userRepository.findInactiveUsers());
            model.addAttribute("message", "?????????????????? ????????????????????????");
            return;
        }
        replaceUsers(model);
        model.addAttribute("message", "???????????????????? ???????????????????????? ???? ??????????????");
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s);
    }
}
