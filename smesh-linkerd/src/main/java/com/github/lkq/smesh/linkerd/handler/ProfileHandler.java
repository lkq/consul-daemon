package com.github.lkq.smesh.linkerd.handler;

import com.github.lkq.smesh.profile.ProfileFactory;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProfileHandler {

    private Gson gson;
    private ProfileFactory profileFactory;

    @Inject
    public ProfileHandler(Gson gson, ProfileFactory profileFactory) {
        this.gson = gson;
        this.profileFactory = profileFactory;
    }

    public String getProfile(Request request, Response response) {
        return gson.toJson(profileFactory.create());
    }
}
