package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import com.sublinksapp.sublinksappapi.api.lemmy.v3.enums.SearchType;
import lombok.Builder;

@Builder
public record Search(
        String q,
        Integer community_id,
        String community_name,
        Integer creator_id,
        SearchType type_,
        String sort,
        String listing_type,
        String page,
        String limit
) {
}