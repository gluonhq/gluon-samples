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

import com.gluonhq.charm.down.Platform;
import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.InAppBillingService;
import com.gluonhq.charm.down.plugins.inappbilling.InAppBillingQueryResult;
import com.gluonhq.charm.down.plugins.inappbilling.InAppBillingQueryResultListener;
import com.gluonhq.charm.down.plugins.inappbilling.Product;
import com.gluonhq.charm.down.plugins.inappbilling.ProductDetails;
import com.gluonhq.charm.down.plugins.inappbilling.ProductOrder;
import javafx.concurrent.Worker;

import java.util.Optional;

public class PlayerService implements InAppBillingQueryResultListener {

    private static final String BASE_64_ANDROID_PUBLIC_KEY = "*** ADD YOUR BASE 64 ENCODED RSA PUBLIC KEY ***";

    private static final PlayerService INSTANCE = new PlayerService();
    public static PlayerService getInstance() {
        return INSTANCE;
    }

    public Optional<InAppBillingService> getService() {
        return Optional.ofNullable(service);
    }

    private final Player player = new Player();

    private final InAppBillingService service;

    private PlayerService() {
        Optional<InAppBillingService> service = Services.get(InAppBillingService.class);
        if (service.isPresent()) {
            this.service = service.get();
            this.service.setQueryResultListener(this);
            this.service.initialize(BASE_64_ANDROID_PUBLIC_KEY, InAppProduct.getRegisteredProducts());
        } else if (! Platform.isDesktop()) {
            throw new RuntimeException("Charm Down In-App Billing service not discovered.");
        } else {
            this.service = null;
        }
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void onQueryResultReceived(InAppBillingQueryResult result) {
        for (ProductOrder productOrder : result.getProductOrders()) {
            if (productOrder.getProduct().getType() == Product.Type.CONSUMABLE &&
                    productOrder.getProduct().getDetails().getState() == ProductDetails.State.APPROVED) {
                Worker<Product> finish = service.finish(productOrder);
                finish.stateProperty().addListener((obs, ov, nv) -> {
                    if (nv == Worker.State.SUCCEEDED) {
                        if (productOrder.getProduct().equals(InAppProduct.HEALTH_POTION.getProduct())) {
                            player.boughtHealthPotion();
                        }
                    } else if (nv == Worker.State.FAILED) {
                        finish.getException().printStackTrace();
                    }
                });
            } else if (productOrder.getProduct().equals(InAppProduct.WOODEN_SHIELD.getProduct())) {
                player.setWoodenShieldOwned(true);
            }
        }
    }
}
