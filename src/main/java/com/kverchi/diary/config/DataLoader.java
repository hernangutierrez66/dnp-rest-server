package com.kverchi.diary.config;

import com.kverchi.diary.model.entity.*;
import com.kverchi.diary.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }


        Department department = new Department();
        department.setDaneCode(73);
        department.setName("Tolima");
        department.setState(1);
        departmentRepository.save(department);

        Municipality municipality = new Municipality();
        municipality.setDaneCode(730001);
        municipality.setName("Ibagué");
        municipality.setState(1);
        municipality.setDepartment(department);
        municipalityRepository.save(municipality);

        // == create initial privileges
        final Privilege superPrivilege = createPrivilegeIfNotFound("SUPER_PRIVILEGE");
        final Privilege createPrivilege = createPrivilegeIfNotFound("CREATE_PRIVILEGE");
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> webMasterPrivileges = new ArrayList<Privilege>(Arrays.asList(superPrivilege, createPrivilege, readPrivilege, writePrivilege, deletePrivilege, passwordPrivilege));
        final List<Privilege> adminPrivileges = new ArrayList<Privilege>(Arrays.asList(createPrivilege, readPrivilege, writePrivilege, deletePrivilege));
        final List<Privilege> jefeDeControlPrivileges = new ArrayList<Privilege>(Arrays.asList(createPrivilege, readPrivilege, writePrivilege, deletePrivilege, passwordPrivilege));
        final List<Privilege> liderDeSeguimientoPrivileges = new ArrayList<Privilege>(Arrays.asList(writePrivilege, deletePrivilege, readPrivilege));
        final List<Privilege> gestorDeMetasPrivileges = new ArrayList<Privilege>(Arrays.asList(readPrivilege));


        final Role webMaster = createRoleIfNotFound("ROLE_WEBMASTER", webMasterPrivileges);
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role jedeDeControlRole = createRoleIfNotFound("ROLE_JEFECONTROL", jefeDeControlPrivileges);
        final Role liderDeSeguimientoRole = createRoleIfNotFound("ROLE_LIDERSEGUIMIENTO", liderDeSeguimientoPrivileges);
        final Role gestoMetasRole = createRoleIfNotFound("ROLE_GESTOR", gestorDeMetasPrivileges);
        //createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial user
        createUserIfNotFound("webmaster", "webmaster", true, "webmaster@mail.com", new ArrayList<Role>(Arrays.asList(webMaster)));

        alreadySetup = true;
    }

    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege.setState(1);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final List<Privilege> privileges) {
        Role role = roleRepository.findByRole(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    private final User createUserIfNotFound(String username, String password, boolean isEnabled, String email, Collection<Role> roles) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
            //user.setMunicipality(municipality);
        }
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
}
