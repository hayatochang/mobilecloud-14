package org.magnum.mobilecloud.video.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLikesRepository extends CrudRepository<VideoLikes, Long> {
    public Collection<VideoLikes> findByVideoId(long videoId);
}
