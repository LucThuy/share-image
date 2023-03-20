package com.projek.projek.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.projek.projek.model.Image;
import com.projek.projek.model.User;
import com.projek.projek.service.ImageService;
import com.projek.projek.service.UserService;


@Controller
public class LoginController {

	private static Path UPLOAD_DIRECTORY = Paths.get(System.getProperty("user.dir"));
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImageService imageService;
	
	@GetMapping(value = {"/", "/projek", "/projek/null"})
	public String index(Model model) {
		List<Image> topThreeImagesOrderByCreateTime = imageService.findTopThreeOrderByCreateTimeAsc();
		List<Image> topSixImagesOrderByCreateTime = imageService.findTopSixOrderByCreateTimeAsc();
		List<Image> topFiveImagesOrderByNumberOfViews = imageService.findTopFiveOrderByNumberOfViews();
		
		model.addAttribute("sliderImages", topThreeImagesOrderByCreateTime);
		model.addAttribute("trendingNowImages", topSixImagesOrderByCreateTime);
		model.addAttribute("topViewsImages", topFiveImagesOrderByNumberOfViews);
		return "index";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("newUser", new User());
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute("user") User user,
			Model model) {
		User userValid = userService.findByUsernameAndPassword(user.getUsername(), user.getPassword());
		if(userValid != null) {
			return "redirect:/projek/" + userValid.getId();
		}
		return "redirect:/login?error=true";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("newUser", new User());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signup(@RequestParam("image") MultipartFile image,
			@ModelAttribute("user") User newUserModel,
			Model model) throws IOException {
		User newUser = new User();
		newUser.setName(newUserModel.getName());
		newUser.setUsername(newUserModel.getUsername());
		newUser.setPassword(newUserModel.getPassword());
		userService.save(newUser);
		
		User user = userService.findByUsernameAndPassword(newUser.getUsername(), newUser.getPassword());
		
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
		userService.save(user);
		return "redirect:/signup?success=true";
	}
}
