package com.faforever.moderatorclient.api.dto;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@RestrictedVisibility("IsModerator")
@Getter
@Setter
@EqualsAndHashCode(of = "domain")
@Type("domainBlacklist")
public class DomainBlacklist {
    @Id
    String domain;
}
