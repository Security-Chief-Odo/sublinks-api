package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import lombok.Builder;

@Builder
public record PurgePost(
        Integer post_id,
        String reason
) {
}