package com.kverchi.diary.model.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name="users")
public class User implements Serializable, UserDetails {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private int userId;
	private String username;
    private String password;
	@Column(name="is_enabled")
    private boolean isEnabled;
    private String email;
	private String information;
	@Column(name = "registration_date", insertable=true)
	private ZonedDateTime registrationDate;


	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    		name="user_roles",
    		joinColumns = {@JoinColumn(name="user_id", referencedColumnName="user_id")},
    		inverseJoinColumns = {@JoinColumn(name="role_id", referencedColumnName="role_id")}
    		)
    private Collection<Role> roles = new HashSet<Role>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "municipio_id")
	private Municipality municipality;


    public User() {super();}
	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(String username, String password, boolean isEnabled, String email, ZonedDateTime registrationDate, Collection<Role> roles, Municipality municipality) {
		this.username = username;
		this.password = password;
		this.isEnabled = isEnabled;
		this.email = email;
		this.registrationDate = registrationDate;
		this.roles = roles;
		this.municipality = municipality;
	}

	public User(int userId) {
		this.userId = userId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Municipality getMunicipality() {
		return municipality;
	}

	public void setMunicipality(Municipality municipality) {
		this.municipality = municipality;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	@JsonIgnore
	public ZonedDateTime getRegistrationDate() {
		DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
		String text = registrationDate.format(formatter);
		registrationDate = ZonedDateTime.parse(text, formatter);
		return registrationDate;
	}

	public void setRegistrationDate(ZonedDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Collection<Role> getRoles() {
		return roles;
	}
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
    
}
