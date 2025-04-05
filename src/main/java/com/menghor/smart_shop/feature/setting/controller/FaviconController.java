package com.menghor.smart_shop.feature.setting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {

    /**
     * Handle favicon.ico requests by returning an empty response with 204 No Content status.
     * This will stop browsers from repeatedly trying to load the favicon.
     */
    @GetMapping("favicon.ico")
    @ResponseBody
    public ResponseEntity<Void> getFavicon() {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header("Content-Type", "image/x-icon")
                .build();
    }
}