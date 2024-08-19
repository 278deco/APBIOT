package apbiot.core.specs;

import java.io.InputStream;

import org.immutables.value.Value;

import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.InlineFieldStyle;
import discord4j.core.spec.Spec;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@InlineFieldStyle
@Value.Enclosing
public final class MessageCreateFields {

    private MessageCreateFields() {
        throw new AssertionError();
    }

    @Value.Immutable
    public interface File extends Spec<Tuple2<String, InputStream>> {

        static File of(String name, InputStream inputStream) {
            return ImmutableMessageCreateFields.File.of(name, inputStream);
        }

        String name();

        InputStream inputStream();

        @Override
        default Tuple2<String, InputStream> asRequest() {
            return Tuples.of(name(), inputStream());
        }
    }

    @SuppressWarnings("immutables:subtype")
    @Value.Immutable
    public interface FileSpoiler extends File {

        static FileSpoiler of(String name, InputStream inputStream) {
            return ImmutableMessageCreateFields.FileSpoiler.of(name, inputStream);
        }

        @Override
        default Tuple2<String, InputStream> asRequest() {
            return Tuples.of(Attachment.SPOILER_PREFIX + name(), inputStream());
        }
    }
}
