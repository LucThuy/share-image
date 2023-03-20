package com.projek.projek.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projek.projek.model.Image;
import com.projek.projek.model.View;
import com.projek.projek.repository.ViewRepository;

@Service
public class ViewService {

	@Autowired
	private ViewRepository viewRepository;
	
	public View findByImageIdAndUserId(Long imageId, Long userId) {
		return viewRepository.findByImageIdAndUserId(imageId, userId);
	}
	
	public List<View> findByImageAndCmtIsNot(Image image, String cmt){
		return viewRepository.findByImageAndCmtIsNot(image, cmt);
	}
	
	public View save(View view) {
		return viewRepository.save(view);
	}
}
