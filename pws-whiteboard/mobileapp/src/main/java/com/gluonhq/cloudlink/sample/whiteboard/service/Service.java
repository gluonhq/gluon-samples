package com.gluonhq.cloudlink.sample.whiteboard.service;

import com.gluonhq.cloudlink.sample.whiteboard.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.model.Model;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.gluoncloud.GluonClient;
import com.gluonhq.connect.gluoncloud.GluonClientBuilder;
import com.gluonhq.connect.gluoncloud.GluonCredentials;
import com.gluonhq.connect.gluoncloud.OperationMode;
import com.gluonhq.connect.gluoncloud.SyncFlag;
import com.gluonhq.connect.provider.DataProvider;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class Service {

    private static final String CLOUDLINK_APPLICATION_KEY = "GLUON_APPLICATION_KEY";
    private static final String CLOUDLINK_APPLICATION_SECRET = "GLUON_APPLICATION_SECRET";

    @Inject private Model model;

    private GluonClient gluonClient;

    @PostConstruct
    public void postConstruct() {
        gluonClient = GluonClientBuilder.create()
                .credentials(new GluonCredentials(CLOUDLINK_APPLICATION_KEY, CLOUDLINK_APPLICATION_SECRET))
                .operationMode(OperationMode.CLOUD_FIRST)
                .build();
    }

    public GluonObservableList<Item> retrieveItems() {
        GluonObservableList<Item> items = DataProvider.retrieveList(gluonClient.createListDataReader("items", Item.class,
                SyncFlag.LIST_READ_THROUGH, SyncFlag.OBJECT_READ_THROUGH,
                SyncFlag.LIST_WRITE_THROUGH, SyncFlag.OBJECT_WRITE_THROUGH));
        model.setItems(items);
        return items;
    }
}
