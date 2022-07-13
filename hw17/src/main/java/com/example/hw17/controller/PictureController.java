package com.example.hw17.controller;

import com.example.hw17.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService pictureService;
    private final RestTemplate restTemplate;

    @GetMapping("/{sol}/largest")
    public ResponseEntity<String> getLargestPicture(@PathVariable("sol") Integer sol) {
        String imgSrc = pictureService.getLargestPictureUrl(sol);
        return restTemplate.getForEntity(imgSrc, String.class);
    }
}
