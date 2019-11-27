package com.coronation.upload.domain;

import com.coronation.upload.domain.enums.GenericStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Email
	@Column(name = "email", unique = true)
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", nullable = false)
	private String password;

	@NotNull
	@Column(name = "first_name")
	private String firstName;

	@NotNull
	@Column(name = "last_name")
	private String lastName = "";

	@NotNull
	@Pattern(regexp="(\\+)?[0-9]{11,20}$")
	private String phoneNumber;

	@Column(name = "address")
	private String address;

	@Column
	@Lob
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private byte[] profileImage;

	@Transient
	private String pictureBase64;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private GenericStatus status = GenericStatus.INACTIVE;

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name="created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name="modified_at")
	private LocalDateTime modifiedAt = LocalDateTime.now();

	@Transient
	private String passcode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public GenericStatus getStatus() {
		return status;
	}

	public void setStatus(GenericStatus status) {
		this.status = status;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		return id != null ? id.equals(user.id) : user.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
