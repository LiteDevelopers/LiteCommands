package dev.rollczi.litecommands.quoted;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.argument.parser.ParseCompletedResult;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.profile.ProfiledMultipleArgumentResolver;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.argument.suggester.SuggesterRegistry;
import dev.rollczi.litecommands.input.raw.RawInput;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.range.Range;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;

public class QuotedStringArgumentResolver<SENDER> extends ProfiledMultipleArgumentResolver<SENDER, String, QuotedProfile> {

    public static final char QUOTE_ESCAPE = '\\';

    private final SuggesterRegistry<SENDER> suggesterRegistry;
    private final char quote;
    private final String quoteString;

    public QuotedStringArgumentResolver(SuggesterRegistry<SENDER> suggesterRegistry, char quote) {
        super(QuotedProfile.NAMESPACE);
        this.suggesterRegistry = suggesterRegistry;
        this.quote = quote;
        this.quoteString = String.valueOf(quote);
    }

    public QuotedStringArgumentResolver(SuggesterRegistry<SENDER> suggesterRegistry) {
        this(suggesterRegistry, '\"');
    }

    @Override
    public ParseCompletedResult<String> parse(Invocation<SENDER> invocation, Argument<String> argument, RawInput rawInput, QuotedProfile quotedProfile) {
        if (!rawInput.hasNext()) {
            return ParseResult.failure(InvalidUsage.Cause.MISSING_ARGUMENT);
        }

        String first = rawInput.seeNext();

        if (!first.startsWith(quoteString)) {
            return ParseResult.success(rawInput.next());
        }

        StringBuilder builder = new StringBuilder();
        boolean isQuoted = false;

        while (rawInput.hasNext()) {
            String text = rawInput.next();

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == quote) {
                    if (i == 0 && !isQuoted) {
                        isQuoted = true;
                        continue;
                    }

                    if (i != 0 && text.charAt(i - 1) == QUOTE_ESCAPE) {
                        builder.deleteCharAt(builder.length() - 1);
                        builder.append(quote);
                        continue;
                    }

                    if (i == text.length() - 1) {
                        return ParseResult.success(builder.toString());
                    }

                    continue;
                }

                builder.append(text.charAt(i));
            }

            builder.append(" ");
        }

        builder.deleteCharAt(builder.length() - 1);

        return ParseResult.success(builder.toString());
    }

    @Override
    public SuggestionResult suggest(Invocation<SENDER> invocation, Argument<String> argument, SuggestionContext context, QuotedProfile quotedProfile) {
        Suggestion current = context.getCurrent();
        ParseCompletedResult<String> parsedResult = parse(invocation, argument, RawInput.of(current.multilevelList()), quotedProfile);

        if (parsedResult.isFailed()) {
            return SuggestionResult.empty();
        }

        String quotedValue = parsedResult.getSuccess()
            .replace(quoteString, QUOTE_ESCAPE + quoteString);
        Suggestion quotedSuggestion = Suggestion.of(quoteString + quotedValue + quoteString);
        SuggestionResult newResult = SuggestionResult.from(quotedSuggestion, current);

        context.setConsumed(quotedSuggestion.lengthMultilevel());

        if (current.multilevel().isEmpty()) {
            newResult.add(Suggestion.of(quoteString));
        }
        else if (!current.firstLevel().startsWith(quoteString)) {
            context.setConsumed(1); // if not quoted, consume only first level
        }

        ArgumentKey currentKey = argument.getKey();

        Suggester<SENDER, String> suggester = suggesterRegistry.getSuggester(String.class, currentKey.withDefaultNamespace());
        SuggestionResult suggestionResult = suggester.suggest(invocation, argument, context);

        for (Suggestion suggestion : suggestionResult.getSuggestions()) {
            if (suggestion.equals(context.getCurrent())) {
                continue;
            }
            newResult.add(Suggestion.of(quoteString + suggestion.multilevel() + quoteString));

            if (suggestion.lengthMultilevel() == 1) {
                newResult.add(suggestion);
            }
        }

        return newResult;
    }

    @Override
    public Range getRange(Argument<String> argument, QuotedProfile quotedProfile) {
        return Range.moreThan(1);
    }

}
