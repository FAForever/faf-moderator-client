package com.faforever.moderatorclient.api;

import com.faforever.moderatorclient.api.dto.Ladder1v1Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ElideRouteBuilderTest {
    @Test
    public void testGetList() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .build(), is("/data/ladder1v1Map"));
    }

    @Test
    public void testGetId() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .id("5")
                .build(), is("/data/ladder1v1Map/5"));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetIdWithFilter() {
        ElideRouteBuilder.of(Ladder1v1Map.class)
                .id("5")
                .filter(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetIdWithPageSize() {
        ElideRouteBuilder.of(Ladder1v1Map.class)
                .id("5")
                .pageSize(5)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetIdWithPageNumber() {
        ElideRouteBuilder.of(Ladder1v1Map.class)
                .id("5")
                .pageNumber(5);
    }

    @Test
    public void testGetListSingleInclude() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .addInclude("mapVersion")
                .build(), is("/data/ladder1v1Map?include=mapVersion"));
    }

    @Test
    public void testGetListMultipleInclude() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .build(), is("/data/ladder1v1Map?include=mapVersion,mapVersion.map"));
    }

    @Test
    public void testGetListFiltered() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .filter(
                        ElideRouteBuilder.qBuilder()
                                .intNum("mapVersion.id").gt(10)
                                .or()
                                .string("hello").eq("nana")
                )
                .build(), is("/data/ladder1v1Map?filter=mapVersion.id=gt=\"10\",hello==\"nana\""));
    }

    @Test
    public void testGetListCombinedFilter() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .pageSize(10)
                .pageNumber(3)
                .filter(
                        ElideRouteBuilder.qBuilder()
                                .intNum("mapVersion.id").gt(10)
                                .or()
                                .string("hello").eq("nana")
                )
                .build(), is("/data/ladder1v1Map?include=mapVersion,mapVersion.map&filter=mapVersion.id=gt=\"10\",hello==\"nana\"&page[size]=10&page[number]=3"));
    }

    @Test
    public void testGetListCombinedId() {
        assertThat(ElideRouteBuilder.of(Ladder1v1Map.class)
                .addInclude("mapVersion")
                .addInclude("mapVersion.map")
                .id("5")
                .build(), is("/data/ladder1v1Map/5?include=mapVersion,mapVersion.map"));
    }

}
