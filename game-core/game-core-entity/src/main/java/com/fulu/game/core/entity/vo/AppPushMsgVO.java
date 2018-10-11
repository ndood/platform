package com.fulu.game.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class AppPushMsgVO {

    private String title;

    private String alert;

    private Map<String, String> extras;

    private Integer[] userIds;

    private AppPushMsgVO(Builder builder) {
        setTitle(builder.title);
        setAlert(builder.alert);
        setExtras(builder.extras);
        setUserIds(builder.userIds);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Integer userId) {
        return new Builder(userId);
    }

    public static Builder newBuilder(Integer[] userIds) {
        return new Builder(userIds);
    }


    public static final class Builder {
        private String title;
        private String alert;
        private Map<String, String> extras;
        private Integer[] userIds;

        private Builder() {
            this.userIds = new Integer[]{};
        }

        private Builder(Integer userId) {
            this.userIds = new Integer[]{userId};
        }

        private Builder(Integer[] userIds) {
            this.userIds = userIds;
        }


        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder alert(String val) {
            alert = val;
            return this;
        }

        public Builder extras(Map<String, String> val) {
            extras = val;
            return this;
        }


        public AppPushMsgVO build() {
            if (extras == null) {
                extras = new HashMap<>();
            }
            return new AppPushMsgVO(this);
        }
    }
}
