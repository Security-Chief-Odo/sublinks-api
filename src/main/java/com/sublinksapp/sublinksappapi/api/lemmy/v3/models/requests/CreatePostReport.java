package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import lombok.Builder;

@Builder
public record CreatePostReport(
        Integer post_id,
        String reason
) {
}