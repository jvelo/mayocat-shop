/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.pdf.internal;

import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import java.nio.file.Path;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.mayocat.pdf.PdfRenderingException;
import org.mayocat.pdf.PdfTemplateRenderer;
import java.util.Map;
import javax.inject.Inject;
import org.mayocat.templating.TemplateRenderer;
import org.mayocat.templating.TemplateRenderingException;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultPdfTemplateRenderer implements PdfTemplateRenderer
{
    @Inject
    private TemplateRenderer templateRenderer;

    @Override
    public void generatePDF(OutputStream outputStream, Path template, Map<String, Object> context) throws PdfRenderingException {
        this.generatePDF(outputStream, template, template.getParent(), context);
    }

    @Override
    public void generatePDF(OutputStream outputStream, Path template, Path renderingRoot, Map<String, Object> context)
            throws PdfRenderingException {
        ITextRenderer renderer = new ITextRenderer();

        try {
            String html = templateRenderer.renderAsString(template, context);

            // Ensure we have a valid XHTML document using JSoup
            Document jsoupDoc = Jsoup.parse(html);
            jsoupDoc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
            jsoupDoc.outputSettings().charset("UTF-8");

            String path = renderingRoot.toAbsolutePath().toUri().toString();
            renderer.setDocumentFromString(jsoupDoc.toString(), path);
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (DocumentException | TemplateRenderingException e) {
            throw new PdfRenderingException(e);
        }
    }
}
