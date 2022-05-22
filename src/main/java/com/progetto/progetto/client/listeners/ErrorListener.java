package com.progetto.progetto.client.listeners;

@FunctionalInterface
public interface ErrorListener {

    void onError(Exception e);
}
