package com.projek.projek.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
import com.projek.projek.model.View;
import com.projek.projek.service.ImageService;
import com.projek.projek.service.UserService;
import com.projek.projek.service.ViewService;

@Controller
public class ImageController {

	private static Path UPLOAD_DIRECTORY = Paths.get(System.getProperty("user.dir"));
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private ViewService viewService;
	
	@GetMapping("/image-add/{id}")
	public String imageAdd(@PathVariable("id") Long id,
			Model model) {
		User user = userService.findById(id);
		
		model.addAttribute("user", user);
		model.addAttribute("image", new Image());
		return "image-add";
	}
	
	@PostMapping("/image-add/{id}")
	public String imageAdd(@PathVariable("id") Long id,
			@RequestParam("image") MultipartFile image,
			@ModelAttribute("image") Image newImageModel,
			Model model) throws IOException {
		Image newImage = new Image();
		newImage.setDetail(newImageModel.getDetail());
		newImage.setTitle(newImageModel.getTitle());
		
		User user = userService.findById(id);
		
		Path staticPath = Paths.get("src","main","resources","static");
		Path imgPath = Paths.get("img");
		Path imagePath = Paths.get("image");
		Path userPath = Paths.get(user.getId().toString());
		
		if(!Files.exists(UPLOAD_DIRECTORY.resolve(staticPath)
				.resolve(imgPath)
				.resolve(imagePath)
				.resolve(userPath))) {
			Files.createDirectories(UPLOAD_DIRECTORY.resolve(staticPath)
					.resolve(imgPath)
					.resolve(imagePath)
					.resolve(userPath));
		}
		Path file = UPLOAD_DIRECTORY.resolve(staticPath)
				.resolve(imgPath)
				.resolve(imagePath)
				.resolve(userPath)
				.resolve(image.getOriginalFilename());
		try(OutputStream os = Files.newOutputStream(file)){
			os.write(image.getBytes());
		}
		
		newImage.setUri(imgPath.resolve(imagePath).resolve(userPath).resolve(image.getOriginalFilename()).toString().replace("\\", "/"));
		newImage.setUser(user);
		newImage.setCreateTime(LocalDateTime.now());
		
		imageService.save(newImage);
		
		return "redirect:/user/" + id;
	}
	
	@GetMapping("/image-view")
	public String imageView(@RequestParam("idi") Long idi,
			Model model) {
		Image image = imageService.findById(idi);
		List<View> views = viewService.findByImageAndCmtIsNot(image, "");
		List<Image> topFiveImagesOrderByNumberOfViews = imageService.findTopFiveOrderByNumberOfViews();
		
		model.addAttribute("imageView", image);
		model.addAttribute("views", views);
		model.addAttribute("topViewsImages", topFiveImagesOrderByNumberOfViews);
		return "image";
	}
	
	@GetMapping("/image-view/{id}")
	public String imageView(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			Model model) {
		User user = userService.findById(id);
		Image image = imageService.findById(idi);
		List<View> views = viewService.findByImageAndCmtIsNot(image, "");
		List<Image> topFiveImagesOrderByNumberOfViews = imageService.findTopFiveOrderByNumberOfViews();

		
		if(user.equals(image.getUser())) {
			return "redirect:/image/" + id + "?idi=" + idi;
		}
		if(viewService.findByImageIdAndUserId(idi, id) == null) {
			View view = new View();
			view.setUser(user);
			view.setImage(image);
			view.setRect(Long.valueOf(0));
			view.setCmt("");
			viewService.save(view);
		}
		View view = viewService.findByImageIdAndUserId(idi, id);
		model.addAttribute("user", user);
		model.addAttribute("imageView", image);
		model.addAttribute("view", view);
		model.addAttribute("views", views);
		model.addAttribute("topViewsImages", topFiveImagesOrderByNumberOfViews);
		return "image";
	}
	
	
	@GetMapping("/image/{id}")
	public String image(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			Model model) {
		User user = userService.findById(id);
		Image image = imageService.findById(idi);
		List<View> views = viewService.findByImageAndCmtIsNot(image, "");
		List<Image> topFiveImagesOrderByNumberOfViews = imageService.findTopFiveOrderByNumberOfViews();
		
		model.addAttribute("user", user);
		model.addAttribute("image", image);
		model.addAttribute("views", views);
		model.addAttribute("topViewsImages", topFiveImagesOrderByNumberOfViews);
		return "image";
	}
	
	@PostMapping("/image/{id}")
	public String imageEdit(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			@RequestParam("image") MultipartFile image,
			@ModelAttribute("image") Image editImage,
			Model model) throws IOException {
		User user = userService.findById(id);
		Image curImage = imageService.findById(idi);
		
		if(editImage.getTitle() != null && !editImage.getTitle().equals(curImage.getTitle())) {
			curImage.setTitle(editImage.getTitle());
		}
		if(editImage.getDetail() != null && !editImage.getDetail().equals(curImage.getDetail())) {
			curImage.setDetail(editImage.getDetail());
		}
		if(!image.getOriginalFilename().equals("")) {
			Path staticPath = Paths.get("src","main","resources","static");
			Path imgPath = Paths.get("img");
			Path imagePath = Paths.get("image");
			Path userPath = Paths.get(user.getId().toString());
			
			if(!Files.exists(UPLOAD_DIRECTORY.resolve(staticPath)
					.resolve(imgPath)
					.resolve(imagePath)
					.resolve(userPath))) {
				Files.createDirectories(UPLOAD_DIRECTORY.resolve(staticPath)
						.resolve(imgPath)
						.resolve(imagePath)
						.resolve(userPath));
			}
			Path file = UPLOAD_DIRECTORY.resolve(staticPath)
					.resolve(imgPath)
					.resolve(imagePath)
					.resolve(userPath)
					.resolve(image.getOriginalFilename());
			try(OutputStream os = Files.newOutputStream(file)){
				os.write(image.getBytes());
			}
			
			curImage.setUri(imgPath.resolve(imagePath).resolve(userPath).resolve(image.getOriginalFilename()).toString());
		}
		
		imageService.save(curImage);
		
		model.addAttribute("user", user);
		model.addAttribute("image", curImage);
		if(user.equals(curImage.getUser())) {
			model.addAttribute("valid", true);
		}
		return "image";
	}
	
	@GetMapping("/image-edit/{id}")
	public String imageEdit(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			Model model) {
		User user = userService.findById(id);
		Image image = imageService.findById(idi);
		model.addAttribute("user", user);
		model.addAttribute("image", image);
		model.addAttribute("edit", true);
		return "image";
	}
	
	@GetMapping("/image-delete/{id}")
	public String imageDelete(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			Model model) {
		Image image = imageService.findById(idi);
		imageService.delete(image);
		return "redirect:/user/" + id;
	}
	
	@GetMapping("/view-edit/{id}")
	public String viewEdit(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			@RequestParam("rect") Long rect,
			Model model) {
		View view = viewService.findByImageIdAndUserId(idi, id);
		view.setRect(rect);
		viewService.save(view);
		
		return "redirect:/image-view/" + id + "?idi=" + idi;
	}
	
	@PostMapping("/view-edit/{id}")
	public String viewEdit(@PathVariable("id") Long id,
			@RequestParam("idi") Long idi,
			@ModelAttribute("view") View viewEdit,
			Model model) {
		View view = viewService.findByImageIdAndUserId(idi, id);
		view.setCmt(viewEdit.getCmt());
		viewService.save(view);
		
		return "redirect:/image-view/" + id + "?idi=" + idi;
	}
}
