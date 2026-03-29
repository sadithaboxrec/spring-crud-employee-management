package com.test.employee.entity;

import com.test.employee.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "access_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column( name = "full_name")
    private String fullName;

    @Column(unique = true , nullable = false)
    private String username;

    @Column( name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
