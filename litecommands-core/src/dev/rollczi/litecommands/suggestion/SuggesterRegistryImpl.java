package dev.rollczi.litecommands.suggestion;

import dev.rollczi.litecommands.argument.ArgumentKey;

import java.util.HashMap;
import java.util.Map;

public class SuggesterRegistryImpl<SENDER> implements SuggesterRegistry<SENDER> {

    private final Suggester<SENDER, ?> noneSuggester = new SuggesterNoneImpl<>();

    private final Map<Class<?>, BucketByArgument<?>> buckets = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> void registerSuggester(Class<T> type, ArgumentKey key, Suggester<SENDER, T> suggester) {
        BucketByArgument<T> bucket = (BucketByArgument<T>) buckets.computeIfAbsent(type, k -> new BucketByArgument<>());

        bucket.registerParser(key, suggester);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <PARSED> Suggester<SENDER, PARSED> getSuggester(Class<PARSED> parsedClass, ArgumentKey key) {
        BucketByArgument<PARSED> bucket = (BucketByArgument<PARSED>) buckets.get(parsedClass);

        if (bucket == null) {
            return (Suggester<SENDER, PARSED>) noneSuggester;
        }

        Suggester<SENDER, PARSED> suggester = bucket.getSuggester(key);

        if (suggester == null) {
            return (Suggester<SENDER, PARSED>) noneSuggester;
        }

        return suggester;
    }

    private class BucketByArgument<PARSED> {

        private Suggester<SENDER, PARSED> universalBucket;
        private final Map<ArgumentKey, Suggester<SENDER, PARSED>> buckets = new HashMap<>();

        private BucketByArgument() {
        }

        void registerParser(ArgumentKey key, Suggester<SENDER, PARSED> parser) {
            if (key.isUniversal()) {
                this.universalBucket = parser;
                return;
            }

            buckets.put(key, parser);
        }

        Suggester<SENDER, PARSED> getSuggester(ArgumentKey key) {
            Suggester<SENDER, PARSED> suggester = buckets.get(key);

            if (suggester != null) {
                return suggester;
            }

            return universalBucket;
        }

    }

}
