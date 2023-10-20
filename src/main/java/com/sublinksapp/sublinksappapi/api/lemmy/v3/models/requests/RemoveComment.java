package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import lombok.Builder;

@Builder
public record RemoveComment(
        Integer comment_id,
        Boolean removed,
        String reason
) {
}