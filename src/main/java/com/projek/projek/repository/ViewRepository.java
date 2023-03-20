package com.projek.projek.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projek.projek.model.Image;
import com.projek.projek.model.View;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {

	public View findByImageIdAndUserId(Long imageId, Long userId);
	
	public List<View> findByImageAndCmtIsNot(Image image, String cmt);
}
