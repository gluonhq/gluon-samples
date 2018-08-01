/**
 * Copyright (c) 2018 Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.sample.inappbilling;

import com.gluonhq.charm.down.plugins.InAppBillingService;
import com.gluonhq.charm.down.plugins.inappbilling.Product;
import com.gluonhq.charm.down.plugins.inappbilling.ProductDetails;
import com.gluonhq.charm.down.plugins.inappbilling.ProductOrder;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class BasicView extends View {

    private static final String LAYER_INVENTORY = "LayerInventory";

    private final Player player = PlayerService.getInstance().getPlayer();

    private GridPane items = new GridPane();

    public BasicView() {

        items.setHgap(5.0);
        items.setVgap(10.0);
        items.setPadding(new Insets(10));

        PlayerService.getInstance().getService().ifPresent(service -> {
            if (service.isReady()) {
                updateProductDetails(service);
            } else {
                service.readyProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            updateProductDetails(service);
                            service.readyProperty().removeListener(this);
                        }
                    }
                });
            }
        });

        Label lblHealth = new Label("", MaterialDesignIcon.HEALING.graphic());
        lblHealth.textProperty().bind(Bindings.concat("Current Health: ", player.healthProperty()));
        lblHealth.setFont(Font.font(24.0));
        lblHealth.textFillProperty().bind(Bindings.when(player.healthProperty().lessThanOrEqualTo(0)).then(Color.RED).otherwise(Color.BLUE));

        Button fight = new Button("Fight!");
        fight.setGraphic(new Icon(MaterialDesignIcon.POWER));
        fight.setOnAction(e -> player.dealDamage());
        fight.disableProperty().bind(player.healthProperty().lessThanOrEqualTo(0));

        getApplication().addLayerFactory(LAYER_INVENTORY, () -> {
            Label lblHealthPotions = new Label("", MaterialDesignIcon.HEALING.graphic());
            lblHealthPotions.textProperty().bind(Bindings.concat("Health Potions: ", player.ownedHealthPotionsProperty()));
            Button btnHealthPotions = new Button("consume");
            btnHealthPotions.disableProperty().bind(player.ownedHealthPotionsProperty().lessThanOrEqualTo(0).or(player.healthProperty().isEqualTo(Player.MAX_HEALTH)));
            btnHealthPotions.setOnAction(e -> player.consumeHealthPotion());
            HBox.setHgrow(lblHealthPotions, Priority.ALWAYS);
            HBox healthPotions = new HBox(10.0, lblHealthPotions, btnHealthPotions);
            healthPotions.setAlignment(Pos.TOP_RIGHT);

            Label lblWoodenShield = new Label("Wooden Shield", MaterialDesignIcon.LOCAL_BAR.graphic());
            lblWoodenShield.textFillProperty().bind(Bindings.when(player.woodenShieldOwnedProperty()).then(Color.BLUEVIOLET).otherwise(Color.LIGHTGRAY));
            HBox woodenShield = new HBox(10.0, lblWoodenShield);
            woodenShield.setAlignment(Pos.CENTER_LEFT);

            VBox inventory = new VBox(15.0, healthPotions, woodenShield);
            inventory.setAlignment(Pos.CENTER_LEFT);

            return new SidePopupView(inventory, Side.RIGHT, true);
        });

        setOnShowing(e -> {
            Button btnInventory = MaterialDesignIcon.SHOPPING_BASKET.button(e2 -> {
                getApplication().showLayer(LAYER_INVENTORY);
            });
            getApplication().getAppBar().getActionItems().add(btnInventory);
        });

        VBox controls = new VBox(15.0, lblHealth, fight);
        controls.setAlignment(Pos.TOP_CENTER);
        controls.setPadding(new Insets(15.0, 5.0, 0.0, 5.0));

        setCenter(controls);
        setBottom(items);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Fight the World!");
    }

    private void updateProductDetails(InAppBillingService service) {
        setBottom(new HBox(10.0, new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS), new Label("Loading Products")));

        Worker<List<Product>> productDetails = service.fetchProductDetails();
        productDetails.stateProperty().addListener((obs, ov, nv) -> {
            switch (nv) {
                case CANCELLED:
                    setBottom(new HBox(10.0, new Label("Loading products cancelled."), MaterialDesignIcon.REFRESH.button(e -> updateProductDetails(PlayerService.getInstance().getService().get()))));
                    break;
                case SUCCEEDED:
                    createProductGrid(productDetails.getValue());
                    break;
                case FAILED:
                    setBottom(new HBox(10.0, new Label("Failed to load products."), MaterialDesignIcon.REFRESH.button(e -> updateProductDetails(PlayerService.getInstance().getService().get()))));
                    break;
            }
        });
    }

    private void createProductGrid(List<Product> products) {
        items.getChildren().clear();

        int row = 0;
        for (Product product : products) {
            final ProductDetails details = product.getDetails();
            Label price = new Label();
            if (details.getCurrency() != null && details.getPrice() != null) {
                price.setText(details.getCurrency() + details.getPrice());
            }
            price.setStyle("-fx-text-fill: -primary-swatch-600;");
            Label title = new Label(details.getTitle());
            Button buy = new Button("", MaterialDesignIcon.ATTACH_MONEY.graphic());
            GlistenStyleClasses.applyStyleClass(buy, GlistenStyleClasses.BUTTON_ROUND);
            buy.setOnAction(e -> buyProduct(product));
            items.addRow(row++, price, title, buy);

            if (product.equals(InAppProduct.WOODEN_SHIELD.getProduct()) && product.getDetails().getState() == ProductDetails.State.APPROVED) {
                buy.setDisable(true);
            }
        }

        setBottom(items);
    }

    private void buyProduct(Product product) {
        PlayerService.getInstance().getService().ifPresent(service -> {
            Worker<ProductOrder> order = service.order(product);
            order.stateProperty().addListener((obs, ov, nv) -> {
                if (nv == Worker.State.SUCCEEDED) {
                    if (order.getValue() != null) {
                        if (order.getValue().getProduct().getType() == Product.Type.CONSUMABLE) {
                            Worker<Product> finish = service.finish(order.getValue());
                            finish.stateProperty().addListener((obs2, ov2, nv2) -> {
                                if (nv2 == Worker.State.SUCCEEDED) {
                                    Product finishedProduct = finish.getValue();
                                    if (finishedProduct.equals(InAppProduct.HEALTH_POTION.getProduct())) {
                                        player.boughtHealthPotion();
                                    }
                                } else if (nv2 == Worker.State.FAILED) {
                                    System.out.println("Finish Failed!");
                                    finish.getException().printStackTrace();
                                }
                            });
                        } else if (order.getValue().getProduct().getType() == Product.Type.NON_CONSUMABLE) {
                            new Toast("Thank you! You now own:\n" + order.getValue().getProduct().getDetails().getTitle(), Duration.seconds(5))
                                    .show();
                            if (order.getValue().getProduct().equals(InAppProduct.WOODEN_SHIELD.getProduct())) {
                                player.setWoodenShieldOwned(true);
                            }
                        }
                    } else {
                        System.out.println("Failed for some unknown reason.");
                    }
                } else if (nv == Worker.State.FAILED) {
                    System.out.println("Order Failed!");
                    order.getException().printStackTrace();
                }
            });
        });
    }
    
}
