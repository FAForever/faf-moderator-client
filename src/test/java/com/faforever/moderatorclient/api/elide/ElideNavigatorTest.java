package com.faforever.moderatorclient.api.elide;

import com.faforever.commons.api.dto.Ladder1v1Map;
import com.faforever.commons.api.dto.MapVersion;
import com.faforever.commons.api.elide.ElideNavigator;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ElideNavigatorTest {
    @Test
    public void testGetList() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .build(), is("/data/ladder1v1Map"));
    }

    @Test
    public void testGetId() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .id("5")
                .build(), is("/data/ladder1v1Map/5"));
    }

    @Test
    public void testGetListSingleInclude() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .addInclude("mapVersion")
                .build(), is("/data/ladder1v1Map?include=mapVersion"));
    }

    @Test
    public void testGetListMultipleInclude() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .build(), is("/data/ladder1v1Map?include=mapVersion,mapVersion.map"));
    }

    @Test
    public void testGetListFiltered() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .setFilter(
                        ElideNavigator.qBuilder()
                                .intNum("mapVersion.id").gt(10)
                                .or()
                                .string("hello").eq("nana")
                )
                .build(), is("/data/ladder1v1Map?filter=mapVersion.id=gt=\"10\",hello==\"nana\""));
    }

    @Test
    public void testGetListCombinedFilter() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .pageSize(10)
                .pageNumber(3)
                .setFilter(
                        ElideNavigator.qBuilder()
                                .intNum("mapVersion.id").gt(10)
                                .or()
                                .string("hello").eq("nana")
                )
                .build(), is("/data/ladder1v1Map?include=mapVersion,mapVersion.map&filter=mapVersion.id=gt=\"10\",hello==\"nana\"&page[size]=10&page[number]=3"));
    }

    @Test
    public void testGetIdMultipleInclude() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .id("5")
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .build(), is("/data/ladder1v1Map/5?include=mapVersion,mapVersion.map"));
    }

    @Test
    public void testGetListSorted() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .collection()
                .addSortingRule("sortCritASC", true)
                .addSortingRule("sortCritDESC", false)
                .build(), is("/data/ladder1v1Map?sort=+sortCritASC,-sortCritDESC"));
    }

    @Test
    public void testNavigateFromIdToId() {
        assertThat(ElideNavigator.of(Ladder1v1Map.class)
                .id("5")
                .navigateRelationship(MapVersion.class, "mapVersion")
                .id("1234")
                .build(), is("/data/ladder1v1Map/5/mapVersion/1234"));
    }
}
