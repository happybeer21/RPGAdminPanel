package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.sql.Date;

public class PlayerSpecification {
    public static Specification<Player> findPlayerByName(final String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Player> findPlayerByTitle(final String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Player> findPlayerByRace(final Race race) {
        if(race == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("race"), race);
    }

    public static Specification<Player> findPlayerByProfession(final Profession profession) {
        if(profession == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("profession"), profession);
    }

    public static Specification<Player> findPlayerByStatus(final Boolean banned) {
        if(banned == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("banned"), banned);
    }

    public static Specification<Player> findPlayerByMaxLevel(final Long maxLevel) {
        if(maxLevel == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);
    }

    public static Specification<Player> findPlayerByMinLevel(final Long minLevel) {
        if(minLevel == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);
    }

    public static Specification<Player> findPlayerByExp(final Long minExp, final Long maxExp) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Predicate minimum;
            Predicate maximum;

            if(minExp == null && maxExp != null) {
                minimum = criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExp);
                return minimum;
            }
            if(minExp != null && maxExp == null) {
                maximum = criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExp);
                return maximum;
            }

            if(minExp != null) {
                maximum = criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExp);
                minimum = criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExp);
                return criteriaBuilder.and(maximum, minimum);
            }

            return null;
        };
    }

    public static Specification<Player> findPlayerByDate(final Long after, final Long before) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Predicate greater;
            Predicate lesser;

            if(after == null && before != null) {
                lesser = criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
                return lesser;
            }
            if(after != null && before == null) {
                greater = criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
                return greater;
            }

            if(after != null) {
                greater = criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
                lesser = criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
                return criteriaBuilder.and(greater, lesser);
            }

            return null;
        };
    }
}
