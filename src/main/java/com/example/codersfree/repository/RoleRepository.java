package com.example.codersfree.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.codersfree.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
 
    //Realizar busquedas por el nombre del rol
    boolean existsByName(String name);

}
