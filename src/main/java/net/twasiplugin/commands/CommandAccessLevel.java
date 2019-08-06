package net.twasiplugin.commands;

import net.twasi.core.database.models.TwitchAccount;
import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.twitchapi.TwitchAPI;
import net.twasi.twitchapi.helix.HelixResponseWrapper;
import net.twasi.twitchapi.helix.users.response.UserFollowDTO;
import net.twasi.twitchapi.options.TwitchRequestOptions;

import java.util.List;

public enum CommandAccessLevel {

    VIEWER(0),
    FOLLOWER(10),
    SUBSCRIBER(20),
    VIP(30),
    MODERATOR(40),
    BROADCASTER(50);

    private int level;

    private CommandAccessLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static CommandAccessLevel getAccessLevelOfUser(TwitchAccount user, User streamer) {
        // Get groups
        List<PermissionGroups> groups = user.getGroups();

        // Check Broadcaster
        if (groups.contains(PermissionGroups.BROADCASTER))
            return BROADCASTER;

        // Check Moderator
        if (groups.contains(PermissionGroups.MODERATOR))
            return MODERATOR;

        // Check VIP
        List<String> vips = TwitchAPI.tmi().chatters().getByName(streamer.getTwitchAccount().getUserName()).getChatters().getVips();
        if (vips.stream().anyMatch(vip -> vip.equalsIgnoreCase(user.getUserName())))
            return VIP;

        // Check Subscriber
        if (groups.contains(PermissionGroups.SUBSCRIBERS))
            return SUBSCRIBER;

        // Check Follower
        try {
            HelixResponseWrapper<UserFollowDTO> userFollowDTO = TwitchAPI.helix().users().getUsersFollows(
                    user.getTwitchId(),
                    streamer.getTwitchAccount().getTwitchId(),
                    new TwitchRequestOptions().withAuth(
                            streamer.getTwitchAccount().toAuthContext()
                    )
            );
            if (userFollowDTO.getTotal() > 0) return FOLLOWER;
        } catch (Exception ignored) {
        }

        return VIEWER;
    }
}
