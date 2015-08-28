package org.magnum.mobilecloud.video.model;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {

    public Collection<Video> findByTitle(String title);
    public Collection<Video> findByDurationLessThan(long duration);
}
