/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.search;

import com.hp.autonomy.frontend.find.HostedFindApplication;
import com.hp.autonomy.frontend.find.core.search.AbstractRelatedConceptsServiceIT;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.search.Entity;
import com.hp.autonomy.hod.client.error.HodErrorException;
import org.springframework.boot.test.SpringApplicationConfiguration;

import java.util.Arrays;

@SpringApplicationConfiguration(classes = HostedFindApplication.class)
public class HodRelatedConceptsServiceIT extends AbstractRelatedConceptsServiceIT<Entity, ResourceIdentifier, HodErrorException> {
    public HodRelatedConceptsServiceIT() {
        super(Arrays.asList(ResourceIdentifier.WIKI_ENG, ResourceIdentifier.NEWS_ENG));
    }
}