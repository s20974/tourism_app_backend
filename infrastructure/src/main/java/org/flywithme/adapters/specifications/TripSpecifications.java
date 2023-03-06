package org.flywithme.adapters.specifications;

import lombok.experimental.UtilityClass;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.entity.Trip;
import org.flywithme.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class TripSpecifications {

    public Specification<Trip> getTripByFilter(String country, Optional<String> from, Optional<String> to, Optional<Integer> max){
        return new SpecificationSupport<>(){
            List<Predicate> buildQuery(Root<Trip> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(criteriaBuilder.equal(root.get("country"), country));
                from.ifPresent(gender -> {
                    predicateList.add(criteriaBuilder.equal(root.get("dateFrom"), gender));
                });
                to.ifPresent(status->{
                    predicateList.add(criteriaBuilder.equal(root.get("dateTo"), status));
                });
                max.ifPresent(status->{
                    predicateList.add(criteriaBuilder.equal(root.get("maxPeople"), status));
                });
                return predicateList;
            }
        };
    }
}
