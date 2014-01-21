package com.philcollins.stickypunch.api.model;

import com.google.common.base.Optional;

public interface WebPushStoreAuth {

    public boolean authUserId(String userId, Optional<String> deviceToken);

}
