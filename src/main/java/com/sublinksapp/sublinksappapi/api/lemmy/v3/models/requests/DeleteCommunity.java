package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import lombok.Builder;

@Builder
public record DeleteCommunity(
        Integer community_id,
        Boolean deleted
) {
}