package com.sublinks.sublinksapi.person.event;

import com.sublinks.sublinksapi.instance.InstanceAggregate;
import com.sublinks.sublinksapi.instance.InstanceAggregateRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PersonCreatedListener implements ApplicationListener<PersonCreatedEvent> {
    private final InstanceAggregateRepository instanceAggregateRepository;

    public PersonCreatedListener(final InstanceAggregateRepository instanceAggregateRepository) {
        this.instanceAggregateRepository = instanceAggregateRepository;
    }

    @Override
    public void onApplicationEvent(final PersonCreatedEvent event) {

        if (!event.getPerson().isLocal()) {
            return;
        }
        final InstanceAggregate instanceAggregate = event.getPerson().getInstance().getInstanceAggregate();
        instanceAggregate.setUserCount(instanceAggregate.getUserCount() + 1);
        instanceAggregateRepository.save(instanceAggregate);
    }
}