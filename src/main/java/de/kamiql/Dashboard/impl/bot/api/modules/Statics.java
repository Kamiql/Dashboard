package de.kamiql.Dashboard.impl.bot.api.modules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Permission;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Statics {
    @JsonProperty("username")
    private String username;

    @JsonProperty("id")
    private String id;

    @JsonProperty("globalName")
    private String globalName;

    @JsonProperty("mention")
    private String mention;

    @JsonProperty("avatarUrl")
    private String avatarUrl;

    @JsonProperty("bannerUrl")
    private String bannerUrl;

    @JsonProperty("guilds")
    private List<GuildInfo> guilds;

    public Statics(GatewayDiscordClient bot) {
        User client = bot.getSelf().block();

        this.username = client.getUsername();
        this.id = client.getId().asString();
        this.globalName = client.getGlobalName().orElse(null);
        this.mention = client.getMention();
        this.avatarUrl = client.getAvatarUrl();
        this.bannerUrl = client.getBannerUrl().orElse(null);
        this.guilds = bot.getGuilds()
                .flatMap(guild -> guild.getSelfMember()
                        .map(member -> new GuildInfo(guild, member)))
                .collectList().block();
    }

    @Getter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class GuildInfo {
        @JsonProperty("guildId")
        private String guildId;

        @JsonProperty("guildName")
        private String guildName;

        @JsonProperty("selfMember")
        private SelfMember selfMember;

        @JsonProperty("members")
        private List<MemberInfo> members;

        public GuildInfo(Guild guild, Member member) {
            this.guildId = guild.getId().asString();
            this.guildName = guild.getName();
            this.selfMember = new SelfMember(member);
            this.members = guild.getMembers().map(MemberInfo::new).collect(Collectors.toList()).block();
        }

        @Getter
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public static class SelfMember {
            @JsonProperty("nickname")
            private String nickname;

            @JsonProperty("joinedAt")
            private String joinedAt;

            @JsonProperty("roles")
            private List<Role> roles;

            public SelfMember(Member member) {
                this.nickname = member.getNickname().orElse(null);
                this.joinedAt = member.getJoinTime().toString();
                this.roles = member.getRoles().map(Role::new).collect(Collectors.toList()).block();
            }
        }

        @Getter
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public static class MemberInfo {
            @JsonProperty("id")
            private String id;

            @JsonProperty("username")
            private String username;

            @JsonProperty("nickname")
            private String nickname;

            @JsonProperty("roles")
            private List<Role> roles;

            @JsonProperty("isBot")
            private boolean isBot;

            public MemberInfo(Member member) {
                this.id = member.getId().asString();
                this.username = member.getUsername();
                this.nickname = member.getNickname().orElse(null);
                this.roles = member.getRoles().map(Role::new).collect(Collectors.toList()).block();
                this.isBot = member.isBot();
            }
        }

        @Getter
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public static class Role {
            @JsonProperty("name")
            private String name;

            @JsonProperty("id")
            private String id;

            @JsonProperty("color")
            private String color;

            @JsonProperty("position")
            private int position;

            @JsonProperty("admin")
            private boolean admin;

            @JsonProperty("mentionable")
            private boolean mentionable;

            public Role(discord4j.core.object.entity.Role role) {
                this.name = role.getName();
                this.id = role.getId().asString();
                this.color = String.format("#%06X", role.getColor().getRGB() & 0xFFFFFF);
                this.position = role.getRawPosition();
                this.admin = role.getPermissions().contains(Permission.ADMINISTRATOR);
                this.mentionable = role.isMentionable();
            }
        }
    }
}
