package com.gluonhq.cloudlink.sample.whiteboard.mobile.storage;

import com.gluonhq.cloudlink.sample.whiteboard.mobile.model.Item;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"in-memory", "mysql", "postgres", "oracle"})
public interface WhiteboardRepository extends JpaRepository<Item, String> {
}
