package org.flywithme.adapters.specifications;

import lombok.experimental.UtilityClass;
import org.flywithme.data.filters.FriendsFilter;
import org.flywithme.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserSpecifications {

    public Specification<User> getUserByFilter(@NotNull FriendsFilter friendsFilter){
        return new SpecificationSupport<>(){
            List<Predicate> buildQuery(Root<User> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery) {
                List<Predicate> predicateList = new ArrayList<>();
                friendsFilter.getCountry().ifPresent(country -> {
                    predicateList.add(criteriaBuilder.equal(root.get("country"), country));
                });
                friendsFilter.getGender().ifPresent(gender -> {
                    predicateList.add(criteriaBuilder.equal(root.get("gender"), gender));
                });
                friendsFilter.getStatus().ifPresent(status->{
                    predicateList.add(criteriaBuilder.equal(root.get("status"), status));
                });
                return predicateList;
            }
        };
    }
}
