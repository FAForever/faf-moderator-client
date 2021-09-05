package com.faforever.moderatorclient.mapstruct;

import com.faforever.commons.api.dto.LeaderboardRatingJournal;
import com.faforever.moderatorclient.ui.domain.LeaderboardRatingJournalFX;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JavaFXMapper.class, LeaderboardMapper.class, PlayerMapper.class, CycleAvoidingMappingContext.class})
public abstract class LeaderboardRatingJournalMapper {

    public abstract LeaderboardRatingJournalFX map(LeaderboardRatingJournal dto);

    public abstract LeaderboardRatingJournal map(LeaderboardRatingJournalFX fxBean);

    public abstract List<LeaderboardRatingJournalFX> map(List<LeaderboardRatingJournal> dtoList);
}