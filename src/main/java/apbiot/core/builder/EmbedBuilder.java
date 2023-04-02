package apbiot.core.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateFields.Field;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.util.annotation.Nullable;

/**
 * Re-implementation of {@link discord4j.core.spec.EmbedCreateSpec} for more versatility<br>
 * <strong>NOT THREAD-SAFE</strong>
 * @author 278deco
 * @deprecated 4.0
 * @since 0.1
 */
public class EmbedBuilder {
	
	private EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
	
	private List<Field> fields = new ArrayList<>();
	
	/**
	 * add a new field inline with the last field added
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * @return an instance of EmbedBuilder
	 */
	public EmbedBuilder addTextInline(String fieldName, String fieldValue) {
		this.fields.add(EmbedCreateFields.Field.of(fieldName, fieldValue, true));
		return this;
	}

	/**
	 * add a new field below the last field added
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * @return an instance of EmbedBuilder
	 */
	public EmbedBuilder addTextBelow(String fieldName, String fieldValue) {
		this.fields.add(EmbedCreateFields.Field.of(fieldName, fieldValue, false));
		return this;
	}
	
	/**
	 * Edit an existing field and replace it name and value
	 * @param oldfieldName - the old field name
	 * @param newFieldname - the new field name
	 * @param newDescription - the new field name
	 * @return an instance of EmbedBuilder
	 */
	public EmbedBuilder editField(String oldfieldName, String newFieldname, String newDescription) {
		for(int i = 0; i < this.fields.size(); i++) {
			if(fields.get(i).name().equals(oldfieldName)) {
				boolean isInline = fields.get(i).inline();
				
				fields.set(i, EmbedCreateFields.Field.of(newFieldname, newDescription, isInline));
			}
		}
		return this;
	}
	
	public EmbedBuilder addExistingField(Field... field) {
		for(Field element : field) {
			this.fields.add(element);
		}
		
		return this;
	}
	
	public EmbedBuilder addExistingField(List<Field> fieldList) {
		this.fields.addAll(fieldList);
		return this;
	}
	
	/**
	 * Remove all existing fields
	 * @return an instance of EmbedBuilder
	 */
	public EmbedBuilder removeAllFields() {
		fields.clear();
		return this;
	}
	
	/**
	 * Set the title of the embed
	 * @param title - the title of the embed
	 * @return an instance of EmbedBuilder
	 */
	public EmbedBuilder setTitle(String title) {
		builder.title(title);
        return this;
    }
	
	/**
	 * Set the description of the embed
	 * @param description - the description of the embed
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setDescription(String description) {
    	builder.description(description);
        return this;
    }

    /**
	 * Set the url of the embed
	 * @param url - the url of the embed
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setUrl(String url) {
    	builder.url(url);
        return this;
    }

    /**
	 * Set the timestamp of the embed
	 * @param timestamp - the timestamp of the embed
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setTimestamp(Instant timestamp) {
    	builder.timestamp(timestamp);
        return this;
    }

    /**
	 * Set the color of the embed
	 * @param color - the color of the embed
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setColor(final Color color) {
    	builder.color(color);
        return this;
    }

    /**
	 * Set the footer of the embed
	 * @param text - the text contained in the footer
	 * @param iconUrl - the iconUrl of the footer
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setFooter(String text, @Nullable String iconUrl) {
    	builder.footer(text, iconUrl);
        return this;
    }

    /**
	 * Set the image of the embed
	 * @param url - the url of the image
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setImage(String url) {
    	builder.image(url);
        return this;
    }


    /**
	 * Set the thumbnail of the embed
	 * @param url - the url of the thumbnail
	 * @return an instance of EmbedBuilder
	 */
    public EmbedBuilder setThumbnail(String url) {
    	builder.thumbnail(url);
        return this;
    }

    /**
     * Set the author of the embed
     * @param name - the name of the author
     * @param url - the url of the author
     * @param iconUrl - the icon of the author
     * @return an instance of EmbedBuilder
     */
    public EmbedBuilder setAuthor(String name, @Nullable String url, @Nullable String iconUrl) {
    	builder.author(name, url, iconUrl);
        return this;
    }
    
    /**
     * Copy all properties of an EmbedBuilder to this instance excepted fields
     * @param builder - the builder you want to copy from
     * @return an instance of EmbedBuilder
     */
    public synchronized EmbedBuilder copyLayout(EmbedBuilder embedBuilderInstance) {
    	this.builder.from(embedBuilderInstance.builder.build());
    	return this;
    }
    
    /**
     * Copy all properties of an EmbedBuilder to this instance and the fields
     * @param builder - the builder you want to copy from
     * @return an instance of EmbedBuilder
     */
    public synchronized EmbedBuilder fullCopy(EmbedBuilder embedBuilderInstance) {
    	this.builder.from(embedBuilderInstance.builder.build());
    	this.fields = embedBuilderInstance.fields;
    	return this;
    }
	
    /**
     * Build the embed and return an sendable embed
     * @return the embed built
     */
	public EmbedCreateSpec build() {
		final EmbedCreateSpec.Builder finalBuilder = builder;
		finalBuilder.addAllFields(this.fields);
		
		return finalBuilder.build();
	}
    
 	
}
