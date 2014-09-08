package org.magnum.mobilecloud.video.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Video not liked before. Cannot unlike it .")
public class VideoNotLikedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VideoNotLikedException(long videoId) {
        super("Video with id " + videoId + " cannot unlike a video which was not liked previously.");
    }
}
