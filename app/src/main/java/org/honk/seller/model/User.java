package org.honk.seller.model;

public class User {

    // Id and authentication type are the primary key of this entity.
    public String id;
    public AuthenticationType authenticationType;
    public String displayName;

    public User(String id, AuthenticationType authenticationType, String displayName) {
        this.id = id;
        this.authenticationType = authenticationType;
        this.displayName = displayName;
    }
}
