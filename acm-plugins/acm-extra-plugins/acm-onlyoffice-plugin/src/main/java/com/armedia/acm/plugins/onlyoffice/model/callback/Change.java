package com.armedia.acm.plugins.onlyoffice.model.callback;

import com.armedia.acm.plugins.onlyoffice.model.config.User;

public class Change {
    private String created;
    private User user;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
