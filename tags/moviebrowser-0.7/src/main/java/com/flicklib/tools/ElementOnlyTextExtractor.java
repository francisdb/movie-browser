package com.flicklib.tools;

import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.TextExtractor;

/**
 * @author francisdb
 *
 */
public class ElementOnlyTextExtractor extends TextExtractor {

    /**
     * Constructs a new ElementOnlyTextExtractor based on the specified segment
     * @param segment
     */
    public ElementOnlyTextExtractor(final Segment segment) {
        super(segment);
    }

    @Override
    public boolean excludeElement(StartTag startTag) {
        //LOGGER.debug(startTag.toString());
        return true;
    }
}
