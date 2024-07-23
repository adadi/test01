package com.cifa.internal.service.datatable.common.helper;

import com.cifa.internal.service.datatable.common.dto.FilterDto;
import com.cifa.internal.service.datatable.common.dto.OperatorType;
import com.cifa.internal.service.datatable.common.dto.WhereDto;
import com.cifa.internal.service.datatable.common.dto.table.Datatable;
import com.cifa.internal.service.datatable.common.dto.table.LinkDto;
import com.cifa.internal.service.datatable.common.dto.table.PagiantionDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatatableHelper {

    public static <T> Datatable<T> search(Class<T> entity, FilterDto filter, EntityManager entityManager) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(
                entity);

        Root<T> from = criteriaQuery.from(entity);
        criteriaQuery.select(from);

        long total = 0;
        CriteriaQuery<Long> criteriaQueryCount = criteriaBuilder.createQuery(
                Long.class);
        Root<T> fromCount = criteriaQueryCount.from(entity);
        criteriaQueryCount.select(criteriaBuilder.count(fromCount));
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicatesCount = new ArrayList<>();
        List<Predicate> expressions = new ArrayList<>();
        List<Predicate> expressionsCount = new ArrayList<>();

        //filter where
        if (!filter.getWheres().isEmpty()) {
            for (WhereDto whereDto : filter.getWheres()
            ) {
                if (OperatorType.EQUAL.getValue().equals(whereDto.getOperator().getValue())) {
                    predicatesCount.add(
                            criteriaBuilder.equal(fromCount.get(whereDto.getName()), whereDto.getValue()));
                    predicates.add(
                            criteriaBuilder.equal(from.get(whereDto.getName()), whereDto.getValue())
                    );
                }
            }
        }
        expressions.add(criteriaBuilder.and(listPredicateToArray(predicates)));
        expressionsCount.add(criteriaBuilder.and(listPredicateToArray(predicatesCount)));
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {

            expressions.add(where(
                    criteriaBuilder,
                    from,
                    filter.getSearchFields(),
                    filter.getSearch().toLowerCase(Locale.ROOT)
            ));
            expressionsCount.add(where(
                    criteriaBuilder,
                    fromCount,
                    filter.getSearchFields(),
                    filter.getSearch().toLowerCase(Locale.ROOT)
            ));
        }
        criteriaQueryCount.where(
                expressionsCount.toArray(new Predicate[expressionsCount.size()])
        );
        criteriaQuery.where(
                expressions.toArray(new Predicate[expressions.size()])
        );
        total = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        if (filter.getSort() != null && !filter.getSort().isEmpty()) {

            if ("asc".equals(filter.getOrder())) {
                criteriaQuery.orderBy(criteriaBuilder.asc(from.get(filter.getSort())));
            }
            if ("desc".equals(filter.getOrder())) {
                criteriaQuery.orderBy(criteriaBuilder.desc(from.get(filter.getSort())));
            }
        }

        Query query = entityManager.createQuery(criteriaQuery);

        query.setFirstResult((filter.getPage() - 1) * filter.getItemsPerPage()) // offset
                .setMaxResults(filter.getItemsPerPage());

        List<T> data = query.getResultList();


        Datatable<T> shopDatatable = new Datatable<>();
        shopDatatable.setData(data);
        shopDatatable.setPagiantionDto(
                generate(
                        filter.getPage()
                        , filter.getItemsPerPage()
                        , (int) total
                )
        );
        return shopDatatable;
    }

    public static <T> Predicate where(CriteriaBuilder criteriaBuilder, Root<T> from, List<String> searchFields, String search) {
        List<Predicate> predicates = new ArrayList<>();
        for (String searchField : searchFields
        ) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(from.get(searchField).as(String.class)),
                    "%" + search + "%"));
        }
        return criteriaBuilder.or(listPredicateToArray(predicates));
    }


    public static PagiantionDto generate(int page, int size, int total) {
        PagiantionDto pagiantionDto = new PagiantionDto();
        pagiantionDto.setPage(page);
        pagiantionDto.setTotal(total);
        pagiantionDto.setFrom((page - 1) * size + 1);
        pagiantionDto.setTo(page * size);
        pagiantionDto.setTotal(total);
        pagiantionDto.setItemsPerPage("" + size);
        int countPage = page != 0 ? (int) Math.ceil(((double) total / (double) size)) : 0;

        LinkDto linkDto = new LinkDto();
        linkDto.setLabel("<<");
        if (page > 1) {
            linkDto.setUrl("/?page=1");
            linkDto.setPage(1);
            linkDto.setActive(true);
            pagiantionDto.setPrevPageUrl(linkDto.getUrl());
        } else {
            linkDto.setActive(false);
        }

        pagiantionDto.addLink(linkDto);

        linkDto = new LinkDto();
        linkDto.setLabel("<");
        if (page > 1) {
            linkDto.setUrl("/?page=" + (page - 1));
            linkDto.setPage(page - 1);
            linkDto.setActive(true);
            pagiantionDto.setPrevPageUrl(linkDto.getUrl());
        } else {
            linkDto.setActive(false);
        }

        pagiantionDto.addLink(linkDto);

        int start = 1;
        int end = countPage;
        if (countPage > 6) {
            if (page < 4) {
                end = 6;
            } else if (page >= countPage - 2) {
                start = countPage - 5;
            } else {
                start = page - 3;
                end = page + 2;
            }
        }
        for (int i = start; i <= end; ++i) {
            linkDto = new LinkDto();
            linkDto.setUrl("/?page=" + i);
            linkDto.setPage(i);
            linkDto.setLabel("" + i);
            linkDto.setActive(true);
            pagiantionDto.addLink(linkDto);
        }

        linkDto = new LinkDto();
        linkDto.setLabel(">");
        if (page < countPage) {
            linkDto.setUrl("/?page=" + (page + 1));
            linkDto.setPage(page + 1);
            linkDto.setActive(true);
            pagiantionDto.setNextPageUrl(linkDto.getUrl());
        } else {
            linkDto.setActive(false);
        }
        pagiantionDto.addLink(linkDto);

        linkDto = new LinkDto();
        linkDto.setLabel(">>");
        if (page < countPage) {
            linkDto.setUrl("/?page=" + countPage);
            linkDto.setPage(countPage);
            linkDto.setActive(true);
            pagiantionDto.setNextPageUrl(linkDto.getUrl());
        } else {
            linkDto.setActive(false);
        }
        pagiantionDto.addLink(linkDto);

        pagiantionDto.setFirstPageUrl("/?page=1");
        pagiantionDto.setLastPage(countPage);
        return pagiantionDto;
    }

    public static Predicate[] listPredicateToArray(List<Predicate> predicates) {
        return predicates.toArray(new Predicate[predicates.size()]);

    }
}