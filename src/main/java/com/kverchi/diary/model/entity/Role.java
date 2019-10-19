package com.kverchi.diary.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class Role implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "role")
	private String role;
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}


	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinTable(
			name = "roles_privileges",
			joinColumns = {@JoinColumn(name = "role_id")},
			inverseJoinColumns = {@JoinColumn(name = "privilege_id")}
	)
	private List<Privilege> privileges;

	public Role() {
		super();
	}

	public Role(String role) {
		this.role = role;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
}
