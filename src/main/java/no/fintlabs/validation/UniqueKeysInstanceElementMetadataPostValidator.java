package no.fintlabs.validation;

import no.fintlabs.model.web.InstanceElementMetadataPost;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.fintlabs.validation.UniqueKeys.DUPLICATE_KEYS;

public class UniqueKeysInstanceElementMetadataPostValidator implements HibernateConstraintValidator<UniqueKeys, List<InstanceElementMetadataPost>> {

    @Override
    public boolean isValid(List<InstanceElementMetadataPost> value, HibernateConstraintValidatorContext hibernateConstraintValidatorContext) {
        List<InstanceElementMetadataPost> instanceElementMetadataPosts = getElements(value);
        List<String> duplicateKeys = findDuplicateKeys(instanceElementMetadataPosts);
        if (duplicateKeys.isEmpty()) {
            return true;
        }
        hibernateConstraintValidatorContext.addMessageParameter(DUPLICATE_KEYS, duplicateKeys);
        return false;
    }

    private List<InstanceElementMetadataPost> getElements(List<InstanceElementMetadataPost> instanceElementMetadataPosts) {
        return Optional.ofNullable(instanceElementMetadataPosts)
                .orElse(emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(this::getElements)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<InstanceElementMetadataPost> getElements(InstanceElementMetadataPost instanceElementMetadataPost) {
        return Stream.concat(
                        Stream.of(instanceElementMetadataPost),
                        Optional.ofNullable(instanceElementMetadataPost.getChildren())
                                .orElse(emptyList())
                                .stream()
                                .filter(Objects::nonNull)
                                .map(this::getElements)
                                .flatMap(Collection::stream)
                )
                .collect(Collectors.toList());
    }

    private List<String> findDuplicateKeys(List<InstanceElementMetadataPost> instanceElementMetadataPosts) {
        Set<String> observedKeys = new HashSet<>();
        return instanceElementMetadataPosts.stream()
                .map(InstanceElementMetadataPost::getKey)
                .filter(Objects::nonNull)
                .filter(n -> !observedKeys.add(n))
                .collect(Collectors.toList());
    }

}
