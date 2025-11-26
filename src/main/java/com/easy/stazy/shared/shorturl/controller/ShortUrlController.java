package com.easy.stazy.shared.shorturl.controller;

import com.easy.stazy.shared.shorturl.service.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;


@Tag(name = "Short URL Management", description = "APIs for creating and managing short URLs")
@RestController
@RequestMapping("/v1/s")
public class ShortUrlController {
    @Autowired
    private ShortUrlService shortUrlService;

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<String> createShortUrl(@RequestParam String originalUrl,HttpServletRequest request) {
        String code = shortUrlService.createShortUrl(originalUrl);
        String shortUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +request.getContextPath()+"/v1/s/" + code;
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{code}")
    public Object redirectToOriginal(@PathVariable String code) {
        String originalUrl = shortUrlService.getOriginalUrl(code)
            .orElseThrow(() -> new RuntimeException("Short URL not found"));
        URI redirectUri = URI.create(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    @PostMapping("/review-link")
    public ResponseEntity<String> createShareableReviewLink(@RequestParam Long pgId, @RequestParam Long tenantId, HttpServletRequest request) {
        String originalUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                request.getContextPath()+"v1/tenant/reviews/" + pgId + "/" + tenantId;
        String code = shortUrlService.createShortUrl(originalUrl);
        String shortUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) + "/s/" + code;
        return ResponseEntity.ok(shortUrl);
    }

    @PostMapping("create/pg")
    public ResponseEntity<String> createPgShortUrl(@RequestParam Long pgId, HttpServletRequest request) {
        String originalUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
               request.getContextPath()+"v1/common/pg/" + pgId;
        String code = shortUrlService.createShortUrl(originalUrl);
        String shortUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) + "/v1/s/" + code;
        return ResponseEntity.ok(shortUrl);
    }

}

