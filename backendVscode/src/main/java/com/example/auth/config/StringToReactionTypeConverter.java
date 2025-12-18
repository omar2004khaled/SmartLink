package com.example.auth.config;

import com.example.auth.enums.ReactionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToReactionTypeConverter implements Converter<String, ReactionType> {
    @Override
    public ReactionType convert(String source) {
        try {
            return ReactionType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}