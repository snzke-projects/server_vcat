package com.vcat.api.web.async;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

import java.util.concurrent.ExecutionException;

/**
 * Created by ylin on 2016/1/28.
 */
public class ServiceListenableFutureAdapter extends ListenableFutureAdapter {

    protected ServiceListenableFutureAdapter(ListenableFuture adaptee) {
        super(adaptee);
    }

    @Override
    protected Object adapt(Object o) throws ExecutionException {
        return null;
    }
}
