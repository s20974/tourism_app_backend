package org.flywithme.adapters.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

abstract class SpecificationSupport<T> implements Specification<T> {
    abstract List<Predicate> buildQuery(Root<T> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery);

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicates = buildQuery(root, criteriaBuilder, query);
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    @Override
    public Specification<T> and(Specification<T> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<T> or(Specification<T> other) {
        return Specification.super.or(other);
    }
}
