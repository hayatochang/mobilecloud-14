package org.magnum.mobilecloud.video.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Video already liked. Cannot like it twice.")
public class VideoAlreadyLikedException extends RuntimeException{

   private static final long serialVersionUID = 1L;

   public VideoAlreadyLikedException(long videoId) {
        super("Video with id " + videoId + " already like.");
    }

}
