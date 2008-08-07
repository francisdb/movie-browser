package com.flicklib.tools;

import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.TextExtractor;

public class ElementOnlyTextExtractor extends TextExtractor {

    public ElementOnlyTextExtractor(final Segment segment) {
        super(segment);
    }

    @Override
    public boolean excludeElement(StartTag startTag) {
        //LOGGER.debug(startTag.toString());
        return true;
    }
}
