package com.gluonhq.cloudlink.sample.whiteboard.base.service;

import com.gluonhq.cloudlink.sample.whiteboard.base.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WhiteboardService {

    private final CrudRepository<Item, String> repository;

    @Autowired
    public WhiteboardService(CrudRepository<Item, String> repository) {
        this.repository = repository;
    }

    public List<Item> listItems() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public Item createItem(String title) {
        Item item = new Item();
        item.setId(UUID.randomUUID().toString());
        item.setTitle(title);
        item.setCreationDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        repository.save(item);
        return item;
    }
}
