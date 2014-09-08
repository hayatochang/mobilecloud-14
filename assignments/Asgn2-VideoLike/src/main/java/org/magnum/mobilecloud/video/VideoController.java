/*
 *
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.exception.VideoAlreadyLikedException;
import org.magnum.mobilecloud.video.exception.VideoNotFoundException;
import org.magnum.mobilecloud.video.exception.VideoNotLikedException;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoLikes;
import org.magnum.mobilecloud.video.repository.VideoLikesRepository;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoController {

    // public static final String TITLE_PARAMETER = "title";
    // public static final String DURATION_PARAMETER = "duration";
    // public static final String TOKEN_PATH = "/oauth/token";

    // The path where we expect the VideoSvc to live
    public static final String VIDEO_SVC_PATH = "/video";

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoLikesRepository videoLikesRepository;

    // The path to get video by id
    public static final String VIDEO_BY_ID_PATH=VIDEO_SVC_PATH + "/{id}";
    @RequestMapping(value=VIDEO_BY_ID_PATH, method=RequestMethod.GET)
    public @ResponseBody Video getVideoById(@PathVariable("id") long id) {
        Video video =videoRepository.findOne(id);
        if(video==null) {
            throw new VideoNotFoundException(id);
        }
        video.setLikes(videoLikesRepository.findByVideoId(video.getId()).size());
        return video;
    }

    @RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Video> getVideoList() {
        return Lists.newArrayList(videoRepository.findAll());
    }

    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
    public @ResponseBody Video addVideo(@RequestBody Video v) {
        videoRepository.save(v);
        return v;
    }

    //Like a particular video
    public static final String LIKE_VIDEO_PATH = VIDEO_SVC_PATH + "/{id}/like";
    @RequestMapping(value=LIKE_VIDEO_PATH, method=RequestMethod.POST)
    public @ResponseBody void likeVideo(@PathVariable("id") long id, Principal principal) {
        Video video = videoRepository.findOne(id);
        if(video==null) {
            throw new VideoNotFoundException(id);
        }
        Collection<VideoLikes> videoLikes=videoLikesRepository.findByVideoId(video.getId());
        for(VideoLikes likes: videoLikes) {
            if(principal.getName().equalsIgnoreCase(likes.getLikeUserName())) {
                throw new VideoAlreadyLikedException(id);
            }
        }
        VideoLikes likes = new VideoLikes();
        likes.setLikeUserName(principal.getName());
        likes.setVideoId(video.getId());
        videoLikesRepository.save(likes);
    }

    //UnLike a particular video
    public static final String UN_LIKE_VIDEO_PATH = VIDEO_SVC_PATH + "/{id}/unlike";
    @RequestMapping(value=UN_LIKE_VIDEO_PATH, method=RequestMethod.POST)
    public @ResponseBody void unLikeVideo(@PathVariable("id") long id, Principal principal) {
        boolean videoLikedByTheUserBefore=false;
        VideoLikes videoLikeToBeUnliked=null;
        Video video = videoRepository.findOne(id);
        if(video==null) {
            throw new VideoNotFoundException(id);
        }
        Collection<VideoLikes> videoLikes=videoLikesRepository.findByVideoId(video.getId());
        for(VideoLikes likes: videoLikes) {
            if(principal.getName().equalsIgnoreCase(likes.getLikeUserName())) {
                videoLikeToBeUnliked=likes;
                videoLikedByTheUserBefore=true;
                break;
            }
        }
        if(!videoLikedByTheUserBefore) {
            throw new VideoNotLikedException(id);
        }
        videoLikesRepository.delete(videoLikeToBeUnliked);
    }

    //Liked by returns all users who liked this video
    public static final String LIKED_BY_VIDEO_PATH = VIDEO_SVC_PATH + "/{id}/likedby";
    @RequestMapping(value=LIKED_BY_VIDEO_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<String> getVideoLikedByList(@PathVariable("id") long id, Principal principal) {
        Video video = videoRepository.findOne(id);
        if(video==null) {
            throw new VideoNotFoundException(id);
        }
        Collection<String> likedByUsers= new ArrayList<String>();
        for(VideoLikes likes : videoLikesRepository.findByVideoId(video.getId())) {
            likedByUsers.add(likes.getLikeUserName());
        }
        return likedByUsers;
    }

    // The path to search videos by title
    public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";
    @RequestMapping(value=VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Video> findVideoByName(@RequestParam("title") String videoName) {
        Collection<Video> videos = videoRepository.findByName(videoName);
        if(videos==null || videos.isEmpty()) {
            return Lists.newArrayList();
        }
        return videos;
    }


    // The path to search videos by title
    public static final String VIDEO_DURATION_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByDurationLessThan";
    @RequestMapping(value=VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Video> findVideoByDurationLessThan(@Param("duration") long duration) {
        Collection<Video> videos = videoRepository.findByDurationLessThan(duration);
        if(videos==null || videos.isEmpty()) {
            return Lists.newArrayList();
        }
        return videos;
    }
}
