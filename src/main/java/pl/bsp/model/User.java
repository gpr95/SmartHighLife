package pl.bsp.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user", nullable = false)
	private long id;
	@Column(name = "first_name", nullable = false, length = 60)
    private String firstName;
	@Column(name = "last_name", nullable = false, length = 60)
    private String lastName;
	@Column(name = "username",nullable = false, length = 30)
    private String username;
	@Column(name = "password", nullable = false, length = 60)
    private String password;
	@Column(name = "role", nullable = false, length = 30)
    private String role;
	@Column(name = "enabled", nullable = false, length = 1)
	private int enabled;
	@Column(name = "email", nullable = false, length = 60)
	private String email;
	@JsonBackReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Resource> resources = new ArrayList<>();
	@JsonBackReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ParentalControlPolicy> policies = new ArrayList<>();
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<ParentalControlPolicy> getPolicies() {
		return policies;
	}
	public void setPolicies(List<ParentalControlPolicy> policies) {
		this.policies = policies;
	}
    	
}
