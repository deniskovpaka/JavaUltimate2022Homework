package com.example.hw17.service;
import com.example.hw17.model.GetPicturesResponse;
import com.example.hw17.model.Photo;
import com.example.hw17.model.PhotoParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;

@Log4j2
@Service
@RequiredArgsConstructor
public class PictureService {

    @Value("${nasa.baseurl}")
    private String baseurl;

    @Value("${nasa.apikey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Cacheable("largestNasaPicture")
    public String getLargestPictureUrl(Integer sol) {
        GetPicturesResponse response = restTemplate.getForObject(buildUrl(sol), GetPicturesResponse.class);
        assert response != null;
        PhotoParams params = response.photos()
                .parallelStream()
                .map(Photo::imgSrc)
                .map(this::getPhotoParams)
                .max(Comparator.comparing(PhotoParams::size))
                .orElseThrow();

        log.info("img src: " + params.imgSrc());
        log.info("img size: " + params.size());
        return params.imgSrc();
    }

    private String buildUrl(Integer sol) {
        return UriComponentsBuilder.fromHttpUrl(baseurl)
                .queryParam("sol", sol)
                .queryParam("api_key", apiKey)
                .build()
                .toString();
    }

    private PhotoParams getPhotoParams(String img) {
        var location = restTemplate.headForHeaders(img).getLocation();
        assert location != null;
        return new PhotoParams(img, restTemplate.headForHeaders(location).getContentLength());
    }
}