package com.github.lokic.dracula.eventbus.interceptor.extension;

import com.github.lokic.dracula.event.Event;
import com.github.lokic.dracula.eventbus.interceptor.Interceptor;

import java.util.List;

public interface ExtensionInterceptorRegistry {

    List<Interceptor<? extends Event>> getAll();
}
