package com.projek.projek.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projek.projek.model.Image;
import com.projek.projek.repository.ImageRepository;
import com.projek.projek.repository.ViewRepository;

@Service
public class ImageService {
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired ViewRepository viewRepository;
	
	public Image findById(Long id) {
		return imageRepository.findById(id).get();
	}
	
	public List<Image> findTopThreeOrderByCreateTimeAsc(){
		return imageRepository.findAll(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createTime"))).toList();
	}
	
	public List<Image> findTopSixOrderByCreateTimeAsc(){
		return imageRepository.findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createTime"))).toList();
	}
	
	public List<Image> findTopFiveOrderByNumberOfViews(){
		List<Image> listImage = imageRepository.findAll();
		listImage.sort((img1, img2) -> {
			return img2.getViews().size() - img1.getViews().size();
		});
		if(listImage.size() < 5) return listImage;
		return listImage.subList(0, 4);
	}
	
	public Image save(Image image) {
		return imageRepository.save(image);
	}
	
	public void delete(Image image) {
		imageRepository.delete(image);
	}
}
