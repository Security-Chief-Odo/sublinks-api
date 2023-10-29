package com.sublinks.sublinksapi.api.lemmy.v3.community.services;

import com.sublinks.sublinksapi.api.lemmy.v3.community.mappers.CommunityModeratorViewMapper;
import com.sublinks.sublinksapi.api.lemmy.v3.community.mappers.CommunityResponseMapper;
import com.sublinks.sublinksapi.api.lemmy.v3.community.mappers.LemmyCommunityMapper;
import com.sublinks.sublinksapi.api.lemmy.v3.community.models.CommunityModeratorView;
import com.sublinks.sublinksapi.api.lemmy.v3.community.models.CommunityResponse;
import com.sublinks.sublinksapi.api.lemmy.v3.community.models.CommunityView;
import com.sublinks.sublinksapi.api.lemmy.v3.enums.SubscribedType;
import com.sublinks.sublinksapi.community.Community;
import com.sublinks.sublinksapi.community.CommunityAggregates;
import com.sublinks.sublinksapi.language.Language;
import com.sublinks.sublinksapi.person.LinkPersonCommunity;
import com.sublinks.sublinksapi.person.Person;
import com.sublinks.sublinksapi.person.enums.LinkPersonCommunityType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class LemmyCommunityService {
    private final LemmyCommunityMapper lemmyCommunityMapper;
    private final CommunityModeratorViewMapper communityModeratorViewMapper;
    private final CommunityResponseMapper communityResponseMapper;

    public LemmyCommunityService(
            final LemmyCommunityMapper lemmyCommunityMapper,
            final CommunityModeratorViewMapper communityModeratorViewMapper,
            final CommunityResponseMapper communityResponseMapper
    ) {
        this.lemmyCommunityMapper = lemmyCommunityMapper;
        this.communityModeratorViewMapper = communityModeratorViewMapper;
        this.communityResponseMapper = communityResponseMapper;
    }

    public SubscribedType getPersonCommunitySubscribeType(final Person person, final Community community) {

        if (person == null || community == null) {
            return SubscribedType.NotSubscribed;
        }
        for (LinkPersonCommunity link : person.getLinkPersonCommunity()) {
            if (link.getCommunity() == community) {
                return switch (link.getLinkType()) {
                    case owner, follower, moderator -> SubscribedType.Subscribed;
                    case pending_follow -> SubscribedType.Pending;
                    default -> SubscribedType.NotSubscribed;
                };
            }
        }
        return SubscribedType.NotSubscribed;
    }

    public CommunityView communityViewFromCommunity(final Community community) {

        final CommunityAggregates communityAggregates = communityAggregates(community);
        return lemmyCommunityMapper.communityToCommunityView(
                community,
                SubscribedType.NotSubscribed,
                false,
                communityAggregates
        );
    }

    public CommunityView communityViewFromCommunity(final Community community, final Person person) {

        SubscribedType subscribedType = SubscribedType.NotSubscribed;
        boolean isBlocked = false;
        for (LinkPersonCommunity linkPersonCommunity : person.getLinkPersonCommunity()) {
            if (Objects.equals(community.getId(), linkPersonCommunity.getCommunity().getId())) {
                subscribedType = switch (linkPersonCommunity.getLinkType()) {
                    case owner, follower, moderator -> SubscribedType.Subscribed;
                    case pending_follow -> SubscribedType.Pending;
                    default -> SubscribedType.NotSubscribed;
                };
                if (linkPersonCommunity.getLinkType() == LinkPersonCommunityType.blocked) {
                    isBlocked = true;
                }
            }
        }
        final CommunityAggregates communityAggregates = communityAggregates(community);
        return lemmyCommunityMapper.communityToCommunityView(
                community,
                subscribedType,
                isBlocked,
                communityAggregates
        );
    }

    public CommunityAggregates communityAggregates(final Community community) {

        return Optional.ofNullable(community.getCommunityAggregates())
                .orElse(CommunityAggregates.builder().community(community).build());
    }

    public Set<String> communityLanguageCodes(final Community community) {

        final Set<String> languageCodes = new HashSet<>();
        for (Language language : community.getLanguages()) {
            languageCodes.add(language.getCode());
        }
        return languageCodes;
    }

    public List<CommunityModeratorView> communityModeratorViewList(final Community community) {

        final List<CommunityModeratorView> moderatorViews = new ArrayList<>();
        for (LinkPersonCommunity linkPerson : community.getLinkPersonCommunity()) {
            final CommunityModeratorView communityModeratorView = communityModeratorViewMapper.map(community, linkPerson.getPerson());
            if (linkPerson.getLinkType() == LinkPersonCommunityType.owner) {
                moderatorViews.add(0, communityModeratorView);
            }
            if (linkPerson.getLinkType() == LinkPersonCommunityType.moderator) {
                moderatorViews.add(communityModeratorView);
            }
        }
        return moderatorViews;
    }

    public CommunityResponse createCommunityResponse(final Community community, final Person person) {

        return communityResponseMapper.map(
                communityViewFromCommunity(community, person),
                communityLanguageCodes(community)
        );
    }

    public CommunityResponse createCommunityResponse(final Community community) {

        return communityResponseMapper.map(
                communityViewFromCommunity(community),
                communityLanguageCodes(community)
        );
    }
}