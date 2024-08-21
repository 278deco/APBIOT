package apbiot.core.specs;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.immutables.value.Value;

import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.Spec;
import discord4j.discordjson.json.MessageEditRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.AllowedMentions;
import discord4j.rest.util.MultipartRequest;

@Value.Immutable(singleton = true)
public interface MessageEditSpecGenerator extends Spec<MultipartRequest<MessageEditRequest>> {

	Possible<Optional<String>> content();

    Possible<Optional<List<EmbedCreateSpec>>> embeds();

    @Value.Default
    default List<MessageCreateFields.File> files() {
        return Collections.emptyList();
    }
    
    @Value.Default
    default List<MessageCreateFields.FileSpoiler> fileSpoilers() {
        return Collections.emptyList();
    }

    Possible<Optional<AllowedMentions>> allowedMentions();

    Possible<Optional<List<Message.Flag>>> flags();

    Possible<Optional<List<LayoutComponent>>> components();

    Possible<Optional<List<Attachment>>> attachments();

    @Override
    default MultipartRequest<MessageEditRequest> asRequest() {
        MessageEditRequest json = MessageEditRequest.builder()
                .content(content())
                .embeds(mapPossibleOptional(embeds(), embeds -> embeds.stream()
                        .map(EmbedCreateSpec::asRequest)
                        .collect(Collectors.toList())))
                .allowedMentions(mapPossibleOptional(allowedMentions(), AllowedMentions::toData))
                .flags(mapPossibleOptional(flags(), f -> f.stream()
                        .mapToInt(Message.Flag::getFlag)
                        .reduce(0, (left, right) -> left | right)))
                .components(mapPossible(components(), components -> components
                        .map(list -> list.stream()
                                .map(LayoutComponent::getData)
                                .collect(Collectors.toList()))
                        .map(Optional::of)
                        .orElse(Optional.of(Collections.emptyList()))))
                // TODO upon v10 upgrade, it is required to also include new files as attachment here
                .attachments(mapPossibleOptional(attachments(), attachments -> attachments.stream()
                        .map(Attachment::getData)
                        .collect(Collectors.toList())))
                .build();
        return MultipartRequest.ofRequestAndFiles(json, Stream.concat(files().stream(), fileSpoilers().stream())
                .map(MessageCreateFields.File::asRequest)
                .collect(Collectors.toList()));
    }
    
    static <T, R> Possible<Optional<R>> mapPossibleOptional(Possible<Optional<T>> value,
            Function<? super T, ? extends R> mapper) {
		return value.isAbsent() ? Possible.absent() : Possible.of(value.get().map(mapper));
	}
    
    static <T, R> Possible<R> mapPossible(Possible<T> value, Function<? super T, ? extends R> mapper) {
        return value.isAbsent() ? Possible.absent() : Possible.of(mapper.apply(value.get()));
    }
}
