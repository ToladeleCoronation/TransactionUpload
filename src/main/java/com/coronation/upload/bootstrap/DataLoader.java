package com.coronation.upload.bootstrap;

import com.coronation.upload.domain.Role;
import com.coronation.upload.domain.User;
import com.coronation.upload.domain.enums.DriverType;
import com.coronation.upload.domain.enums.GenericStatus;
import com.coronation.upload.domain.enums.RoleType;
import com.coronation.upload.repo.RoleRepository;
import com.coronation.upload.repo.UserRepository;
import com.coronation.upload.util.GenericUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder encoder;

	private Logger logger = LogManager.getLogger(DataLoader.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		seedRoles();
		seedUsers();
		loadDrivers();
		createStoragePath();
	}

	private void seedUsers() {
		List<User> users = userRepository.findAll();
		List<User> saveUsers= new ArrayList<>();
		if (users.isEmpty()) {
			User user = new User();

			user.setFirstName("Sunday");
			user.setLastName("Admin");
			user.setPhoneNumber("08039213119");
			user.setEmail("hold@gmail.com");
			user.setPassword(encoder.encode("password"));
			user.setStatus(GenericStatus.ACTIVE);
			Role role = roleRepository.findByName(RoleType.OP_ADMIN);
			user.setRole(role);
			User user1 = new User();
			user1.setFirstName("Gold");
			user1.setLastName("Admin");
			user1.setPhoneNumber("08131248746");
			user1.setEmail("gold.o@ng.lopworks.com");
			user1.setPassword(encoder.encode("password"));
			user1.setStatus(GenericStatus.ACTIVE);
			Role role1 = roleRepository.findByName(RoleType.IT_ADMIN);
			user1.setRole(role1);
			saveUsers.add(user);
			saveUsers.add(user1);
			saveUsers= userRepository.saveAll(saveUsers);
			for(User u: saveUsers) {
				logger.info("Saved user - id:" + u.getId());
			}
		}
	}

	private void seedRoles() {
		Arrays.stream(RoleType.values()).forEach(roleType -> {
			Role role = roleRepository.findByName(roleType);
			if (role == null) {
				role = new Role();
				role.setName(roleType);
				role.setRoleDescription(roleType.name());
				roleRepository.saveAndFlush(role);
			}
		});
	}

	private void loadDrivers() {
		try {
			for (DriverType driverType: DriverType.values()) {
				if (driverType.equals(DriverType.ORACLE)) {
					//DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
					Class.forName("oracle.jdbc.driver.OracleDriver");
				} else {
					Class.forName(driverType.getValue()).newInstance();
				}
			}
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}
	}

	private void createStoragePath() {
		String path = GenericUtil.getStoragePath();
		File file = Paths.get(path).toFile();
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
