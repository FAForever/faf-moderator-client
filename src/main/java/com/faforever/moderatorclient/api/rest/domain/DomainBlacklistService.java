package com.faforever.moderatorclient.api.rest.domain;

import com.faforever.commons.api.dto.DomainBlacklist;
import com.faforever.moderatorclient.api.rest.ElideRouteBuilder;
import com.faforever.moderatorclient.api.rest.FafApiCommunicationService;
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
        List<DomainBlacklist> result = fafApi.getAll(ElideRouteBuilder.of(DomainBlacklist.class));
        log.trace("found {} avatars", result.size());
        return result;
    }

    public void remove(String domain) {
        log.debug("Deleting domainBlacklist: {}", domain);
        fafApi.delete(ElideRouteBuilder.of(DomainBlacklist.class).id(domain));
    }

    public void add(DomainBlacklist domainBlacklist) {
        log.debug("Adding domainBlacklist: {}", domainBlacklist.getDomain());
        fafApi.post(ElideRouteBuilder.of(DomainBlacklist.class), domainBlacklist);
    }

}
