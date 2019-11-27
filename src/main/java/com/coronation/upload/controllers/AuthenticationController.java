package com.coronation.upload.controllers;

import com.coronation.upload.dto.ADLogin;
import com.coronation.upload.dto.ADLoginResponse;
import com.coronation.upload.dto.AuthToken;
import com.coronation.upload.dto.LoginUser;
import com.coronation.upload.security.ProfileDetails;
import com.coronation.upload.security.TokenProvider;
import com.coronation.upload.services.UserService;
import com.coronation.upload.util.Constants;
import com.coronation.upload.util.GenericUtil;
import com.coronation.upload.util.Utilities;
import com.coronation.upload.ws.AuthResponseDTO;
import com.coronation.upload.ws.EntrustMultiFactorAuthImpl;
import com.coronation.upload.ws.TokenAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/token")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private Utilities utilities;

    @Autowired
    private EntrustMultiFactorAuthImpl entrustMultiFactorAuth;

    private Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Value("${app.entrust.code}")
    private String entrustAppCode;

    @Value("${app.entrust.desc}")
    private String entrustAppDescription;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginUser loginUser) throws AuthenticationException {
        loginUser.setUsername(loginUser.getUsername().toLowerCase());
        if (GenericUtil.isStaffEmail(loginUser.getUsername())) {
            if (staffAuthenticated(loginUser)) {
                String password = userService.generatePassword(loginUser.getUsername());
                loginUser.setPassword(password);
                logger.info("Logged staff in");
            } else {
                logger.info("Unable to log staff in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(new AuthToken(token));
    }

    private boolean staffAuthenticated(LoginUser loginUser) {
        ADLogin adLogin = new ADLogin();
        adLogin.setUsername(loginUser.getUsername());
        adLogin.setPasswd(loginUser.getPassword());
        ResponseEntity<ADLoginResponse> loginResponse = utilities.adLogin(adLogin);
        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            return loginResponse.getBody().isStatus();
        }
        return false;
    }

    @PostMapping("/{token}/validate")
    public ResponseEntity<AuthResponseDTO> validateToken(@PathVariable("token") String token,
                                                         @AuthenticationPrincipal ProfileDetails profileDetails) {
        TokenAuthDTO tokenAuthDTO = new TokenAuthDTO();
        tokenAuthDTO.setUserName(profileDetails.getUsername());
        tokenAuthDTO.setTokenPin(token);
        tokenAuthDTO.setAppCode(entrustAppCode);
        tokenAuthDTO.setAppDesc(entrustAppDescription);
        tokenAuthDTO.setGroup(Constants.STAFF_ENTRUST_GROUP);
        return ResponseEntity.ok(entrustMultiFactorAuth.performTokenAuth(tokenAuthDTO));
    }
}
