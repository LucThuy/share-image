package com.projek.projek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projek.projek.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	
}
