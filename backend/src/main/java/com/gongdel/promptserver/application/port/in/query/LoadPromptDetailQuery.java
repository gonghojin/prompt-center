package com.gongdel.promptserver.application.port.in.query;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.UUID;

@Getter
public class LoadPromptDetailQuery {
    private final UUID promptUuid;
    private final Long userId;

    @Builder
    public LoadPromptDetailQuery(UUID promptUuid, Long userId) {
        Assert.notNull(promptUuid, "promptUuid must not be null");
        Assert.notNull(userId, "userId must not be null");
        this.promptUuid = promptUuid;
        this.userId = userId;
    }
}
