package org.magnum.mobilecloud.video.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No video found with this ID")
public class VideoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public VideoNotFoundException(long videoId) {
        super("Video with id " + videoId + " not found.");
    }
}
