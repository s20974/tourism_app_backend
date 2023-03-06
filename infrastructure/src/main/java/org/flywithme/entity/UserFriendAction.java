package org.flywithme.entity;

public enum UserFriendAction {
    IS_FRIEND("accepted"),
    IS_FRIEND_REQUEST("pending_your_approval"),
    NOT_FRIEND("not_friend"),
    IS_YOUR_REQUEST("pending_friend_approval");

    public final String label;

    private UserFriendAction(String label) {
        this.label = label;
    }

    public String getResponse() {
        return label;
    }
}
