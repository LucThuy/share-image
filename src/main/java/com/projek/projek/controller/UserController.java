package com.projek.projek.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.projek.projek.model.Image;
import com.projek.projek.model.User;
import com.projek.projek.service.ImageService;
import com.projek.projek.service.UserService;

@Controller
public class UserController {

	private static Path UPLOAD_DIRECTORY = Paths.get(System.getProperty("user.dir"));
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImageService imageService;
	
	@GetMapping("/projek/{id}")
	public String indexUser(@PathVariable("id") Long id,
			Model model) {
		User user = userService.findById(id);
		List<Image> topThreeImagesOrderByCreateTime = imageService.findTopThreeOrderByCreateTimeAsc();
		List<Image> topSixImagesOrderByCreateTime = imageService.findTopSixOrderByCreateTimeAsc();
		List<Image> topFiveImagesOrderByNumberOfViews = imageService.findTopFiveOrderByNumberOfViews();
		
		model.addAttribute("user", user);
		model.addAttribute("sliderImages", topThreeImagesOrderByCreateTime);
		model.addAttribute("trendingNowImages", topSixImagesOrderByCreateTime);
		model.addAttribute("topViewsImages", topFiveImagesOrderByNumberOfViews);
		return "index";
	}
	
	@GetMapping("/user-view")
	public String userView(@RequestParam("idu") Long idu,
			Model model) {
		User userView = userService.findById(idu);
		List<Image> images = userView.getImages();
		model.addAttribute("userView", userView);
		model.addAttribute("images", images);
		return "user";
	}
	
	@GetMapping("/user-view/{id}")
	public String user(@PathVariable("id") Long id,
			@RequestParam("idu") Long idu,
			Model model) {
		User user = userService.findById(id);
		User userView = userService.findById(idu);
		List<Image> images = userView.getImages();
		model.addAttribute("user", user);
		model.addAttribute("userView", userView);
		model.addAttribute("images", images);
		
		if(user.equals(userView)) {
			return "redirect:/user/" + id;
		}
		return "user";
	}
	
	@GetMapping("/user/{id}")
	public String user(@PathVariable("id") Long id,
			Model model) {
		User user = userService.findById(id);
		List<Image> images = user.getImages();
		model.addAttribute("user", user);
		model.addAttribute("images", images);
		return "user";
	}
	
	@PostMapping("/user/{id}")
	public String userEdit(@PathVariable("id") Long id,
			@RequestParam("image") MultipartFile image,
			@ModelAttribute("user") User editUser,
			Model model) throws IOException {
		User user = userService.findById(id);
		
		if(editUser.getName() != null && !editUser.getName().equals(user.getName())) {
			user.setName(editUser.getName());
		}
		if(editUser.getBio() != null && !editUser.getBio().equals(user.getBio())) {
			user.setBio(editUser.getBio());
		}
		if(!image.getOriginalFilename().equals("")) {
			Path staticPath = Paths.get("src","main","resources","static");
			Path imgPath = Paths.get("img");
			Path avatarPath = Paths.get("avatar");
			Path userPath = Paths.get(user.getId().toString());
			
			if(!Files.exists(UPLOAD_DIRECTORY.resolve(staticPath)
					.resolve(imgPath)
					.resolve(avatarPath)
					.resolve(userPath))) {
				Files.createDirectories(UPLOAD_DIRECTORY.resolve(staticPath)
						.resolve(imgPath)
						.resolve(avatarPath)
						.resolve(userPath));
			}
			Path file = UPLOAD_DIRECTORY.resolve(staticPath)
					.resolve(imgPath)
					.resolve(avatarPath)
					.resolve(userPath)
					.resolve(image.getOriginalFilename());
			try(OutputStream os = Files.newOutputStream(file)){
				os.write(image.getBytes());
			}
			
			user.setAvatar(imgPath.resolve(avatarPath).resolve(userPath).resolve(image.getOriginalFilename()).toString().replace("\\", "/"));
		}
		
		userService.save(user);
		List<Image> images = new ArrayList<>(user.getImages());
		model.addAttribute("user", user);
		model.addAttribute("images", images);
		return "user";
	}
	
	@GetMapping("/user-edit/{id}")
	public String userEdit(@PathVariable("id") Long id,
			Model model) {
		User user = userService.findById(id);
		List<Image> images = new ArrayList<>(user.getImages());
		model.addAttribute("edit", true);
		model.addAttribute("user", user);
		model.addAttribute("images", images);
		return "user";
	}
}
