package com.faforever.moderatorclient.api.rest;

import com.github.jasminb.jsonapi.annotations.Type;
import com.github.rutledgepaulv.qbuilders.builders.QBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.RSQLVisitor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
public class ElideRouteBuilder<T> {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Class<T> dtoClass;
    private String id = null;
    private Condition<?> filterCondition;
    private List<String> includes = new ArrayList<>();
    private List<String> sorts = new ArrayList<>();
    private Integer pageSize = null;
    private Integer pageNumber = null;

    private ElideRouteBuilder() {
    }

    public static <T> ElideRouteBuilder<T> of(Class<T> clazz) {
        ElideRouteBuilder<T> b = new ElideRouteBuilder<>();
        b.setDtoClass(clazz);
        return b;
    }

    public static <T extends QBuilder<T>> QBuilder<T> qBuilder() {
        return new QBuilder<>();
    }

    public ElideRouteBuilder<T> addInclude(String include) {
        log.trace("include added: {}", include);
        includes.add(include);
        return this;
    }

    public ElideRouteBuilder<T> id(String id) {
        Assert.state(filterCondition == null, "lookup of id and filter cannot be combined");
        Assert.state(pageSize == null, "lookup of id and pageSize cannot be combined");
        Assert.state(pageNumber == null, "lookup of id and pageNumber cannot be combined");
        log.trace("id added: {}", id);
        this.id = id;
        return this;
    }

    public ElideRouteBuilder<T> filter(Condition<?> eq) {
        Assert.state(id == null, "lookup of id and filter cannot be combined");
        log.trace("filter set: {}", eq.toString());
        filterCondition = eq;
        return this;
    }

    public ElideRouteBuilder<T> sort(String field, boolean ascending) {
        log.trace("{} sort added: {}", ascending ? "ascending" : "descending", field);
        sorts.add(ascending ? "+" : "-" + field);
        return this;
    }

    public ElideRouteBuilder<T> pageSize(int size) {
        Assert.state(id == null, "lookup of id and filter cannot be combined");
        log.trace("page size set: {}", size);
        pageSize = size;
        return this;
    }

    public ElideRouteBuilder<T> pageNumber(int number) {
        Assert.state(id == null, "lookup of id and filter cannot be combined");
        log.trace("page number set: {}", number);
        pageNumber = number;
        return this;
    }

    public String build() {
        String dtoPath = dtoClass.getDeclaredAnnotation(Type.class).value();

        StringJoiner queryArgs = new StringJoiner("&", "?", "")
                .setEmptyValue("");

        if (includes.size() > 0) {
            queryArgs.add(String.format("include=%s", includes.stream().collect(Collectors.joining(","))));
        }

        if (filterCondition != null) {
            queryArgs.add(String.format("filter=%s", filterCondition.query(new RSQLVisitor())));
        }

        if (sorts.size() > 0) {
            queryArgs.add(String.format("sort=%s", sorts.stream().collect(Collectors.joining(","))));

        }

        if (pageSize != null) {
            queryArgs.add(String.format("page[size]=%s", pageSize));
        }

        if (pageNumber != null) {
            queryArgs.add(String.format("page[number]=%s", pageNumber));
        }

        String route = "/data/" +
                dtoPath +
                (id == null ? "" : "/" + id) +
                queryArgs.toString();
        log.debug("Route built: {}", route);
        return route;
    }
}
