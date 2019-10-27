package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.DomainBlacklist;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DomainBlacklistService {
    private final FafApiCommunicationService fafApi;

    public DomainBlacklistService(FafApiCommunicationService fafApi) {
        this.fafApi = fafApi;
    }

    public List<DomainBlacklist> getAll() {
        log.debug("Retrieving all domainBlacklists");
        List<DomainBlacklist> result = fafApi.getAll(DomainBlacklist.class, ElideNavigator.of(DomainBlacklist.class).collection());
        log.trace("found {} avatars", result.size());
        return result;
    }

    public void remove(String domain) {
        log.debug("Deleting domainBlacklist: {}", domain);
        fafApi.delete(ElideNavigator.of(DomainBlacklist.class).id(domain));
    }

    public void add(DomainBlacklist domainBlacklist) {
        log.debug("Adding domainBlacklist: {}", domainBlacklist.getDomain());
        fafApi.post(ElideNavigator.of(DomainBlacklist.class).collection(), domainBlacklist);
    }

}
